package peru.sugoi.perugatya;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hm.moe.pokkedoll.warscore.WarsCore;
import hm.moe.pokkedoll.warscore.utils.ItemUtil;

public class Winning {
	private String rare;
	private double chance;
	private TradeItem item;

	public Winning(ItemType type, String name, int amount, String rare, double chance){
		item = new TradeItem(type, name, amount);
		this.rare = rare;
		this.chance = chance;
	}

	public int getAmount() {
		return item.amount();
	}

	public double getChance() {
		return chance;
	}

	public ItemStack getDisplayItem() {
		ItemStack displayitem = ItemUtil.getItemJava(item.name());
		ItemMeta itemmeta;
		if (displayitem != null){
			itemmeta = displayitem.getItemMeta();
			if (itemmeta.hasDisplayName()) {
				itemmeta.setDisplayName(itemmeta.getDisplayName() + " x" + item.amount());
			}else {
				itemmeta.setDisplayName(item.name() + " x" + item.amount());
			}

		}else {
			displayitem = new ItemStack(Material.STONE);
			itemmeta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
			itemmeta.setDisplayName("???");

		}
		displayitem.setItemMeta(itemmeta);
		return displayitem;
	}

	public String getName() {
		return item.name();
	}

	public String getRare() {
		if (rare != null) {
			return rare;
		}else {
			return "common";
		}
	}

	public ItemType getType() {
		return item.getType();
	}

	public void win(Player player) {
		WarsCore.getInstance().database().addWeapon4J("" + player.getUniqueId(), item.getType().toString(), item.name(), item.amount());
	}
}
