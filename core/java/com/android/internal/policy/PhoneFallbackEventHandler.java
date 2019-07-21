/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.internal.policy;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.SearchManager;
import android.app.StatusBarManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.FallbackEventHandler;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import com.android.internal.policy.PhoneWindow;

/**
 * @hide
 */
public class PhoneFallbackEventHandler implements FallbackEventHandler {
    private static String TAG = "PhoneFallbackEventHandler";
    private static final boolean DEBUG = false;

    @UnsupportedAppUsage
    Context mContext;
    @UnsupportedAppUsage
    View mView;

    AudioManager mAudioManager;
    KeyguardManager mKeyguardManager;
    SearchManager mSearchManager;
    TelephonyManager mTelephonyManager;
    MediaSessionManager mMediaSessionManager;
    StatusBarManager mStatusBarManager;
    //Handler mHandler;

    private int mUserId;

    // Whether to launch the camera application on camera button long-press
    private boolean mCameraButtonLaunchEnabled;

    /* Extra intents for launching camera by gesture
     * Keep in sync with:
     * packages/SystemUI/src/com/android/systemui/statusbar/phone/KeyguardBottomAreaView.java */
    public static final String EXTRA_CAMERA_LAUNCH_SOURCE
            = "com.android.systemui.camera_launch_source";
    public static final String CAMERA_LAUNCH_SOURCE_CAMERA_BUTTON = "camera_button";

    @UnsupportedAppUsage
    public PhoneFallbackEventHandler(Context context) {
        mContext = context;
    }

    public void setView(View v) {
        updateCameraLongPressEnabled();
        //mUserId = ActivityManager.getCurrentUser();
        // TODO: Don't re-register receivers
        try {
            mContext.getContentResolver().unregisterContentObserver(mSettingObserver);
            mContext.unregisterReceiver(mUserReceiver);
        } catch (Exception e) {
            // Catch me
        }
        mContext.registerReceiver(mUserReceiver, new IntentFilter(Intent.ACTION_USER_SWITCHED));
        registerContentObservers();
        mView = v;
    }

    public void preDispatchKeyEvent(KeyEvent event) {
        getAudioManager().preDispatchKeyEvent(event, AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        final int action = event.getAction();
        final int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_DOWN) {
            return onKeyDown(keyCode, event);
        } else {
            return onKeyUp(keyCode, event);
        }
    }

