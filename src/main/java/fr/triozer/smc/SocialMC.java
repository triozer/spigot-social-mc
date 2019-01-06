package fr.triozer.smc;

import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.api.database.Database;
import fr.triozer.smc.api.integration.IntegrationManager;
import fr.triozer.smc.api.message.MessageManager;
import fr.triozer.smc.api.ui.builder.InventoryBuilder;
import fr.triozer.smc.api.utils.Console;
import fr.triozer.smc.command.SocialCommand;
import fr.triozer.smc.listener.CacheListener;
import fr.triozer.smc.listener.InventoryListener;
import fr.triozer.smc.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Cédric / Triozer
 */
public class SocialMC extends JavaPlugin {

	public static final String     PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "SocialMC" + ChatColor.DARK_GRAY + "] ";
	public static       Console    LOG    = new Console("[SocialMC]");
	public static       SocialInfo SERVER_INFO;

	private static SocialMC               instance;
	private static IntegrationManager     integrationManager;
	private static MessageManager         messageManager;
	private        List<InventoryBuilder> inventories;
	private        Database               database;

	@Override
	public void onEnable() {
		instance = this;

		saveDefaultConfig();
		new File(getDataFolder(), "data").mkdir();
		integrationManager = new IntegrationManager();
		messageManager = new MessageManager(new File(getDataFolder(), "messages.yml"));
		this.inventories = new ArrayList<>();

		if (Settings.isMySQL()) {
			String host     = getConfig().getString("options.mysql.host");
			int    port     = getConfig().getInt("options.mysql.port");
			String database = getConfig().getString("options.mysql.database");
			this.database = new Database(host, port, database)
					.auth(getConfig().getString("options.mysql.user"),
							getConfig().getString("options.mysql.password"),
							(db) -> Bukkit.getScheduler().runTaskTimer(this, () ->
											SocialInfo.getPlayers().values()
													.stream().filter(info -> !info.getName().equals("server"))
													.forEach(((info) -> info.getSocials()
															.stream().filter((integration -> db.isCertified(info, integration)))
															.forEach(integration -> info.setCertified(integration, true)))),
									1L, getConfig().getInt("options.mysql.update") * 20L));
		} else if (!Settings.isMySQL() && Settings.isCertifiable())
			LOG.error("	[!] Tu dois utiliser MySQL pour certifier les comptes de tes joueurs.");

		SERVER_INFO = SocialInfo.get();

		registerListeners();
		getCommand("social").setExecutor(new SocialCommand());
	}

	@Override
	public void onDisable() {
		Set<HumanEntity> openers = new HashSet<>();

		for (InventoryBuilder inventory : this.inventories) openers.addAll(inventory.build().getViewers());
		for (HumanEntity opener : openers) opener.closeInventory();
	}

	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new CacheListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
	}

	public static SocialMC getInstance() {
		return instance;
	}

	public static IntegrationManager getIntegrationManager() {
		return integrationManager;
	}

	public static MessageManager getMessageManager() {
		return messageManager;
	}

	public final Database getDatabase() {
		return this.database;
	}

	public final List<InventoryBuilder> getInventories() {
		return this.inventories;
	}

}
