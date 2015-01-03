/**
 * Copyright 2014 Latrice Sebastian, Peter "Felix" Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class FloatingPoint {
    /** SHARED **/
    // IEEE-754 Bit Representations
    private String spacedBinString;
    private String normalBinString;
    // IEEE-754 Dec Representation
    private String defaultDecString;
    // Base and Precision
    public enum Base {TEN, TWO;}
    public enum Precision {HALF, SINGLE, DOUBLE}
    private Base base;
    private Precision precision;
    // Precision Constants
    private final int DOUBLE_BIAS = 1023;
    private final int SINGLE_BIAS = 127;
    private final int HALF_BIAS = 15;
    private final int DOUB_SIGNIF_BITS = 52;
    private final int SING_SIGNIF_BITS = 23;
    private final int HALF_SIGNIF_BITS = 10;
    private final int DOUB_EXP_BITS = 11;
    private final int SING_EXP_BITS = 8;
    private final int HALF_EXP_BITS = 5;

    /** BASE TEN **/
    // After Normalized
    private String signBit;
    private String exponentBits;
    private String mantissaBits;
    // Before Normalized
    private BigDecimal number;
    private BigDecimal absoluteValue;
    // Decimal Parts
    private BigDecimal beforeShift;
    private BigDecimal afterShift;
    private BigDecimal intPartBaseTen;
    private String intPartBaseTwo;
    private BigDecimal fracPartBaseTen;
    private String fracPartBaseTwo;
    // Exponent
    int maxSignificantBits;
    int maxExpBits;
    private int bitShift;
    private BigInteger exponent;
    private int bias;
    // Track shifts
    private int shiftLeft;
    private int shiftRight;
    private int remainingBits;

    /** BASE TWO **/
    private BigDecimal fraction;
    private BigDecimal intPart;
    private int exponentInt = 0;
    private BigDecimal fractionPart;

    public FloatingPoint(BigDecimal number, Precision precision) {
        base = Base.TEN;
        this.precision = precision;
        this.number = number;
        absoluteValue = number.abs();
        defaultDecString = number.toString();
        calculateBinary();
    }

    /**
     * Constructs a FloatingPoint
     * 
     * @param sign
     *            string that represents the sign bit in IEEE 754 format
     * @param exp
     *            string that represents the exponent bits in IEEE 754 format
     * @param frac
     *            string that represents the fraction bits in IEEE 754 format
     */
    public FloatingPoint(String sign, String exponent, String mantissa) {
        base = Base.TWO;
        signBit = sign;
        exponentBits = exponent;
        mantissaBits = mantissa;
        spacedBinString = sign + " " + exponent + " " + mantissa;
        normalBinString = sign + exponent + mantissa;
        defaultDecString = calculateDecimal(signBit, exponentBits, mantissaBits);
    }

    public String spacedBinString() {
        return spacedBinString;
    }

    public String normalBinString() {
        return normalBinString;
    }

    /**
     * Sends signBit, expBits, and fracBits to calculateDecimal and returns a
     * string, decNum
     * 
     * @return string that represents a base 10 floating-point number
     */
    public String getDecimalNotated() {
        String returnValue = defaultDecString;

        if (precision == Precision.HALF) {
            // need special code to set scale
            // and round at this precision
        } else if (precision == Precision.SINGLE) {
            Float temp = new BigDecimal(defaultDecString).floatValue();
            returnValue = temp.toString();
        } else if (precision == Precision.DOUBLE) {
            Double temp = new BigDecimal(defaultDecString).doubleValue();
            returnValue = temp.toString();
        }

        return returnValue;
    }

    /**
     * Casted
     */
    public String getDecimalNotated(Precision precision) {
        String returnValue = defaultDecString;

        if (precision == Precision.HALF) {
            // need special code to set scale
            // and round at this precision
        } else if (precision == Precision.SINGLE) {
            Float temp = new BigDecimal(defaultDecString).floatValue();
            returnValue = temp.toString();
        } else if (precision == Precision.DOUBLE) {
            Double temp = new BigDecimal(defaultDecString).doubleValue();
            returnValue = temp.toString();
        }

        return returnValue;
    }

    /**
     * I think Latrice should add documentation here
     * for language consistency
     * 
     * @return
     */
    public String getDecimalVerbose() {
        String returnValue = defaultDecString;

        if (precision == Precision.HALF) {
            // need special code to set scale
            // and round at this precision
        } else if (precision == Precision.SINGLE) {
            Float temp = new BigDecimal(defaultDecString).floatValue();
            returnValue = new BigDecimal(temp).toPlainString();
        } else if (precision == Precision.DOUBLE) {
            Double temp = new BigDecimal(defaultDecString).doubleValue();
            returnValue = new BigDecimal(temp).toPlainString();
        }

        return returnValue;
    }

    /**
     * Casted
     */
    public String getDecimalVerbose(Precision precision) {
        String returnValue = defaultDecString;

        if (precision == Precision.HALF) {
            // need special code to set scale
            // and round at this precision
        } else if (precision == Precision.SINGLE) {
            Float temp = new BigDecimal(defaultDecString).floatValue();
            returnValue = new BigDecimal(temp).toPlainString();
        } else if (precision == Precision.DOUBLE) {
            Double temp = new BigDecimal(defaultDecString).doubleValue();
            returnValue = new BigDecimal(temp).toPlainString();
        }

        return returnValue;
    }

    private void calculateBinary() {
        if (precision == Precision.HALF) {
            bias = HALF_BIAS;
            maxSignificantBits = HALF_SIGNIF_BITS;
            maxExpBits = HALF_EXP_BITS;
        } else if (precision == Precision.SINGLE) {
            bias = SINGLE_BIAS;
            maxSignificantBits = SING_SIGNIF_BITS;
            maxExpBits = SING_EXP_BITS;
        } else if (precision == Precision.DOUBLE) {
            bias = DOUBLE_BIAS;
            maxSignificantBits = DOUB_SIGNIF_BITS;
            maxExpBits = DOUB_EXP_BITS;
        } else {
            System.out.println("Unimplemented Precision");
        }

        intPartBaseTen = new BigDecimal(absoluteValue.toBigInteger().toString());
        intPartBaseTwo = intPartBaseTen.toBigInteger().toString(2);

        bitShift = 0;
        BigDecimal intPart = absoluteValue.setScale(0, BigDecimal.ROUND_DOWN);

        beforeShift = new BigDecimal(intPartBaseTwo + "." + "0");
        String beforeShiftStr = beforeShift.toString();
        int i = 0;

        shiftLeft = 0;
        boolean decimalFound = false;

        while (!decimalFound) {
            if (beforeShiftStr.charAt(i) == '.') {
                shiftLeft = i - 1;
                decimalFound = true;
            }
            i++;
        }

        remainingBits = maxSignificantBits - shiftLeft;
        shiftRight = 0;

        fracPartBaseTen  = absoluteValue.subtract(intPart);
        BigDecimal mutatedFrac = fracPartBaseTen;
        fracPartBaseTwo = "";

        i = 0;
        if (shiftLeft > 0) {
            while (i < remainingBits) {
                BigDecimal localNum = ((mutatedFrac.multiply(BigDecimal
                        .valueOf(2))).setScale(0, BigDecimal.ROUND_DOWN));
                mutatedFrac = (mutatedFrac.multiply(BigDecimal.valueOf(2))
                        .subtract(localNum));
                fracPartBaseTwo += localNum;
                i++;
            }
            bitShift = shiftLeft;
            beforeShift = new BigDecimal(intPartBaseTwo + "." + fracPartBaseTwo);
            afterShift = beforeShift.movePointLeft(shiftLeft);
            mantissaBits = afterShift.toString().substring(2, maxSignificantBits + 2); /////// <<< this right here is an issue
        } else if (shiftLeft == 0) {
            int index = 0;
            boolean firstBitFound = false;
            boolean noMoreBits = false;

            while (!noMoreBits && i < bias) {
                BigDecimal localNum = ((mutatedFrac.multiply(BigDecimal
                        .valueOf(2))).setScale(0, BigDecimal.ROUND_DOWN));
                mutatedFrac = (mutatedFrac.multiply(BigDecimal.valueOf(2))
                        .subtract(localNum));
                fracPartBaseTwo += localNum;

                if (fracPartBaseTwo.charAt(i) == '1' && firstBitFound == false) {
                    shiftRight = i + 1;
                    firstBitFound = true;
                }

                if (firstBitFound == true) {
                    if (index <= maxSignificantBits) {
                        index++;
                    }
                }

                if (index > maxSignificantBits) {
                    noMoreBits = true;
                }
                i++;
            }
            if (!fracPartBaseTwo.contains("1")) {
                fracPartBaseTwo = "";
                for (int j = 0; j < maxSignificantBits; j++) {
                    fracPartBaseTwo += "0";
                }
                if (intPartBaseTwo.compareTo("0") == 0) {
                    bitShift = -127;
                }
            } else {
                bitShift = -1 * shiftRight;
            }
            beforeShift = new BigDecimal(intPartBaseTwo + "." + fracPartBaseTwo);
            if (intPart.compareTo(BigDecimal.ZERO) == 0) {
                afterShift = beforeShift.movePointRight(shiftRight);
            } else {
                afterShift = beforeShift;
            }
            mantissaBits = afterShift.toPlainString().substring(2, maxSignificantBits + 2);
        }

        exponent = BigInteger.valueOf(bias).add(BigInteger.valueOf(bitShift));
        int hiddenZeroes = maxExpBits - exponent.toString(2).length();
        exponentBits = exponent.toString(2);

        for (int j = 0; j < hiddenZeroes; j++) {
            exponentBits = "0" + exponentBits;
        }

        signBit = (number.compareTo(BigDecimal.ZERO) < 0) ? "1" : "0";

        if (bitShift > bias) {
            exponentBits = "";
            for (int k = 0; k < maxExpBits; k++) {
                exponentBits = "1" + exponentBits;
            }

            mantissaBits = "";
            for (int k = 0; k < maxSignificantBits; k++) {
                mantissaBits = "0" + mantissaBits;
            }
        }
        spacedBinString = signBit + " " + exponentBits + " " + mantissaBits;
        normalBinString = signBit + exponentBits + mantissaBits;
    }



    /**
     * Calculates a decimal number based on three strings that represent the
     * number in the IEEE 754 format
     * 
     * @param sign
     *            string that represents the sign bit in IEEE 754 format
     * @param exp
     *            string that represents the exponent bits in IEEE 754 format
     * @param frac
     *            string that represents the fraction bits in IEEE 754 format
     * @return string that represents a special value like infinity or a base 10
     *         floating-point number
     */
    private String calculateDecimal(String sign, String exponent,
            String mantissa) {
        int bias;
        int shift = 0;
        int onesExpCount = 0;
        int zeroFracCount = 0;
        int zeroExpCount = 0;
        String tempStr = "";

        BigDecimal tempInt = new BigDecimal("0");
        BigDecimal tempFrac = new BigDecimal("0");
        BigDecimal base = new BigDecimal("2");

        for (int i = 0; i < mantissa.length(); i++) {
            if (mantissa.charAt(i) == '0')
                zeroFracCount++;
        }

        for (int i = 0; i < exponent.length(); i++) {
            if (exponent.charAt(i) == '0')
                zeroExpCount++;
        }

        for (int i = 0; i < exponent.length(); i++) {
            if (exponent.charAt(i) == '1')
                onesExpCount++;
        }

        if ((zeroExpCount == exponent.length())
                && (zeroFracCount == mantissa.length()))
            tempStr = "0";

        else if ((onesExpCount == exponent.length())
                && (zeroFracCount == mantissa.length()))
            tempStr = "INF";

        else if ((onesExpCount == exponent.length())
                && (zeroFracCount != mantissa.length()))
            tempStr = "NaN";

        else {
            if (exponent.length() <= 5) {
                bias = HALF_BIAS;
                precision = Precision.HALF;
            }
            else if (exponent.length() <= 8) {
                bias = SINGLE_BIAS;
                precision = Precision.SINGLE;
            }
            else {
                bias = DOUBLE_BIAS;
                precision = Precision.DOUBLE;
            }
            for (int i = exponent.length() - 1, j = 0; i >= 0; i--, j++)
                this.exponentInt += (exponent.charAt(i) == '1') ? Math.pow(2, j)
                        : 0;

                shift = this.exponentInt - bias;

                if (shift > mantissa.length()) {
                    for (int i = 0, j = shift - mantissa.length(); i < j; i++)
                        mantissa += "0";
                }

                tempStr = "1." + mantissa;
                fraction = new BigDecimal(tempStr);
                fraction = fraction.movePointRight(shift);
                tempStr = fraction.toPlainString();

                intPart = fraction.setScale(0, BigDecimal.ROUND_DOWN);
                fractionPart = fraction.subtract(intPart);

                tempStr = intPart.toPlainString();
                for (int i = tempStr.length() - 1, j = 0; i >= 0; i--, j++)
                    if (tempStr.charAt(i) == '1')
                        tempInt = tempInt.add(base.pow(j));
                //Bug fix
                tempStr = (fractionPart.compareTo(BigDecimal.ZERO) != 0) ? fractionPart
                        .toPlainString().substring(2) : "0";

                        for (int i = 0, j = -1; i < tempStr.length(); i++, j--)
                            if (tempStr.charAt(i) == '1')
                                tempFrac = tempFrac.add(BigDecimal.ONE.divide(
                                        base.pow(j * -1), 32, RoundingMode.CEILING));

                        tempStr = tempInt.toPlainString()
                                + tempFrac.toPlainString().substring(1);
        }

        if (sign.equals("1"))
            tempStr = '-' + tempStr;

        return tempStr;
    }

    public void examineValues() {
        if (base == Base.TEN) {
            System.out.println("      shiftLeft: " + shiftLeft                  );
            System.out.println("     shiftRight: " + shiftRight                 );
            System.out.println("  remainingBits: " + remainingBits              );
            System.out.println(                                                 );
            System.out.println("         number: " + number                     );
            System.out.println("  absoluteValue: " + absoluteValue              );
            System.out.println(" intPartBaseTen: " + intPartBaseTen             );
            System.out.println("fracPartBaseTen: " + fracPartBaseTen            );
            System.out.println("    beforeShift: " + beforeShift.toPlainString());
            System.out.println("     afterShift: " + afterShift.toPlainString() );
            System.out.println(" intPartBaseTwo: " + intPartBaseTwo             );
            System.out.println("fracPartBaseTwo: " + fracPartBaseTwo            );
            System.out.println("   mantissaBits: " + mantissaBits               );
            System.out.println("spacedBinString: " + spacedBinString            );
            System.out.println("normalBinString: " + normalBinString            );
            System.out.println(                                                 );
            System.out.println("***********************************************************");
        } else if (base == Base.TWO) {
            // don't have anything yet
        }
    }
}

