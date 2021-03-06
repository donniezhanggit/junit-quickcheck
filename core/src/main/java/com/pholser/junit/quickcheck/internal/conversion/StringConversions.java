/*
 The MIT License

 Copyright (c) 2010-2020 Paul R. Holser, Jr.

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

package com.pholser.junit.quickcheck.internal.conversion;

import static com.pholser.junit.quickcheck.internal.Reflection.defaultValueOf;
import static com.pholser.junit.quickcheck.internal.Reflection.findConstructor;
import static com.pholser.junit.quickcheck.internal.Reflection.findMethod;
import static com.pholser.junit.quickcheck.internal.Reflection.instantiate;
import static com.pholser.junit.quickcheck.internal.Reflection.maybeWrap;

import com.pholser.junit.quickcheck.conversion.StringConversion;
import com.pholser.junit.quickcheck.generator.Also;
import com.pholser.junit.quickcheck.generator.Only;
import com.pholser.junit.quickcheck.internal.ParameterTypeContext;
import com.pholser.junit.quickcheck.internal.ReflectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class StringConversions {
    private StringConversions() {
        throw new UnsupportedOperationException();
    }

    public static StringConversion to(Class<?> clazz) {
        Class<?> wrapped = maybeWrap(clazz);

        try {
            Method method =
                findMethod(clazz, "valueOf", String.class);
            if (Modifier.isStatic(method.getModifiers())
                && wrapped.equals(method.getReturnType())) {

                return new MethodInvokingStringConversion(method);
            }
        } catch (ReflectionException reserveJudgment) {
            // fall back to other means of conversion
        }

        if (Character.class.equals(wrapped))
            return characterConversion(clazz);

        return new ConstructorInvokingStringConversion(
            findConstructor(wrapped, String.class));
    }

    public static StringConversion decide(ParameterTypeContext p, Only only) {
        return decide(p, only.by(), Only.class);
    }

    public static StringConversion decide(ParameterTypeContext p, Also also) {
        return decide(p, also.by(), Also.class);
    }

    private static <T extends Annotation> StringConversion decide(
        ParameterTypeContext p,
        Class<? extends StringConversion> custom,
        Class<T> markerClass) {

        return custom.equals(defaultValueOf(markerClass, "by"))
            ? to(p.getRawClass())
            : instantiate(custom);
    }

    private static StringConversion characterConversion(Class<?> clazz) {
        return raw -> {
            if (raw.length() > 1) {
                throw new IllegalArgumentException(
                    "Cannot convert " + raw + " into an instance of " + clazz);
            }

            return raw.charAt(0);
        };
    }
}
