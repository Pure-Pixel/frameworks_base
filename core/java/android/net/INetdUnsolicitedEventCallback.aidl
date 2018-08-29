/*
 * Copyright (C) 2016 The Android Open Source Project
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

package android.net;

/** {@hide} */
oneway interface INetdUnsolicitedEventCallback {

    // Possible addNetdEventCallback callers.
    const int CALLBACK_CALLER_NETWORKMANAGEMENT_SERVICE = 0;

    /**
     * Represents that interfaces have been idle for a certain period of time.
     *
     * @param isActive true for active status, false for idle.
     * @param name unique identifier of the idletimer.
     * @param timestamp timestamp of this event, 0 for no timestamp.
     * @param uid uid of this event, -1 for no uid.
     *            It represents the uid that was responsible for waking the radio.
     */
    void onInterfaceClassActivityEvent(boolean isActive, String name, long timestamp , int uid);

    /**
     * Represents that the specific interface reaches its quota limitation.
     *
     * @param alertName alert name of the quota limitation.
     * @param ifName interface which reaches the limitation.
     */
    void onQuotaLimitEvent(String alertName,  String ifName);

    /**
     * Represents that some dns servers are added on the specific interface.
     *
     * @param ifName interface name.
     * @param lifetime lifetime for the dns servers.
     * @param servers the address of servers.
     */
    void onInterfaceDnsServersEvent(String ifName, long lifetime, in String[] servers);

    /**
     * Represents that an address is changed on the specific interface.
     *
     * @param updated true for update, false for remove.
     * @param addr address of this change.
     * @param ifName interface name of this change.
     * @param flags address flags of this change.
     * @param scope address scope of this change.
     */
    void onInterfaceAddressChangeEvent(
            boolean updated,
            String addr,
            String ifName,
            int flags,
            int scope);

    /**
     * Represents that a interface is added.
     *
     * @param ifName interface name of this event.
     */
    void onInterfaceAddEvent(String ifName);

    /**
     * Represents that a interface is removed.
     *
     * @param ifName interface name of this event.
     */
    void onInterfaceRemoveEvent(String ifName);

    /**
     * Represents that the status of the specific interface is changed.
     *
     * @param ifName interface name of this change.
     * @param status true for interface up, false for down.
     */
    void onInterfaceChangedEvent(String ifName, boolean status);

    /**
     * Represents that the link status of the specific interface is changed.
     *
     * @param ifName interface name of this change.
     * @param status true for interface link status up, false for link status down.
     */
    void onInterfaceLinkStatusEvent(String ifName, boolean status);

    /**
     * Represents that a routing rule is changed.
     *
     * @param updated true for update, false for remove.
     * @param route destination address of this routing rule.
     * @param gateway address of gateway, empty string for no gateway.
     * @param ifName interface name of this routing rule.
     */
    void onRouteChangeEvent(boolean updated, String route, String gateway, String ifName);

    /**
     * Represents that detecting sockets sending data not wrapped
     * inside a layer of SSL/TLS encryption.
     *
     * @param uid uid of this event.
     * @param hex packet content in hex format.
     */
    void onStrictCleartextEvent(int uid, String hex);

}
