/*
 * Copyright (C) 2019 The Android Open Source Project
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

package android.os;

import static com.android.internal.util.Preconditions.checkNotNull;

import android.annotation.SystemService;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Slog;
import android.view.View;

import android.content.res.Resources;
import android.util.MutableBoolean;
import android.view.KeyEvent;

//import com.android.internal.os.IGestureLauncher;
import android.os.IGestureLauncher;

// Ultimate goal: Keep a GestureLauncher running and provide an interface to
// access its internal variables
// GestureLauncher should register Content Observers and Boot Receivers
// The interface to GestureLauncher should be accessible from PhoneFallbackEventHandler

/**
 * Allow accessing gesture-based features
 *
 * @hide
 */
@SystemService(Context.GESTURE_LAUNCHER_SERVICE)
public class GestureLauncherManager {

    private Context mContext;
    private IGestureLauncher mService;
    private IBinder mToken = new Binder();

    private int mUserId;

    /** @hide */
    public GestureLauncherManager(IGestureLauncher service) {
        mService = checkNotNull(service, "missing IGestureLauncher");
    }

    /*
    private GestureLauncherManager() {
        // Constructors here
    }
    */


    public GestureLauncherManager(Context context) {
        mContext = context;
    }

    private synchronized IGestureLauncher getService() {
        if (mService == null) {
            mService = IGestureLauncher.Stub.asInterface(
                    ServiceManager.getService(Context.GESTURE_LAUNCHER_SERVICE));
            if (mService == null) {
                Slog.w("GestureLauncherManager", "warning: no GESTURE_LAUNCHER_SERVICE");
            }
        }
        return mService;
    }

    // TODO: Expose more public functions. Initially, we only need those exposing camera key features.
    // Further along, we want the registration of Motion sensors etc. to happen
    // here instead of in GestureLauncherService.java

    //public static boolean isGestureLauncherEnabled(Resources resources) {
    /*
    public static boolean isGestureLauncherEnabled(Resources resources) {
        try {
            final IGestureLauncher svc = getService();
            if (svc != null) {
                return svc.isGestureLauncherEnabled(resources);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    */

    /*
    public static boolean isGestureLauncherEnabled() {
        try {
            final IGestureLauncher svc = getService();
            if (svc != null) {
                return svc.isGestureLauncherEnabled(mContext.getResources());
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    */

    public boolean isCameraButtonLaunchSettingEnabled() {
        try {
            final IGestureLauncher svc = getService();
            if (svc != null) {
                return svc.isCameraButtonLaunchSettingEnabled();
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
        return false;
    }

    /*
    public boolean interceptPowerKeyDown(KeyEvent event, boolean interactive,
            MutableBoolean outLaunched) {
        try {
            final IGestureLauncher svc = getService();
            if (svc != null) {
                return svc.interceptPowerKeyDown(event, interactive, outLaunched);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
    */

}
