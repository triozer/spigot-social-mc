package fr.triozer.smc.api.message;

import com.google.common.base.Charsets;
import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.utils.AbstractManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Cédric / Triozer
 */
public class MessageManager implements AbstractManager<String, String> {

	private final File              file;
	private       YamlConfiguration configuration;

	public MessageManager(File file) {
		this.file = file;
		SocialMC.getInstance().saveResource("messages.yml", false);
		this.configuration = YamlConfiguration.loadConfiguration(file);
	}

	@Override
	public String get(String key) {
		return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(key));
	}

	public String parse(String key, String... args) {
		String text = this.get(key);
		for (int i = 0; i < args.length; i++) text = text.replaceAll("\\{" + i + "}", args[i]);
		return text;
	}

	public List<String> getList(String key) {
		List<String> list = this.configuration.getStringList(key);
		for (int i = 0; i < list.size(); i++)
			list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
		return list;
	}

	@Override
	public void add(String value) {
	}

	public void add(String key, String value) {
		this.configuration.set(key, value.replaceAll("§", "&"));
	}

	@Override
	public void remove(String key) {
		this.configuration.set(key, null);
	}

	public void reload() {
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
		InputStream defaultConfiguration = SocialMC.getInstance().getResource("messages.yml");
		if (defaultConfiguration != null) {
			this.configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfiguration, Charsets.UTF_8)));
		}
	}

	@Override
	public Stream<String> values() {
		return this.configuration.getKeys(false).stream();
	}

	public final File getFile() {
		return this.file;
	}

	public final YamlConfiguration getConfiguration() {
		return this.configuration;
	}

}
