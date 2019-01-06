package fr.triozer.smc.api.integration;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.utils.AbstractManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Cédric / Triozer
 */
public class IntegrationManager implements AbstractManager<String, Integration> {

	private final Map<String, Integration> map;

	public IntegrationManager() {
		this.map = new HashMap<>();

		FileConfiguration config = SocialMC.getInstance().getConfig();

		config.getConfigurationSection("socials").getKeys(false).forEach(key -> {
			ConfigurationSection info = config.getConfigurationSection("socials." + key);
			Set<String>          keys = info.getKeys(false);

			if (keys.size() < 6) SocialMC.LOG.error("[IntegrationManager] " + key + " missing parameters.");
			else if (!keys.contains("name")) SocialMC.LOG.fine("[IntegrationManager] " + key + " missing name.");
			else if (!keys.contains("url")) SocialMC.LOG.fine("[IntegrationManager] " + key + " missing url.");
			else if (!keys.contains("base64")) SocialMC.LOG.fine("[IntegrationManager] " + key + " missing base64.");
			else if (!keys.contains("active")) SocialMC.LOG.fine("[IntegrationManager] " + key + " missing active.");
			else if (!keys.contains("server-only"))
				SocialMC.LOG.fine("[IntegrationManager] " + key + " missing server-only.");
			else if (!keys.contains("description"))
				SocialMC.LOG.fine("[IntegrationManager] " + key + " missing description.");
			else
				this.add(new Integration.Builder()
						.id(key)
						.name(info.getString("name"))
						.url(info.getString("url"))
						.base64(info.getString("base64"))
						.description(info.getStringList("description"))
						.active(info.getBoolean("active"))
						.serverOnly(info.getBoolean("server-only"))
						.build());
		});
	}

	@Override
	public Integration get(String key) {
		return this.map.get(key);
	}

	@Override
	public void add(Integration value) {
		SocialMC.LOG.fine("[IntegrationManager] + " + value.getName());
		this.map.put(value.getId(), value);
	}

	@Override
	public void remove(Integration value) {
		this.map.remove(value.getId());
	}

	@Override
	public Stream<Integration> values() {
		return this.map.values().stream();
	}

}
