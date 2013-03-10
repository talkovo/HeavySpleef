/**
 *   HeavySpleef - The simple spleef plugin for bukkit
 *   
 *   Copyright (C) 2013 matzefratze123
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.matzefratze123.heavyspleef.command;

import me.matzefratze123.heavyspleef.core.Game;
import me.matzefratze123.heavyspleef.core.GameManager;
import me.matzefratze123.heavyspleef.core.LoseCause;
import me.matzefratze123.heavyspleef.utility.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKick extends HSCommand {

	public CommandKick() {
		setMinArgs(1);
		setOnlyIngame(true);
		setPermission(Permissions.KICK.getPerm());
		setUsage("/spleef kick <Player> [Reason]");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player)sender;
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			player.sendMessage(_("playerNotOnline"));
			return;
		}
		
		if (!GameManager.isInAnyGame(target)) {
			player.sendMessage(_("playerIsntInAnyGame"));
			return;
		}
		
		String reasonMessage = args.length > 1 ? " for " : "";
		StringBuilder reasonBuilder = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			reasonBuilder.append(args[i]).append(" ");
		reasonMessage += reasonBuilder.toString();
		
		Game game = GameManager.getGameFromPlayer(target);
		game.removePlayer(target, LoseCause.KICK);
		target.teleport(game.getLosePoint());
		target.sendMessage(_("kickedOfToPlayer", player.getName(), reasonMessage));
		player.sendMessage(_("kickedOfToKicker", target.getName(), game.getName(), reasonMessage));
	}

}