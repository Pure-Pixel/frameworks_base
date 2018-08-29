/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.server;


import android.content.Context;
import android.net.INetdUnsolicitedEventCallback;
import android.net.INetdUnsolicitedEventListener;
import android.os.RemoteException;
import android.util.Log;

import com.android.internal.annotations.GuardedBy;


/**
 * Implementation of the INetdUnsolicitedEventListener interface.
 */
public class NetdUnsolicitedEventListenerService extends INetdUnsolicitedEventListener.Stub {

    public static final String SERVICE_NAME = "netd_ulistener";

    private static final String TAG = NetdUnsolicitedEventListenerService.class.getSimpleName();
    private static final boolean DBG = false;

    /**
     * Binder context for this service
     */
    private final Context mContext;

    /**
     * There are 1 possible callbacks currently.
     *
     * mNetdEventCallbackList[CALLBACK_CALLER_NETWORKMANAGEMENT_SERVICE]
     * Callback registered/unregistered by NetworkManagementService.
     */
    @GuardedBy("this")
    private static final int[] ALLOWED_CALLBACK_TYPES = {
        INetdUnsolicitedEventCallback.CALLBACK_CALLER_NETWORKMANAGEMENT_SERVICE
    };

    @GuardedBy("this")
    private INetdUnsolicitedEventCallback[] mNetdEventCallbackList =
            new INetdUnsolicitedEventCallback[ALLOWED_CALLBACK_TYPES.length];

    public synchronized boolean addNetdUnsolicitedEventCallback(int callerType,
            INetdUnsolicitedEventCallback callback) {
        if (!isValidCallerType(callerType)) {
            Log.e(TAG, "Invalid caller type: " + callerType);
            return false;
        }
        mNetdEventCallbackList[callerType] = callback;
        return true;
    }

    public synchronized boolean removeNetdEventCallback(int callerType) {
        if (!isValidCallerType(callerType)) {
            Log.e(TAG, "Invalid caller type: " + callerType);
            return false;
        }
        mNetdEventCallbackList[callerType] = null;
        return true;
    }

    private static boolean isValidCallerType(int callerType) {
        for (int i = 0; i < ALLOWED_CALLBACK_TYPES.length; i++) {
            if (callerType == ALLOWED_CALLBACK_TYPES[i]) {
                return true;
            }
        }
        return false;
    }

    public NetdUnsolicitedEventListenerService(Context context) {
        mContext = context;
    }

    @Override
    public synchronized void onInterfaceClassActivityEvent(boolean isActive,
            String name, long timestamp , int uid)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceClassActivityEvent(isActive, name, timestamp, uid);
            }
        }
    }

    @Override
    public synchronized void onQuotaLimitEvent(String alertName,  String ifName)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onQuotaLimitEvent(alertName, ifName);
            }
        }
    }

    @Override
    public synchronized void onInterfaceDnsServersEvent(String ifName,
            long lifetime, String[] servers)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceDnsServersEvent(ifName, lifetime, servers);
            }
        }
    }

    @Override
    public synchronized void onInterfaceAddressChangeEvent(boolean updated,
            String addr, String ifName, int flags, int scope)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceAddressChangeEvent(updated, addr, ifName, flags, scope);
            }
        }
    }

    @Override
    public synchronized void onInterfaceAddEvent(String ifName)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceAddEvent(ifName);
            }
        }
    }

    @Override
    public synchronized void onInterfaceRemoveEvent(String ifName)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceRemoveEvent(ifName);
            }
        }
    }

    @Override
    public synchronized void onInterfaceChangedEvent(String ifName, boolean status)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceChangedEvent(ifName, status);
            }
        }
    }

    @Override
    public synchronized void onInterfaceLinkStatusEvent(String ifName, boolean status)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onInterfaceLinkStatusEvent(ifName, status);
            }
        }
    }

    @Override
    public synchronized void onRouteChangeEvent(boolean updated,
            String route, String gateway, String ifName)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onRouteChangeEvent(updated, route, gateway, ifName);
            }
        }
    }

    @Override
    public synchronized void onStrictCleartextEvent(int uid, String firstPacket)
            throws RemoteException {
        for (INetdUnsolicitedEventCallback callback : mNetdEventCallbackList) {
            if (callback != null) {
                callback.onStrictCleartextEvent(uid, firstPacket);
            }
        }
    }
}
