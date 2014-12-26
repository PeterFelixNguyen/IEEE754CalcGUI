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

public class BinaryFractionCalc {
    private BigDecimal fraction;
    private long intPart;
    private int exponent = 0;
    private BigDecimal fractionPart; // this variable has been changed to a BigDecimal object
    private String signBit;
    private String doubleFracStr = "";
    private String singleFracStr = "";
    private String halfFracStr = "";
    private String expBits;
    private String fracBits;
    private String decNum;
    private final int DOUBLE_BIAS = 1023;
    private final int SINGLE_BIAS = 127;
    private final int HALF_BIAS = 15;
    private final int DOUB_FRAC_BITS = 52;
    private final int SING_FRAC_BITS = 23;
    private final int HALF_FRAC_BITS = 10;
    private final int DOUB_EXP_BITS = 11;
    private final int SING_EXP_BITS = 8;
    private final int HALF_EXP_BITS = 5;

    //BigDecimal has replaced all doubles after the constructor
    public BinaryFractionCalc(double number) {
        signBit = (number < 0) ? "1 " : "0 ";
        if (signBit.equals("1 "))
            number = number * -1;
        fraction = BigDecimal.valueOf(number);
    }

    public BinaryFractionCalc(String sign, String exp, String frac){
        signBit = sign;
        expBits = exp;
        fracBits = frac;
    }

    public String getDecimal(){
        calculateDecimal(signBit, expBits, fracBits);
        return decNum;
    }

    private void calculateDecimal(String sign, String exp, String frac){

    }

    public String getDouble() {
        calculateDouble(fraction);
        return doubleFracStr;
    }

    public String getSingle() {
        calculateSingle(fraction);
        return singleFracStr;
    }

    public String getHalf() {
        calculateHalf(fraction);
        return halfFracStr;
    }

    // Includes Peter's code changes, but uses constants to work for
    // singles,doubles and half. Also the function returns a String
    //Includes more BigDecimal calculations in attempt to solve a current issue
    private String helpFunction(BigDecimal number, String fracStr, int bias,
            int bits, int expBits) {
        intPart = number.longValue();
        boolean zero = false;

        if (intPart == 0) {
            zero = true;
        }
        double temporary = number.floatValue() - intPart;
        fractionPart = new BigDecimal(temporary);

        String localStr = "";

        if (!zero) {

            for (; intPart > 0; intPart = intPart / 2) {
                localStr = ((intPart % 2 == 0) ? "0" : "1") + localStr;
            }

            localStr += ".";
            for (int fracBits = bits; fracBits > 0; fracBits--) {
                int localNum =
                        ((fractionPart.multiply(BigDecimal.valueOf(2)))
                                .intValue());
                fractionPart =
                        (fractionPart.multiply(BigDecimal.valueOf(2)).
                                subtract(BigDecimal.valueOf(localNum)));
                localStr += localNum;
            }

            for (exponent = 0; localStr.charAt(exponent) != '.'; exponent++)
                ;

            if (exponent != 0) {
                fracStr = localStr.substring(1, exponent)
                        + localStr.substring(exponent + 1, localStr.length());
            }
            if (fracStr.length() > bits)
                fracStr = fracStr.substring(0, bits);
            exponent = exponent - 1 + bias;
            localStr = "";

            for (int i = 0; i < expBits; i++) {
                localStr = ((exponent % 2 == 0) ? "0" : "1") + localStr;
                exponent = exponent / 2;
            }
            localStr += " ";
            fracStr = signBit + localStr + fracStr;
        } else {

            String localFrac = "";
            for (int fracBits = bits; fracBits > 0; fracBits--) {
                int localNum =
                        ((fractionPart.multiply(BigDecimal.valueOf(2)))
                                .intValue());
                fractionPart =
                        (fractionPart.multiply(BigDecimal.valueOf(2)).
                                subtract(BigDecimal.valueOf(localNum)));
                localFrac += localNum;
            }

            exponent = 0;

            for (; exponent < localFrac.length(); exponent++) {
                if (localFrac.charAt(exponent) == '1') {
                    exponent++;
                    break;
                }
            }

            localFrac = localFrac.substring(exponent);

            for (int i = 0; i < exponent; i++) {
                localFrac = localFrac + "0";
            }

            exponent = bias - exponent;
            localStr = "";

            for (int i = 0; i < expBits; i++) {
                localStr = ((exponent % 2 == 0) ? "0" : "1") + localStr;
                exponent = exponent / 2;
            }
            localStr += " ";
            fracStr = signBit + localStr + localFrac;
        }
        return fracStr;
    }

    private void calculateDouble(BigDecimal number) {
        doubleFracStr = helpFunction(number, doubleFracStr, DOUBLE_BIAS,
                DOUB_FRAC_BITS, DOUB_EXP_BITS);
    }

    private void calculateSingle(BigDecimal number) {
        singleFracStr = helpFunction(number, singleFracStr, SINGLE_BIAS,
                SING_FRAC_BITS, SING_EXP_BITS);
    }

    private void calculateHalf(BigDecimal number) {
        halfFracStr = helpFunction(number, halfFracStr, HALF_BIAS,
                HALF_FRAC_BITS, HALF_EXP_BITS);
    }
}
