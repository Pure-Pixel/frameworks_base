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

package com.android.server.net;

import android.net.INetdUnsolicitedEventCallback;

/**
 * Base {@link INetdUnsolicitedEventCallback} that provides no-op
 * implementations which can be overridden.
 *
 * @hide
 */
public class BaseNetdUnsolicitedEventCallback extends INetdUnsolicitedEventCallback.Stub {
    @Override
    public void onInterfaceClassActivityEvent(boolean isActive,
            String name, long timestamp , int uid) {
        // default no-op
    }

    @Override
    public void onQuotaLimitEvent(String alertName,  String ifName) {
        // default no-op
    }

    @Override
    public void onInterfaceDnsServersEvent(String ifName, long lifetime, String[] servers) {
        // default no-op
    }

    @Override
    public void onInterfaceAddressChangeEvent(boolean updated,
            String addr, String ifName, int flags, int scope) {
        // default no-op
    }
    @Override
    public void onInterfaceAddEvent(String ifName) {
        // default no-op
    }

    @Override
    public void onInterfaceRemoveEvent(String ifName) {
        // default no-op
    }

    @Override
    public void onInterfaceChangedEvent(String ifName, boolean status) {
        // default no-op
    }

    @Override
    public void onInterfaceLinkStatusEvent(String ifName, boolean status) {
        // default no-op
    }

    @Override
    public void onRouteChangeEvent(boolean updated, String route, String gateway, String ifName) {
        // default no-op
    }

    @Override
    public void onStrictCleartextEvent(int uid, String hex) {
        // default no-op
    }
}
