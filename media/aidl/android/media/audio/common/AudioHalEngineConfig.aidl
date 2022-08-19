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

import android.media.audio.common.AudioHalProductStrategy;
import android.media.audio.common.AudioHalCapCriterion;
import android.media.audio.common.AudioHalCapCriterionType;
import android.media.audio.common.AudioHalVolumeGroup;

/**
 * AudioHalEngineConfig defines the configuration items that are used upon
 * initial engine loading.
 */
@JavaDerive(equals=true, toString=true)
@VintfStability
parcelable AudioHalEngineConfig {
    /**
     * Determines which engine library to load (i.e. whether to load the default
     * audio policy engine implementation or the configurable audio policy (CAP)
     * engine implementation).
     */
    boolean isEngineConfigurable = false;
    /**
     * A non-empty list of product strategies is mandatory for the configurable
     * audio policy (CAP) engine. If no product strategies are provided
     * when using the default audio policy engine, however, the engine shall use
     * the default product strategies.
     */
    AudioHalProductStrategy[] productStrategies;
    /**
     * A non-empty list of volume groups is mandatory for the configurable audio
     * policy (CAP) engine. If no volume groups are provided when using the
     * default audio policy engine, however, the engine shall provide the
     * required volume groups populated with default values.
     */
    AudioHalVolumeGroup[] volumeGroups;
    @VintfStability
    parcelable CapConfig {
        AudioHalCapCriterion[] criteria;
        AudioHalCapCriterionType[] criterionTypes;
    }
    /**
     * Specifies the criteria that are specific to the Configurable Audio Policy
     * (CAP) engine. The default audio policy engine will ignore this value if
     * isEngineConfigurable is false, so it's suggested that this be null for
     * the default engine.
     */
    @nullable CapConfig capConfig;
}