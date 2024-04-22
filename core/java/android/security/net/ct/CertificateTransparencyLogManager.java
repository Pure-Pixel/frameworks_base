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
package android.security.net.ct;

import static android.annotation.SystemApi.Client.MODULE_LIBRARIES;

import android.annotation.FlaggedApi;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.RequiresPermission;
import android.annotation.SystemApi;
import android.annotation.SystemService;
import android.content.Context;
import android.security.Flags;

import com.android.org.conscrypt.ct.CertificateEntry;
import com.android.org.conscrypt.ct.SignedCertificateTimestamp;

/** A manager of Certificate Transparency log lists. */
@FlaggedApi(Flags.FLAG_CERTIFICATE_TRANSPARENCY_LOG_LIST_SERVICE)
@SystemService(Context.CERTIFICATE_TRANSPARENCY_SERVICE)
public final class CertificateTransparencyLogManager {

    private final Context mContext;
    private final ICertificateTransparencyLogManager mService;

    /** {@hide} */
    CertificateTransparencyLogManager(Context context, ICertificateTransparencyLogManager service) {
        mContext = context;
        mService = service;
    }

    /**
     * @return the latest {@link CertificateTransparencyLogList}, or {@code null} if no log list is
     *     available.
     */
    @Nullable
    CertificateTransparencyLogList getLogList() {
        return null;
    }

    /**
     * @return A {@link CertificateTransparencyLogList} matching the provided {@param version}, or
     *     {@code null} if no log list with that version is available.
     */
    @Nullable
    CertificateTransparencyLogList getLogList(@NonNull String version) {
        return null;
    }

    /**
     * @hide Verify the signature of a signed certificate timestamp for the given certificate entry.
     * @return the result of the verification.
     */
    @CertificateTransparencyConstants.VerificationStatus
    int verifySingleSct(@NonNull SignedCertificateTimestamp sct, @NonNull CertificateEntry entry) {
        return CertificateTransparencyConstants.CT_LOG_VERIFICATION_STATUS_UNABLE_TO_VERIFY;
    }

    /**
     * @hide Force an update of the {@link CertificateTransparencyLogList} stored on the device.
     * @return the {@link UpdateStatus} of the request.
     */
    @SystemApi(client = MODULE_LIBRARIES)
    @RequiresPermission("MANAGE_CERTIFICATE_TRANSPARENCY_POLICY")
    @CertificateTransparencyConstants.UpdateStatus
    public int forceLogListUpdate() {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
