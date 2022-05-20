package com.github.belikhun.Countdown.Commands;

import java.util.Arrays;

import com.github.belikhun.Countdown.CountInstance;
import com.github.belikhun.Countdown.Countdown;
import com.github.belikhun.Countdown.CountInstance.CountdownCallback;

import org.apache.commons.lang.StringUtils;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CountdownCommand implements CommandExecutor {
	public CountdownCommand() { }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("countdown.countdown") && !(sender instanceof ConsoleCommandSender))
			return false;

		if (args.length < 1) {
			sender.sendMessage(Countdown.colorize("&fSử dụng &a/countdown &b<length> &7[message]"));
			return true;
		}

		String title = "Đếm Ngược Kết Thúc";
		int length = Integer.parseInt(args[0]);

		if (args.length >= 2)
			title = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), ' ');
		
		countdown(title, length);
		return true;
	}

	public void countdown(String title, int seconds) {
		new CountInstance(seconds, new CountdownCallback() {

			@Override
			public void begin(BossBar bar) {
				
			}

			@Override
			public void update(BossBar bar, double remain) {
				String message = String.format("&r&a%s&r sau %s", title, CountInstance.readableTime(remain));
				bar.setTitle(Countdown.colorize(message));
			}

			@Override
			public void complete(BossBar bar) {
				String message = String.format("&r&a%s&r đã bắt đầu!", title);
				bar.setTitle(Countdown.colorize(message));
			}
		});
	}
}
