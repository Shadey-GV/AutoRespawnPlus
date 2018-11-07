package ga.ishadey.autorespawnplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Cmd implements CommandExecutor {
	private Main plugin;

	public Cmd(Main pl) {
		this.plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			info(sender);
			return true;
		}
		if ((args.length >= 1) && ((sender.hasPermission("autorespawn.help"))
				|| (sender.hasPermission("autorespawn.enable")) || (sender.hasPermission("autorespawn.disable"))
				|| (sender.hasPermission("autorespawn.toggle")) || (sender.hasPermission("autorespawn.check")))) {
			if ((args[0].equalsIgnoreCase("help")) && (perm(sender, "help"))) {
				help(sender);
				return true;
			}
			if ((args[0].equalsIgnoreCase("enable")) && (perm(sender, "enable"))) {
				setEnabled(true);
				sender.sendMessage(cc("&aAuto Respawn enabled!"));
				return true;
			}
			if ((args[0].equalsIgnoreCase("disable")) && (perm(sender, "disable"))) {
				setEnabled(false);
				sender.sendMessage(cc("&cAuto Respawn disabled!"));
				return true;
			}
			if ((args[0].equalsIgnoreCase("toggle")) && (perm(sender, "toggle"))) {
				if (plugin.getConfig().getBoolean("enabled")) {
					setEnabled(false);
					sender.sendMessage(cc("&cAuto Respawn disabled!"));
					return true;
				}
				setEnabled(true);
				sender.sendMessage(cc("&aAuto Respawn enabled!"));
				return true;
			}
			if ((args[0].equalsIgnoreCase("check")) && (perm(sender, "check"))) {
				try {
					plugin.spu.refresh(true);
					if (plugin.spu.hasUpdate()) {
						sender.sendMessage(cc("&aVersion " + plugin.spu.getLatestVersion()
								+ " is now available! You are " + plugin.spu.getVersionsBehind()
								+ " versions behind. Update at http://joshuahagon.ga/url/autorespawnplus"));
						return true;
					}
					sender.sendMessage(cc("&aYou're all up to date."));
				} catch (Exception e) {
					sender.sendMessage(cc("&cAn error occured whilst checking for updates! Check console."));
					e.printStackTrace();
				}
				return true;
			}
			help(sender);
			return true;
		}
		info(sender);
		return false;
	}

	private String cc(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	private boolean perm(CommandSender sender, String perm) {
		return sender.hasPermission("autorespawn." + perm);
	}

	private void help(CommandSender sender) {
		String v = plugin.getDescription().getVersion();
		sender.sendMessage(cc("&8&m+---------&r&8[&b AutoRespawnPlus " + v + " &8]&m---------+"));
		sender.sendMessage(" ");
		sender.sendMessage(cc("&b/arp &a- &7Main command"));
		if (perm(sender, "help"))
			sender.sendMessage(cc("&b/arp help &a- &7Show this help page"));
		if (perm(sender, "enable"))
			sender.sendMessage(cc("&b/arp enable &a- &7Enable auto respawning"));
		if (perm(sender, "disable"))
			sender.sendMessage(cc("&b/arp disable &a- &7Disable auto respawning"));
		if (perm(sender, "toggle"))
			sender.sendMessage(cc("&b/arp toggle &a- &7Toggle auto respawning"));
		if (perm(sender, "check"))
			sender.sendMessage(cc("&b/arp check &a- &7Check for updates"));
	}

	private void info(CommandSender sender) {
		String v = plugin.getDescription().getVersion();
		sender.sendMessage(cc("&8&m+---------&r&8[&b AutoRespawnPlus " + v + " &8]&m---------+"));
		sender.sendMessage(" ");
		sender.sendMessage(cc("&7Author: &aiShadey // TrollStar12345"));
		sender.sendMessage(cc("&7Version: &a" + v));
		sender.sendMessage(cc("&ahttp://joshuahagon.ga/url/autorespawnplus"));
		sender.sendMessage(" ");
	}

	public void setEnabled(boolean bool) {
		this.plugin.getConfig().set("enabled", Boolean.valueOf(bool));
		this.plugin.saveConfig();
	}
}