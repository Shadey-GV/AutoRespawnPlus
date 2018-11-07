package me.ishadey.autorespawn;

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

import me.ishadey.autorespawn.events.PlayerAutoRespawnEvent;
import me.ishadey.autorespawn.events.PlayerPreAutoRespawnEvent;
import me.ishadey.web.SpigotPluginUpdater;

public class Main extends JavaPlugin implements Listener {

	public SpigotPluginUpdater spu = new SpigotPluginUpdater(this, "AutoRespawnPlus");

	private File file;
	private FileConfiguration config;

	public void onEnable() {
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
			getLogger().log(Level.WARNING, "Report this to iShadey:");
			e.printStackTrace();
		}
		if (getConfig().getBoolean("checkForUpdates")) {
			if (spu.needsUpdate()) {
				if (getConfig().getBoolean("autoUpdate"))
					spu.update();
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Location deathLoc = e.getEntity().getLocation();
		if (config.getBoolean("enabled")) {
			if (e.getEntity().hasPermission("autorespawn.respawn")) {
				Player player = e.getEntity();
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
					@Override
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
}
