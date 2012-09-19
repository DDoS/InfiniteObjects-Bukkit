/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.infiniteobjects.material;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialPickers {
	private static final Map<String, Constructor<? extends MaterialPicker>> PICKERS =
			new HashMap<String, Constructor<? extends MaterialPicker>>();

	static {
		register("innerouter", InnerOuterPicker.class);
		register("randominnerouter", RandomInnerOuterPicker.class);
		register("randomuniform", RandomUniformPicker.class);
	}

	public static MaterialPicker get(String name, String type) {
		try {
			return PICKERS.get(type.toLowerCase()).newInstance(name);
		} catch (Exception ex) {
			return null;
		}
	}

	public static void register(String type, Class<? extends MaterialPicker> picker) {
		try {
			PICKERS.put(type, picker.getConstructor(String.class));
		} catch (Exception ex) {
			Logger.getLogger(MaterialPickers.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
