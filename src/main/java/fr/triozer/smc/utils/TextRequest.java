package fr.triozer.smc.utils;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.api.integration.Integration;
import fr.triozer.smc.api.ui.ClickableItem;
import fr.triozer.smc.api.ui.XMaterial;
import fr.triozer.smc.api.ui.builder.InventoryBuilder;
import fr.triozer.smc.api.ui.builder.ItemBuilder;
import fr.triozer.smc.utils.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author Cédric / Triozer
 */
public class TextRequest implements Listener {

	private final Player      player;
	private final SocialInfo  socialInfo;
	private final Integration integration;

	private String text;

	public TextRequest(Player player, SocialInfo socialInfo, Integration integration) {
		this.player = player;
		this.socialInfo = socialInfo;
		this.integration = integration;

		player.sendMessage(SocialMC.PREFIX + SocialMC.getMessageManager()
				.parse("message.validator.choose", socialInfo.getName(), integration.getName()));
		Bukkit.getPluginManager().registerEvents(this, SocialMC.getInstance());
		close();
	}

	@EventHandler
	public void onText(AsyncPlayerChatEvent event) {
		if (event.getPlayer().getUniqueId() == this.player.getUniqueId()) {
			this.text = event.getMessage();
			validate();
			event.setCancelled(true);
		}
	}

	public void validate() {
		if (this.text.equalsIgnoreCase(socialInfo.getSocialName(this.integration))) {
			this.player.sendMessage(SocialMC.getMessageManager().get("message.validator.same"));
			return;
		}
		InventoryBuilder inventory = new InventoryBuilder("SocialMC - Validator", 9 * 5, true);
		inventory.fill(ClickableItem.EMPTY);
		if (this.text.toLowerCase().startsWith("-cancel"))
			cancel(inventory);
		else if (this.text.toLowerCase().startsWith("-remove"))
			remove(inventory);
		else
			open(inventory,
					ClickableItem.empty(new ItemBuilder(Material.NAME_TAG).name(ChatColor.GREEN + this.text).lore("", this.integration.getURL().replaceAll("\\{from}", this.text))),
					ClickableItem.of(new ItemBuilder(XMaterial.GREEN_WOOL.parseMaterial()).durability(13).name(SocialMC.getMessageManager().get("item.validator.accept")),
							event -> {
								socialInfo.setIntegration(this.integration, this.text);
								this.player.sendMessage(SocialMC.getMessageManager().parse("message.validator.accept",
										socialInfo.getName(), this.integration.getName()));
								if (socialInfo == SocialMC.SERVER_INFO) socialInfo.setCertified(this.integration, true);
								else if (Settings.isCertifiable()) {
									this.player.sendMessage(SocialMC.getMessageManager().parse("message.validator.token",
											socialInfo.generateToken(this.integration), this.integration.getName()));
								}
								unregister();
								close();
							}),
					ClickableItem.of(new ItemBuilder(XMaterial.RED_WOOL.parseMaterial()).durability(14).name(SocialMC.getMessageManager().get("item.validator.decline")),
							event -> {
								this.player.sendMessage(SocialMC.getMessageManager().parse("message.validator.decline",
										socialInfo.getName(), this.integration.getName()));
								unregister();
								close();
							}));
		this.player.openInventory(inventory.build());
	}

	private void open(InventoryBuilder inventory, ClickableItem item, ClickableItem accept, ClickableItem decline) {
		inventory.setItem(13, item);
		inventory.setItem(30, accept);
		inventory.setItem(32, decline);
	}

	private void cancel(InventoryBuilder inventory) {
		open(inventory,
				ClickableItem.empty(new ItemBuilder(Material.BARRIER).name(SocialMC.getMessageManager().get("item.validator.cancel"))),
				ClickableItem.of(new ItemBuilder(XMaterial.GREEN_WOOL.parseMaterial()).durability(13).name(SocialMC.getMessageManager().get("item.validator.accept")),
						event -> {
							this.player.sendMessage(SocialMC.getMessageManager().parse("message.validator.cancel",
									this.integration.getName(), socialInfo.getName()));
							unregister();
							close();
						}),
				ClickableItem.of(new ItemBuilder(XMaterial.RED_WOOL.parseMaterial()).durability(14).name(SocialMC.getMessageManager().get("item.validator.decline")),
						event -> close()));
	}

	private void remove(InventoryBuilder inventory) {
		open(inventory,
				ClickableItem.empty(new ItemBuilder(Material.BARRIER).name(SocialMC.getMessageManager().get("item.validator.remove"))),
				ClickableItem.of(new ItemBuilder(XMaterial.GREEN_WOOL.parseMaterial()).durability(13).name(SocialMC.getMessageManager().get("item.validator.accept")),
						event -> {
							socialInfo.removeIntegration(this.integration);
							this.player.sendMessage(SocialMC.getMessageManager().parse("message.validator.remove",
									socialInfo.getName(), this.integration.getName()));
							unregister();
							close();
						}),
				ClickableItem.of(new ItemBuilder(XMaterial.RED_WOOL.parseMaterial()).durability(14).name(SocialMC.getMessageManager().get("item.validator.decline")),
						event -> close()));
	}

	private void close() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(SocialMC.getInstance(), this.player::closeInventory, 5L);
	}

	private void unregister() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(SocialMC.getInstance(), () -> AsyncPlayerChatEvent.getHandlerList().unregister(this), 5L);
	}

	public Player getPlayer() {
		return this.player;
	}

	public SocialInfo getSocialInfo() {
		return this.socialInfo;
	}

	public Integration getIntegration() {
		return this.integration;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
