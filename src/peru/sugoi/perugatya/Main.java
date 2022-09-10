package peru.sugoi.perugatya;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
	public static Main instance;

	public static Main getInstance() {
		return instance;
	}

	public Main(){
		instance = this;
	}

	@Override
	public void onDisable() {
		super.onDisable();

	}

	@Override
	public void onEnable() {
		super.onEnable();

		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new EventListener(), this);

		getCommand("gatya").setExecutor(new GatyaCommand());

		Gatya.loadConfig();
		

		new BukkitRunnable() {
			@Override
			public void run() {
				GatyaGamen.tick();
			}
		}.runTaskTimer(this, 0, 1);
	}

}
