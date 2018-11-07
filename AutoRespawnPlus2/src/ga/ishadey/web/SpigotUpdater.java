package ga.ishadey.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * Updater class made fully by iShadey (Spigot: TrollStar12345).
 * 
 */

public class SpigotUpdater implements Listener {

	private JavaPlugin plugin;
	private String version;
	private long nextInformationCheck;
	private int resource;

	public SpigotUpdater(JavaPlugin plugin, int resource) {
		this.plugin = plugin;
		this.nextInformationCheck = 0L;
		this.resource = resource;
		this.version = "0";
		refresh(true);
		Bukkit.getPluginManager().registerEvents(this, plugin);
		new BukkitRunnable() {
			@Override
			public void run() {
				notifyUpdate();
			}
		}.runTaskTimer(plugin, 18000, 18000);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				notifyUpdate(e.getPlayer());
			}
		}.runTaskLater(plugin, 80);
	}

	public String getRawLatestVersion() {
		return version;
	}

	public void checkForUpdates() throws Exception {
		plugin.getLogger().info("Checking for updates...");
		if (this.canRefresh())
			this.refresh(false);
	}

	public boolean hasUpdate() {
		if (canRefresh()) {
			try {
				checkForUpdates();
			} catch (Exception e) {
				System.out.println("[" + plugin.getName() + " Failed to check for update.");
				return false;
			}
		}
		return !version.equalsIgnoreCase(plugin.getDescription().getVersion());
	}

	public String getLatestVersion() {
		return isBeta() ? version.replace("-BETA", "") : version;
	}

	public boolean isBeta() {
		return version.endsWith("-BETA");
	}

	public int getVersionsBehind() {
		return Integer.parseInt(version.replaceAll("[^0-9]", ""))
				- Integer.parseInt(plugin.getDescription().getVersion().replaceAll("[^0-9]", ""));
	}

	public boolean canRefresh() {
		return nextInformationCheck <= System.currentTimeMillis();
	}

	public void refresh(boolean force) {
		if (!force && nextInformationCheck > System.currentTimeMillis())
			return;
		try {
			URLConnection con = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resource)
					.openConnection();
			version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
		} catch (Exception e) {
		}
	}

	public void notifyUpdate() {
		try {
			if (!hasUpdate())
				return;
			plugin.getLogger()
					.info("A" + (isBeta() ? " beta" : "n") + " update is available! v"
							+ plugin.getDescription().getVersion() + " -> v" + version + ". You are " + getVersionsBehind()
							+ " versions behind. Update at https://spigotmc.org/resources/" + resource);
			for (Player p : Bukkit.getOnlinePlayers())
				notifyUpdate(p);
		} catch (Exception e) {
			System.out.println("[" + plugin.getName() + " Failed to check for update.");
		}
	}

	public void notifyUpdate(Player arg0) {
		try {
			if (!hasUpdate() || !arg0.isOp())
				return;
			arg0.sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&8[&6" + plugin.getName() + "&8] &eA" + (isBeta() ? " beta" : "n") + " update is available! v"
							+ plugin.getDescription().getVersion() + " -> v" + version + ". You are " + getVersionsBehind()
							+ " versions behind. Update at https://spigotmc.org/resources/" + resource));
		} catch (Exception e) {
			System.out.println("[" + plugin.getName() + " Failed to perform an update task.");
		}
	}
}