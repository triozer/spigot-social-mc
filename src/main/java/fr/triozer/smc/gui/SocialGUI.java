package fr.triozer.smc.gui;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.SocialInfo;
import fr.triozer.smc.api.integration.Integration;
import fr.triozer.smc.api.ui.ClickableItem;
import fr.triozer.smc.api.ui.XMaterial;
import fr.triozer.smc.api.ui.builder.InventoryBuilder;
import fr.triozer.smc.api.ui.builder.ItemBuilder;
import fr.triozer.smc.utils.TextRequest;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Cédric / Triozer
 */
public class SocialGUI {

	public static void set(Player from, SocialInfo info) {
		List<Integration> integrations = SocialMC.getIntegrationManager().values()
				.collect(Collectors.toList());

		InventoryBuilder inventory = new InventoryBuilder("SocialMC - Configuration", (int) ((Math.ceil((double) integrations.size() / 7) + 2) * 9), true);
		inventory.fill(ClickableItem.EMPTY);

		fillFromList(integrations, (slot, integration) -> inventory.setItem(slot, integration.getHead()
				.to(event -> new TextRequest(from, info, integration))));

		from.openInventory(inventory.build());
	}

	public static InventoryBuilder of(Player from, SocialInfo info, boolean is) {
		List<Integration> integrations = info.getSocials();

		InventoryBuilder inventory = new InventoryBuilder("SocialMC", (int) ((Math.ceil((double) integrations.size() / 7) + 2) * 9), true);
		inventory.fill(ClickableItem.EMPTY);

		fillFromList(integrations, (slot, integration) -> inventory.setItem(slot, integration.getHead().from(from, info)));

		if (is || from.getUniqueId() == info.getUniqueId())
			inventory.setItem(inventory.getSize() - 1, ClickableItem
					.of(new ItemBuilder(XMaterial.NAME_TAG.parseMaterial())
									.name(SocialMC.getMessageManager().parse("item.edit.name", info.getName()))
									.lore(SocialMC.getMessageManager().getList("item.edit.lore")),
							event -> set(from, info)));

		return inventory;
	}

	private static void fillFromList(List<Integration> integrations, BiConsumer<Integer, Integration> action) {
		int slot = 10;

		for (int i = 0; i < integrations.size(); i++) {
			Integration integration = integrations.get(i);
			if (!integration.isActive()) continue;
			action.accept(slot++, integration);
			if ((slot % 9) == 0) {
				slot += 2;
			}
		}
	}

}
