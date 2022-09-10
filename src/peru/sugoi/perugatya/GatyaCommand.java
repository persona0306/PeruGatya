package peru.sugoi.perugatya;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import hm.moe.pokkedoll.warscore.utils.Item;
import hm.moe.pokkedoll.warscore.utils.ItemUtil;
import net.md_5.bungee.api.ChatColor;

public class GatyaCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String index, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("ムリ");
			return true;
		}else {
			Player player = (Player)sender;
			if (!player.hasPermission("pokkeperu.op")) {
				return true;
			}
			
			if (args.length < 3) {
				help(sender);
				return true;
			}
			
			Gatya gatya = Gatya.getGatya(args[0]);
			if (gatya == null) {
				sender.sendMessage(ChatColor.RED + "ガチャ " + args[0] + "はないぞｗ");
				return true;
			}
			
			double chance;
			try {
				chance = Double.parseDouble(args[2]);
			}catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "確率は「" + args[2] + "」を数字として読めませんでした");
				return true;
			}
			Block block = player.getTargetBlock(null, 20);
			if (block.getType() != Material.CHEST) {
				sender.sendMessage(ChatColor.RED + "チェストを見てくんろ");
				return true;
			}
			
			Chest chest = (Chest) block.getState();

			new BukkitRunnable() {
				int tick = 100;
				@Override
				public void run() {
					tick --;
					if (tick < 0) {
						cancel();
					}else if (tick % 2 == 1) {
						chest.open();
					}else {
						chest.close();
					}
				}
			}.runTaskTimer(Main.getInstance(), 0, 1);

			Inventory chestinv = chest.getBlockInventory();
			ArrayList<Item> items = new ArrayList<Item>();
			for (int i = 0; i < 27; i++) {
				ItemStack item = chestinv.getItem(i);
				if (item == null || item.getType() != Material.AIR) {
					continue;
				}
				
				String key = ItemUtil.getKey(item);
				if (key == null || key.isEmpty()) {
					String name;
					try {
						name = item.getItemMeta().getDisplayName();
					}catch (Exception e) {
						name = item.getType().toString();
					}
					player.sendMessage(name + ChatColor.RED + "が/itemに登録されてるか確認してね");
					player.sendMessage("微妙に違うときもあるから/item getした直後のを使ってね");
					return true;
				}else {
					items.add(new Item(key, item.getAmount()));
				}
			}

			LinkedHashSet<Winning> winnings = new LinkedHashSet<Winning>();
			if (args[1].equalsIgnoreCase("legendary") || args[1].equalsIgnoreCase("epic") || args[1].equalsIgnoreCase("rare") || args[1].equalsIgnoreCase("uncommon")) {
				for (Winning winning : gatya.getWinnings()) {
					if (!winning.getRare().equalsIgnoreCase(args[1])) {
						winnings.add(winning);
					}
				}
			}else {
				for (Winning winning : gatya.getWinnings()) {
					if (winning.getRare().equalsIgnoreCase("legendary") || winning.getRare().equalsIgnoreCase("epic") || winning.getRare().equalsIgnoreCase("rare") || winning.getRare().equalsIgnoreCase("uncommon")) {
						winnings.add(winning);
					}
				}
			}

			for (Item item : items) {
				ItemStack itemstack = ItemUtil.getItemJava(item.name());
				ItemType type = ItemType.item;
				try {
					for (String line : itemstack.getItemMeta().getLore()) {
						if (line.contains("Primary")) {
							type = ItemType.primary;
							break;
						}else if (line.contains("Secondary")) {
							type = ItemType.secondary;
							break;
						}else if (line.contains("Melee")) {
							type = ItemType.melee;
							break;
						}else if (line.contains("Hat")) {
							type = ItemType.head;
							break;
						}
					}
				}catch (Exception e) {}
				winnings.add(new Winning(type, item.name(), item.amount(), args[1], chance / items.size()));
			}
			gatya.setWinnings(winnings);
			gatya.save();

			String rare;
			if (args[1].equalsIgnoreCase("legendary")) {
				rare = ChatColor.GOLD + "Legendary";
			}else if (args[1].equalsIgnoreCase("epic")) {
				rare = ChatColor.LIGHT_PURPLE + "Epic";
			}else if (args[1].equalsIgnoreCase("rare")) {
				rare = ChatColor.AQUA + "Rare";
			}else if (args[1].equalsIgnoreCase("uncommon")) {
				rare = ChatColor.GREEN + "Uncommon";
			}else {
				rare = ChatColor.WHITE + "Common";
			}

			player.sendMessage(ChatColor.GREEN + "おっけー！！ レア度" + rare + ChatColor.GREEN + "のアイテムを設定したよん");
			return true;
		}
	}

	private static void help(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "/gatya " + ChatColor.AQUA + "<ガチャ名> <レア度> <確率>");
		sender.sendMessage("見ているチェストの中身を景品にします");
		sender.sendMessage("レア度は " + ChatColor.GOLD + "legendary " + ChatColor.LIGHT_PURPLE + "epic " + ChatColor.AQUA + "rare " + ChatColor.GREEN + "uncommon " + ChatColor.WHITE + "common");
		sender.sendMessage("たとえば確率をlegendaryが1、epicが4、rareが5、");
		sender.sendMessage("uncommonが15、commonが25にすると");
		sender.sendMessage("legendaryが2%、epicが8%、rareが10%・・・");
		sender.sendMessage("のようになるので合計が100%じゃなくても大丈夫です");
		sender.sendMessage("また、同じレア度のアイテムが複数あるときは確率が個数で割られます");
		sender.sendMessage("上の例だとepicのアイテムを4個設定するとそれぞれ2%になります");
	}
}
