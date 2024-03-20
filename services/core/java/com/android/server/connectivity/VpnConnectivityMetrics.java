/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkAgent;
import android.net.NetworkCapabilities;
import android.net.ipsec.ike.exceptions.IkeNetworkLostException;
import android.net.ipsec.ike.exceptions.IkeNonProtocolException;
import android.net.ipsec.ike.exceptions.IkeProtocolException;
import android.net.ipsec.ike.exceptions.IkeTimeoutException;
import android.os.SystemClock;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for logging VPN connectivity health event
 * @hide
 */
public class VpnConnectivityMetrics {
    private List<VpnConnection> mVpnConnectionList = new ArrayList<>();
    private Map<Integer, VpnMetricCollector> mVpnMetricCollectorMap = new HashMap<>();
    private final Dependencies mDependencies;
    private final ConnectivityManager mConnectivityManager;

    public VpnConnectivityMetrics(Context context) {
        this(context, new Dependencies());
    }

    @VisibleForTesting
    public VpnConnectivityMetrics(Context context, Dependencies dependencies) {
        mDependencies = dependencies;
        mConnectivityManager = context.getSystemService(ConnectivityManager.class);
    }

    @VisibleForTesting
    public static class Dependencies {
        public long getElapsedRealtime() {
            return SystemClock.elapsedRealtime();
        }
    }

    public class VpnMetricCollector {
        private final int mUserId;
        private long mVpnConnectionPeriodMs = 0;
        // Each user should have only 1 VPN network agent connected but in the case where the VPN is
        // restarted, a new network agent will be connected before the old network is disconnected.
        private Map<NetworkAgent, Long> mVpnConnectedTimestampMs = new HashMap<>();
        private Set<Integer> mUnderlyingTypeSet = new HashSet<>();
        private long mVpnValidatedPeriodMs = 0;
        private Map<NetworkAgent, Long> mVpnValidatedTimestampMs = new HashMap<>();
        private int mValidationAttempts = 0;
        private int mValidationAttemptsSuccess = 0;
        private List<Integer> mErrorCodes = new ArrayList<>();

        VpnMetricCollector(int userId) {
            mUserId = userId;
        }

        private VpnConnectionParams mVpnConnectionParams = null;

        private void updateTimestamps(long timeNow) {
            for (Map.Entry<NetworkAgent, Long> entry : mVpnConnectedTimestampMs.entrySet()) {
                mVpnConnectionPeriodMs += timeNow - entry.getValue();
                entry.setValue(timeNow);
            }
            for (Map.Entry<NetworkAgent, Long> entry : mVpnValidatedTimestampMs.entrySet()) {
                mVpnValidatedPeriodMs += timeNow - entry.getValue();
                entry.setValue(timeNow);
            }
        }

        /** Build VpnConnection proto and add it to the stored list. */
        private void buildAndAppendVpnConnectionMetric() {
            if (mVpnConnectionParams == null) {
                return;
            }
            updateTimestamps(mDependencies.getElapsedRealtime());
            final VpnConnection vpnConnection = new VpnConnection();
            vpnConnection.setVpnConnectionParams(mVpnConnectionParams);
            vpnConnection.setConnectedPeriodSeconds((int) (mVpnConnectionPeriodMs / 1000));
            for (int type : mUnderlyingTypeSet) {
                vpnConnection.addUnderlyingNetworkType(type);
            }
            vpnConnection.setVpnValidatedPeriodSeconds((int) (mVpnValidatedPeriodMs / 1000));
            vpnConnection.setValidationAttempts(mValidationAttempts);
            vpnConnection.setValidationAttemptsSuccess(mValidationAttemptsSuccess);

            for(int code : mErrorCodes) {
                vpnConnection.addErrorCode(code);
            }

            mVpnConnectionList.add(vpnConnection);
        }

