package peru.sugoi.perugatya;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK && isSign(e.getClickedBlock())) {
			try {
				Sign sign = (Sign)e.getClickedBlock().getState();
				if (sign.getLine(0).contains("Gatya") || sign.getLine(0).contains("ガチャ")) {
					GatyaGamen.confirm(e.getPlayer(), sign.getLine(1));
				}
			}catch(Exception ex) {
				ex.printStackTrace();
				e.getPlayer().sendMessage(ex.toString());
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getView().getPlayer();
		if (e.getView().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
			if (e.isShiftClick()) {
				e.setCancelled(true);
				if (GatyaGamen.isAuto(player)) {
					GatyaGamen.setAuto(player, false);
					player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 0.5f);
				}else {
					GatyaGamen.setAuto(player, true);
					player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 0.6f);
				}
				return;
			}
			if (e.getRawSlot() < 45) {
				e.setCancelled(true);
				GatyaGamen.clickDoko(player, e.getRawSlot());
			}else {
				if (GatyaGamen.getState(player).equals("preroll") || GatyaGamen.getState(player).equals("rolling")) {
					e.setCancelled(true);
				}
			}
		}
	}

	private boolean isSign(Block block) {
		Material material = block.getType();
		if (material == Material.ACACIA_SIGN || material == Material.ACACIA_WALL_SIGN || material == Material.BIRCH_SIGN || material == Material.BIRCH_WALL_SIGN || material == Material.CRIMSON_SIGN || material == Material.CRIMSON_WALL_SIGN || material == Material.DARK_OAK_SIGN || material == Material.DARK_OAK_WALL_SIGN || material == Material.JUNGLE_SIGN || material == Material.JUNGLE_WALL_SIGN || material == Material.OAK_SIGN || material == Material.OAK_WALL_SIGN || material == Material.SPRUCE_SIGN || material == Material.SPRUCE_WALL_SIGN || material == Material.WARPED_SIGN || material == Material.WARPED_WALL_SIGN) {
			return true;
		}else {
			return false;
		}
	}
}
