/*
 * Copyright 2024 The Android Open Source Project
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
package com.android.server.security.net.ct;

import android.os.RemoteException;
import android.security.net.ct.CertificateTransparencyConstants;
import android.security.net.ct.CertificateTransparencyLogList;
import android.security.net.ct.ICertificateTransparencyLogManager;

/**
 * A BinderService for Certificate Transparency logs.
 *
 * @hide
 */
final class CertificateTransparencyBinderService extends ICertificateTransparencyLogManager.Stub {

    @Override
    public CertificateTransparencyLogList getLogList(String version) throws RemoteException {
        return new CertificateTransparencyLogList();
    }

    @Override
    @CertificateTransparencyConstants.UpdateStatus
    public int forceLogListUpdate() throws RemoteException {
        return CertificateTransparencyConstants.CT_LOG_UPDATE_STATUS_FAILURE;
    }
}
