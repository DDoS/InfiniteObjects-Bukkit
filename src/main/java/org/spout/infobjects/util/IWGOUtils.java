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
package org.spout.infobjects.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Utility methods for InfinteObjects.
 */
public class IWGOUtils {
	/**
	 * Gets the next integer from the provided random between the two bounds, inclusively.
	 *
	 * @param random The random to use
	 * @param min The lower bound
	 * @param max The higher bound
	 * @return An integer in the [min, max] range
	 */
	public static int nextInt(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * Gets the next double from the provided random between the two bounds, exclusively for the
	 * higher bound.
	 *
	 * @param random The random to use
	 * @param min The lower bound
	 * @param max The higher bound
	 * @return An integer in the [min, max[ range
	 */
	public static double nextDouble(Random random, double min, double max) {
		return random.nextDouble() * (max - min) + min;
	}

	/**
	 * Converts the children of the node into a string, string map. The resulting map has all of the
	 * children full, "." separated, paths associated with all of the values.
	 *
	 * <pre>
	 * a:
	 *     b: 1
	 *     c:
	 *         d: 2
	 *         e: 5
	 * f:
	 *     g: 9
	 *     h: 7
	 * i: 10
	 * </pre>
	 *
	 * returns: a.b=1, a.c.d=2, a.c.e=5, f.g=9, f.h=7 and i=10
	 *
	 * @param node The node to convert
	 * @return The node's children as a string, string map
	 */
	public static Map<String, String> toStringMap(ConfigurationSection node) {
		final Map<String, String> propertiesMap = new HashMap<String, String>();
		for (String key : node.getKeys(true)) {
			if (!node.isConfigurationSection(key)) {
				propertiesMap.put(key, node.getString(key));
			}
		}
		return propertiesMap;
	}

	/**
	 * Attempts to get a block material from it's name. Throws an exception if this fails.
	 *
	 * @param name The name of the block material
	 * @return The block material
	 * @throws IllegalArgumentException If the name is null or an empty string, if the name isn't
	 * associated to a material
	 */
	public static Material tryGetBlockMaterial(String name) {
		if (name == null || name.trim().equals("")) {
			throw new IllegalArgumentException("Name can not be null or empty");
		}
		final Material material = Material.getMaterial(name.toUpperCase());
		if (material == null || !material.isBlock()) {
			throw new IllegalArgumentException("\"" + name + "\" is not a block material");
		}
		return material;
	}
}
