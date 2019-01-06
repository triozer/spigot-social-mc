package fr.triozer.smc.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.api.integration.Integration;
import fr.triozer.smc.api.ui.ClickableItem;
import fr.triozer.smc.api.utils.TextBuilder;
import fr.triozer.smc.utils.TextRequest;
import fr.triozer.smc.api.utils.lang.ServerPackage;
import fr.triozer.smc.utils.Settings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Cédric / Triozer
 */
public class Head {

	private final Integration integration;
	private final String      base64;
	private       ItemStack   head;

	public Head(Integration integration, String base64) {
		this.integration = integration;
		this.base64 = base64;

		this.head = null;
		if (ServerPackage.getServerVersionNumber() < 13) {
			this.head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
		} else {
			this.head = new ItemStack(Material.PLAYER_HEAD, 1);
		}
		SkullMeta meta = (SkullMeta) this.head.getItemMeta();
		Field     profileField;
		try {
			profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, getFakeProfile());
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		meta.setDisplayName(ChatColor.RESET + integration.getName());
		List<String> lore = new ArrayList<>();
		integration.getDescription().forEach(line -> lore.add(ChatColor.RESET + line));
		lore.add("");
		lore.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + integration.getURL());
		meta.setLore(lore);
		this.head.setItemMeta(meta);
	}

	private GameProfile getFakeProfile() {
		GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), null);
		fakeProfile.getProperties().put("textures", new Property("textures", this.base64));
		return fakeProfile;
	}

	public final ClickableItem to(Consumer<InventoryClickEvent> event) {
		return ClickableItem.of(this.head, event);
	}

	public ClickableItem from(Player who, SocialInfo socialInfo) {
		ItemStack    from     = this.head.clone();
		ItemMeta     itemMeta = from.getItemMeta();
		List<String> list     = new ArrayList<>();
		itemMeta.setDisplayName(itemMeta.getDisplayName() + " " + ChatColor.DARK_GRAY);
		if (Settings.isCertifiable()) {
			String is;
			if (socialInfo.isCertified(this.integration)) is = SocialMC.getMessageManager().get("head.certified");
			else is = SocialMC.getMessageManager().get("head.non-certified");
			itemMeta.setDisplayName(itemMeta.getDisplayName() + " " + ChatColor.DARK_GRAY + "(" + is + "" + ChatColor.DARK_GRAY + ")");
		}
		list.add("");
		list.add(SocialMC.getMessageManager().get("head.open"));
		if (canEdit(who, socialInfo)) list.add(SocialMC.getMessageManager().get("head.change"));
		if (canClick(socialInfo))
			list.addAll(Arrays.asList("", ChatColor.GRAY.toString() + ChatColor.ITALIC + socialInfo.getURL(this.integration)));
		else
			list.addAll(Arrays.asList("", ChatColor.GRAY.toString() + ChatColor.ITALIC + socialInfo.getSocialName(this.integration)));
		if (Settings.isCertifiable() && !socialInfo.isCertified(this.integration))
			list.addAll(Arrays.asList("", ChatColor.RESET.toString() + ChatColor.GRAY + "Token: " + ChatColor.YELLOW + socialInfo.getToken(this.integration)));
		itemMeta.setLore(list);
		from.setItemMeta(itemMeta);
		return ClickableItem.of(from, event -> {
			Player player = (Player) event.getWhoClicked();

			if (canEdit(player, socialInfo) && event.getClick().isLeftClick()) {
				new TextRequest(player, socialInfo, this.integration);
			} else if (event.getClick().isRightClick()) {
				TextComponent prefix = new TextComponent(SocialMC.PREFIX);
				String text = SocialMC.getMessageManager()
						.parse("message.head.open-link", this.integration.getName(), socialInfo.getName());
				TextBuilder message;
				if (canClick(socialInfo)) {
					message = new TextBuilder(text)
							.click(ClickEvent.Action.OPEN_URL, socialInfo.getURL(this.integration))
							.hove(HoverEvent.Action.SHOW_TEXT, SocialMC.getMessageManager().parse("message.head.hove-link", socialInfo.getURL(this.integration)));
				} else {
					message = new TextBuilder(SocialMC.getMessageManager()
							.parse("message.head.display-name", this.integration.getName(), socialInfo.getName(), socialInfo.getSocialName(this.integration)));
				}
				prefix.addExtra(message.build());
				player.spigot().sendMessage(prefix);
				Bukkit.getScheduler().runTaskLaterAsynchronously(SocialMC.getInstance(), player::closeInventory, 5L);
			}

		});
	}

	private boolean canEdit(Player who, SocialInfo info) {
		return who.getUniqueId() == info.getUniqueId() || who.hasPermission(info.getEditPermission());
	}

	private boolean canClick(SocialInfo info) {
		return !this.integration.isServerOnly() || info == SocialMC.SERVER_INFO;
	}

	public final String getBase64() {
		return this.base64;
	}

	public final ItemStack getHead() {
		return this.head;
	}

}
