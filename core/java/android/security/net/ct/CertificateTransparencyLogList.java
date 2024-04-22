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
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.security.Flags;

import java.time.Instant;
import java.util.List;

/** A list of {@link CertificateTransparencyLog}s. */
@FlaggedApi(Flags.FLAG_CERTIFICATE_TRANSPARENCY_LOG_LIST_SERVICE)
public interface CertificateTransparencyLogList {

    /**
     * @return {@code true} when the list contains all known logs, including not yet usable and no
     *     longer usable ones.
     */
    boolean isAllLogs();

    /**
     * @return the version of this log list. The version will change whenever a change is made to
     *     any part of this log list.
     */
    @Nullable
    String getVersion();

    /**
     * @return the time at which this version of the log list was published.
     */
    @Nullable
    Instant getLogListTimestamp();

    /**
     * @return the list of {@link CertificateTransparencyLog}s.
     */
    @NonNull
    List<CertificateTransparencyLog> getLogList();
}
