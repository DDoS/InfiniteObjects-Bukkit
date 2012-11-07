/*
 * This file is part of InfObjects.
 *
 * Copyright (c) 2012, SpoutDev <http://www.spout.org/>
 * InfObjects is licensed under the SpoutDev License Version 1.
 *
 * InfObjects is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * InfObjects is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.infobjects.value;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.congrace.exp4j.constant.Constants;
import de.congrace.exp4j.function.Functions;

public class ValueParser {
	private static final String RANDOM_INT_VALUE_REGEX = "ranI\\=.*";
	private static final String RANDOM_DOUBLE_VALUE_REGEX = "ranF\\=.*";
	private static final String RANDOM_MATH_EXP_VALUE_REGEX = ".*ran[IF]\\(.*";

	public static Value parse(String expression) {
		expression = expression.trim();
		if (expression.matches(RANDOM_INT_VALUE_REGEX)) {
			return new RandomIntValue(expression);
		} else if (expression.matches(RANDOM_DOUBLE_VALUE_REGEX)) {
			return new RandomDoubleValue(expression);
		} else if (expression.matches(RANDOM_MATH_EXP_VALUE_REGEX)) {
			if (hasVariable(expression)) {
				try {
					return new VariableMathExpressionValue(expression);
				} catch (Exception ex) {
					ex.printStackTrace();
					return null;
				}
			}
			try {
				return new MathExpressionValue(expression);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		try {
			return new DoubleValue(Double.parseDouble(expression));
		} catch (NumberFormatException nfe) {
			try {
				return new DoubleValue(expression);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}

	private static boolean hasVariable(String expression) {
		final Matcher matcher =
				VariableMathExpressionValue.VARIABLE_PATTERN.matcher(expression);
		while (matcher.find()) {
			final String find = matcher.group();
			if (!Functions.isFunction(find)
					&& !Constants.isConstant(find)) {
				return true;
			}
		}
		return false;
	}
}