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

import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;

/**
 *
 * @author DDoS
 */
public class IWGOCommands {
	@Command(aliases = {"iwgo"}, usage = "<name>", flags = "f", desc = "Spawn a IWGO at your location. Use -f to ignore conditions check", min = 1, max = 2)
	@CommandPermissions("infobjects.place")
	public void iwgo(CommandContext args, CommandSource source) throws CommandException {
		if (!(source instanceof Player)) {
			throw new CommandException("You must be a player.");
		}
		final IWGO iwgo = InfObjectsPlugin.getIWGOManager().getIWGO(args.getString(0));
		if (iwgo == null) {
			throw new CommandException("Invalid IWGO name.");
		}
		final Player player = (Player) source;
		final Point loc = player.getTransform().getPosition();
		final World world = loc.getWorld();
		final int x = loc.getBlockX();
		final int y = loc.getBlockY();
		final int z = loc.getBlockZ();
		final boolean force = args.hasFlag('f');
		if (!iwgo.canPlaceObject(world, x, y, z)) {
			player.sendMessage("Couldn't place the IWGO.");
			if (!force) {
				return;
			}
			player.sendMessage("Forcing placement.");
		}
		iwgo.placeObject(world, x, y, z);
		iwgo.randomize();
	}

	@Command(aliases = {"reloadiwgos"}, desc = "Reload the IWGOs")
	@CommandPermissions("infobjects.reload")
	public void reloadIWGOs(CommandContext args, CommandSource source) throws CommandException {
		InfObjectsPlugin.getIWGOManager().reloadIWGOs();
		source.sendMessage("Reloaded " + InfObjectsPlugin.getIWGOManager().getIWGOMap().size() + " IWGO(s) successfully.");
	}
}
