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

package com.android.server.timedetector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.runner.AndroidJUnit4;

import com.android.internal.util.IndentingPrintWriter;
import com.android.server.timezonedetector.ReferenceWithHistory;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.StringWriter;

@RunWith(AndroidJUnit4.class)
public class ReferenceWithHistoryTest {

    @Test
    public void testBasicReferenceBehavior() {
        // Create a reference that will retain 2 history values.
        ReferenceWithHistory<String> referenceWithHistory =
                new ReferenceWithHistory<>(2 /* history */);

        // Check unset behavior.
        assertNull(referenceWithHistory.get());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
        assertEquals("null", referenceWithHistory.toString());

        // Try setting null.
        assertNull(referenceWithHistory.set(null));
        assertNull(referenceWithHistory.get());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
        assertEquals("null", referenceWithHistory.toString());

        // Try setting a non-null value.
        assertNull(referenceWithHistory.set("Foo"));
        assertEquals("Foo", referenceWithHistory.get());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
        assertEquals("Foo", referenceWithHistory.toString());

        // Try setting null again.
        assertEquals("Foo", referenceWithHistory.set(null));
        assertNull(referenceWithHistory.get());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
        assertEquals("null", referenceWithHistory.toString());

        // Try a non-null value again.
        assertNull(referenceWithHistory.set("Bar"));
        assertEquals("Bar", referenceWithHistory.get());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
        assertEquals("Bar", referenceWithHistory.toString());
    }

    @Test
    public void testValueHistoryBehavior() {
        // Create a reference that will retain 2 history values.
        ReferenceWithHistory<String> referenceWithHistory =
                new ReferenceWithHistory<>(2 /* history */);
        TestRef<String> reference = new TestRef<>();

        // Assert behavior before anything is set.
        assertEquals(reference.get(), referenceWithHistory.get());

        assertEquals(0, referenceWithHistory.getHistoryCount());
        assertNotNull(referenceWithHistory.toString());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));

        // Set a value (1).
        assertEquals(reference.set("V1"), referenceWithHistory.set("V1"));
        assertEquals(reference.get(), referenceWithHistory.get());

        assertEquals(1, referenceWithHistory.getHistoryCount());
        assertNotNull(referenceWithHistory.toString());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));

        // Set a value (2).
        assertEquals(reference.set("V2"), referenceWithHistory.set("V2"));
        assertEquals(reference.get(), referenceWithHistory.get());

        assertEquals(2, referenceWithHistory.getHistoryCount());
        assertNotNull(referenceWithHistory.toString());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));

        // Set a value (3).
        // We should have hit the limit of "2 history values retained per key".
        assertEquals(reference.set("V1"), referenceWithHistory.set("V1"));
        assertEquals(reference.get(), referenceWithHistory.get());

        assertEquals(2, referenceWithHistory.getHistoryCount());
        assertNotNull(referenceWithHistory.toString());
        assertNotNull(dumpReferenceWithHistory(referenceWithHistory));
    }

    /**
     * A simple class that has the same behavior as ReferenceWithHistory without the history. Used
     * in tests for comparison.
     */
    private static class TestRef<V> {
        private V mValue;

        public V get() {
            return mValue;
        }

        public V set(V value) {
            V previous = mValue;
            mValue = value;
            return previous;
        }
    }

    private static String dumpReferenceWithHistory(ReferenceWithHistory<?> referenceWithHistory) {
        StringWriter stringWriter = new StringWriter();
        try (IndentingPrintWriter ipw = new IndentingPrintWriter(stringWriter, " ")) {
            referenceWithHistory.dump(ipw);
            return stringWriter.toString();
        }
    }
}
