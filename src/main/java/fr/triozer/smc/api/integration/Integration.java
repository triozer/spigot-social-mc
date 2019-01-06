package fr.triozer.smc.api.integration;

import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.gui.Head;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;

/**
 * @author Cédric / Triozer
 */
public class Integration {

	private final String       id;
	private final String       name;
	private final String       url;
	private final Head         head;
	private final List<String> description;

	private boolean active;
	private boolean serverOnly;

	private Integration(String id, String name, String url, String base64, List<String> description, boolean active, boolean serverOnly) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.active = active;
		this.serverOnly = serverOnly;
		this.description = description;
		for (int i = 0; i < this.description.size(); i++)
			this.description.set(i, ChatColor.translateAlternateColorCodes('&', this.description.get(i)));

		this.head = new Head(this, base64);
	}

	public String parse(String from) {
		return this.getURL().replaceAll("\\{from}", from);
	}

	public String parse(SocialInfo info) {
		return this.parse(info.getSocialName(this));
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return ChatColor.translateAlternateColorCodes('&', this.name);
	}

	public String getURL() {
		String textUrl = this.url;
		if (!textUrl.endsWith("/"))
			textUrl += '/';
		return textUrl;
	}

	public List<String> getDescription() {
		return this.description;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isServerOnly() {
		return this.serverOnly;
	}

	public void setServerOnly(boolean serverOnly) {
		this.serverOnly = serverOnly;
	}

	public Head getHead() {
		return this.head;
	}

	public ConfigurationSection toYAML(SocialInfo info) {
		if (!info.getSocials().contains(this)) return null;
		ConfigurationSection section = new MemoryConfiguration();
		section.set("social-name", info.getSocialName(this));
		section.set("certified", info.isCertified(this));
		section.set("token", info.getToken(this));
		section.set("url", info.getURL(this));
		return section;
	}

	public static class Builder {

		private String       name;
		private String       url;
		private String       id;
		private String       base64;
		private List<String> description;
		private boolean      active;
		private boolean      serverOnly;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public Builder base64(String base64) {
			this.base64 = base64;
			return this;
		}

		public Builder active(boolean active) {
			this.active = active;
			return this;
		}

		public Builder serverOnly(boolean serverOnly) {
			this.serverOnly = serverOnly;
			return this;
		}

		public Builder description(List<String> lines) {
			this.description = lines;
			return this;
		}

		public Integration build() {
			return new Integration(this.id, this.name, this.url, this.base64, this.description, this.active, this.serverOnly);
		}

	}
}
