/*
 * Copyright (C) 2022 The Android Open Source Project
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

package android.media.audio.common;

/**
 * AudioHalVolumeCurve defines a set of curve points mapping a value from [0,100]
 * to an attenuation in millibels, and associates them with a device category.
 */
@JavaDerive(equals=true, toString=true)
@VintfStability
parcelable AudioHalVolumeCurve {
    /**
     * Valid device_category strings are one of the following:
     * {"DEVICE_CATEGORY_HEADSET", "DEVICE_CATEGORY_SPEAKER",
     *  "DEVICE_CATEGORY_EARPIECE", "DEVICE_CATEGORY_EXT_MEDIA",
     *  "DEVICE_CATEGORY_HEARING_AID"}.
     *  Engine will default to "DEFAULT_CATEGORY_SPEAKER" if no valid
     *  deviceCategory string is given.
     */
    @utf8InCpp String deviceCategory = "DEVICE_CATEGORY_SPEAKER";
    @VintfStability
    parcelable CurvePoint {
       /**
        * must be a value in the range [0, 100]
        */
        byte index;
        /**
         * attenuation in millibels
         */
        int attenuationMb;
    }
    /**
     * Each curve point maps a value in the range [0,100] -> an attenuation in
     * millibels. This is used to create a volume curve using linear
     * interpolation.
     */
    CurvePoint[] curvePoints;
}