package peru.sugoi.perugatya;

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
		ItemStack displayItem = ItemUtil.getItemJava(item.name());
		if (displayItem != null) displayItem = displayItem.clone();
		String name;
		
		if (displayItem != null
				&& displayItem.hasItemMeta()
				&& displayItem.getItemMeta().hasDisplayName()) {
			name = displayItem.getItemMeta().getDisplayName() + " x" + item.amount();
		}else {
			if (displayItem == null) {
				displayItem = new ItemStack(Material.STONE);
			}
			name = item.name() + " x" + item.amount();
		}
		
		ItemMeta itemMeta = displayItem.getItemMeta();
		itemMeta.setDisplayName(name);
		displayItem.setItemMeta(itemMeta);
		
		return displayItem;
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
