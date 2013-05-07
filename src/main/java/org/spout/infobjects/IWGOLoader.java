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
package org.spout.infobjects;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import org.spout.infobjects.condition.Condition;
import org.spout.infobjects.exception.ConditionLoadingException;
import org.spout.infobjects.exception.IWGOLoadingException;
import org.spout.infobjects.exception.InstructionLoadingException;
import org.spout.infobjects.exception.MaterialSetterLoadingException;
import org.spout.infobjects.exception.ShapeLoadingException;
import org.spout.infobjects.exception.VariableLoadingException;
import org.spout.infobjects.instruction.BlockInstruction;
import org.spout.infobjects.instruction.Instruction;
import org.spout.infobjects.instruction.ShapeInstruction;
import org.spout.infobjects.instruction.RepeatInstruction;
import org.spout.infobjects.material.MaterialSetter;
import org.spout.infobjects.shape.Shape;
import org.spout.infobjects.util.IWGOUtils;
import org.spout.infobjects.value.IncrementableValue;
import org.spout.infobjects.value.ValueParser;
import org.spout.infobjects.variable.Variable;
import org.spout.infobjects.variable.VariableSource;

/**
 * A static class for loading iWGO from files and configurations.
 */
public class IWGOLoader {
	static {
		// Load all included resources so they can register themselves
		try {
			Class.forName("org.spout.infobjects.condition.CuboidCondition");
			Class.forName("org.spout.infobjects.instruction.ShapeInstruction");
			Class.forName("org.spout.infobjects.instruction.RepeatInstruction");
			Class.forName("org.spout.infobjects.instruction.BlockInstruction");
			Class.forName("org.spout.infobjects.material.InnerOuterSetter");
			Class.forName("org.spout.infobjects.material.RandomInnerOuterSetter");
			Class.forName("org.spout.infobjects.material.SimpleSetter");
			Class.forName("org.spout.infobjects.material.RandomSimpleSetter");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(IWGOLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private IWGOLoader() {
	}

	/**
	 * Attempts to load an iWGO from a file. This file must be a YAML configuration.
	 *
	 * @param file The YAML configuration file
	 * @return The loaded and ready to use iWGO
	 * @throws IWGOLoadingException If loading of the iWGO fails
	 */
	public static IWGO loadIWGO(File file) throws IWGOLoadingException {
		return loadIWGO(YamlConfiguration.loadConfiguration(file), file.getPath());
	}

	/**
	 * Attempts to load an iWGO from a configuration.
	 *
	 * @param config The configuration
	 * @return The loaded and ready to use iWGO
	 * @throws IWGOLoadingException If loading of the iWGO fails
	 */
	public static IWGO loadIWGO(Configuration config) throws IWGOLoadingException {
		return loadIWGO(config, null);
	}

	/**
	 * Attempts to load an iWGO from a configuration. This method accepts the source of the
	 * configuration for better debugging of iWGO loading failures. This source maybe a file path,
	 * plugin name or any other string representation of the source of the configuration.
	 *
	 * @param config The configuration
	 * @param source The source of the configuration. May be null
	 * @return The loaded and ready to use iWGO
	 * @throws IWGOLoadingException If loading of the iWGO fails
	 */
	public static IWGO loadIWGO(Configuration config, String source) throws IWGOLoadingException {
		try {
			final IWGO iwgo = new IWGO(config.getString("name"));
			loadVariables(iwgo, config.getConfigurationSection("variables"), iwgo);
			loadMaterialSetters(iwgo, config.getConfigurationSection("setters"));
			loadConditions(iwgo, config.getConfigurationSection("conditions"));
			loadInstructions(iwgo, config.getConfigurationSection("instructions"));
			iwgo.randomize();
			return iwgo;
		} catch (Exception ex) {
			throw new IWGOLoadingException(source != null ? source : "unknown source", ex);
		}
	}

	/**
	 * Loads the variables into the source.
	 *
	 * @param destination The source to load to
	 * @param variableNode The configuration node with the variable info
	 * @param sources The variable sources for the new variables (as these might depend on other
	 * already loaded variables)
	 * @throws VariableLoadingException If variable loading fails
	 */
	public static void loadVariables(VariableSource destination, ConfigurationSection variableNode,
			VariableSource... sources) throws VariableLoadingException {
		for (String key : variableNode.getKeys(false)) {
			try {
				destination.addVariable(new Variable(key, ValueParser.parse(variableNode.getString(key), sources)));
			} catch (Exception ex) {
				throw new VariableLoadingException(key, ex);
			}
		}
	}

	/**
	 * Loads the material setter to the iWGO.
	 *
	 * @param iwgo The iWGO to load to
	 * @param settersNode The configuration node with the material setter info
	 * @throws MaterialSetterLoadingException If material setter loading fails
	 */
	public static void loadMaterialSetters(IWGO iwgo, ConfigurationSection settersNode)
			throws MaterialSetterLoadingException {
		for (String key : settersNode.getKeys(false)) {
			try {
				final ConfigurationSection setterNode = settersNode.getConfigurationSection(key);
				final MaterialSetter setter =
						MaterialSetter.newMaterialSetter(setterNode.getString("type"), key);
				setter.configure(IWGOUtils.toStringMap(setterNode.getConfigurationSection("properties")));
				iwgo.addMaterialSetter(setter);
			} catch (Exception ex) {
				throw new MaterialSetterLoadingException(key, ex);
			}
		}
	}

	/**
	 * Loads the instructions to the iWGO.
	 *
	 * @param iwgo The iWGO to load to
	 * @param instructionsNode The configuration node with the instruction info
	 * @throws InstructionLoadingException If instruction loading fails
	 */
	public static void loadInstructions(IWGO iwgo, ConfigurationSection instructionsNode)
			throws InstructionLoadingException {
		for (String key : instructionsNode.getKeys(false)) {
			try {
				final ConfigurationSection instructionNode = instructionsNode.getConfigurationSection(key);
				final Instruction instruction =
						Instruction.newInstruction(instructionNode.getString("type"), iwgo, key);
				final ConfigurationSection variableNode = instructionNode.getConfigurationSection("variables");
				if (variableNode != null) {
					loadVariables(instruction, variableNode, iwgo, instruction);
				}
				if (instruction instanceof ShapeInstruction) {
					loadShapeInstruction((ShapeInstruction) instruction, instructionNode);
				} else if (instruction instanceof RepeatInstruction) {
					loadRepeatInstruction((RepeatInstruction) instruction, instructionNode);
				} else if (instruction instanceof BlockInstruction) {
					loadBlockInstruction((BlockInstruction) instruction, instructionNode);
				}
				iwgo.addInstruction(instruction);
			} catch (Exception ex) {
				throw new InstructionLoadingException(key, ex);
			}
		}
	}

	private static void loadShapeInstruction(ShapeInstruction instruction, ConfigurationSection placeNode)
			throws ShapeLoadingException {
		final IWGO iwgo = instruction.getIWGO();
		final ConfigurationSection shapesNode = placeNode.getConfigurationSection("shapes");
		for (String key : shapesNode.getKeys(false)) {
			try {
				final ConfigurationSection shapeNode = shapesNode.getConfigurationSection(key);
				final Shape shape = Shape.newShape(shapeNode.getString("type"), iwgo);
				shape.setSize(ValueParser.parse(IWGOUtils.toStringMap(shapeNode.getConfigurationSection("size")), iwgo, instruction));
				shape.setPosition(ValueParser.parse(IWGOUtils.toStringMap(shapeNode.getConfigurationSection("position")), iwgo, instruction));
				final MaterialSetter setter = iwgo.getMaterialSetter(shapeNode.getString("material"));
				if (setter == null) {
					throw new ShapeLoadingException("Material setter \"" + shapeNode.getString("material")
							+ "\" does not exist");
				}
				shape.setMaterialSetter(setter);
				instruction.addShape(shape);
			} catch (Exception ex) {
				throw new ShapeLoadingException(key, ex);
			}
		}
	}

	private static void loadRepeatInstruction(RepeatInstruction instruction, ConfigurationSection repeatNode)
			throws InstructionLoadingException {
		final IWGO iwgo = instruction.getIWGO();
		final Instruction repeat = iwgo.getInstruction(repeatNode.getString("repeat"));
		if (repeat == null) {
			throw new InstructionLoadingException("Repeat instruction \""
					+ repeatNode.getString("repeat") + "\" does not exist");
		}
		instruction.setRepeat(repeat);
		instruction.setTimes(ValueParser.parse(repeatNode.getString("times"), iwgo, instruction));
		final ConfigurationSection incrementNode = repeatNode.getConfigurationSection("increment");
		for (String key : incrementNode.getKeys(false)) {
			final Variable increment = iwgo.getVariable(key);
			if (increment == null) {
				throw new InstructionLoadingException("Increment variable \"" + key + "\" does not exist");
			}
			instruction.addIncrementableValue(key, new IncrementableValue(increment.getRawValue(),
					ValueParser.parse(incrementNode.getString(key), iwgo, instruction)));
		}
	}

	private static void loadBlockInstruction(BlockInstruction instruction, ConfigurationSection blockNode)
			throws InstructionLoadingException {
		final IWGO iwgo = instruction.getIWGO();
		final ConfigurationSection positionNode = blockNode.getConfigurationSection("position");
		instruction.setX(ValueParser.parse(positionNode.getString("x"), iwgo, instruction));
		instruction.setY(ValueParser.parse(positionNode.getString("y"), iwgo, instruction));
		instruction.setZ(ValueParser.parse(positionNode.getString("z"), iwgo, instruction));
		final MaterialSetter setter = iwgo.getMaterialSetter(blockNode.getString("material"));
		if (setter == null) {
			throw new InstructionLoadingException("Material setter \"" + blockNode.getString("material")
					+ "\" does not exist");
		}
		instruction.setMaterialSetter(setter);
		instruction.setOuter(Boolean.parseBoolean(blockNode.getString("outer")));
	}

	/**
	 * Loads the conditions to the iWGO.
	 *
	 * @param iwgo The iWGO to load to
	 * @param conditionsNode The configuration node with the condition info
	 * @throws ConditionLoadingException If condition loading fails
	 */
	public static void loadConditions(IWGO iwgo, ConfigurationSection conditionsNode)
			throws ConditionLoadingException {
		for (String key : conditionsNode.getKeys(false)) {
			try {
				final ConfigurationSection conditionNode = conditionsNode.getConfigurationSection(key);
				final Condition condition = Condition.newCondition(conditionNode.getString("shape"), iwgo);
				condition.setMode(Condition.ConditionMode.valueOf(conditionNode.getString("mode").toUpperCase()));
				condition.setSize(ValueParser.parse(IWGOUtils.toStringMap(conditionNode.getConfigurationSection("size")), iwgo));
				condition.setPosition(ValueParser.parse(IWGOUtils.toStringMap(conditionNode.getConfigurationSection("position")), iwgo));
				for (String name : conditionNode.getStringList("check")) {
					condition.addBlockMaterial(IWGOUtils.tryGetBlockMaterial(name));
				}
				iwgo.addCondition(condition);
			} catch (Exception ex) {
				throw new ConditionLoadingException(key, ex);
			}
		}
	}

	/**
	 * Logs iWGO loading exceptions to the Bukkit default logger (as defined by {@link org.bukkit.Bukkit#getLogger()})
	 * in a user friendly manner.
	 *
	 * @param exception The exception to log
	 */
	public static void logIWGOLoadingException(IWGOLoadingException exception) {
		logIWGOLoadingException(Bukkit.getLogger(), exception);
	}

	/**
	 * Logs iWGO loading exceptions to the specified logger in a user friendly manner.
	 *
	 * @param logger The logger to log to
	 * @param exception The exception to log
	 */
	public static void logIWGOLoadingException(Logger logger, IWGOLoadingException exception) {
		logger.log(Level.WARNING, "------------------------");
		logger.log(Level.WARNING, "| IWGO LOADING FAILURE |");
		logger.log(Level.WARNING, "------------------------");
		exception.printStackTrace();
		Throwable cause = exception;
		int tabCount = 0;
		do {
			String tabs = "";
			for (int i = 0; i < tabCount; i++) {
				tabs += "\t";
			}
			final String message = cause.getMessage();
			if (message != null) {
				logger.log(Level.WARNING, tabs + cause.getMessage());
			} else {
				logger.log(Level.WARNING, tabs + cause.getClass().getSimpleName());
			}
			tabCount++;
		} while ((cause = cause.getCause()) != null);
	}
}
