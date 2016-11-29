package me.ishadey.autorespawn.events;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Deprecated
public class PlayerAutoRespawnEvent extends Event {
	
	private Player p;
	private Location deathLoc;
	private Location respawnLoc;
	private int respawnDelayTicks = 0;
	
	public PlayerAutoRespawnEvent(Player p, Location deathLoc, Location respawnLoc) {
		this.p = p;
		this.deathLoc = deathLoc;
		this.respawnLoc = respawnLoc;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Location getDeathLocation() {
		return deathLoc;
	}
	
	public Location getRespawnLocation() {
		return respawnLoc;
	}
	
	public DamageCause getDeathCause() {
		return p.getLastDamageCause().getCause();
	}
	
	@Deprecated
	public int getRespawnDelayTicks() {
		return respawnDelayTicks;
	}
	
	@Deprecated
	public double getRespawnDelaySeconds() {
		return (Double.parseDouble(respawnDelayTicks + ".0") / 20);
	}
	
	public boolean killedByPlayer() {
		if (p.getLastDamageCause().getEntity() instanceof Player) {
			return true;
		}
		if (p.getLastDamageCause().getEntity() instanceof Arrow) {
			Arrow a = (Arrow) p.getLastDamageCause().getEntity();
			if (a.getShooter() instanceof Player) {
				return true;
			}
			return false;
		}
		if (p.getLastDamageCause().getEntity() instanceof Snowball) {
			Snowball a = (Snowball) p.getLastDamageCause().getEntity();
			if (a.getShooter() instanceof Player) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public Player getKiller() {
		return p.getKiller();
	}
	
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
