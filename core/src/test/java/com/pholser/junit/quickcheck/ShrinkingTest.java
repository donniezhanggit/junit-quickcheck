/*
 The MIT License

 Copyright (c) 2010-2015 Paul R. Holser, Jr.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.pholser.junit.quickcheck;

import java.util.ArrayList;
import java.util.List;

import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.pholser.junit.quickcheck.test.generator.Foo;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import static org.junit.experimental.results.PrintableResult.*;
import static org.junit.experimental.results.ResultMatchers.*;

public class ShrinkingTest {
    @Test public void complete() throws Exception {
        assertThat(
            testResult(ShrinkingCompletely.class),
            hasSingleFailureContaining(String.format("shrunken to [%s]", new Foo(1))));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class ShrinkingCompletely {
        @Property(maxShrinks = Integer.MAX_VALUE, maxShrinkDepth = Integer.MAX_VALUE)
        public void shouldHold(Foo f) {
            assumeThat(f.i(), greaterThan(0));

            assertThat(f.i(), lessThan(1));
        }
    }

    @Test public void partial() throws Exception {
        assertThat(
            testResult(ShrinkingPartially.class),
            hasSingleFailureContaining("shrunken to ["));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class ShrinkingPartially {
        @Property public void shouldHold(Foo f) {
            assumeThat(f.i(), greaterThan(Integer.MAX_VALUE / 2));

            assertThat(f.i(), lessThan(Integer.MAX_VALUE / 2));
        }
    }

    @Test public void assumptionFailureWhileShrinking() {
        assertThat(
            testResult(FailedAssumptionDuringShrinking.class),
            hasSingleFailureContaining("shrunken to ["));
    }

    @RunWith(JUnitQuickcheck.class)
    public static class FailedAssumptionDuringShrinking {
        private static boolean shrinking;

        @Property public void shouldHold(Foo f) {
            if (!shrinking) {
                shrinking = true;
                fail();
            }

            assumeFalse(shrinking);
        }
    }

    @Test public void disablingShrinking() {
        assertThat(testResult(DisablingShrinking.class), failureCountIs(1));
        assertEquals(1, DisablingShrinking.attempts.size());
    }

    @RunWith(JUnitQuickcheck.class)
    public static class DisablingShrinking {
        static List<Foo> attempts = new ArrayList<>();

        @Property(shrink = false) public void shouldHold(Foo f) {
            attempts.add(f);

            fail();
        }
    }
}