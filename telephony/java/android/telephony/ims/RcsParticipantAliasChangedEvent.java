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
 * An event that indicates an {@link RcsParticipant}'s alias was changed.
 *
 * @hide - TODO(109759350) make this public
 */
public class RcsParticipantAliasChangedEvent extends RcsEvent {
    private RcsParticipant mParticipant;
    private String mOldAlias;
    private String mNewAlias;

    RcsParticipantAliasChangedEvent(int id, long timestamp, RcsParticipant participant,
            String oldAlias, String newAlias) {
        super(id, timestamp);
        mParticipant = participant;
        mOldAlias = oldAlias;
        mNewAlias = newAlias;
    }

    /**
     * @return Returns the {@link RcsParticipant} that had their alias changed.
     */
    public RcsParticipant getParticipant() {
        return mParticipant;
    }

    /**
     * @return Returns the alias that the {@link RcsParticipant} had before this event occured.
     */
    public String getOldAlias() {
        return mOldAlias;
    }

    /**
     * @return Returns the alias that the {@link RcsParticipant} has after this event occured.
     */
    public String getNewAlias() {
        return mNewAlias;
    }

    /**
     * Use this builder to create an instance of {@link RcsParticipantAliasChangedEvent}. The event
     * object will be persisted into storage after it is built. {@link RcsEvent}s are immutable and
     * can only be deleted after they are created.
     */
    public static class Builder extends RcsEvent.Builder<Builder> {
        private RcsParticipant mBuilderParticipant;
        private String mBuilderOldAlias;
        private String mBuilderNewAlias;

        /**
         * Create a new Builder for {@link RcsParticipantAliasChangedEvent}
         * @param participant The participant that had their alias changed.
         */
        public Builder(@NonNull RcsParticipant participant) {
            mBuilderParticipant = participant;
            mBuilderOldAlias = participant.getAlias();
        }

        /**
         * Sets the new alias of the {@link RcsParticipant} that this event is associated to
         * @param alias The new alias the participant has
         * @return The same instance of {@link Builder} to chain methods.
         */
        public Builder setNewAlias(String alias) {
            mBuilderNewAlias = alias;
            return this;
        }

        /**
         * Creates an instance of {@link RcsParticipantAliasChangedEvent} and saves into storage.
         * @return A new instance of {@link RcsParticipantAliasChangedEvent}
         */
        @Override
        @WorkerThread
        public RcsParticipantAliasChangedEvent buildAndSave() {
            try {
                IRcs iRcs = IRcs.Stub.asInterface(ServiceManager.getService("ircs"));
                if (iRcs != null) {
                    int id = iRcs.createParticipantAliasChangedEvent(mBuilderTimestamp,
                            mBuilderParticipant.getId(), mBuilderOldAlias, mBuilderNewAlias);
                    return new RcsParticipantAliasChangedEvent(0, mBuilderTimestamp,
                            mBuilderParticipant, mBuilderOldAlias, mBuilderNewAlias);
                }
            } catch (RemoteException re) {
                Log.e(RcsMessageStore.TAG,
                        "RcsParticipantAliasChangedEvent.Builder: Exception happened during "
                                + "buildAndSave",
                        re);
            }
            return null;
        }

        /**
         * @hide
         */
        @VisibleForTesting
        public RcsParticipantAliasChangedEvent buildForTest() {
            return new RcsParticipantAliasChangedEvent(0, mBuilderTimestamp, mBuilderParticipant,
                    mBuilderOldAlias, mBuilderNewAlias);
        }

        /**
         * @hide
         */
        @Override
        Builder self() {
            return this;
        }
    }

    public static final Creator<RcsParticipantAliasChangedEvent> CREATOR =
            new Creator<RcsParticipantAliasChangedEvent>() {
                @Override
                public RcsParticipantAliasChangedEvent createFromParcel(Parcel in) {
                    return new RcsParticipantAliasChangedEvent(in);
                }

                @Override
                public RcsParticipantAliasChangedEvent[] newArray(int size) {
                    return new RcsParticipantAliasChangedEvent[size];
                }
            };

    protected RcsParticipantAliasChangedEvent(Parcel in) {
        super(in);
        mOldAlias = in.readString();
        mNewAlias = in.readString();
        mParticipant = in.readParcelable(RcsParticipant.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mOldAlias);
        dest.writeString(mNewAlias);
        dest.writeParcelable(mParticipant, flags);
    }
}
