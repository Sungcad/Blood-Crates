package me.sungcad.bloodcrates.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BloodCrateSpawnEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player killer, killed;
    Location location;

    public BloodCrateSpawnEvent(Player killer, Player killed) {
        this.killer = killer;
        this.killed = killed;
        location = killed.getLocation();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Player getKiller() {
        return killer;
    }

    public Player getKilled() {
        return killed;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
