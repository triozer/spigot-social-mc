package fr.triozer.smc.api;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.integration.Integration;
import fr.triozer.smc.api.integration.IntegrationManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Cédric / Triozer
 */
public class SocialInfo {

	private static Map<UUID, SocialInfo> players = new HashMap<>();

	public static Map<UUID, SocialInfo> getPlayers() {
		return players;
	}

	private final UUID                 uniqueId;
	private final Map<String, String>  socials;
	private final Map<String, String>  tokens;
	private final Map<String, Boolean> certified;

	private YamlConfiguration data;
	private File              file;

	private SocialInfo(UUID uniqueId, boolean server) {
		this.uniqueId = uniqueId;
		this.socials = new HashMap<>();
		this.tokens = new HashMap<>();
		this.certified = new HashMap<>();

		if (server)
			this.file = new File(SocialMC.getInstance().getDataFolder(), "/data/server.yml");
		else
			this.file = new File(SocialMC.getInstance().getDataFolder() + "/data", this.uniqueId + ".yml");

		try {
			this.file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = YamlConfiguration.loadConfiguration(this.file);

		players.put(uniqueId, this);
	}

	private static void load(SocialInfo socialInfo) {
		if (socialInfo.getData() != null && !socialInfo.getData().getKeys(false).isEmpty()) {
			IntegrationManager integrationManager = SocialMC.getIntegrationManager();

			socialInfo.getData().getKeys(false).forEach(id -> {
				ConfigurationSection section = socialInfo.getData().getConfigurationSection(id);
				socialInfo.setCertified(integrationManager.get(id), section.getBoolean("certified", false));
				socialInfo.setIntegration(integrationManager.get(id), section.getString("social-name"));
				socialInfo.setToken(integrationManager.get(id), section.getString("token"));
			});
		}
	}

	private void setToken(Integration integration, String token) {
		this.tokens.put(integration.getId(), token);
	}

	public static SocialInfo get(UUID uniqueId) {
		if (players.containsKey(uniqueId)) return players.get(uniqueId);
		else {
			SocialInfo socialInfo = new SocialInfo(uniqueId, false);
			load(socialInfo);
			return socialInfo;
		}
	}

	public static SocialInfo get() {
		SocialInfo socialInfo = new SocialInfo(null, true);
		load(socialInfo);
		return socialInfo;
	}

	public String generateToken(Integration integration) {
		String token = UUID.randomUUID().toString().split("-")[0];
		setToken(integration, token);
		SocialMC.getInstance().getDatabase().add(this, integration);
		save();
		return token;
	}

	public void setIntegration(Integration integration, String name) {
		this.socials.put(integration.getId(), name);
		save();
	}

	public void removeIntegration(Integration integration) {
		this.socials.remove(integration.getId());
		this.certified.remove(integration.getId());
		save();
	}

	public void setCertified(Integration integration, boolean value) {
		this.certified.put(integration.getId(), value);
		save();
	}

	public void save() {
		this.getSocials().forEach(social -> this.data.set(social.getId(), social.toYAML(this)));

		try {
			this.data.save(this.file);
			this.data = YamlConfiguration.loadConfiguration(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isCertified(Integration integration) {
		return this.certified.containsKey(integration.getId()) && this.certified.get(integration.getId());
	}

	public String getName() {
		return this.uniqueId == null ? "server" : Bukkit.getPlayer(this.uniqueId).getDisplayName();
	}

	public String getEditPermission() {
		return this.uniqueId == null ? "social-mc.edit.server" : "social-mc.edit.other";
	}

	public String getSocialName(Integration integration) {
		return this.socials.get(integration.getId());
	}

	public String getURL(Integration integration) {
		return integration.parse(this.getSocialName(integration));
	}

	public String getToken(Integration integration) {
		return this.tokens.get(integration.getId());
	}

	public YamlConfiguration getData() {
		return this.data;
	}

	public List<Integration> getSocials() {
		List<Integration> integrations = new ArrayList<>();
		this.socials.keySet().forEach(id -> integrations.add(SocialMC.getIntegrationManager().get(id)));
		return integrations;
	}

	public UUID getUniqueId() {
		return this.uniqueId;
	}

}
