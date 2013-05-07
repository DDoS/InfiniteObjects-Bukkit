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

/**
 * A simple mutable 3D integer vector.
 */
public class IntVector3 {
	private int x;
	private int y;
	private int z;

	/**
	 * Construct a new 3D integer vector with all components at zero.
	 */
	public IntVector3() {
		this(0, 0, 0);
	}

	/**
	 * Construct a new 3D integer vector with the specified component values.
	 *
	 * @param x The x value
	 * @param y The y value
	 * @param z The z value
	 */
	public IntVector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the value of the x component
	 *
	 * @return The value of the x component
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the value of the y component
	 *
	 * @return The value of the y component
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the value of the z component
	 *
	 * @return The value of the z component
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets the value of the x component.
	 *
	 * @param x The value to set for x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the value of the y component.
	 *
	 * @param y The value to set for y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the value of the z component.
	 *
	 * @param z The value to set for z
	 */
	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * Sets the value of the x, y and z components
	 *
	 * @param x The value to set for x
	 * @param y The value to set for y
	 * @param z The value to set for z
	 */
	public void set(int x, int y, int z) {
		setX(x);
		setY(y);
		setZ(z);
	}
}
