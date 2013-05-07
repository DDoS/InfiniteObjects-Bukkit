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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Commands for the InfiniteObjects plugin. These are mostly designed for testing and
 * administration.
 */
public class IWGOCommands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final String cmd = command.getName();
		if (cmd.equals("reloadiwgos")) {
			final IWGOManager manager = InfObjects.getIWGOManager();
			manager.reloadIWGOs();
			tell(sender, manager.getIWGOs().size() + " iWGO(s) reloaded.");
			return true;
		} else if (cmd.equals("iwgo")) {
			if (!(sender instanceof Player)) {
				warn(sender, "You need to be a player to use this command.");
				return true;
			}
			if (args.length < 1) {
				return false;
			}
			final IWGO iwgo = InfObjects.getIWGOManager().getIWGO(args[0]);
			if (iwgo == null) {
				warn(sender, "Couldn't find an iWGO named \"" + args[0] + "\"");
				return true;
			}
			final boolean force;
			if (args.length > 1 && args[1].equalsIgnoreCase("true")) {
				force = true;
			} else {
				force = false;
			}
			final Player player = (Player) sender;
			final Location location = player.getLocation();
			final World world = location.getWorld();
			final int x = location.getBlockX();
			final int y = location.getBlockY();
			final int z = location.getBlockZ();
			if (force || iwgo.canPlaceObject(world, x, y, z)) {
				iwgo.placeObject(world, x, y, z);
				tell(sender, "iWGO placed successfully.");
			} else {
				warn(sender, "Can't place the iWGO here.");
			}
		}
		return false;
	}

	private static void tell(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.GOLD + "[InfObjects] " + ChatColor.WHITE + msg);
	}

	private static void warn(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.GOLD + "[InfObjects] " + ChatColor.DARK_RED + msg);
	}
}
