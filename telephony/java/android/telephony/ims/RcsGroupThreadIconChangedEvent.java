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
package android.telephony.ims;

import android.annotation.NonNull;
import android.annotation.WorkerThread;
import android.net.Uri;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.ims.aidl.IRcs;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;

/**
 * An event that indicates an {@link RcsGroupThread}'s icon was changed.
 *
 * @hide - TODO(109759350) make this public
 */
public class RcsGroupThreadIconChangedEvent extends RcsGroupThreadEvent {
    private final Uri mOldIcon;
    private final Uri mNewIcon;

    private RcsGroupThreadIconChangedEvent(int id, long timestamp, RcsGroupThread rcsGroupThread,
            RcsParticipant originatingParticipant, Uri oldIcon, Uri newIcon) {
        super(id, timestamp, rcsGroupThread, originatingParticipant);
        mOldIcon = oldIcon;
        mNewIcon = newIcon;
    }

    /**
     * @return Returns the old icon the {@link RcsGroupThread} had
     */
    public Uri getOldIcon() {
        return mOldIcon;
    }

    /**
     * @return Returns the new icon the {@link RcsGroupThread} has
     */
    public Uri getNewIcon() {
        return mNewIcon;
    }

    /**
     * Use this builder to create an instance of {@link RcsGroupThreadIconChangedEvent}. The event
     * object will be persisted into storage after it is built. {@link RcsEvent}s are immutable and
     * can only be deleted after they are created.
     */
    public static class Builder extends RcsGroupThreadEvent.Builder<Builder> {
        private Uri mBuilderOldIcon;
        private Uri mBuilderNewIcon;

        /**
         * Create a new Builder for {@link RcsGroupThreadIconChangedEvent}
         *
         * @param rcsGroupThread         The {@link RcsGroupThread} that had its icon changed
         * @param originatingParticipant The {@link RcsParticipant} that changed the icon.
         */
        public Builder(@NonNull RcsGroupThread rcsGroupThread,
                RcsParticipant originatingParticipant) {
            super(rcsGroupThread, originatingParticipant);
        }

        /**
         * Sets the new icon of the {@link RcsGroupThread}
         * @param newIcon
         * @return The same instance of {@link Builder} to chain methods.
         */
        public Builder setNewIcon(Uri newIcon) {
            mBuilderNewIcon = newIcon;
            return this;
        }

        /**
         * Creates an instance of {@link RcsGroupThreadIconChangedEvent} and saves into storage.
         * @return A new instance of {@link RcsGroupThreadIconChangedEvent}
         */
        @Override
        @WorkerThread
        public RcsGroupThreadIconChangedEvent buildAndSave() {
            try {
                IRcs iRcs = IRcs.Stub.asInterface(ServiceManager.getService("ircs"));
                if (iRcs != null) {
                    mBuilderOldIcon = mBuilderRcsGroupThread.getGroupIcon();
                    int participantId = mOriginatingParticipant == null ? 0
                            : mOriginatingParticipant.getId();
                    int id = iRcs.createGroupThreadIconChangedEvent(mBuilderTimestamp,
                            mBuilderRcsGroupThread.getThreadId(), participantId,
                            mBuilderOldIcon, mBuilderNewIcon);
                    return new RcsGroupThreadIconChangedEvent(id, mBuilderTimestamp,
                            mBuilderRcsGroupThread, mOriginatingParticipant, mBuilderOldIcon,
                            mBuilderNewIcon);
                }
            } catch (RemoteException re) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadIconChangedEvent.Builder: Exception happened during "
                                + "buildAndSave",
                        re);
            }
            return null;
        }

        /**
         * @hide
         */
        @VisibleForTesting
        public RcsGroupThreadIconChangedEvent buildForTest() {
            return new RcsGroupThreadIconChangedEvent(0, mBuilderTimestamp, mBuilderRcsGroupThread,
                    mOriginatingParticipant, mBuilderOldIcon, mBuilderNewIcon);
        }

        /**
         * @hide
         */
        @Override
        Builder self() {
            return this;
        }
    }

    public static final Creator<RcsGroupThreadIconChangedEvent> CREATOR =
            new Creator<RcsGroupThreadIconChangedEvent>() {
                @Override
                public RcsGroupThreadIconChangedEvent createFromParcel(Parcel in) {
                    return new RcsGroupThreadIconChangedEvent(in);
                }

                @Override
                public RcsGroupThreadIconChangedEvent[] newArray(int size) {
                    return new RcsGroupThreadIconChangedEvent[size];
                }
            };

    protected RcsGroupThreadIconChangedEvent(Parcel in) {
        super(in);
        mOldIcon = in.readParcelable(Uri.class.getClassLoader());
        mNewIcon = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mOldIcon, flags);
        dest.writeParcelable(mNewIcon, flags);
    }
}