    @UnsupportedAppUsage
    boolean onKeyDown(int keyCode, KeyEvent event) {
        /* ****************************************************************************
         * HOW TO DECIDE WHERE YOUR KEY HANDLING GOES.
         * See the comment in PhoneWindow.onKeyDown
         * ****************************************************************************/
        final KeyEvent.DispatcherState dispatcher = mView.getKeyDispatcherState();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE: {
                handleVolumeKeyEvent(event);
                return true;
            }


            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                /* Suppress PLAY/PAUSE toggle when phone is ringing or in-call
                 * to avoid music playback */
                if (getTelephonyManager().getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                    return true;  // suppress key event
                }
            case KeyEvent.KEYCODE_MUTE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK: {
                handleMediaKeyEvent(event);
                return true;
            }

            case KeyEvent.KEYCODE_CALL: {
                if (isNotInstantAppAndKeyguardRestricted(dispatcher)) {
                    break;
                }
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    dispatcher.performedLongPress(event);
                    if (isUserSetupComplete()) {
                        mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        // launch the VoiceDialer
                        Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            sendCloseSystemWindows();
                            mContext.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            startCallActivity();
                        }
                    } else {
                        Log.i(TAG, "Not starting call activity because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }

            case KeyEvent.KEYCODE_CAMERA: {
                // TODO: Why were instant apps exempted? Security? Instant apps
                // can't capture this button anyway, no?
                // In either case, we don't need to check for keyguard here
                // since it's handled in cameraLongPress()
                /*
                if (isNotInstantAppAndKeyguardRestricted(dispatcher)) {
                    break;
                }
                */
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    dispatcher.performedLongPress(event);
                    if (isUserSetupComplete()) {
                        updateCameraLongPressEnabled();
                        if (mCameraButtonLaunchEnabled) {
                            mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            cameraLongPress(dispatcher);
                        } else {
                            // TODO(@Sony): Get rid of this block entirely?
                            // TODO: Don't trigger haptic feedback twice
                            // StatusBar.java:vibrateForCameraGesture already does the job
                            mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            sendCloseSystemWindows();
                            // Broadcast an intent that the Camera button was longpressed
                            /*
                            Intent intent = new Intent(Intent.ACTION_CAMERA_BUTTON, null);
                            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                            intent.putExtra(Intent.EXTRA_KEY_EVENT, event);
                            mContext.sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT_OR_SELF,
                                    null, null, null, 0, null, null);
                            */
                        }
                    } else {
                        Log.i(TAG, "Not dispatching CAMERA long press because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }

            case KeyEvent.KEYCODE_SEARCH: {
                if (isNotInstantAppAndKeyguardRestricted(dispatcher)) {
                    break;
                }
                if (event.getRepeatCount() == 0) {
                    dispatcher.startTracking(event, this);
                } else if (event.isLongPress() && dispatcher.isTracking(event)) {
                    Configuration config = mContext.getResources().getConfiguration();
                    if (config.keyboard == Configuration.KEYBOARD_NOKEYS
                            || config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
                        if (isUserSetupComplete()) {
                            // launch the search activity
                            Intent intent = new Intent(Intent.ACTION_SEARCH_LONG_PRESS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            try {
                                mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                                sendCloseSystemWindows();
                                getSearchManager().stopSearch();
                                mContext.startActivity(intent);
                                // Only clear this if we successfully start the
                                // activity; otherwise we will allow the normal short
                                // press action to be performed.
                                dispatcher.performedLongPress(event);
                                return true;
                            } catch (ActivityNotFoundException e) {
                                // Ignore
                            }
                        } else {
                            Log.i(TAG, "Not dispatching SEARCH long press because user "
                                    + "setup is in progress.");
                        }
                    }
                }
                break;
            }
        }
        return false;
    }

    private boolean isNotInstantAppAndKeyguardRestricted(KeyEvent.DispatcherState dispatcher) {
        return !mContext.getPackageManager().isInstantApp()
                && (getKeyguardManager().inKeyguardRestrictedInputMode() || dispatcher == null);
    }

    @UnsupportedAppUsage
    boolean onKeyUp(int keyCode, KeyEvent event) {
        if (DEBUG) {
            Log.d(TAG, "up " + keyCode);
        }
        final KeyEvent.DispatcherState dispatcher = mView.getKeyDispatcherState();
        if (dispatcher != null) {
            dispatcher.handleUpEvent(event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE: {
                if (!event.isCanceled()) {
                    handleVolumeKeyEvent(event);
                }
                return true;
            }

            case KeyEvent.KEYCODE_HEADSETHOOK:
            case KeyEvent.KEYCODE_MUTE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK: {
                handleMediaKeyEvent(event);
                return true;
            }

            case KeyEvent.KEYCODE_CAMERA: {
                if (isNotInstantAppAndKeyguardRestricted(dispatcher)) {
                    break;
                }
                // TODO: Cancel event before onKeyUp when camera is launched
                if (event.isTracking() && !event.isCanceled()) {
                    // Add short press behavior here if desired
                }
                return true;
            }

            case KeyEvent.KEYCODE_CALL: {
                if (isNotInstantAppAndKeyguardRestricted(dispatcher)) {
                    break;
                }
                if (event.isTracking() && !event.isCanceled()) {
                    if (isUserSetupComplete()) {
                        startCallActivity();
                    } else {
                        Log.i(TAG, "Not starting call activity because user "
                                + "setup is in progress.");
                    }
                }
                return true;
            }
        }
        return false;
    }

    @UnsupportedAppUsage
    void startCallActivity() {
        sendCloseSystemWindows();
        Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "No activity found for android.intent.action.CALL_BUTTON.");
        }
    }

    SearchManager getSearchManager() {
        if (mSearchManager == null) {
            mSearchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        }
        return mSearchManager;
    }

    TelephonyManager getTelephonyManager() {
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager)mContext.getSystemService(
                    Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }

    KeyguardManager getKeyguardManager() {
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager)mContext.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return mKeyguardManager;
    }

    AudioManager getAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManager;
    }

    MediaSessionManager getMediaSessionManager() {
        if (mMediaSessionManager == null) {
            mMediaSessionManager =
                    (MediaSessionManager) mContext.getSystemService(Context.MEDIA_SESSION_SERVICE);
        }
        return mMediaSessionManager;
    }

    StatusBarManager getStatusBarManager() {
        if (mStatusBarManager == null) {
            mStatusBarManager = (StatusBarManager) mContext.getSystemService(
                    android.app.Service.STATUS_BAR_SERVICE);
        }
        return mStatusBarManager;
    }

    void sendCloseSystemWindows() {
        PhoneWindow.sendCloseSystemWindows(mContext, null);
    }

    private void handleVolumeKeyEvent(KeyEvent keyEvent) {
        getMediaSessionManager().dispatchVolumeKeyEventAsSystemService(keyEvent,
                AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        getMediaSessionManager().dispatchMediaKeyEventAsSystemService(keyEvent);
    }

    private void cameraLongPress(KeyEvent.DispatcherState dispatcher) {

        final boolean keyguardActive = (getKeyguardManager().inKeyguardRestrictedInputMode()
                || dispatcher == null);
        if (!keyguardActive) {
            // Abort possibly stuck animations.
            // TODO: Need to steal from PhoneWindowManager because we don't get
            // the windowManagerFuncs that its init() gets
            //mHandler.post(mWindowManagerFuncs::triggerAnimationFailsafe);
            // TODO: "Propose" new camera intent only for camera button
            // Maybe make the intent generic("gesture_*") and let camera
            // application decide based on extra intent
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            intent.putExtra(EXTRA_CAMERA_LAUNCH_SOURCE, CAMERA_LAUNCH_SOURCE_CAMERA_BUTTON);
            try {
                mContext.startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, "No activity found for MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA");
            }
        } else {
            // Use the StatusBarManager interface to handle keyguard and intent resolving
            getStatusBarManager().onCameraLaunchGestureDetected(
                    StatusBarManager.CAMERA_LAUNCH_SOURCE_CAMERA_BUTTON);
        }
    }

    // TODO: Register content observer elsewhere and register message receiver here?
    // For now, just register the stuff on setView()
    /*
    public void onBootPhase(int phase) {
        if (phase == PHASE_THIRD_PARTY_APPS_CAN_START) {
            Resources resources = mContext.getResources();
            updateCameraLongPressEnabled();
            mUserId = ActivityManager.getCurrentUser();
            mContext.registerReceiver(mUserReceiver, new IntentFilter(Intent.ACTION_USER_SWITCHED));
            registerContentObservers();
        }
    }
    */

    private final BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_USER_SWITCHED.equals(intent.getAction())) {
                mUserId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, 0);
                mContext.getContentResolver().unregisterContentObserver(mSettingObserver);
                registerContentObservers();
                updateCameraLongPressEnabled();
            }
        }
    };

    private final ContentObserver mSettingObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange, android.net.Uri uri, int userId) {
            if (userId == mUserId) {
                updateCameraLongPressEnabled();
            }
        }
    };

    private void registerContentObservers() {
        mContext.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.CAMERA_LONG_PRESS_GESTURE_DISABLED),
                false, mSettingObserver, mUserId);
    }

    void updateCameraLongPressEnabled() {
        // TODO: Make mCameraButtonLaunchEnabled part of some kind of interface
        // that PhoneFallbackEventHandler can query That way we avoid setting
        // up observers
        boolean enabled = (mContext.getResources().getBoolean(
                    com.android.internal.R.bool.config_cameraButtonLaunchEnabled)
                    && (Settings.Secure.getIntForUser(mContext.getContentResolver(),
                        Settings.Secure.CAMERA_LONG_PRESS_GESTURE_DISABLED, 0, mUserId) == 0));
        // TODO: mUserId?
        Log.w(TAG, "mCameraButtonLaunchEnabled: " + enabled);
        synchronized (this) {
            mCameraButtonLaunchEnabled = enabled;
        }
    }

    private boolean isUserSetupComplete() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                Settings.Secure.USER_SETUP_COMPLETE, 0) != 0;
    }
}