        /** Inform the VpnMetricCollector that an app starts a Vpn. */
        public void onAppStarted() {
            mVpnConnectionParams = new VpnConnectionParams();
            // TODO: Fill the values of VpnConnectionParams.
        }

        /** Inform the VpnMetricCollector that a Vpn network is connected. */
        public void onVpnConnected(NetworkAgent networkAgent) {
            if (mVpnConnectedTimestampMs.containsKey(networkAgent)) {
                Log.wtf(getTag(), "onVpnConnected called on an already connected NetworkAgent: "
                        + networkAgent);
            }
            mVpnConnectedTimestampMs.put(networkAgent, mDependencies.getElapsedRealtime());
        }

        /** Inform the VpnMetricCollector that a Vpn network is disconnected. */
        public void onVpnDisconnected(NetworkAgent networkAgent) {
            if (!mVpnConnectedTimestampMs.containsKey(networkAgent)) {
                Log.wtf(getTag(), "onVpnDisconnected called on an unknown NetworkAgent: "
                        + networkAgent);
                return;
            }
            mVpnConnectionPeriodMs +=
                    mDependencies.getElapsedRealtime() - mVpnConnectedTimestampMs.get(networkAgent);
            if (mVpnValidatedTimestampMs.containsKey(networkAgent)) {
                mVpnValidatedPeriodMs += mDependencies.getElapsedRealtime()
                        - mVpnValidatedTimestampMs.get(networkAgent);
                mVpnValidatedTimestampMs.remove(networkAgent);
            }
            mVpnConnectedTimestampMs.remove(networkAgent);
        }

        /** Inform the VpnMetricCollector that the underlying networks are updated. */
        public void onSetUnderlyingNetworks(Network[] networks) {
            for (Network network : networks) {
                final int[] networkTypes = getNetworkTypes(network);
                if (networkTypes == null) {
                    Log.wtf(getTag(), "Unable to get network types of underlying network");
                    continue;
                }
                for (int type : networkTypes) {
                    mUnderlyingTypeSet.add(type);
                }
            }
        }

        /** Inform the VpnMetricCollector of a validation attempt and the result. */
        public void onValidationStatus(NetworkAgent networkAgent, int status) {
            if (!mVpnConnectedTimestampMs.containsKey(networkAgent)) {
                Log.wtf(getTag(), "onValidationStatus called on an unconnected NetworkAgent: "
                        + networkAgent);
            }
            mValidationAttempts++;
            if (status == NetworkAgent.VALIDATION_STATUS_VALID) {
                mValidationAttemptsSuccess++;
                mVpnValidatedTimestampMs.putIfAbsent(networkAgent,
                        mDependencies.getElapsedRealtime());
            } else if (status == NetworkAgent.VALIDATION_STATUS_NOT_VALID) {
                if (mVpnValidatedTimestampMs.containsKey(networkAgent)) {
                    mVpnValidatedPeriodMs += mDependencies.getElapsedRealtime()
                            - mVpnValidatedTimestampMs.get(networkAgent);
                }
                mVpnValidatedTimestampMs.remove(networkAgent);
            }
        }

        /** Inform the VpnMetricCollector of the given exception */
        public void onException(Exception exception) {
            final int errorCode;
            if (exception instanceof IkeProtocolException) {
                final IkeProtocolException error = (IkeProtocolException) exception;
                errorCode = convertIkeProtocolExceptionErrorToProto(error.getErrorType());
            } else if (exception instanceof IkeNetworkLostException) {
                errorCode = 20; /* NETWORK_LOST */
            } else if (exception instanceof IkeNonProtocolException) {
                if (exception.getCause() instanceof UnknownHostException) {
                    errorCode = 18; /* NETWORK_UNKNOWN_HOST */
                } else if (exception.getCause() instanceof IkeTimeoutException) {
                    errorCode = 19; /* NETWORK_PROTOCOL_TIMEOUT */
                } else if (exception.getCause() instanceof IOException) {
                    errorCode = 21; /* NETWORK_IO */
                } else {
                    errorCode = 1; /* UNKNOWN */
                }
            } else {
                errorCode = 1; /* UNKNOWN */
            }
            mErrorCodes.add(errorCode);
        }

