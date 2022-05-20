package com.github.belikhun.Countdown.Commands;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.github.belikhun.Countdown.CountInstance;
import com.github.belikhun.Countdown.Countdown;
import com.github.belikhun.Countdown.CountInstance.CountdownCallback;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import net.md_5.bungee.api.ChatColor;

public class ShutdownCommand implements CommandExecutor {
	public static CountInstance instance;
	
	public ShutdownCommand() { }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("countdown.shutdown") && !(sender instanceof ConsoleCommandSender))
			return false;

		if (args.length < 1) {
			sender.sendMessage(Countdown.colorize("&fSử dụng &a/shutdown &b<length> &7[message]"));
			return true;
		}

		if (args[0].equalsIgnoreCase("cancel")) {
			if (instance == null) {
				sender.sendMessage(Countdown.colorize("&cKhông có lịch tắt máy chủ."));
				return true;
			}

			instance.stop("&a&lTắt Máy Chủ Đã Được Hủy!");
			instance.bar.setColor(BarColor.GREEN);
			Bukkit.broadcastMessage("Đã hủy tắt máy chủ.");
			instance = null;
			return true;
		}

		if (instance != null) {
			sender.sendMessage(Countdown.colorize("&cTắt máy chủ đã được lên lịch!&f Hủy lịch bằng &a/shutdown &ecancel&f."));
			return true;
		}

		String message = null;
		int length = Integer.parseInt(args[0]);

		if (args.length >= 2)
			message = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), ' ');
		
		start(message, length);
		return true;
	}

	public void start(String reason, int seconds) {
		if (instance != null)
			return;

		instance = new CountInstance(seconds, new CountdownCallback() {

			@Override
			public void begin(BossBar bar) {
				String message = (reason != null)
					? String.format("&rMáy chủ sẽ tắt sau &b%ds &rnữa! &7(lí do: %s)", seconds, reason)
					: String.format("&rMáy chủ sẽ tắt sau &b%ds &rnữa!", seconds);
				
				Bukkit.broadcastMessage(Countdown.colorize(message));
			}

			@Override
			public void update(BossBar bar, double remain) {
				ChatColor textColor = ChatColor.of("#ffabc0");
				ChatColor numberColor = ChatColor.of("#abcfff");

				String message = (reason != null)
					? String.format("&r%s&lTắt Máy Chủ&r sau %s%.3fs &7(%s)", textColor, numberColor, remain, reason)
					: String.format("&r%s&lTắt Máy Chủ&r sau %s%.3fs", textColor, numberColor, remain);

				bar.setTitle(Countdown.colorize(message));
			}

			@Override
			public void complete(BossBar bar) {
				String message = String.format("&r&e&lĐang Tắt Máy Chủ...&r");
				Bukkit.broadcastMessage("Đang tắt máy chủ...");
				bar.setColor(BarColor.YELLOW);
				bar.setTitle(Countdown.colorize(message));
				instance = null;

				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						Bukkit.shutdown();
					}
				}, 3000);
			}
		});
	}
}
