package ga.ishadey.autorespawnplus;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ga.ishadey.autorespawnplus.events.PlayerAutoRespawnEvent;
import ga.ishadey.autorespawnplus.events.PlayerPreAutoRespawnEvent;
import ga.ishadey.web.SpigotUpdater;

public class Main extends JavaPlugin implements Listener {
	
	public SpigotUpdater spu;
	private File file;
	private FileConfiguration config;

	public void onEnable() {
		spu = new SpigotUpdater(this, 14412);
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("arp").setExecutor(new Cmd(this));
		file = new File(getDataFolder(), "config.yml");
		config = new YamlConfiguration();
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			saveResource("config.yml", false);
		}
		try {
			config.load(file);
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Could not load config.yml, exception: " + e.getCause());
			getLogger().log(Level.WARNING, "Please check your config for errors: ");
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) {
		final Location deathLoc = e.getEntity().getLocation();
		if ((config.getBoolean("enabled", true)) && (e.getEntity().hasPermission("autorespawn.respawn"))) {
			final Player player = e.getEntity();
			List<String> worlds = config.getStringList("blocked-worlds");
			if (worlds != null) {
				for (int i = 0; i < worlds.size(); i++) {
					if (worlds.get(i).equalsIgnoreCase(player.getWorld().getName()))
						return;
				}
			}
			PlayerPreAutoRespawnEvent ppare = new PlayerPreAutoRespawnEvent(player, deathLoc);
			Bukkit.getPluginManager().callEvent(ppare);
			if (ppare.isCancelled())
				return;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					player.spigot().respawn();
					Location respawnLoc = e.getEntity().getLocation();
					Bukkit.getPluginManager()
							.callEvent(new PlayerAutoRespawnEvent(e.getEntity(), deathLoc, respawnLoc));
				}
			}, 1L);
		}
	}
}