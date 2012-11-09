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
package org.spout.infobjects.instruction;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.spout.infobjects.IWGO;
import org.spout.infobjects.util.IWGOUtils;
import org.spout.infobjects.value.Value;
import org.spout.infobjects.value.VariableMathExpressionValue;
import org.spout.infobjects.variable.Variable;
import org.spout.infobjects.variable.VariableSource;

public class Instruction implements VariableSource {
	private static final Map<String, Constructor<? extends Instruction>> INSTRUCTIONS =
			new HashMap<String, Constructor<? extends Instruction>>();
	private final IWGO parent;
	private final String name;
	private final Map<String, Variable> variables = new LinkedHashMap<String, Variable>();

	static {
		try {
			register("place", PlaceInstruction.class);
			//register("repeat", RepeatInstruction.class);
		} catch (Exception ex) {
			System.err.println("Failed to register the instructions");
			ex.printStackTrace();
		}
	}

	public Instruction(IWGO parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public IWGO getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public void calculateVariables() {
		IWGOUtils.calculateVariables(variables.values());
	}

	@Override
	public Variable getVariable(String name) {
		return variables.get(name);
	}

	@Override
	public Collection<Variable> getVariables() {
		return variables.values();
	}

	@Override
	public Map<String, Variable> getVariableMap() {
		return Collections.unmodifiableMap(variables);
	}

	@Override
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	@Override
	public void addVariable(Variable variable) {
		final Value value = variable.getRawValue();
		if (value instanceof VariableMathExpressionValue) {
			((VariableMathExpressionValue) value).addVariableSources(parent, this);
		}
		variables.put(variable.getName(), variable);
	}

	public static void register(String type, Class<? extends Instruction> instruction)
			throws NoSuchMethodException {
		INSTRUCTIONS.put(type, instruction.getConstructor(IWGO.class, String.class));
	}

	public static Instruction newInstruction(String type, IWGO parent, String name) {
		try {
			return INSTRUCTIONS.get(type).newInstance(parent, name);
		} catch (Exception ex) {
			return null;
		}
	}
}
