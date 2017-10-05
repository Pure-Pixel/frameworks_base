/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.net.metrics;

import static org.junit.Assert.assertTrue;

import android.net.NetworkCapabilities;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.android.internal.util.BitUtils;

import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class MetricsTest {

    @Test
    public void testDnsEventPrinting() {
        int[] evTypes = {DnsEvent.EVENT_GETADDRINFO, DnsEvent.EVENT_GETHOSTBYNAME};
        int[][] returnCode = {null, {1, 4, 3, 7, 6}, {3, 4, 5, 6, 2}}; // indexed by evType
        int maxIterations = 100;
        int maxEvents = 1000;
        int maxLatencyMs = 50000;
        int netId = 100;
        long transports = BitUtils.packBits(new int[]{NetworkCapabilities.TRANSPORT_WIFI});
        int capacity = 100;

        Random r = new Random();

        for (int i = 0; i < maxIterations; i++) {
            DnsEvent dnsEvent = new DnsEvent(netId, transports, capacity);
            int nEvent = r.nextInt(maxEvents);
            for (int j = 0; j < nEvent; j++) {
                int ev = evTypes[r.nextInt(evTypes.length)];
                int result = returnCode[ev][r.nextInt(returnCode[ev].length)];
                int latency = r.nextInt(maxLatencyMs);
                dnsEvent.addResult((byte) ev, (byte) result, latency);
            }

            String got = dnsEvent.toString();
            assertTrue(got.contains(Integer.toString(netId)));
            assertTrue(got.contains("WIFI"));

            netId++;
        }
    }
}
