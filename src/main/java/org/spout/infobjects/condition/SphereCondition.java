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
package org.spout.infobjects.condition;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

import org.spout.infobjects.IWGO;
import org.spout.infobjects.exception.ConditionLoadingException;
import org.spout.infobjects.util.RandomOwner;
import org.spout.infobjects.value.Value;

/**
 * An implementation of {@link ShapeCondition}. This condition will check spherical volumes.
 */
public class SphereCondition extends ShapeCondition {
	private Value radiusX;
	private Value radiusY;
	private Value radiusZ;

	static {
		Condition.register("sphere", SphereCondition.class);
	}

	/**
	 * Constructs a new sphere condition from the parent iWGO.
	 *
	 * @param iwgo The parent iWGO
	 */
	public SphereCondition(IWGO iwgo) {
		super(iwgo);
	}

	/**
	 * Sets the size of the sphere volume. Expected size values are radiusX, radiusY, and radiusZ.
	 *
	 * @param sizes The sizes mapped as name and value
	 * @throws ConditionLoadingException If any of the expect values are missing
	 */
	@Override
	public void setSize(Map<String, Value> sizes) throws ConditionLoadingException {
		if (!sizes.containsKey("radiusX")) {
			throw new ConditionLoadingException("radiusX size is missing");
		}
		if (!sizes.containsKey("radiusY")) {
			throw new ConditionLoadingException("radiusY size is missing");
		}
		if (!sizes.containsKey("radiusZ")) {
			throw new ConditionLoadingException("radiusZ size is missing");
		}
		radiusX = sizes.get("radiusX");
		radiusY = sizes.get("radiusY");
		radiusZ = sizes.get("radiusZ");
	}

	/**
	 * Checks the sphere volume defined from the position and the radiuses.
	 *
	 * @return True if successful, false if not
	 */
	@Override
	public boolean check() {
		final int px = (int) getX().getValue();
		final int py = (int) getY().getValue();
		final int pz = (int) getZ().getValue();
		final double rx = radiusX.getValue() + 0.5;
		final double ry = radiusY.getValue() + 0.5;
		final double rz = radiusZ.getValue() + 0.5;
		final double invRadiusX = 1 / rx;
		final double invRadiusY = 1 / ry;
		final double invRadiusZ = 1 / rz;
		final int ceilRadiusX = (int) Math.ceil(rx);
		final int ceilRadiusY = (int) Math.ceil(ry);
		final int ceilRadiusZ = (int) Math.ceil(rz);
		final IWGO iwgo = getIWGO();
		final ConditionMode mode = getMode();
		final Set<Material> materials = getMaterials();
		double nextXn = 0;
		forX:
		for (int xx = 0; xx <= ceilRadiusX; xx++) {
			final double xn = nextXn;
			nextXn = (xx + 1) * invRadiusX;
			double nextYn = 0;
			forY:
			for (int yy = 0; yy <= ceilRadiusY; yy++) {
				final double yn = nextYn;
				nextYn = (yy + 1) * invRadiusY;
				double nextZn = 0;
				forZ:
				for (int zz = 0; zz <= ceilRadiusZ; zz++) {
					final double zn = nextZn;
					nextZn = (zz + 1) * invRadiusZ;
					if (xn * xn + yn * yn + zn * zn > 1) {
						if (zz == 0) {
							if (yy == 0) {
								break forX;
							}
							break forY;
						}
						break forZ;
					}
					final Location pos0 = iwgo.transform(px + xx, py + yy, pz + zz);
					if (!mode.check(pos0.getWorld().getBlockAt(pos0.getBlockX(),
							pos0.getBlockY(), pos0.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos1 = iwgo.transform(px - xx, py + yy, pz + zz);
					if (!mode.check(pos1.getWorld().getBlockAt(pos1.getBlockX(),
							pos1.getBlockY(), pos1.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos2 = iwgo.transform(px + xx, py - yy, pz + zz);
					if (!mode.check(pos2.getWorld().getBlockAt(pos2.getBlockX(),
							pos2.getBlockY(), pos2.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos3 = iwgo.transform(px + xx, py + yy, pz - zz);
					if (!mode.check(pos3.getWorld().getBlockAt(pos3.getBlockX(),
							pos3.getBlockY(), pos3.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos4 = iwgo.transform(px - xx, py - yy, pz + zz);
					if (!mode.check(pos4.getWorld().getBlockAt(pos4.getBlockX(),
							pos4.getBlockY(), pos4.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos5 = iwgo.transform(px + xx, py - yy, pz - zz);
					if (!mode.check(pos5.getWorld().getBlockAt(pos5.getBlockX(),
							pos5.getBlockY(), pos5.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos6 = iwgo.transform(px - xx, py + yy, pz - zz);
					if (!mode.check(pos6.getWorld().getBlockAt(pos6.getBlockX(),
							pos6.getBlockY(), pos6.getBlockZ()).getType(), materials)) {
						return false;
					}
					final Location pos7 = iwgo.transform(px - xx, py - yy, pz - zz);
					if (!mode.check(pos7.getWorld().getBlockAt(pos7.getBlockX(),
							pos7.getBlockY(), pos7.getBlockZ()).getType(), materials)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Randomizes the x, y and z position and radiuses of the sphere. The radiuses will only change
	 * if they are randomizable.
	 */
	@Override
	public void randomize() {
		super.randomize();
		radiusX.calculate();
		radiusY.calculate();
		radiusZ.calculate();
	}

	/**
	 * Sets the randoms of the position and radius values to the provided one if they implement
	 * {@link org.spout.infobjects.util.RandomOwner}.
	 *
	 * @param random The random to set
	 */
	@Override
	public void setRandom(Random random) {
		super.setRandom(random);
		if (radiusX instanceof RandomOwner) {
			((RandomOwner) radiusX).setRandom(random);
		}
		if (radiusY instanceof RandomOwner) {
			((RandomOwner) radiusY).setRandom(random);
		}
		if (radiusZ instanceof RandomOwner) {
			((RandomOwner) radiusZ).setRandom(random);
		}
	}

	/**
	 * Returns a string representation of this condition.
	 *
	 * @return The string representation of the condition
	 */
	@Override
	public String toString() {
		return "SphereCondition{x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", materials="
				+ getMaterials() + ", radiusX=" + radiusX + ", radiusY=" + radiusY
				+ ", radiusZ=" + radiusZ + ", mode=" + getMode() + '}';
	}
}
