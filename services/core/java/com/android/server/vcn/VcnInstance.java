/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.server.vcn;

import android.annotation.NonNull;
import android.net.NetworkRequest;
import android.net.vcn.VcnConfig;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;

import java.util.Objects;

/**
 * Represents an single instance of a VCN.
 *
 * <p>Each VcnInstance manages all tunnels for a given subscription group, including per-capability
 * networks, network selection, and multi-homing.
 *
 * @hide
 */
public class VcnInstance extends Handler {
    private static final String TAG = VcnInstance.class.getSimpleName();

    // TODO: Expose DEFAULT_INTERNET_CONNECTION_SCORE from Telephony
    /**
     * The score of default VCN Networks.
     *
     * <p>This is identical to the DataConnection.DEFAULT_INTERNET_CONNECTION_SCORE.
     */
    public static final int VCN_NETWORK_SCORE = 50;

    @NonNull private final VcnContext mVcnContext;
    @NonNull private final ParcelUuid mSubscriptionGroup;
    @NonNull private final Dependencies mDeps;

    @NonNull private VcnConfig mConfig;

    public VcnInstance(
            @NonNull VcnContext vcnContext,
            @NonNull ParcelUuid subscriptionGroup,
            @NonNull VcnConfig config) {
        this(vcnContext, subscriptionGroup, config, new Dependencies());
    }

    private VcnInstance(
            @NonNull VcnContext vcnContext,
            @NonNull ParcelUuid subscriptionGroup,
            @NonNull VcnConfig config,
            @NonNull Dependencies deps) {
        super(Objects.requireNonNull(vcnContext, "Missing vcnContext").getLooper());
        mVcnContext = vcnContext;
        mSubscriptionGroup = Objects.requireNonNull(subscriptionGroup, "Missing subscriptionGroup");
        mDeps = Objects.requireNonNull(deps, "Missing deps");

        mConfig = Objects.requireNonNull(config, "Missing config");
    }

    /** Asynchronously updates the configuration and triggers a re-evaluation of Networks */
    public void updateConfig(@NonNull VcnConfig config) {
        Objects.requireNonNull(config, "Missing config");
        // TODO: Proxy to handler, and make config there.
    }

    /** Asynchronously tears down this VcnInstance, along with all tunnels and Networks */
    public void teardown() {
        // TODO: Proxy to handler, and teardown there.
    }

    /** Notifies this VcnInstance of a new NetworkRequest */
    public void onNetworkRequested(@NonNull NetworkRequest request, int score, int providerId) {
        Objects.requireNonNull(request, "Missing request");

        // TODO: Proxy to handler, and handle there.
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        // TODO: Do something
    }

    private static class Dependencies {}
}
