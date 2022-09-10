package peru.sugoi.perugatya;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.yaml.snakeyaml.Yaml;

public class Gatya {
	private static HashMap<String, Gatya> gatyas = new HashMap<String, Gatya>();

	private String name;
	private TradeItem cost;
	private LinkedHashSet<Winning> winnings;
	private double totalchance;
	private boolean containslegendary;
	private boolean containsepic;
	private boolean containsrare;
	private boolean containsuncommon;
	private boolean containscommon;

	public static boolean exist(String gatyaname) {
		return gatyas.containsKey(gatyaname);
	}

	public static Gatya getGatya(String gatyaname) {
		if (gatyas.containsKey(gatyaname)) {
			return gatyas.get(gatyaname);
		}
		return null;
	}

	public static Collection<Gatya> getGatyas(){
		return gatyas.values();
	}

	@SuppressWarnings("unchecked")
	public static void loadConfig() {
		gatyas = new HashMap<String, Gatya>();

		Yaml yaml = new Yaml();

		File folder = new File("plugins/PokkePeru/Gatya/");
		File[] files = folder.listFiles();

		for (File file : files) {
			if (!file.isFile()) {
				continue;
			}
			
			if (file.getName().startsWith("-")) {
				System.out.println("Ignoring " + file.getName());
				continue;
			}
			
			try (InputStream in = Files.newInputStream(file.toPath())) {
				System.out.println("loading " + file.getName());
				Map<String, Object> gatyatypeloader = (Map<String, Object>) yaml.loadAs(in, Map.class);
				Map<String, Object> costloader = (Map<String, Object>) gatyatypeloader.get("cost");

				TradeItem cost;
				if (costloader != null) {
					try {
						ItemType type;
						try {
							type = ItemType.valueOf(((String) costloader.get("type")).toLowerCase());
						}catch(Exception e) {
							type = ItemType.item;
						}
						String name = (String) costloader.get("name");
						int amount = (int) costloader.get("amount");


						cost = new TradeItem(type, name, amount);
					} catch (Exception e) {
						cost = null;
					}
				}else {
					cost = null;
				}

				List<Map<String, Object>> winningsloader = (List<Map<String, Object>>) gatyatypeloader.get("winnings");
				Set<Winning> winnings = new LinkedHashSet<Winning>();

				try {
					for(Map<String, Object> winningloader : winningsloader) {
						ItemType type;
						try {
							type = ItemType.valueOf(((String) winningloader.get("type")).toLowerCase());
						}catch(Exception e) {
							type = ItemType.item;
						}

						String name;
						try {
							name = (String)winningloader.get("name");
						}catch(Exception e) {
							name = null;
						}

						String rare;
						try {
							rare = (String) winningloader.get("rare");
						}catch (Exception ex) {
							rare = "common";
						}
						if (rare == null) {
							rare = "common";
						}

						float chance = -1;
						try {
							chance = (float)((double) winningloader.get("chance"));
						}catch (Exception e) {
							chance = (int) winningloader.get("chance");
						}

						int amount;
						try {
							amount = (int) winningloader.get("amount");
						} catch (Exception e) {
							amount = 1;
						}

						if (type != null && name != null && chance > 0) {
							winnings.add(new Winning(type, name, amount, rare, chance));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				String gatyaname = file.getName().substring(0, file.getName().length() - 4);

				new Gatya(gatyaname, cost, winnings);

				System.out.println("Successfully loaded " + file.getName());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(ChatColor.RED + "Failed loading " + file.getName());
			}
		}


	}

	public Gatya(String name, TradeItem cost, Set<Winning> winnings){
		this.name = name;
		this.cost = cost;
		try {
			this.winnings = new LinkedHashSet<Winning>(winnings);
			for(Winning winning : winnings) {
				totalchance = totalchance + winning.getChance();
				if (winning.getRare().equalsIgnoreCase("legendary")) {
					containslegendary = true;
				}else if (winning.getRare().equalsIgnoreCase("epic")) {
					containsepic = true;
				}else if (winning.getRare().equalsIgnoreCase("rare")) {
					containsrare = true;
				}else if (winning.getRare().equalsIgnoreCase("uncommon")) {
					containsuncommon = true;
				}else {
					containscommon = true;
				}
			}
		} catch (Exception e) {
			this.winnings = new LinkedHashSet<Winning>();
		}

		gatyas.put(name, this);
	}

	public boolean containsCommon() {
		return containscommon;
	}

	public boolean containsEpic() {
		return containsepic;
	}

	public boolean containsLegendary() {
		return containslegendary;
	}

	public boolean containsRare() {
		return containsrare;
	}

	public boolean containsUncommon() {
		return containsuncommon;
	}

	public TradeItem getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public double getTotalChance() {
		return totalchance;
	}

	public LinkedHashSet<Winning> getWinnings() {
		return winnings;
	}

	public Winning roll() {
		double deme = totalchance * Math.random();
		for(Winning winning : winnings) {
			deme = deme - winning.getChance();
			if (deme <= 0) {
				return winning;
			}
		}
		System.err.println("[PokkePeru] error at Gatya.roll()");
		return null;
	}

	public void save() {
		try {
			FileWriter fw = new FileWriter("plugins/PokkePeru/Gatya/" + getName() + ".yml");
			fw.write("cost:\n"
					+ "  type: " + cost.getType() + "\n"
					+ "  name: " + cost.name() + "\n"
					+ "  amount: " + cost.amount() + "\n"
					+ "winnings:\n");
			for (Winning winning : winnings) {
				fw.write("  - type: " + winning.getType() + "\n"
						+ "    name: " + winning.getName() + "\n"
						+ "    rare: " + winning.getRare() + "\n"
						+ "    amount: " + winning.getAmount() + "\n"
						+ "    chance: " + winning.getChance() + "\n");
			}

			fw.close();
		} catch (Exception ex) {
			System.err.println("Saving " + getName() + " failed");
		}
	}

	public void setCost(TradeItem cost) {
		this.cost = cost;
	}

	public void setWinnings(LinkedHashSet<Winning> winnings) {
		this.winnings = winnings;
		totalchance = 0;
		for(Winning winning : winnings) {
			totalchance = totalchance + winning.getChance();
			if (winning.getRare().equalsIgnoreCase("legendary")) {
				containslegendary = true;
			}else if (winning.getRare().equalsIgnoreCase("epic")) {
				containsepic = true;
			}else if (winning.getRare().equalsIgnoreCase("rare")) {
				containsrare = true;
			}else if (winning.getRare().equalsIgnoreCase("uncommon")) {
				containsuncommon = true;
			}else {
				containscommon = true;
			}
		}
	}
}
