/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ashes.of.datadog.client.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;


/**
 * Based on gflogger formatter
 */
public class BufferFormatter {

    public static ByteBuf append(ByteBuf buffer, boolean b) {
        return b ?
                buffer.writeByte('t').writeByte('r').writeByte('u').writeByte('e') :
                buffer.writeByte('f').writeByte('a').writeByte('l').writeByte('s').writeByte('e');
    }

    public static ByteBuf append(ByteBuf buffer, char ch) {
        buffer.writeByte(ch);
        return buffer;
    }

    public static ByteBuf append(ByteBuf buffer, String str) {
        buffer.writeCharSequence(str, StandardCharsets.US_ASCII);
        return buffer;
    }

    public static ByteBuf append(ByteBuf buffer, int i) {
        if (i == Integer.MIN_VALUE) {
            // uses java.lang.Integer string constant of MIN_VALUE
            return append(buffer, Integer.toString(i));
        }

        put(buffer, i);
        return buffer;
    }

    public static ByteBuf append(ByteBuf buffer, long i) {
        if (i == Long.MIN_VALUE) {
            // uses java.lang.Long string constant of MIN_VALUE
            return append(buffer, Long.toString(i));
        }
        put(buffer, i);
        return buffer;
    }

    public static ByteBuf append(ByteBuf buffer, double i, int precision, boolean tailZeros) {
        put(buffer, i, precision < 0 ? 8 : precision, tailZeros);
        return buffer;
    }

    public static ByteBuf append(ByteBuf buffer, double v) {
        put(buffer, v);
        return buffer;
    }

