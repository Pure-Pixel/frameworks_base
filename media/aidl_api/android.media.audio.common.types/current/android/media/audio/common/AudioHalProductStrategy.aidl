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
///////////////////////////////////////////////////////////////////////////////
// THIS FILE IS IMMUTABLE. DO NOT EDIT IN ANY CASE.                          //
///////////////////////////////////////////////////////////////////////////////

// This file is a snapshot of an AIDL file. Do not edit it manually. There are
// two cases:
// 1). this is a frozen version file - do not edit this in any case.
// 2). this is a 'current' file. If you make a backwards compatible change to
//     the interface (from the latest frozen version), the build system will
//     prompt you to update this file with `m <name>-update-api`.
//
// You must not make a backward incompatible change to any AIDL file built
// with the aidl_interface module type with versions property set. The module
// type is used to build AIDL files in a way that they can be used across
// independently updatable components of the system. If a device is shipped
// with such a backward incompatible change, it has a high risk of breaking
// later when a module using the interface is updated, e.g., Mainline modules.

package android.media.audio.common;
@JavaDerive(equals=true, toString=true) @VintfStability
parcelable AudioHalProductStrategy {
  @utf8InCpp String name;
  android.media.audio.common.AudioHalAttributesGroup[] attributesGroups;
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
}
