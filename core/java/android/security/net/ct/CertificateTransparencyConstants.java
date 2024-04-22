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

import android.annotation.FlaggedApi;
import android.annotation.IntDef;
import android.security.Flags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Contains the Certificate Transparency constants. */
@FlaggedApi(Flags.FLAG_CERTIFICATE_TRANSPARENCY_LOG_LIST_SERVICE)
interface CertificateTransparencyConstants {

    /**
     * @hide The result of a log list update request.
     */
    @IntDef({CT_LOG_UPDATE_STATUS_SUCCESS, CT_LOG_UPDATE_STATUS_FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    @interface UpdateStatus {}

    int CT_LOG_UPDATE_STATUS_SUCCESS = 0;
    int CT_LOG_UPDATE_STATUS_FAILURE = 1;

    /**
     * @hide The state of the log from the log list distributor's perspective.
     */
    @IntDef({
        CT_LOG_STATE_PENDING,
        CT_LOG_STATE_QUALIFIED,
        CT_LOG_STATE_USABLE,
        CT_LOG_STATE_READONLY,
        CT_LOG_STATE_RETIRED,
        CT_LOG_STATE_REJECTED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LogState {}

    int CT_LOG_STATE_PENDING = 0;
    int CT_LOG_STATE_QUALIFIED = 1;
    int CT_LOG_STATE_USABLE = 2;
    int CT_LOG_STATE_READONLY = 3;
    int CT_LOG_STATE_RETIRED = 4;
    int CT_LOG_STATE_REJECTED = 5;

    /**
     * @hide The state of the log from the log list distributor's perspective.
     */
    @IntDef({
        CT_LOG_VERIFICATION_STATUS_UNKNOWN_LOG,
        CT_LOG_VERIFICATION_STATUS_INVALID_SIGNATURE,
        CT_LOG_VERIFICATION_STATUS_INVALID_SCT,
        CT_LOG_VERIFICATION_STATUS_VALID
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface VerificationStatus {}

    int CT_LOG_VERIFICATION_STATUS_UNABLE_TO_VERIFY = 0;
    int CT_LOG_VERIFICATION_STATUS_UNKNOWN_LOG = 1;
    int CT_LOG_VERIFICATION_STATUS_INVALID_SIGNATURE = 2;
    int CT_LOG_VERIFICATION_STATUS_INVALID_SCT = 3;
    int CT_LOG_VERIFICATION_STATUS_VALID = 4;
}
