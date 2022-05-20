package com.github.belikhun.Countdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvent implements Listener {
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CountInstance.handlePlayerJoin(event.getPlayer());
    }

	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CountInstance.handlePlayerQuit(event.getPlayer());
    }
}
