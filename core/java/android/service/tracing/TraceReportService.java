/*
 * Copyright (C) 2021 The Android Open Source Project
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

package android.service.tracing;

import static android.annotation.SystemApi.Client.PRIVILEGED_APPS;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.tracing.TraceReportParams;
import android.util.Log;

import java.io.IOException;

/** @hide */
@SystemApi(client = PRIVILEGED_APPS)
public class TraceReportService extends Service {
    private static final String TAG = "TraceReportService";
    private Messenger mMessenger = null;

    /** @hide */
    public static final int MSG_REPORT_TRACE = 1;

    /** @hide */
    @SystemApi(client = PRIVILEGED_APPS)
    public static final class TraceParams {
        private final ParcelFileDescriptor mFd;
        private final long mUuidLsb;
        private final long mUuidMsb;

        /** @hide */
        public TraceParams(TraceReportParams params) {
            mFd = params.fd;
            mUuidLsb = params.uuidLsb;
            mUuidMsb = params.uuidMsb;
        }

        @NonNull
        public ParcelFileDescriptor getFd() {
            return mFd;
        }

        public long getUuidLsb() {
            return mUuidLsb;
        }

        public long getUuidMsb() {
            return mUuidMsb;
        }
    }

    // Methods to override.
    /**
     * Called when a trace is reported and sent to this class.
     */
    public void onReportTrace(@NonNull TraceParams args) {
    }

    /**
     * Called immediately after |onReportTrace| with the same arguments.
     *
     * NOTE: the default implementation of this method closes the fd provided
     * in |args| to avoid leaking it. If this FD is stored for further use, this
     * method should be overridden and a no-op implementation should be provided.
     */
    public void onPostReportTrace(@NonNull TraceParams args) {
        try {
            args.getFd().close();
        } catch (IOException ignored) {
        }
    }

    // Optional methods to override.
    // Realistically, these methods are internal implementation details but since this class is
    // a SystemApi, it's better to err on the side of flexibility just in-case we need to override
    // these methods down the line.
    public boolean onMessage(@NonNull Message msg) {
        if (msg.what == MSG_REPORT_TRACE) {
            if (msg.sendingUid != Process.SYSTEM_UID) {
                Log.e(TAG, "Invalid UID " + msg.sendingUid + " for report trace message.");
                return false;
            }
            if (!(msg.obj instanceof TraceReportParams)) {
                Log.e(TAG, "Received invalid type for report trace message.");
                return false;
            }
            TraceParams params = new TraceParams((TraceReportParams) msg.obj);
            onReportTrace(params);
            onPostReportTrace(params);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        if (mMessenger == null) {
            mMessenger = new Messenger(new Handler(Looper.getMainLooper(), this::onMessage));
        }
        return mMessenger.getBinder();
    }
}