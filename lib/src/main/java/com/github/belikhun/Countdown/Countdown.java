package com.github.belikhun.Countdown;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.github.belikhun.Countdown.Commands.CountdownCommand;
import com.github.belikhun.Countdown.Commands.ShutdownCommand;
import com.mojang.brigadier.tree.LiteralCommandNode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import net.md_5.bungee.api.ChatColor;

public class Countdown extends JavaPlugin {
	public static Countdown instance;
	public static PluginManager manager;

	public Commodore commodore;
	public Logger log;

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();
		manager = Bukkit.getServer().getPluginManager();

		// Register player join/quit events.
		manager.registerEvents(new PlayerEvent(), this);

		PluginCommand countdown = getCommand("countdown");
		countdown.setExecutor(new CountdownCommand());

		PluginCommand shutdown = getCommand("shutdown");
		shutdown.setExecutor(new ShutdownCommand());

		// Check if brigadier is supported
        if (CommodoreProvider.isSupported()) {
            commodore = CommodoreProvider.getCommodore(this);
			loadCommodore(countdown);
			loadCommodore(shutdown);
        } else {
			log.warning("Commodore is not supported on this server!");
		}
	}
	
	@Override
	public void onDisable() {
		if (ShutdownCommand.instance != null)
			ShutdownCommand.instance.cancel();

		CountdownCommand.stopAll();
	}

	public void loadCommodore(Command command) {
		String filename = command.getName() + ".commodore";

		try {
			InputStream file = getResource("commodores/" + filename);

			if (file == null)
				throw new FileNotFoundException("File " + file + " does not exist!");

			LiteralCommandNode<?> commodoreFile = CommodoreFileFormat.parse(getResource("commodores/" + filename));
			commodore.register(command, commodoreFile);
			log.info("Loaded commodore file " + filename);
		} catch(Exception e) {
			log.warning("Cannot load commodore file " + filename);
			log.warning(e.getMessage());
		}
	}

	public static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static int parseTime(String input) {
		try {
			if (input.endsWith("d")) {
				return Integer.parseInt(input.replace("d", "")) * 86400;
			} else if (input.endsWith("h")) {
				return Integer.parseInt(input.replace("h", "")) * 3600;
			} else if (input.endsWith("m")) {
				return Integer.parseInt(input.replace("m", "")) * 60;
			} else {
				return Integer.parseInt(input);
			}
		} catch(NumberFormatException e) {
			return -1;
		}
	}
}
