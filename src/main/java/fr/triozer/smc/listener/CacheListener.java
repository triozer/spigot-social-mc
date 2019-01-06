package fr.triozer.smc.listener;

import fr.triozer.smc.api.SocialInfo;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Cédric / Triozer
 */
public class CacheListener implements Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		SocialInfo.getPlayers().remove(event.getPlayer().getUniqueId());
	}

}
