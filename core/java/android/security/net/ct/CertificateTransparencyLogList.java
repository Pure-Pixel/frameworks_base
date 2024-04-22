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
import android.os.Parcel;
import android.os.Parcelable;
import android.security.Flags;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/** A list of {@link CertificateTransparencyLog}s. */
@FlaggedApi(Flags.FLAG_CERTIFICATE_TRANSPARENCY_LOG_LIST_SERVICE)
public final class CertificateTransparencyLogList implements Parcelable {

    @NonNull
    public static final Parcelable.Creator<CertificateTransparencyLogList> CREATOR =
            new Parcelable.Creator<CertificateTransparencyLogList>() {

                @Override
                public CertificateTransparencyLogList createFromParcel(Parcel source) {
                    return new CertificateTransparencyLogList();
                }

                @Override
                public CertificateTransparencyLogList[] newArray(int size) {
                    return new CertificateTransparencyLogList[size];
                }
            };

    /**
     * @return {@code true} when the list contains all known logs, including not yet usable and no
     *     longer usable ones.
     */
    public boolean isAllLogs() {
        return true;
    }

    /**
     * @return the version of this log list. The version will change whenever a change is made to
     *     any part of this log list.
     */
    @Nullable
    public String getVersion() {
        return null;
    }

    /**
     * @return the time at which this version of the log list was published.
     */
    @Nullable
    public Instant getLogListTimestamp() {
        return null;
    }

    /**
     * @return the list of {@link CertificateTransparencyLog}s.
     */
    @NonNull
    public List<CertificateTransparencyLog> getLogList() {
        return Collections.emptyList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}
}
