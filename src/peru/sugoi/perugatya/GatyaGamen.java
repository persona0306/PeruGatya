package peru.sugoi.perugatya;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import hm.moe.pokkedoll.warscore.WarsCore;
import hm.moe.pokkedoll.warscore.utils.Item;
import hm.moe.pokkedoll.warscore.utils.ItemUtil;

public class GatyaGamen {
	public static final String TITLE = ChatColor.YELLOW + "Gatya";

	private static HashMap<String, Inventory> confirmmenu = new HashMap<String, Inventory>();
	public static Map<Player,Boolean> auto = new HashMap<Player,Boolean>();

	private static Map<Player,String> states = new HashMap<Player,String>();
	private static Map<Player,String> openingtype = new HashMap<Player,String>();
	private static Map<Player,Winning> winyoti = new HashMap<Player,Winning>();
	private static Map<Player,String> rollphases = new HashMap<Player,String>();
	private static Map<Player,Integer> rolltimes = new HashMap<Player,Integer>();
	private static Map<Player,Long> lastspin = new HashMap<Player,Long>();

	public static void clickDoko(Player player, int slot) {
		if (getState(player).equals("confirm")) {
			if (slot == 22) {
				preroll(player);
				return;
			}else if (slot >= 45) {
				ItemStack item22 = new ItemStack(Material.CHEST);
				ItemMeta item22meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.CHEST);
				Item item = Gatya.getGatya(getOpenGatya(player)).getCost();

				String name;
				ItemStack itemStack;
				try {
					itemStack = ItemUtil.getItemJava(item.name());

					if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
						name = itemStack.getItemMeta().getDisplayName() + " x" + item.amount();
					}else {
						name = item.name() + " x" + item.amount();
					}
				}catch (Exception ex) {
					name = "???";
				}

				item22meta.setDisplayName(ChatColor.YELLOW + name + ChatColor.RESET + " でガチャを回す！");
				item22.setItemMeta(item22meta);
				setWaku(player.getOpenInventory().getTopInventory(), true);
				player.getOpenInventory().setItem(22, item22);
				return;
			}else {
				clickElse(player);
				return;
			}
		}else if (getState(player).equalsIgnoreCase("preroll")) {
			clickElse(player);
			return;
		}else if (getState(player).equalsIgnoreCase("rolling")) {
			clickElse(player);
			return;
		}else if (getState(player).equalsIgnoreCase("result")) {
			if (slot == 40) {
				preroll(player);
				return;
			}else {
				clickElse(player);
				return;
			}
		}
		Bukkit.getLogger().warning("[PeruGatya] Unknown state type");
		return;
	}

	private static void clickElse(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1f, 1f);
	}

	public static void confirm(Player player, String gatyaname) {
		if (!Gatya.exist(gatyaname)) {
			player.sendMessage(ChatColor.RED + "そのガチャはないョ");
			return;
		}else if (Gatya.getGatya(gatyaname).getCost() == null) {
			player.sendMessage(ChatColor.RED + "このガチャは準備中です！");
			if (player.hasPermission("pokkeperu.op")) {
				player.sendMessage(ChatColor.RED + "(OP用メッセージ : このガチャにはコストが設定されていません)");
			}
			return;
		}else if (Gatya.getGatya(gatyaname).getWinnings().isEmpty()) {
			player.sendMessage(ChatColor.RED + "このガチャは準備中です！");
			if (player.hasPermission("pokkeperu.op")) {
				player.sendMessage(ChatColor.RED + "(OP用メッセージ : このガチャには景品がありません)");
			}
			return;
		}
		setOpenGatya(player, gatyaname);

		Inventory menu;
		if (!confirmmenu.containsKey(gatyaname)) {
			menu = Bukkit.createInventory(null, 45, TITLE);
			ItemStack chest = new ItemStack(Material.CHEST);
			Item item = Gatya.getGatya(getOpenGatya(player)).getCost();

			ItemStack costItem = ItemUtil.getItemJava(item.name());
			if (costItem != null) costItem = costItem.clone();
			String costItemName;
			
			if (costItem != null
					&& costItem.hasItemMeta()
					&& costItem.getItemMeta().hasDisplayName()) {
				costItemName = costItem.getItemMeta().getDisplayName() + " x" + item.amount();
			}else {
				if (costItem == null) {
					costItem = new ItemStack(Material.STONE);
				}
				costItemName = item.name() + " x" + item.amount();
			}
			
			ItemMeta chestItemMeta = chest.getItemMeta();
			ItemMeta costItemMeta = costItem.getItemMeta();
			
			chestItemMeta.setDisplayName(costItemName);
			costItemMeta.setDisplayName(costItemName);
			
			chest.setItemMeta(chestItemMeta);
			costItem.setItemMeta(costItemMeta);
			
			menu.setItem(22, chest);
			menu.setItem(31, costItem);
		}else {
			menu = confirmmenu.get(gatyaname);
		}
		player.openInventory(menu);
		setState(player, "confirm");
	}

	public static void flash(Player player) {
		if (!player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
			return;
		}

		Stream.iterate(0, i -> ++i).limit(45).forEach(i -> {
			ItemStack item = player.getOpenInventory().getItem(i);
			if (isStainedGlassPane(item)) {
				int random = (int)(Math.random() * 7);
				if (random == 0) {
					item.setType(Material.WHITE_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 1) {
					item.setType(Material.ORANGE_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 2) {
					item.setType(Material.MAGENTA_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 3) {
					item.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 4) {
					item.setType(Material.YELLOW_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 5) {
					item.setType(Material.LIME_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}else if (random == 6) {
					item.setType(Material.PINK_STAINED_GLASS_PANE);
					player.getOpenInventory().setItem(i, item);
				}
			}
		});
	}

	private static boolean isStainedGlassPane(ItemStack item) {
		if (item.getType() == Material.BLACK_STAINED_GLASS_PANE || item.getType() == Material.BLUE_STAINED_GLASS_PANE || item.getType() == Material.BROWN_STAINED_GLASS_PANE || item.getType() == Material.CYAN_STAINED_GLASS_PANE || item.getType() == Material.GRAY_STAINED_GLASS_PANE || item.getType() == Material.GREEN_STAINED_GLASS_PANE || item.getType() == Material.LIGHT_BLUE_STAINED_GLASS_PANE || item.getType() == Material.LIGHT_GRAY_STAINED_GLASS_PANE || item.getType() == Material.LIME_STAINED_GLASS_PANE || item.getType() == Material.MAGENTA_STAINED_GLASS_PANE || item.getType() == Material.ORANGE_STAINED_GLASS_PANE || item.getType() == Material.PINK_STAINED_GLASS_PANE || item.getType() == Material.PURPLE_STAINED_GLASS_PANE || item.getType() == Material.RED_STAINED_GLASS_PANE || item.getType() == Material.WHITE_STAINED_GLASS_PANE || item.getType() == Material.YELLOW_STAINED_GLASS_PANE) {
			return true;
		}else {
			return false;
		}
	}

	public static String getOpenGatya(Player player) {
		return openingtype.get(player);
	}

	public static String getRollPhase(Player player) {
		if (!rolltimes.containsKey(player)) {
			return "";
		}
		return rollphases.get(player);
	}

	public static int getRolltime(Player player) {
		if (!rolltimes.containsKey(player)) {
			rolltimes.put(player, 0);
		}
		return rolltimes.get(player);
	}

	public static String getState(Player player) {
		String state = states.get(player);
		if (state == null) {
			state = "confirm";
		}
		return state;
	}

	public static Winning getWinyoti(Player player) {
		return winyoti.get(player);
	}

	public static boolean isAuto(Player player) {
		Boolean isauto = auto.get(player);
		if (isauto == null) {
			auto.put(player, false);
			isauto = false;
		}
		return isauto;
	}

	public static void preroll(Player player) {
		if (getState(player).equals("preroll") || getState(player).equals("rolling")) {
			return;
		}else {
			long time = Calendar.getInstance().getTimeInMillis();
			if (lastspin.containsKey(player)) {
				if (time - 3000 < lastspin.get(player)) {
					return;
				}
			}
			lastspin.put(player, time);
		}

		TradeItem cost = Gatya.getGatya(getOpenGatya(player)).getCost();
		if (!canAfford(player, cost)) {
			ItemStack item22 = new ItemStack(Material.BEDROCK);
			ItemMeta item22meta = Bukkit.getServer().getItemFactory().getItemMeta(Material.BEDROCK);

			Item item = cost;

			String name;
			ItemStack itemStack;
			try {
				itemStack = ItemUtil.getItemJava(item.name());

				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
					name = itemStack.getItemMeta().getDisplayName() + " x" + item.amount();
				}else {
					name = item.name() + " x" + item.amount();
				}
			}catch (Exception ex) {
				name = "???";
			}

			item22meta.setDisplayName(ItemUtil.getItemJava(cost.name()).getItemMeta().getDisplayName() + "が足りません :(");
			item22.setItemMeta(item22meta);
			player.getOpenInventory().getTopInventory().setItem(22, item22);
			return;
		}

		WarsCore.getInstance().database().addWeapon4J("" + player.getUniqueId(), cost.getType().toString(), cost.name(), cost.amount() * -1);

		setState(player, "preroll");

		winyoti.put(player, Gatya.getGatya(getOpenGatya(player)).roll());

		roll(player);
	}

	private static boolean canAfford(Player player, TradeItem cost) {
		ItemType type = cost.getType();
		String name = cost.name();
		int amount = cost.amount();

		List<Item> items = WarsCore.getInstance().database().getWeapons4J("" + player.getUniqueId(), type.toString(), 0);
		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			if (item.name().equals(name)) {
				return item.amount() >= amount;
			}
		}

		return false;
	}

	public static void result(Player player) {
		setState(player, "result");

		if (isAuto(player) && player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
			setRolltime(player, 20);
			setRollPhase(player, "auto");
		}

		Winning winning = getWinyoti(player);
		winning.win(player);

		String name = winning.getDisplayItem().getItemMeta().getDisplayName();

		if (winning.getRare().equalsIgnoreCase("legendary")) {
			Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " ちゃんがガチャ " + getOpenGatya(player) + " で " +  "Legendary " + ChatColor.RESET + ChatColor.GOLD + name + "(" + winning.getType() + ")" + " を当てちゃったよ～ｗｗ");
			for(Player target : Bukkit.getServer().getOnlinePlayers()) {
				target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
			}
		}else if (winning.getRare().equalsIgnoreCase("epic")) {
			Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getName() + " さんがガチャ " + getOpenGatya(player) + " で " + "Epic " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + name + "(" + winning.getType() + ")" + " を当てました！");
			for(Player target : Bukkit.getServer().getOnlinePlayers()) {
				target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
			}
		}else if (winning.getRare().equalsIgnoreCase("rare")) {
			if (!isAuto(player)) {
				player.sendMessage("ガチャ " + getOpenGatya(player) + " で " + ChatColor.AQUA + "Rare " + ChatColor.RESET + ChatColor.AQUA + name + "(" + winning.getType() + ")" + ChatColor.RESET + " が当たりましたよ～");
			}
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1, 1);
		}else if (winning.getRare().equalsIgnoreCase("uncommon")) {
			if (!isAuto(player)) {
				player.sendMessage("ガチャ " + getOpenGatya(player) + " で " + ChatColor.GREEN + "Uncommon " + ChatColor.RESET + name + ChatColor.GREEN + "(" + winning.getType() + ")" + ChatColor.RESET + " が当たりましたよ～");
			}
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
		}else {
			if (!isAuto(player)) {
				player.sendMessage("ガチャ " + getOpenGatya(player) + " で Common " + name + ChatColor.RESET + "(" + winning.getType() + ")" + ChatColor.RESET + " が当たりましたよ～");
			}
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
		}

		if (player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
			ItemStack display = winning.getDisplayItem();
			ItemMeta displaymeta = display.getItemMeta();
			List<String> displaylore = displaymeta.getLore();
			if (displaylore == null) {
				displaylore = new ArrayList<String>();
			}
			String rarity = winning.getRare();
			if (rarity.equalsIgnoreCase("legendary")) {
				displaylore.add(ChatColor.GOLD + "LEGENDARY");
			}else if (rarity.equalsIgnoreCase("epic")) {
				displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
			}else if (rarity.equalsIgnoreCase("rare")) {
				displaylore.add(ChatColor.AQUA + "RARE");
			}else if (rarity.equalsIgnoreCase("uncommon")) {
				displaylore.add(ChatColor.GREEN + "UNCOMMON");
			}else {
				displaylore.add(ChatColor.WHITE + "COMMON");
			}
			displaymeta.setLore(displaylore);
			display.setItemMeta(displaymeta);
			ItemStack chest = new ItemStack(Material.CHEST);
			ItemMeta chestmeta = Bukkit.getItemFactory().getItemMeta(Material.CHEST);

			Item item = Gatya.getGatya(getOpenGatya(player)).getCost();

			ItemStack itemStack;
			try {
				itemStack = ItemUtil.getItemJava(item.name());

				if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
					name = itemStack.getItemMeta().getDisplayName() + " x" + item.amount();
				}else {
					name = item.name() + " x" + item.amount();
				}
			}catch (Exception ex) {
				name = "???";
			}

			chestmeta.setDisplayName(ChatColor.YELLOW + name + ChatColor.RESET + " でガチャを回す！");
			chest.setItemMeta(chestmeta);

			if (winning.getRare().equalsIgnoreCase("legendary")) {
				setWaku(player.getOpenInventory().getTopInventory(), (short)1, true);
			}else if (winning.getRare().equalsIgnoreCase("epic")) {
				setWaku(player.getOpenInventory().getTopInventory(), (short)2, true);
			}else if (winning.getRare().equalsIgnoreCase("rare")) {
				setWaku(player.getOpenInventory().getTopInventory(), (short)3, true);
			}else if (winning.getRare().equalsIgnoreCase("uncommon")) {
				setWaku(player.getOpenInventory().getTopInventory(), (short)5, true);
			}else {
				setWaku(player.getOpenInventory().getTopInventory(), (short)0, true);
			}
			player.getOpenInventory().setItem(22, display);
			player.getOpenInventory().setItem(40, chest);
		}
	}

	public static void roll(Player player) {
		if (player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya") && getOpenGatya(player) != null) {
			setWaku(player.getOpenInventory().getTopInventory());

			if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
				Double random = Math.random();
				if (random < 0.75) {
					setRollPhase(player, "yokoku1");
					setRolltime(player, 10);
				}else if (random < 0.95) {
					setRollPhase(player, "freeze");
					setRolltime(player, 100);
				}else {
					if (Math.random() < 0.2) {
						setRolltime(player, 200);
					}else {
						setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
					}
					setRollPhase(player, "roll");
				}
			}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
				Double random = Math.random();
				if (random < 0.8) {
					setRollPhase(player, "yokoku1");
					setRolltime(player, 10);
				}else {
					setRollPhase(player, "roll");
					setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
				}
			}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
				Double random = Math.random();
				if (random < 0.6) {
					setRollPhase(player, "yokoku1");
					setRolltime(player, 10);
				}else {
					setRollPhase(player, "roll");
					setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
				}
			}else if (getWinyoti(player).getRare().equalsIgnoreCase("uncommon")) {
				Double random = Math.random();
				if (random < 0.33) {
					setRollPhase(player, "yokoku1");
					setRolltime(player, 10);
				}else {
					setRollPhase(player, "roll");
					setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
				}
			}else {
				setRollPhase(player, "roll");
				setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
			}
			setState(player, "rolling");
		}else {
			result(player);
		}
	}

	public static void setAuto(Player player, boolean isauto) {
		auto.put(player, isauto);
	}

	public static void setOpenGatya(Player player, String Gatya) {
		openingtype.put(player, Gatya);
	}

	public static void setRollPhase(Player player, String rollphase) {
		rollphases.put(player, rollphase);
	}

	public static void setRolltime(Player player, int rolltime) {
		rolltimes.put(player, rolltime);
	}

	public static void setState(Player player, String state) {
		states.put(player, state);
	}

	public static void setWaku(Inventory menu) {
		setWaku(menu, (short)4, false);
	}

	public static void setWaku(Inventory menu, boolean setumei) {
		setWaku(menu, (short)4, setumei);
	}

	public static void setWaku(Inventory menu, short durability) {
		setWaku(menu, durability, false);
	}

	public static void setWaku(Inventory menu, short durability, boolean setumei) {
		ItemStack waku;
		if (durability == 1) {
			waku = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
		}else if (durability == 2) {
			waku = new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE);
		}else if (durability == 3) {
			waku = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
		}else if (durability == 4) {
			waku = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
		}else if (durability == 5) {
			waku = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		}else if (durability == 6) {
			waku = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
		}else if (durability == 7) {
			waku = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		}else if (durability == 8) {
			waku = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		}else if (durability == 9) {
			waku = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
		}else if (durability == 10) {
			waku = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
		}else if (durability == 11) {
			waku = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		}else if (durability == 12) {
			waku = new ItemStack(Material.BROWN_STAINED_GLASS_PANE);
		}else if (durability == 13) {
			waku = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
		}else if (durability == 14) {
			waku = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		}else if (durability == 15) {
			waku = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		}else {
			waku = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		}
		if (setumei) {
			ItemMeta wakumeta = Bukkit.getServer().getItemFactory().getItemMeta(waku.getType());
			wakumeta.setDisplayName(ChatColor.GOLD + "チェストをクリックしてガチャを回そう！");
			waku.setItemMeta(wakumeta);
		}
		for(int i = 0; i < 45; i++) {
			menu.setItem(i, waku);
		}
	}

	public static void tick() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			try {
				if (!player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
					setAuto(player, false);
				}
				if (getRolltime(player) >= 0) {
					if (getState(player).equals("rolling")) {
						if (player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
							if (getRollPhase(player).equals("yokoku1")) {
								if (getRolltime(player) == 10) {
									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
								}else if (getRolltime(player) == 0) {
									if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
										if (Math.random() < 0.35) {
											setRollPhase(player, "337");
											setRolltime(player, 129);
										}else {
											setRollPhase(player, "roll");
											if (Math.random() < 0.2) {
												setRolltime(player, 201);
											}else {
												setRolltime(player, 46 + ((int)Math.random() * 7) * 5);
											}
										}
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
										if (Math.random() < 0.15) {
											setRollPhase(player, "337");
											setRolltime(player, 129);
										}else {
											setRollPhase(player, "roll");
											setRolltime(player, 46 + ((int)(Math.random() * 6)) * 5);
										}
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
										setRollPhase(player, "roll");
										setRolltime(player, 46 + ((int)(Math.random() * 5)) * 5);
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("uncommon")) {
										setRollPhase(player, "roll");
										setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
									}else {
										setRollPhase(player, "roll");
										setRolltime(player, 46 + ((int)(Math.random() * 4)) * 5);
									}
								}
							}else if (getRollPhase(player).equals("roll")) {
								if (getRolltime(player) % 5 == 0) {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
									if (getRolltime(player) == 0) {

										ItemStack winyotidisplay = getWinyoti(player).getDisplayItem();
										ItemMeta winyotidisplaymeta = winyotidisplay.getItemMeta();
										List<String> winyotidisplaylore = winyotidisplaymeta.getLore();
										if (winyotidisplaylore == null) {
											winyotidisplaylore = new ArrayList<String>();
										}

										String rarity = getWinyoti(player).getRare();
										if (rarity.equalsIgnoreCase("legendary")) {
											winyotidisplaylore.add(ChatColor.GOLD + "LEGENDARY");
										}else if (rarity.equalsIgnoreCase("epic")) {
											winyotidisplaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
										}else if (rarity.equalsIgnoreCase("rare")) {
											winyotidisplaylore.add(ChatColor.AQUA + "RARE");
										}else if (rarity.equalsIgnoreCase("uncommon")) {
											winyotidisplaylore.add(ChatColor.GREEN + "UNCOMMON");
										}else {
											winyotidisplaylore.add(ChatColor.WHITE + "COMMON");
										}
										winyotidisplaymeta.setLore(winyotidisplaylore);
										winyotidisplay.setItemMeta(winyotidisplaymeta);

										if (winyotidisplay.isSimilar(player.getOpenInventory().getItem(22))) {
											result(player);
										}else {
											setRollPhase(player, "rerolltaiki");
											setRolltime(player, 20);
										}
									}else {
										Winning displaywinning = null;
										if (getRolltime(player) == 25) {
											if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
												if (Math.random() < 0.8) {
													if (Gatya.getGatya(getOpenGatya(player)).containsCommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (!displaywinning.getRare().equalsIgnoreCase("legendary") && !displaywinning.getRare().equalsIgnoreCase("epic") && !displaywinning.getRare().equalsIgnoreCase("rare") && !displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsUncommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsRare()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("rare")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsEpic()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("epic")) {
																break;
															}
														}
													}else {
														displaywinning = getWinyoti(player);
													}
												}else {
													displaywinning = getWinyoti(player);
												}
											}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
												if (Math.random() < 0.65) {
													if (Gatya.getGatya(getOpenGatya(player)).containsCommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (!displaywinning.getRare().equalsIgnoreCase("legendary") && !displaywinning.getRare().equalsIgnoreCase("epic") && !displaywinning.getRare().equalsIgnoreCase("rare") && !displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsUncommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsRare()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("rare")) {
																break;
															}
														}
													}else {
														displaywinning = getWinyoti(player);
													}
												}else {
													displaywinning = getWinyoti(player);
												}
											}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
												if (Math.random() < 0.2) {
													if (Gatya.getGatya(getOpenGatya(player)).containsCommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (!displaywinning.getRare().equalsIgnoreCase("legendary") && !displaywinning.getRare().equalsIgnoreCase("epic") && !displaywinning.getRare().equalsIgnoreCase("rare") && !displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else if (Gatya.getGatya(getOpenGatya(player)).containsUncommon()) {
														for(int i = 0; i < 999; i++) {
															displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
															if (displaywinning.getRare().equalsIgnoreCase("uncommon")) {
																break;
															}
														}
													}else {
														displaywinning = getWinyoti(player);
													}
												}else {
													displaywinning = getWinyoti(player);
												}
											}else if (getWinyoti(player).getRare().equalsIgnoreCase("uncommon")) {
												if (Math.random() < 0.05 && Gatya.getGatya(getOpenGatya(player)).containsCommon()) {
													for(int i = 0; i < 999; i++) {
														displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
														if (!displaywinning.getRare().equalsIgnoreCase("legendary") && !displaywinning.getRare().equalsIgnoreCase("epic") && !displaywinning.getRare().equalsIgnoreCase("rare") && !displaywinning.getRare().equalsIgnoreCase("uncommon")) {
															break;
														}
													}
												}else {
													displaywinning = getWinyoti(player);
												}
											}else {
												displaywinning = getWinyoti(player);
											}
										}else {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
										}

										ItemStack display = displaywinning.getDisplayItem();
										ItemMeta displaymeta = display.getItemMeta();
										List<String> displaylore = displaymeta.getLore();
										if (displaylore == null) {
											displaylore = new ArrayList<String>();
										}
										String rarity = displaywinning.getRare();
										if (rarity.equalsIgnoreCase("legendary")) {
											displaylore.add(ChatColor.GOLD + "LEGENDARY");
										}else if (rarity.equalsIgnoreCase("epic")) {
											displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
										}else if (rarity.equalsIgnoreCase("rare")) {
											displaylore.add(ChatColor.AQUA + "RARE");
										}else if (rarity.equalsIgnoreCase("uncommon")) {
											displaylore.add(ChatColor.GREEN + "UNCOMMON");
										}else {
											displaylore.add(ChatColor.WHITE + "COMMON");
										}
										displaymeta.setLore(displaylore);
										display.setItemMeta(displaymeta);
										flash(player);
										for(int i = 0; i < 8; i ++) {
											player.getOpenInventory().setItem(26 - i, player.getOpenInventory().getItem(25 - i));
										}
										player.getOpenInventory().setItem(18, display);
									}
								}
							}else if (getRollPhase(player).equals("337")) {
								if (getRolltime(player) % 4 == 0 && getRolltime(player) != 4 && getRolltime(player) != 36 && getRolltime(player) != 52 && getRolltime(player) != 68 && getRolltime(player) != 100 && getRolltime(player) != 116) {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
									Winning displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
									if (getRolltime(player) <= 64) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("legendary") || displaywinning.getRare().equalsIgnoreCase("epic")) {
												break;
											}
										}
									}
									ItemStack display = displaywinning.getDisplayItem();
									ItemMeta displaymeta = display.getItemMeta();
									List<String> displaylore = displaymeta.getLore();
									if (displaylore == null) {
										displaylore = new ArrayList<String>();
									}
									String rarity = displaywinning.getRare();
									if (rarity.equalsIgnoreCase("legendary")) {
										displaylore.add(ChatColor.GOLD + "LEGENDARY");
									}else if (rarity.equalsIgnoreCase("epic")) {
										displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
									}else if (rarity.equalsIgnoreCase("rare")) {
										displaylore.add(ChatColor.AQUA + "RARE");
									}else if (rarity.equalsIgnoreCase("uncommon")) {
										displaylore.add(ChatColor.GREEN + "UNCOMMON");
									}else {
										displaylore.add(ChatColor.WHITE + "COMMON");
									}
									displaymeta.setLore(displaylore);
									display.setItemMeta(displaymeta);
									flash(player);
									player.getOpenInventory().setItem(22, display);
									if (getRolltime(player) == 0) {
										result(player);
									}
								}
							}else if (getRollPhase(player).equals("rerolltaiki")) {
								if (getRolltime(player) == 19) {
									ItemStack item22 = player.getOpenInventory().getItem(22);
									setWaku(player.getOpenInventory().getTopInventory());
									flash(player);
									player.getOpenInventory().setItem(22, item22);
								}else if (getRolltime(player) == 0) {
									if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
										if (Math.random() < 0.5) {
											setRollPhase(player, "337");
											setRolltime(player, 129);
										}else {
											setRollPhase(player, "reroll");
											setRolltime(player, (int)Math.random() * 40 + 25);
										}
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
										if (Math.random() < 0.25) {
											setRollPhase(player, "337");
											setRolltime(player, 129);
										}else {
											setRollPhase(player, "reroll");
											setRolltime(player, (int)Math.random() * 35 + 20);
										}
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
										setRollPhase(player, "reroll");
										setRolltime(player, (int)Math.random() * 30 + 15);
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("uncommon")) {
										setRollPhase(player, "reroll");
										setRolltime(player, (int)Math.random() * 25 + 15);
									}
								}
							}else if (getRollPhase(player).equals("rerolltaiki2")) {
								if (getRolltime(player) == 19) {
									Winning displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
									if (Gatya.getGatya(getOpenGatya(player)).containsUncommon()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("uncommon")) {
												break;
											}
										}
									}else if (Gatya.getGatya(getOpenGatya(player)).containsRare()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("rare")) {
												break;
											}
										}
									}else if (Gatya.getGatya(getOpenGatya(player)).containsEpic()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("epic")) {
												break;
											}
										}
									}else {
										displaywinning = getWinyoti(player);
									}
									ItemStack display = displaywinning.getDisplayItem();
									ItemMeta displaymeta = display.getItemMeta();
									List<String> displaylore = displaymeta.getLore();
									if (displaylore == null) {
										displaylore = new ArrayList<String>();
									}
									String rarity = displaywinning.getRare();
									if (rarity.equalsIgnoreCase("legendary")) {
										displaylore.add(ChatColor.GOLD + "LEGENDARY");
									}else if (rarity.equalsIgnoreCase("epic")) {
										displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
									}else if (rarity.equalsIgnoreCase("rare")) {
										displaylore.add(ChatColor.AQUA + "RARE");
									}else if (rarity.equalsIgnoreCase("uncommon")) {
										displaylore.add(ChatColor.GREEN + "UNCOMMON");
									}
									displaymeta.setLore(displaylore);
									display.setItemMeta(displaymeta);
									flash(player);
									player.getOpenInventory().setItem(22, display);
								}else if (getRolltime(player) == 0) {
									if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
										setRollPhase(player, "reroll2");
										setRolltime(player, (int)Math.random() * 50 + 30);
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
										setRollPhase(player, "reroll2");
										setRolltime(player, (int)Math.random() * 45 + 25);
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
										setRollPhase(player, "reroll2");
										setRolltime(player, (int)Math.random() * 40 + 20);
									}
								}
							}else if (getRollPhase(player).equals("rerolltaiki3")) {
								if (getRolltime(player) == 39) {
									Winning displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
									if (Gatya.getGatya(getOpenGatya(player)).containsRare()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("rare")) {
												break;
											}
										}
									}else if (Gatya.getGatya(getOpenGatya(player)).containsEpic()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("epic")) {
												break;
											}
										}
									}else {
										displaywinning = getWinyoti(player);
									}
									ItemStack display = displaywinning.getDisplayItem();
									ItemMeta displaymeta = display.getItemMeta();
									List<String> displaylore = displaymeta.getLore();
									if (displaylore == null) {
										displaylore = new ArrayList<String>();
									}
									String rarity = displaywinning.getRare();
									if (rarity.equalsIgnoreCase("legendary")) {
										displaylore.add(ChatColor.GOLD + "LEGENDARY");
									}else if (rarity.equalsIgnoreCase("epic")) {
										displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
									}else if (rarity.equalsIgnoreCase("rare")) {
										displaylore.add(ChatColor.AQUA + "RARE");
									}else if (rarity.equalsIgnoreCase("uncommon")) {
										displaylore.add(ChatColor.GREEN + "UNCOMMON");
									}
									displaymeta.setLore(displaylore);
									display.setItemMeta(displaymeta);
									flash(player);
									player.getOpenInventory().setItem(22, display);
								}else if (getRolltime(player) == 0) {
									if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
										setRollPhase(player, "reroll3");
										setRolltime(player, (int)Math.random() * 30 + 60);
									}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
										setRollPhase(player, "reroll3");
										setRolltime(player, (int)Math.random() * 25 + 50);
									}
								}
							}else if (getRollPhase(player).equals("rerolltaiki4")) {
								if (getRolltime(player) == 59) {
									Winning displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
									if (Gatya.getGatya(getOpenGatya(player)).containsEpic()) {
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("epic")) {
												break;
											}
										}
									}else {
										displaywinning = getWinyoti(player);
									}
									ItemStack display = displaywinning.getDisplayItem();
									ItemMeta displaymeta = display.getItemMeta();
									List<String> displaylore = displaymeta.getLore();
									if (displaylore == null) {
										displaylore = new ArrayList<String>();
									}
									String rarity = displaywinning.getRare();
									if (rarity.equalsIgnoreCase("legendary")) {
										displaylore.add(ChatColor.GOLD + "LEGENDARY");
									}else if (rarity.equalsIgnoreCase("epic")) {
										displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
									}else if (rarity.equalsIgnoreCase("rare")) {
										displaylore.add(ChatColor.AQUA + "RARE");
									}else if (rarity.equalsIgnoreCase("uncommon")) {
										displaylore.add(ChatColor.GREEN + "UNCOMMON");
									}
									displaymeta.setLore(displaylore);
									display.setItemMeta(displaymeta);
									flash(player);
									player.getOpenInventory().setItem(22, display);
								}else if (getRolltime(player) == 0) {
									setRollPhase(player, "reroll4");
									setRolltime(player, 100);
								}
							}else if (getRollPhase(player).equals("reroll")) {
								if (getRolltime(player) % 4 == 0) {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.25f);
									if (getRolltime(player) == 0) {
										if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
											if (Math.random() < 0.95) {
												setRollPhase(player, "rerolltaiki2");
												setRolltime(player, 20);
											}else {
												result(player);
											}
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
											if (Math.random() < 0.85) {
												setRollPhase(player, "rerolltaiki2");
												setRolltime(player, 20);
											}else {
												result(player);
											}
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
											if (Math.random() < 0.8) {
												setRollPhase(player, "rerolltaiki2");
												setRolltime(player, 20);
											}else {
												result(player);
											}
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("uncommon")) {
											result(player);
										}
									}else {
										Winning displaywinning = null;
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("legendary") || displaywinning.getRare().equalsIgnoreCase("epic") || displaywinning.getRare().equalsIgnoreCase("rare") || displaywinning.getRare().equalsIgnoreCase("uncommon")) {
												break;
											}
										}
										ItemStack display = displaywinning.getDisplayItem();
										ItemMeta displaymeta = display.getItemMeta();
										List<String> displaylore = displaymeta.getLore();
										if (displaylore == null) {
											displaylore = new ArrayList<String>();
										}
										String rarity = displaywinning.getRare();
										if (rarity.equalsIgnoreCase("legendary")) {
											displaylore.add(ChatColor.GOLD + "LEGENDARY");
										}else if (rarity.equalsIgnoreCase("epic")) {
											displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
										}else if (rarity.equalsIgnoreCase("rare")) {
											displaylore.add(ChatColor.AQUA + "RARE");
										}else if (rarity.equalsIgnoreCase("uncommon")) {
											displaylore.add(ChatColor.GREEN + "UNCOMMON");
										}
										displaymeta.setLore(displaylore);
										display.setItemMeta(displaymeta);
										flash(player);
										player.getOpenInventory().setItem(22, display);
									}
								}
							}else if (getRollPhase(player).equals("reroll2")) {
								if (getRolltime(player) % 3 == 0) {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.5f);
									if (getRolltime(player) == 0) {
										if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
											if (Math.random() < 0.95) {
												setRollPhase(player, "rerolltaiki3");
												setRolltime(player, 40);
											}else {
												result(player);
											}
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
											if (Math.random() < 0.95) {
												setRollPhase(player, "rerolltaiki3");
												setRolltime(player, 40);
											}else {
												result(player);
											}
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("rare")) {
											result(player);
										}
									}else {
										Winning displaywinning = null;
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("legendary") || displaywinning.getRare().equalsIgnoreCase("epic") || displaywinning.getRare().equalsIgnoreCase("rare")) {
												break;
											}
										}
										ItemStack display = displaywinning.getDisplayItem();
										ItemMeta displaymeta = display.getItemMeta();
										List<String> displaylore = displaymeta.getLore();
										if (displaylore == null) {
											displaylore = new ArrayList<String>();
										}
										String rarity = displaywinning.getRare();
										if (rarity.equalsIgnoreCase("legendary")) {
											displaylore.add(ChatColor.GOLD + "LEGENDARY");
										}else if (rarity.equalsIgnoreCase("epic")) {
											displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
										}else if (rarity.equalsIgnoreCase("rare")) {
											displaylore.add(ChatColor.AQUA + "RARE");
										}
										displaymeta.setLore(displaylore);
										display.setItemMeta(displaymeta);
										flash(player);
										player.getOpenInventory().setItem(22, display);
									}
								}
							}else if (getRollPhase(player).equals("reroll3")) {
								if (getRolltime(player) % 2 == 0) {
									player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.75f);
									if (getRolltime(player) == 0) {
										if (getWinyoti(player).getRare().equalsIgnoreCase("legendary")) {
											setRollPhase(player, "rerolltaiki4");
											setRolltime(player, 60);
										}else if (getWinyoti(player).getRare().equalsIgnoreCase("epic")) {
											result(player);
										}
									}else {
										Winning displaywinning = null;
										for(int i = 0; i < 999; i++) {
											displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
											if (displaywinning.getRare().equalsIgnoreCase("legendary") || displaywinning.getRare().equalsIgnoreCase("epic")) {
												break;
											}
										}
										ItemStack display = displaywinning.getDisplayItem();
										ItemMeta displaymeta = display.getItemMeta();
										List<String> displaylore = displaymeta.getLore();
										if (displaylore == null) {
											displaylore = new ArrayList<String>();
										}
										String rarity = displaywinning.getRare();
										if (rarity.equalsIgnoreCase("legendary")) {
											displaylore.add(ChatColor.GOLD + "LEGENDARY");
										}else if (rarity.equalsIgnoreCase("epic")) {
											displaylore.add(ChatColor.LIGHT_PURPLE + "EPIC");
										}
										displaymeta.setLore(displaylore);
										display.setItemMeta(displaymeta);
										flash(player);
										player.getOpenInventory().setItem(22, display);
									}
								}
							}else if (getRollPhase(player).equals("reroll4")) {
								Winning displaywinning = null;
								for(int i = 0; i < 999; i++) {
									displaywinning = Gatya.getGatya(getOpenGatya(player)).roll();
									if (displaywinning.getRare().equalsIgnoreCase("legendary")) {
										break;
									}
								}
								ItemStack display = displaywinning.getDisplayItem();
								ItemMeta displaymeta = display.getItemMeta();
								List<String> displaylore = displaymeta.getLore();
								if (displaylore == null) {
									displaylore = new ArrayList<String>();
								}
								displaylore.add(ChatColor.GOLD + "LEGENDARY");
								displaymeta.setLore(displaylore);
								display.setItemMeta(displaymeta);
								flash(player);
								player.getOpenInventory().setItem(22, display);
								player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
								if (getRolltime(player) == 0) {
									if (!getState(player).equals("result")) {
										result(player);
									}
								}
							}else if (getRollPhase(player).equals("freeze")) {
								if (getRolltime(player) == 99) {
									setWaku(player.getOpenInventory().getTopInventory(), (short)15);
									player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 1);
								}else if (getRolltime(player) == 0) {
									result(player);
								}
							}else if (getRollPhase(player).equals("")) {
								player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1, 0);
							}
						}else {
							result(player);
						}
					}else if (getRollPhase(player).equals("auto")) {
						if (getRolltime(player) % 5 == 0 && getRolltime(player) != 0) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
						}
						if (!player.getOpenInventory().getTitle().equals(ChatColor.YELLOW + "Gatya")) {
							setRolltime(player, 0);
						}else if (getRolltime(player) == 0 && isAuto(player)) {
							preroll(player);
						}
					}
					setRolltime(player, getRolltime(player) - 1);
				}
			}catch (Exception e) {
				if (getState(player).equals("rolling")) {
					setState(player, "result");
					result(player);
				}
			}
		}
	}
}
