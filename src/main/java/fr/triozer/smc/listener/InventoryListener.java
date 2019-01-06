package fr.triozer.smc.listener;

import fr.triozer.smc.SocialMC;
import fr.triozer.smc.api.ui.builder.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/**
 * @author Cédric / Triozer
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void interact(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;

        for (InventoryBuilder builder : SocialMC.getInstance().getInventories()) {
            if (builder.getInventory().equals(event.getClickedInventory())) {
                if (builder.getListener() != null) {
                    builder.getListener().interact((Player) event.getWhoClicked(), event);
                } else
                    event.setCancelled(true);

                builder.getContent(event.getSlot()).accept(event);
            }
        }

    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        InventoryBuilder b = null;

        for (InventoryBuilder builder : SocialMC.getInstance().getInventories()) {
            if (builder.getInventory().equals(event.getInventory())) {
                if (builder.getListener() != null) {
                    builder.getListener().close((Player) event.getPlayer(), event);
                }

                b = builder;
            }
        }

        if (b != null) {
            if (!b.isCloseable()) {
                InventoryBuilder finalB = b;
                Bukkit.getScheduler().runTaskAsynchronously(SocialMC.getInstance(),
                        () -> event.getPlayer().openInventory(finalB.getInventory()));
            } else
                SocialMC.getInstance().getInventories().remove(b);
        }
    }


    @EventHandler
    public void drag(InventoryDragEvent event) {
        for (InventoryBuilder builder : SocialMC.getInstance().getInventories()) {
            if (builder.getInventory().equals(event.getInventory())) {
                if (builder.getListener() != null) {
                    builder.getListener().drag((Player) event.getWhoClicked(), event);
                } else
                    event.setCancelled(true);
            }
        }
    }
}
