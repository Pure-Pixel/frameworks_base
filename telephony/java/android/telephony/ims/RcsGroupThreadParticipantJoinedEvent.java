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
 * An event that indicates an RCS participant has joined an {@link RcsThread}.
 *
 * @hide - TODO(109759350) make this public
 */
public class RcsGroupThreadParticipantJoinedEvent extends RcsGroupThreadEvent {
    private RcsParticipant mJoinedParticipant;

    private RcsGroupThreadParticipantJoinedEvent(int id, long timestamp,
            RcsGroupThread rcsGroupThread, RcsParticipant originatingParticipant,
            RcsParticipant rcsParticipant) {
        super(id, timestamp, rcsGroupThread, originatingParticipant);
        mJoinedParticipant = rcsParticipant;
    }

    /**
     * @return Returns the {@link RcsParticipant} that joined the {@link RcsGroupThread} after this
     * event.
     */
    public RcsParticipant getJoinedParticipant() {
        return mJoinedParticipant;
    }

    /**
     * Use this builder to create an instance of {@link RcsGroupThreadParticipantJoinedEvent}. The
     * event object will be persisted into storage after it is built. {@link RcsEvent}s are
     * immutable and can only be deleted after they are created.
     */
    public static class Builder extends RcsGroupThreadEvent.Builder<Builder> {
        private RcsParticipant mBuilderJoinedRcsParticipant;

        /**
         * Create a Builder for {@link RcsGroupThreadParticipantJoinedEvent}
         *
         * @param rcsGroupThread         The {@link RcsGroupThread} that had a new participant join
         * @param originatingParticipant The {@link RcsParticipant} that added a new participant to
         *                               the {@link RcsGroupThread}
         */
        public Builder(@NonNull RcsGroupThread rcsGroupThread,
                RcsParticipant originatingParticipant) {
            super(rcsGroupThread, originatingParticipant);
        }

        /**
         * Sets the participant that joined this group thread.
         * @param rcsParticipant
         * @return The same instance of {@link Builder} to chain methods.
         */
        public Builder setParticipant(RcsParticipant rcsParticipant) {
            mBuilderJoinedRcsParticipant = rcsParticipant;
            return self();
        }

        /**
         * Creates an instance of {@link RcsGroupThreadParticipantJoinedEvent} and saves into
         * storage.
         * @return A new instance of {@link RcsGroupThreadParticipantJoinedEvent}
         */
        @Override
        @WorkerThread
        public RcsGroupThreadParticipantJoinedEvent buildAndSave() {
            if (mBuilderJoinedRcsParticipant == null) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadParticipantJoinedEvent.Builder: Can't add null "
                                + "RcsParticipant to an RcsGroupThread");
                return null;
            }

            try {
                IRcs iRcs = IRcs.Stub.asInterface(ServiceManager.getService("ircs"));
                if (iRcs != null) {
                    int originatingParticipantId = mOriginatingParticipant == null ? 0
                            : mOriginatingParticipant.getId();
                    int id = iRcs.createGroupThreadParticipantJoinedEvent(mBuilderTimestamp,
                            mBuilderRcsGroupThread.getThreadId(),
                            originatingParticipantId, mBuilderJoinedRcsParticipant.getId());
                    return new RcsGroupThreadParticipantJoinedEvent(id, mBuilderTimestamp,
                            mBuilderRcsGroupThread, mOriginatingParticipant,
                            mBuilderJoinedRcsParticipant);
                }
            } catch (RemoteException re) {
                Log.e(RcsMessageStore.TAG,
                        "RcsGroupThreadParticipantJoinedEvent.Builder: Exception happened during "
                                + "buildAndSave",
                        re);
            }
            return null;
        }

        /**
         * @hide
         */
        @VisibleForTesting
        public RcsGroupThreadParticipantJoinedEvent buildForTest() {
            return new RcsGroupThreadParticipantJoinedEvent(0, mBuilderTimestamp,
                    mBuilderRcsGroupThread, mOriginatingParticipant, mBuilderJoinedRcsParticipant);
        }

        /**
         * @hide
         */
        @Override
        Builder self() {
            return this;
        }
    }

    public static final Creator<RcsGroupThreadParticipantJoinedEvent> CREATOR =
            new Creator<RcsGroupThreadParticipantJoinedEvent>() {
                @Override
                public RcsGroupThreadParticipantJoinedEvent createFromParcel(Parcel in) {
                    return new RcsGroupThreadParticipantJoinedEvent(in);
                }

                @Override
                public RcsGroupThreadParticipantJoinedEvent[] newArray(int size) {
                    return new RcsGroupThreadParticipantJoinedEvent[size];
                }
            };

    protected RcsGroupThreadParticipantJoinedEvent(Parcel in) {
        super(in);
        mJoinedParticipant = in.readParcelable(RcsParticipant.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mJoinedParticipant, flags);
    }
}
