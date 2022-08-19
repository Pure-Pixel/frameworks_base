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

import android.media.audio.common.AudioHalAttributesGroup;

/**
 * AudioHalProductStrategy is a grouping of AudioHalAttributesGroups that will
 * share device behavior (i.e. device routing, muting, etc).
 */
@JavaDerive(equals=true, toString=true)
@VintfStability
parcelable AudioHalProductStrategy {
    /**
    * The following string constants are strategy names used for the default
    * Audio Policy Engine.
    */
    const @utf8InCpp String NONE = "STRATEGY_NONE";
    const @utf8InCpp String MEDIA = "STRATEGY_MEDIA";
    const @utf8InCpp String PHONE = "STRATEGY_PHONE";
    const @utf8InCpp String SONIFICATION = "STRATEGY_SONIFICATION";
    const @utf8InCpp String SONIFICATION_RESPECTFUL = "STRATEGY_SONIFICATION_RESPECTFUL";
    const @utf8InCpp String DTMF = "STRATEGY_DTMF";
    const @utf8InCpp String ENFORCED_AUDIBLE = "STRATEGY_ENFORCED_AUDIBLE";
    const @utf8InCpp String TRANSMITTED_THROUGH_SPEAKER = "STRATEGY_TRANSMITTED_THROUGH_SPEAKER";
    const @utf8InCpp String ACCESSIBILITY = "STRATEGY_ACCESSIBILITY";
    const @utf8InCpp String REROUTING = "STRATEGY_REROUTING";
    const @utf8InCpp String PATCH = "STRATEGY_PATCH";
    const @utf8InCpp String CALL_ASSISTANT = "STRATEGY_CALL_ASSISTANT";

    /**
     * Name is used to identify the behavior that corresponds to this strategy.
     * For the default Audio Policy Engine, one of the provided constants should
     * be used. For the Configurable Audio Policy (CAP) Engine, it is not
     * required to use one of these constants, as that system defines its own
     * behavior for the strategy.
     */
    @utf8InCpp String name;
    /**
     * This is the list of use cases that follow the same routing strategy.
     */
    AudioHalAttributesGroup[] attributesGroups;
}