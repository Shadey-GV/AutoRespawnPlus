package me.ishadey.autorespawn;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class Cmd implements CommandExecutor {
	
	private Main plugin;

	public Cmd(Main pl) {
		plugin = pl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length == 0) {
			info(sender);
			return true;
		}
		
		if (args.length >= 1 && (sender.hasPermission("autorespawn.help") || sender.hasPermission("autorespawn.enable")
				|| sender.hasPermission("autorespawn.disable") || sender.hasPermission("autorespawn.toggle")
				|| sender.hasPermission("autorespawn.check") || sender.hasPermission("autorespawn.update"))) {
			
			if (args[0].equalsIgnoreCase("help") && perm(sender, "help")) {
				help(sender);
				return true;
			}
			
			if (args[0].equalsIgnoreCase("enable") && perm(sender, "enable")) {
				setEnabled(true);
				sender.sendMessage(cc("&aAuto Respawn enabled!"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("disable") && perm(sender, "disable")) {
				setEnabled(false);
				sender.sendMessage(cc("&cAuto Respawn disabled!"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("toggle") && perm(sender, "toggle")) {
				if (plugin.getConfig().getBoolean("enabled")) {
					setEnabled(false);
					sender.sendMessage(cc("&cAuto Respawn disabled!"));
					return true;
				}
				setEnabled(true);
				sender.sendMessage(cc("&aAuto Respawn enabled!"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("check") && perm(sender, "check")) {
				if (plugin.spu.needsUpdate()) {
					sender.sendMessage(cc("&aThere is a new update available! Type &b/arp update&a to update!"));
					return true;
				}
				sender.sendMessage(cc("&aYou're all up to date."));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("update") && perm(sender, "update")) {
				plugin.spu.update();
				sender.sendMessage(cc("&aPlugin updated! Please restart server to apply updates."));
				return true;
			}
			
			help(sender);
			return true;
			
		} else {
			info(sender);
		}
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
		if (perm(sender, "update"))
			sender.sendMessage(cc("&b/arp update &a- &7Force update"));
	}

	private void info(CommandSender sender) {
		String v = plugin.getDescription().getVersion();
		sender.sendMessage(cc("&8&m+---------&r&8[&b AutoRespawnPlus " + v + " &8]&m---------+"));
		sender.sendMessage(" ");
		sender.sendMessage(cc("&7Author: &aiShadey // TrollStar12345"));
		sender.sendMessage(cc("&7Version: &a" + v));
		sender.sendMessage(cc("&ahttp://ishadey.ga/pl/AutoRespawnPlus"));
		sender.sendMessage(" ");
	}

	public void setEnabled(boolean bool) {
		plugin.getConfig().set("enabled", bool);
		plugin.saveConfig();
	}
}
