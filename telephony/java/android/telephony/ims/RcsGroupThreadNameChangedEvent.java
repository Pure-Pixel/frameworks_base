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
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.ims.aidl.IRcs;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;

/**
 * An event that indicates an {@link RcsGroupThread}'s name was changed.
 *
 * @hide - TODO(109759350) make this public
 */
public class RcsGroupThreadNameChangedEvent extends RcsGroupThreadEvent {
    private String mOldName;
    private String mNewName;

    private RcsGroupThreadNameChangedEvent(int id, long timestamp, RcsGroupThread rcsGroupThread,
            RcsParticipant originatingParticipant, String oldName, String newName) {
        super(id, timestamp, rcsGroupThread, originatingParticipant);
        mOldName = oldName;
        mNewName = newName;
    }

    /**
     * @return Returns the old name of this {@link RcsGroupThread}
     */
    public String getOldName() {
        return mOldName;
    }

    /**
     * @return Returns the new name of this {@link RcsGroupThread}
     */
    public String getNewName() {
        return mNewName;
    }

    /**
     * Use this builder to create an instance of {@link RcsGroupThreadNameChangedEvent}. The event
     * object will be persisted into storage after it is built. {@link RcsEvent}s are immutable and
     * can only be deleted after they are created.
     */
    public static class Builder extends RcsGroupThreadEvent.Builder<Builder> {
        private String mBuilderOldName;
        private String mBuilderNewName;

        /**
         * Create a new Builder for {@link RcsGroupThreadNameChangedEvent}
         *
         * @param rcsGroupThread         The {@link RcsGroupThread} that had its name changed
         * @param originatingParticipant The {@link RcsParticipant} that changed the name.
         */
        public Builder(@NonNull RcsGroupThread rcsGroupThread,
                RcsParticipant originatingParticipant) {
            super(rcsGroupThread, originatingParticipant);
        }

        /**
         * Sets the new name of the {@link RcsGroupThread}
         * @param newName
         * @return The same instance of {@link Builder} to chain methods.
         */
        public Builder setNewName(String newName) {
            mBuilderNewName = newName;
            return this;
        }

        /**
         * Creates an instance of {@link RcsGroupThreadNameChangedEvent} and saves into storage.
         * @return A new instance of {@link RcsGroupThreadNameChangedEvent}
         */
        @Override
        @WorkerThread
        public RcsGroupThreadNameChangedEvent buildAndSave() {
            try {
                IRcs iRcs = IRcs.Stub.asInterface(ServiceManager.getService("ircs"));
                if (iRcs != null) {
                    mBuilderOldName = mBuilderRcsGroupThread.getGroupName();
                    int participantId = mOriginatingParticipant == null ? 0
                            : mOriginatingParticipant.getId();
                    int id = iRcs.createGroupThreadNameChangedEvent(mBuilderTimestamp,
                            mBuilderRcsGroupThread.getThreadId(), participantId,
                            mBuilderOldName, mBuilderNewName);
                    return new RcsGroupThreadNameChangedEvent(id, mBuilderTimestamp,
                            mBuilderRcsGroupThread, mOriginatingParticipant, mBuilderOldName,
                            mBuilderNewName);
                }
            } catch (RemoteException re) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadNameChangedEvent.Builder: Exception happened during "
                                + "buildAndSave",
                        re);
            }
            return null;
        }

        /**
         * @hide
         */
        @VisibleForTesting
        public RcsGroupThreadNameChangedEvent buildForTest() {
            mBuilderOldName = mBuilderRcsGroupThread.getGroupName();
            return new RcsGroupThreadNameChangedEvent(0, mBuilderTimestamp, mBuilderRcsGroupThread,
                    mOriginatingParticipant, mBuilderOldName, mBuilderNewName);
        }

        /**
         * @hide
         */
        @Override
        Builder self() {
            return this;
        }
    }

    public static final Creator<RcsGroupThreadNameChangedEvent> CREATOR =
            new Creator<RcsGroupThreadNameChangedEvent>() {
                @Override
                public RcsGroupThreadNameChangedEvent createFromParcel(Parcel in) {
                    return new RcsGroupThreadNameChangedEvent(in);
                }

                @Override
                public RcsGroupThreadNameChangedEvent[] newArray(int size) {
                    return new RcsGroupThreadNameChangedEvent[size];
                }
            };

    protected RcsGroupThreadNameChangedEvent(Parcel in) {
        super(in);
        mOldName = in.readString();
        mNewName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mOldName);
        dest.writeString(mNewName);
    }
}
