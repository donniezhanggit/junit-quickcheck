/*
 The MIT License

 Copyright (c) 2010-2013 Paul R. Holser, Jr.

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

package com.pholser.junit.quickcheck.generator.java.math;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.*;
import static java.util.Arrays.*;

import com.google.common.collect.ImmutableMap;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.generator.Precision;
import com.pholser.junit.quickcheck.generator.BasicGeneratorTheoryParameterTest;

import static com.pholser.junit.quickcheck.internal.Reflection.*;
import static org.mockito.Mockito.*;

public class RangedBigDecimalNoMaxWithGreaterSpecifiedPrecisionTest
    extends BasicGeneratorTheoryParameterTest {

    private final BigDecimal min = new BigDecimal("-98765432198.7654321");
    private final BigInteger minBigInt = min.movePointRight(8).toBigInteger();

    @Override protected void primeSourceOfRandomness() {
        when(randomForParameterGenerator.nextBigInteger(
            minBigInt.add(TEN.movePointRight(8).toBigInteger()).subtract(minBigInt).bitLength()))
            .thenReturn(new BigInteger("6"));
        when(randomForParameterGenerator.nextBigInteger(
            minBigInt.add(TEN.pow(2).movePointRight(8).toBigInteger()).subtract(minBigInt).bitLength()))
            .thenReturn(new BigInteger("35"));
    }

    @Override protected Type parameterType() {
        return BigDecimal.class;
    }

    @Override protected int sampleSize() {
        return 2;
    }

    @Override protected List<?> randomValues() {
        return asList(new BigDecimal("-98765432198.76543204"), new BigDecimal("-98765432198.76543175"));
    }

    @Override protected Map<Class<? extends Annotation>, Annotation> configurations() {
        InRange range = mock(InRange.class);
        when(range.min()).thenReturn(min.toString());
        when(range.max()).thenReturn((String) defaultValueOf(InRange.class, "max"));
        Precision precision = mock(Precision.class);
        when(precision.scale()).thenReturn(8);
        return ImmutableMap.<Class<? extends Annotation>, Annotation> builder()
            .put(InRange.class, range)
            .put(Precision.class, precision)
            .build();
    }

    @Override public void verifyInteractionWithRandomness() {
        verify(randomForParameterGenerator).nextBigInteger(
            minBigInt.add(TEN.movePointRight(8).toBigInteger()).subtract(minBigInt).bitLength());
        verify(randomForParameterGenerator).nextBigInteger(
            minBigInt.add(TEN.pow(2).movePointRight(8).toBigInteger()).subtract(minBigInt).bitLength());
    }
}
