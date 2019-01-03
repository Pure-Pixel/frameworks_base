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
import android.os.Parcelable;
import android.util.Log;

/**
 * The base class for events that can happen on {@link RcsParticipant}s and {@link RcsThread}s.
 * @hide - TODO(109759350) make this public
 */
public abstract class RcsEvent implements Parcelable {
    static final int RCS_PARTICIPANT_ALIAS_CHANGED_EVENT = 2001;

    protected int mId;
    protected long mTimestamp;

    RcsEvent(int id, long timestamp) {
        mId = id;
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    abstract static class Builder<B extends Builder<B>> {
        protected long mBuilderTimestamp;

        public B setTimestamp(long timestamp) {
            mBuilderTimestamp = timestamp;
            return self();
        }

        abstract B self();

        abstract RcsEvent buildAndSave();
    }

    RcsEvent(Parcel in) {
        Log.e("###TEST", "Reading RcsEvent from parcel");
        mId = in.readInt();
        Log.e("###TEST", "Read mId: " + mId);
        mTimestamp = in.readLong();
        Log.e("###TEST", "Read mTimestamp: " + mTimestamp);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.e("###TEST", "Writing RcsEvent to parcel");
        dest.writeInt(mId);
        Log.e("###TEST", "Wrote mId: " + mId);
        dest.writeLong(mTimestamp);
        Log.e("###TEST", "Wrote mTimestamp: " + mTimestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
