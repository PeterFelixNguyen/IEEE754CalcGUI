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
	private BigDecimal intPart;
	private int exponent = 0;
	private BigDecimal fractionPart;
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

	/**
	 * Constructs a BinaryFractionCalc by assigning the user's number to
	 * fraction and by determining the signBit
	 * 
	 * @param number
	 *            the BigDecimal, in base 10, that the user enters
	 */
	public BinaryFractionCalc(BigDecimal number) {
		signBit = (number.compareTo(BigDecimal.ZERO) < 0) ? "1 " : "0 ";
		if (signBit.equals("1 "))
			number = number.abs();
		fraction = number;
		//System.out.println("The full fractional number: " + fraction);
	}

	/**
	 * Constructs a BinaryFractionCalc
	 * 
	 * @param sign
	 *            string that represents the sign bit in IEEE 754 format
	 * @param exp
	 *            string that represents the exponent bits in IEEE 754 format
	 * @param frac
	 *            string that represents the fraction bits in IEEE 754 format
	 */
	public BinaryFractionCalc(String sign, String exp, String frac) {
		signBit = sign;
		expBits = exp;
		fracBits = frac;
	}

	/**
	 * Sends signBit, expBits, and fracBits to calculateDecimal and returns a
	 * string, decNum
	 * 
	 * @return string that represents a base 10 floating-point number
	 */
	public String getDecimal() {
		calculateDecimal(signBit, expBits, fracBits);
		return decNum;
	}

	/**
	 * This method has yet to be implemented
	 * 
	 * @param sign
	 *            string that represents the sign bit in IEEE 754 format
	 * @param exp
	 *            string that represents the exponent bits in IEEE 754 format
	 * @param frac
	 *            string that represents the fraction bits in IEEE 754 format
	 */
	private void calculateDecimal(String sign, String exp, String frac) {

	}

	/**
	 * Sends the private member fraction to calculateDouble and returns the
	 * private member doubleFracStr
	 * 
	 * @return string that contains the IEEE 754 64-bit binary number
	 */
	public String getDouble() {
		calculateDouble(fraction);
		return doubleFracStr;
	}

	/**
	 * Sends the private member fraction to calculateSingle and returns the
	 * private member singleFracStr
	 * 
	 * @return string that contains the IEEE 754 32-bit binary number
	 */
	public String getSingle() {
		calculateSingle(fraction);
		return singleFracStr;
	}

	/**
	 * Sends the private member fraction to calculateHalf and returns the
	 * private member halfFracStr
	 * 
	 * @return string that contains the IEEE 754 16-bit binary number
	 */
	public String getHalf() {
		calculateHalf(fraction);
		return halfFracStr;
	}

	// Includes Peter's code changes, but uses constants to work for
	// singles,doubles and half. Also the function returns a String
	/**
	 * Splits number into two parts: intPart and fractionPart, and converts to
	 * IEEE 754 format
	 * 
	 * @param number
	 *            BigDecimal number that the user enters
	 * @param fracStr
	 *            String that will hold the entire converted value
	 * @param bias
	 *            integer that represents the bias which differs for each level
	 *            of precision
	 * @param bits
	 *            integer that represents the number of fraction bits and
	 *            differs based on precision level
	 * @param expBits
	 *            integer that represents the number of exponent bits and
	 *            differs based on precision level
	 * @return String that contains the full converted value
	 * 
	 */
	private String helpFunction(BigDecimal number, String fracStr, int bias,
			int bits, int expBits) {
		intPart = number.setScale(0, BigDecimal.ROUND_DOWN);
		// System.out.println("The intPart of the number: " + intPart);
		boolean zero = false;

		if (intPart.compareTo(BigDecimal.ZERO) == 0) {
			zero = true;
		}

		fractionPart = number.subtract(intPart);
		// System.out.println("The fractionPart of the number: "
		// + number.subtract(intPart));
		String localStr = "";

		if (!zero) {

			for (; intPart.compareTo(BigDecimal.ZERO) > 0; intPart = intPart
					.divide(BigDecimal.valueOf(2), BigDecimal.ROUND_DOWN)) {
				localStr = (((intPart.remainder(BigDecimal.valueOf(2))
						.compareTo(BigDecimal.ZERO) == 0)) ? "0" : "1")
						+ localStr;
			}

			localStr += ".";
			for (int fracBits = bits; fracBits > 0; fracBits--) {
				BigDecimal localNum = ((fractionPart.multiply(BigDecimal
						.valueOf(2))).setScale(0, BigDecimal.ROUND_DOWN));
				fractionPart = (fractionPart.multiply(BigDecimal.valueOf(2))
						.subtract(localNum));
				localStr += localNum;
			}

			for (exponent = 0; localStr.charAt(exponent) != '.'; exponent++)
				;

			if (exponent != 0) {
				fracStr = localStr.substring(1, exponent)
						+ localStr.substring(exponent + 1, localStr.length());
			}
			// Rounding implementation
			if (fracStr.length() > bits + 1)
				fracStr = fracStr.substring(0, bits + 1);
			int j = 0;
			if (fracStr.charAt(fracStr.length() - 1) == '1') {
				for (int i = fracStr.length() - 1; i > 0; i--, j++) {
					if (fracStr.charAt(i) == '0') {
						fracStr = fracStr.substring(0, i) + '1';
						break;
					} else {
						fracStr = fracStr.substring(0, i);
					}
				}
			}
			for (; j > 0; j--)
				fracStr += '0';
			// End of rounding
			
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
			for (int fracBits = bits + 2; fracBits > 0; fracBits--) {
				BigDecimal localNum = ((fractionPart.multiply(BigDecimal
						.valueOf(2))).setScale(0, BigDecimal.ROUND_DOWN));
				fractionPart = (fractionPart.multiply(BigDecimal.valueOf(2))
						.subtract(localNum));
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

			// Rounding implementation
			if (localFrac.length() > bits + 1)
				localFrac = localFrac.substring(0, bits + 1);
			int j = 0;
			if (localFrac.charAt(localFrac.length() - 1) == '1') {
				for (int i = localFrac.length() - 1; i > 0; i--, j++) {
					if (localFrac.charAt(i) == '0') {
						localFrac = localFrac.substring(0, i) + '1';
						break;
					} else {
						localFrac = localFrac.substring(0, i);
					}
				}
			}
			for (; j > 0; j--)
				localFrac += '0';
			// End of Rounding

			for (int i = 0; i < exponent; i++) {
				localFrac = localFrac + "0";
			}

			// Clips fractional string localFrac if it is too many bits
			if (localFrac.length() > bits)
				localFrac = localFrac.substring(0, bits);

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

	/**
	 * Sends the user's number, the correct string, bias, number of fractional
	 * bits, and number of exponent bits to helpFunction in order to calculate
	 * the double precision IEEE 754 format fractional number and modify the
	 * respective string
	 * 
	 * @param number
	 *            a BigDecimal number in base 10
	 */
	private void calculateDouble(BigDecimal number) {
		doubleFracStr = helpFunction(number, doubleFracStr, DOUBLE_BIAS,
				DOUB_FRAC_BITS, DOUB_EXP_BITS);
	}

	/**
	 * Sends the user's number, the correct string, bias, number of fractional
	 * bits, and number of exponent bits to helpFunction in order to calculate
	 * the single precision IEEE 754 format fractional number and modify the
	 * respective string
	 * 
	 * @param number
	 *            a BigDecimal number in base 10
	 */
	private void calculateSingle(BigDecimal number) {
		singleFracStr = helpFunction(number, singleFracStr, SINGLE_BIAS,
				SING_FRAC_BITS, SING_EXP_BITS);
	}

	/**
	 * Sends the user's number, the correct string, bias,number of fractional
	 * bits, and number of exponent bits to helpFunction in order to calculate
	 * the half precision IEEE 754 format fractional number and modify the
	 * respective string
	 * 
	 * @param number
	 *            a BigDecimal number in base 10
	 */
	private void calculateHalf(BigDecimal number) {
		halfFracStr = helpFunction(number, halfFracStr, HALF_BIAS,
				HALF_FRAC_BITS, HALF_EXP_BITS);
	}
}