        private String getTag() {
            return VpnMetricCollector.class.getSimpleName() + "/" + mUserId;
        }

        private int[] getNetworkTypes(Network network) {
            final NetworkCapabilities nc = mConnectivityManager.getNetworkCapabilities(network);
            if (nc == null) return null;
            return nc.getTransportTypes();
        }

        private int convertIkeProtocolExceptionErrorToProto(int errorCode) {
            switch (errorCode) {
                case IkeProtocolException.ERROR_TYPE_AUTHENTICATION_FAILED:
                    return 9; /* AUTHENTICATION_FAILED */
                case IkeProtocolException.ERROR_TYPE_CHILD_SA_NOT_FOUND:
                    return 17; /* CHILD_SA_NOT_FOUND */
                case IkeProtocolException.ERROR_TYPE_FAILED_CP_REQUIRED:
                    return 13; /* FAILED_CP_REQUIRED */
                case IkeProtocolException.ERROR_TYPE_INTERNAL_ADDRESS_FAILURE:
                    return 12; /* INTERNAL_ADDRESS_FAILURE */
                case IkeProtocolException.ERROR_TYPE_INVALID_IKE_SPI:
                    return 3; /* INVALID_IKE_SPI */
                case IkeProtocolException.ERROR_TYPE_INVALID_KE_PAYLOAD:
                    return 8; /* INVALID_KE_PAYLOAD */
                case IkeProtocolException.ERROR_TYPE_INVALID_MAJOR_VERSION:
                    return 4; /* INVALID_MAJOR_VERSION */
                case IkeProtocolException.ERROR_TYPE_INVALID_MESSAGE_ID:
                    return 6; /* INVALID_MESSAGE_ID */
                case IkeProtocolException.ERROR_TYPE_INVALID_SELECTORS:
                    return 15; /* INVALID_SELECTORS */
                case IkeProtocolException.ERROR_TYPE_INVALID_SYNTAX:
                    return 5; /* INVALID_SYNTAX */
                case IkeProtocolException.ERROR_TYPE_NO_ADDITIONAL_SAS:
                    return 11; /* NO_ADDITIONAL_SAS */
                case IkeProtocolException.ERROR_TYPE_NO_PROPOSAL_CHOSEN:
                    return 7; /* NO_PROPOSAL_CHOSEN */
                case IkeProtocolException.ERROR_TYPE_SINGLE_PAIR_REQUIRED:
                    return 10; /* SINGLE_PAIR_REQUIRED */
                case IkeProtocolException.ERROR_TYPE_TEMPORARY_FAILURE:
                    return 16; /* TEMPORARY_FAILURE */
                case IkeProtocolException.ERROR_TYPE_TS_UNACCEPTABLE:
                    return 14; /* TS_UNACCEPTABLE */
                case IkeProtocolException.ERROR_TYPE_UNSUPPORTED_CRITICAL_PAYLOAD:
                    return 2; /* UNSUPPORTED_CRITICAL_PAYLOAD */
            }
            return 1; /* UNKNOWN */
        }
    }

    /** Create a new VpnMetricCollector for the given user, to be used in Vpn */
    public VpnMetricCollector newCollector(int userId) {
        final VpnMetricCollector newVpnMetricCollector = new VpnMetricCollector(userId);
        mVpnMetricCollectorMap.put(userId, newVpnMetricCollector);
        return newVpnMetricCollector;
    }

    /** Build and get the currently stored metrics */
    @VisibleForTesting
    public List<VpnConnection> pullMetrics() {
        for (VpnMetricCollector vpnMetricCollector : mVpnMetricCollectorMap.values()) {
            vpnMetricCollector.buildAndAppendVpnConnectionMetric();
        }

        return mVpnConnectionList;
    }
}