    private static char[] DIGIT_TENS = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    };

    private static char[] DIGIT_ONES = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    };

    /**
     * All possible chars for representing a number as a String
     */
    private static char[] DIGITS = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    // based on java.lang.Integer.getChars(int i, int index, char[] buf)
    private static void put(ByteBuf buffer, int i) {
        int size = numberOfDigits(i);

        int q, r;

        buffer.writeZero(size);
        int writeIndex = buffer.writerIndex();

        char sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buffer.setByte(--writeIndex, DIGIT_ONES[r]);
            buffer.setByte(--writeIndex, DIGIT_TENS[r]);
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        while (true) {
            // 52429 = (1 << 15) + (1 << 14) + (1 << 11) + (1 << 10) + (1 << 7) + (1 << 6) + (1 << 3) + (1 << 2) + 1
            // 52429 = 32768 + 16384 + 2048 + 1024 + 128 + 64 + 8 + 4 + 1
            /*/
            q = ((i << 15) + (i << 14) + (i << 11) + (i << 10) + (i << 7) + (i << 6) + (i << 3) + (i << 2) + i) >> (16 + 3);
            /*/
            q = i * 52429 >>> 16 + 3;
            //*/
            r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buffer.setByte(--writeIndex, (byte) DIGITS[r]);
            i = q;
            if (i == 0) break;
        }

        if (sign != 0) {
            buffer.setByte(--writeIndex, (byte) sign);
        }
    }

    private static long[] LONG_POWERS_OF_TEN = {
            1L,
            10L,
            100L,
            1000L,
            10000L,
            100000L,
            1000000L,
            10000000L,
            100000000L,
            1000000000L,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L,
            1000000000000000000L
    };

    /**
     * @return for given positive long x, returns the number of decimal digits required
     * to represent value of x.
     */
    private static int numberOfDigits(long x) {
        if (x < 0) {
            if (x == Long.MIN_VALUE) {
                // Life is hard: -Long.MIN_VALUE == Long.MIN_VALUE
                return 1 + LONG_POWERS_OF_TEN.length;
            } else {
                return 1 + numberOfDigits(-x);
            }
        }
        for (int pow = 0; pow < LONG_POWERS_OF_TEN.length; pow++) {
            long tenPower = LONG_POWERS_OF_TEN[pow];
            if (x < tenPower) {
                return Math.max(pow, 1);
            }
        }
        return LONG_POWERS_OF_TEN.length;
    }

    private static int[] INT_POWERS_OF_TEN = {
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
            1000000000
    };

    /**
     * @return for given positive int x, returns the number of decimal digits required
     * to represent value of x.
     */
    private static int numberOfDigits(int x) {
        if (x < 0) {
            if (x == Integer.MIN_VALUE) {
                // Life is hard: -Long.MIN_VALUE == Long.MIN_VALUE
                return 1 + INT_POWERS_OF_TEN.length;
            } else {
                return 1 + numberOfDigits(-x);
            }
        }
        for (int pow = 0; pow < INT_POWERS_OF_TEN.length; pow++) {
            long tenPower = INT_POWERS_OF_TEN[pow];
            if (x < tenPower) {
                return Math.max(pow, 1);
            }
        }
        return INT_POWERS_OF_TEN.length;
    }

    // based on java.lang.Long.getChars(int i, int index, char[] buf)
    private static void put(ByteBuf buffer, long l) {
        int size = numberOfDigits(l);

        buffer.writeZero(size);
        int writeIndex = buffer.writerIndex();

        long q;
        int r;
        char sign = 0;

        if (l < 0) {
            sign = '-';
            l = -l;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (l > Integer.MAX_VALUE) {
            q = l / 100;
            // really: r = i - (q * 100);
            r = (int) (l - ((q << 6) + (q << 5) + (q << 2)));
            l = q;
            buffer.setByte(--writeIndex, DIGIT_ONES[r]);
            buffer.setByte(--writeIndex, DIGIT_TENS[r]);
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) l;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buffer.setByte(--writeIndex, DIGIT_ONES[r]);
            buffer.setByte(--writeIndex, DIGIT_TENS[r]);
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        while (true) {
            // 52429 = (1 << 15) + (1 << 14) + (1 << 11) + (1 << 10) + (1 << 7) + (1 << 6) + (1 << 3) + (1 << 2) + 1
            /*/
            q2 = ((i2 << 15) + (i2 << 14) + (i2 << 11) + (i2 << 10) + (i2 << 7) + (i2 << 6) + (i2 << 3) + (i2 << 2) + i2) >> (16 + 3);
            /*/
            q2 = i2 * 52429 >>> 16 + 3;
            //*/
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            buffer.setByte(--writeIndex, (byte) DIGITS[r]);
            i2 = q2;
            if (i2 == 0) break;
        }
        if (sign != 0) {
            buffer.setByte(--writeIndex, (byte) sign);
        }
    }

    /**
     * Bit 63 represents the sign of the floating-point number.
     *
     * @see Double#doubleToLongBits(double)
     */
    private static final long SIGN_MASK = 0x8000000000000000L;

    /**
     * Bits 62-52 represent the exponent.
     *
     * @see Double#doubleToLongBits(double)
     */
    private static final long EXP_MASK = 0x7ff0000000000000L;

    /**
     * Bits 51-0 represent the significant (sometimes called the mantissa) of
     * the floating-point number.
     *
     * @see Double#doubleToLongBits(double)
     */
    private static final long MANTISSA_MASK = 0x000fffffffffffffL;

    private static final long EXP_BIAS = 1023;

    private static final int EXP_SHIFT = 52;

    /**
     * assumed High-Order bit
     */
    private static final long FRACT_HOB = 1L << EXP_SHIFT;

    /**
     * exponent of 1.0
     */
    private static final long EXP_ONE = EXP_BIAS << EXP_SHIFT;

    private static final String INFINITY = "Infinity";
    private static final String NAN = "NaN";
    private static final String ZERO_DOT_ZERO = "0.0";

    /**
     * log2(2^53) = 15.9
     */
    private static final int DOUBLE_DIGITS = 15;

    private static void put(ByteBuf buffer, double v) {
        if (Double.isNaN(v)) {
            append(buffer, NAN);
            return;
        }
        long bits = Double.doubleToRawLongBits(v);

        boolean isNegative = (bits & SIGN_MASK) != 0;
        if (isNegative) {
            // reset sign bit
            bits = bits & ~SIGN_MASK;
            v = Double.longBitsToDouble(bits);
            buffer.writeByte('-');
        }
        if (v == Double.POSITIVE_INFINITY) {
            append(buffer, INFINITY);
            return;
        }
        if (bits == 0) {
            append(buffer, ZERO_DOT_ZERO);
            return;
        }

        int digits = guessFractionDigits(bits);

        put(buffer, v, digits, false);
    }

    private static int guessFractionDigits(long doubleBits) {
        //RC: here we try to guess digits-after-the-point required to represent this
        // double, but sometimes we fail: like for -1.0000000000000002E15, there it is
        // guessed we need 0 digit, so last meaningful digit (2) is lost

        long exponent = ((EXP_MASK & doubleBits) >> EXP_SHIFT) - EXP_BIAS;
        long significant = MANTISSA_MASK & doubleBits;
        //restore implicit leading bit

        long fractBits = 1L << EXP_SHIFT + 1 | significant;
        double d2 = Double.longBitsToDouble(EXP_ONE | fractBits & ~FRACT_HOB);
        int decExp = (int) Math.floor((d2 - 1.5D) * 0.289529654D + 0.176091259 + exponent * 0.301029995663981);

        // do not handle negative dec exp
        return DOUBLE_DIGITS - (decExp > 0 ? decExp : 0);
    }

    private static void put(ByteBuf buffer,
                            double v,
                            int digits,
                            boolean forceTailZeros) {
        if (Double.isNaN(v)) {
            append(buffer, NAN);
            return;
        }

        long bits = Double.doubleToRawLongBits(v);

        boolean isNegative = (bits & SIGN_MASK) != 0;
        if (isNegative) {
            // reset sign bit
            bits = bits & ~SIGN_MASK;
            v = Double.longBitsToDouble(bits);
            buffer.writeByte('-');
        }
        if (v == Double.POSITIVE_INFINITY) {
            append(buffer, INFINITY);
            return;
        }
        if (bits == 0) {
            append(buffer, ZERO_DOT_ZERO);
            return;
        }

        //scientific notation required -> fallback to JDK
        if (v > 0 && (v > 1e18 || v < 1e-18) || v < 0 && (v < -1e18 || v > -1e-18)) {
            append(buffer, Double.toString(v));
            return;
        }

        long integerPart = (long) v;
        put(buffer, integerPart);
        buffer.writeByte('.');


        //wrap digits to [0, LONG_POWERS_OF_TEN.length-1] inclusive
        int realDigits = Math.min(
                Math.max(0, digits),
                LONG_POWERS_OF_TEN.length - 1
        );
        long multiplier = LONG_POWERS_OF_TEN[realDigits];

        long remainderAsLong = (long) ((v - integerPart) * multiplier);

        int oldPos = buffer.writerIndex();

        // add leading zeros

        if (remainderAsLong != 0) {
            int remainderDigits = numberOfDigits(remainderAsLong);

            int leadingZeros = realDigits - remainderDigits;
            for (int i = 0; i < leadingZeros; i++) {
                buffer.writeByte('0');
            }
        }

        put(buffer, remainderAsLong);

        if (forceTailZeros) {
            int pos = buffer.writerIndex();

            if (pos - oldPos < digits) {
                int j = digits - (pos - oldPos);
                for (int i = 0; i < j; i++) {
                    buffer.writeByte('0');
                }
            }
        }
    }
}
