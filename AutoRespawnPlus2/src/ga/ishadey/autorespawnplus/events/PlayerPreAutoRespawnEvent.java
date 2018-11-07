package ga.ishadey.autorespawnplus.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerPreAutoRespawnEvent extends Event implements Cancellable {
	private Player p;
	private Location deathLoc;
	private boolean cancelled = false;
	private static final HandlerList handlers = new HandlerList();

	public PlayerPreAutoRespawnEvent(Player p, Location deathLoc) {
		this.p = p;
		this.deathLoc = deathLoc;
	}

	public Player getPlayer() {
		return this.p;
	}

	public Location getDeathLocation() {
		return this.deathLoc;
	}

	public EntityDamageEvent.DamageCause getDeathCause() {
		return this.p.getLastDamageCause().getCause();
	}

	public boolean killedByPlayer() {
		if ((this.p.getLastDamageCause().getEntity() instanceof Player))
			return true;
		if ((this.p.getLastDamageCause().getEntity() instanceof Projectile))
			return ((Projectile) this.p.getLastDamageCause().getEntity()).getShooter() instanceof Player;
		return false;
	}

	public Player getKiller() {
		return this.p.getKiller();
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean arg0) {
		this.cancelled = arg0;
	}
}