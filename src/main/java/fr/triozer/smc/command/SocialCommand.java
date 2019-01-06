package fr.triozer.smc.command;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.gui.SocialGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Cédric / Triozer
 */
public class SocialCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

		Player player = (Player) commandSender;

		if (args.length == 0) {
			player.openInventory(SocialGUI.of(player, SocialMC.SERVER_INFO, player.hasPermission(SocialMC.SERVER_INFO.getEditPermission())).build());
		} else {
			if (args[0].equalsIgnoreCase("me")) {
				player.openInventory(SocialGUI.of(player, SocialInfo.get(player.getUniqueId()), true).build());
				return true;
			} else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("social-mc.reload")) {
				SocialMC.getMessageManager().reload();
				return true;
			}
			Player who = Bukkit.getPlayerExact(args[0]);
			if (who == null) {
				player.sendMessage(SocialMC.getMessageManager().parse("message.unknown.player", args[0]));
				return true;
			}
			SocialInfo info = SocialInfo.get(player.getUniqueId());
			player.openInventory(SocialGUI.of(player, info, player.hasPermission(info.getEditPermission())).build());
		}

		return false;
	}

}
