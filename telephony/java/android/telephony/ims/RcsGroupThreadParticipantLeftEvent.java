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
 * An event that indicates an RCS participant has left an {@link RcsThread}.
 * @hide - TODO(109759350) make this public
 */
public class RcsGroupThreadParticipantLeftEvent extends RcsGroupThreadEvent {
    private RcsParticipant mLeftParticipant;

    public RcsGroupThreadParticipantLeftEvent(int id, long timestamp,
            RcsGroupThread rcsGroupThread, RcsParticipant originatingParticipant,
            RcsParticipant rcsParticipant) {
        super(id, timestamp, rcsGroupThread, originatingParticipant);
        mLeftParticipant = rcsParticipant;
    }

    /**
     * @return Returns the {@link RcsParticipant} that left the group.
     */
    public RcsParticipant getLeftParticipant() {
        return mLeftParticipant;
    }

    /**
     * Use this builder to create an instance of {@link RcsGroupThreadParticipantLeftEvent}. The
     * event object will be persisted into storage after it is built. {@link RcsEvent}s are
     * immutable and can only be deleted after they are created.
     */
    public static class Builder extends RcsGroupThreadEvent.Builder<Builder> {
        private RcsParticipant mBuilderLeftRcsParticipant;

        /**
         * Create a new Builder for {@link RcsGroupThreadParticipantLeftEvent}
         *
         * @param rcsGroupThread         The {@link RcsGroupThread} that had a participant leave
         * @param originatingParticipant The {@link RcsParticipant} that removed the left
         *                               participant
         */
        public Builder(@NonNull RcsGroupThread rcsGroupThread,
                RcsParticipant originatingParticipant) {
            super(rcsGroupThread, originatingParticipant);
        }

        /**
         * Sets the participant that left this {@link RcsGroupThread}
         * @param rcsParticipant The participant that left the group
         * @return The same instance of {@link Builder} to chain methods
         */
        public Builder setParticipant(RcsParticipant rcsParticipant) {
            mBuilderLeftRcsParticipant = rcsParticipant;
            return this;
        }

        /**
         * Creates an instance of {@link RcsGroupThreadParticipantLeftEvent} and saves into storage.
         * @return A new instance of {@link RcsGroupThreadParticipantLeftEvent}
         */
        @Override
        @WorkerThread
        public RcsGroupThreadParticipantLeftEvent buildAndSave() {
            if (mBuilderLeftRcsParticipant == null) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadParticipantLeftEvent.Builder: Can't remove null "
                                + "RcsParticipant from an RcsGroupThread");
                return null;
            }
            try {
                IRcs iRcs = IRcs.Stub.asInterface(ServiceManager.getService("ircs"));
                if (iRcs != null) {
                    int participantId = mOriginatingParticipant == null ? 0
                            : mOriginatingParticipant.getId();
                    int id = iRcs.createGroupThreadParticipantLeftEvent(mBuilderTimestamp,
                            mBuilderRcsGroupThread.getThreadId(),
                            participantId, mBuilderLeftRcsParticipant.getId());
                    return new RcsGroupThreadParticipantLeftEvent(id, mBuilderTimestamp,
                            mBuilderRcsGroupThread, mOriginatingParticipant,
                            mBuilderLeftRcsParticipant);
                }
            } catch (RemoteException re) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadParticipantLeftEvent.Builder: Exception happened during "
                                + "buildAndSave",
                        re);
            }
            return null;
        }

        /**
         * @hide
         */
        @VisibleForTesting
        public RcsGroupThreadParticipantLeftEvent buildForTest() {
            return new RcsGroupThreadParticipantLeftEvent(0, mBuilderTimestamp,
                    mBuilderRcsGroupThread, mOriginatingParticipant, mBuilderLeftRcsParticipant);
        }

        /**
         * @hide
         */
        @Override
        Builder self() {
            return this;
        }
    }

    public static final Creator<RcsGroupThreadParticipantLeftEvent> CREATOR =
            new Creator<RcsGroupThreadParticipantLeftEvent>() {
                @Override
                public RcsGroupThreadParticipantLeftEvent createFromParcel(Parcel in) {
                    return new RcsGroupThreadParticipantLeftEvent(in);
                }

                @Override
                public RcsGroupThreadParticipantLeftEvent[] newArray(int size) {
                    return new RcsGroupThreadParticipantLeftEvent[size];
                }
            };

    protected RcsGroupThreadParticipantLeftEvent(Parcel in) {
        super(in);
        mLeftParticipant = in.readParcelable(RcsParticipant.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mLeftParticipant, flags);
    }
}
