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

import android.os.Parcel;

/**
 * An event that happened on an {@link RcsThread}.
 *
 * @hide - TODO(109759350) make this public
 */
public abstract class RcsGroupThreadEvent extends RcsEvent {
    private final RcsGroupThread mRcsGroupThread;
    private final RcsParticipant mOriginatingParticipant;

    RcsGroupThreadEvent(int id, long timestamp, RcsGroupThread rcsGroupThread,
            RcsParticipant originatingParticipant) {
        super(id, timestamp);
        mRcsGroupThread = rcsGroupThread;
        mOriginatingParticipant = originatingParticipant;
    }

    public RcsGroupThread getRcsGroupThread() {
        return mRcsGroupThread;
    }

    public RcsParticipant getOriginatingParticipant() {
        return mOriginatingParticipant;
    }

    abstract static class Builder<B extends Builder<B>> extends RcsEvent.Builder<B> {
        RcsGroupThread mBuilderRcsGroupThread;
        RcsParticipant mOriginatingParticipant;

        Builder(RcsGroupThread rcsGroupThread, RcsParticipant originatingParticipant) {
            mBuilderRcsGroupThread = rcsGroupThread;
            mOriginatingParticipant = originatingParticipant;
        }
    }

    RcsGroupThreadEvent(Parcel in) {
        super(in);
        mRcsGroupThread = in.readParcelable(RcsGroupThread.class.getClassLoader());
        mOriginatingParticipant = in.readParcelable(RcsParticipant.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mRcsGroupThread, flags);
        dest.writeParcelable(mOriginatingParticipant, flags);
    }
}
