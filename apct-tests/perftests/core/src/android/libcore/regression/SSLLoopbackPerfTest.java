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

package android.libcore.regression;

import android.perftests.utils.BenchmarkState;
import android.perftests.utils.PerfStatusReporter;

import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import libcore.java.security.TestKeyStore;
import libcore.javax.net.ssl.TestSSLContext;
import libcore.javax.net.ssl.TestSSLSocketPair;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.net.ssl.SSLSocket;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SSLLoopbackPerfTest {
    @Rule public PerfStatusReporter mPerfStatusReporter = new PerfStatusReporter();

    @Test
    public void time() throws Exception {
        BenchmarkState state = mPerfStatusReporter.getBenchmarkState();
        while (state.keepRunning()) {
            TestSSLContext context =
                    TestSSLContext.create(TestKeyStore.getClient(), TestKeyStore.getServer());
            SSLSocket[] sockets = TestSSLSocketPair.connect(context, null, null);
            context.close();
            sockets[0].close();
            sockets[1].close();
        }
    }
}
