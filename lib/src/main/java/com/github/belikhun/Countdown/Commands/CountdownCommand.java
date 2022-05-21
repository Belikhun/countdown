package com.github.belikhun.Countdown.Commands;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.belikhun.Countdown.CountInstance;
import com.github.belikhun.Countdown.Countdown;
import com.github.belikhun.Countdown.CountInstance.CountdownCallback;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class CountdownCommand implements CommandExecutor {
	protected class Instance {
		public int id;
		public String title;
		public CountInstance count;

		public Instance(String title) {
			this.title = title;
		}

		public void start(int seconds) {
			count = new CountInstance(seconds, new CountdownCallback() {

				@Override
				public void begin(BossBar bar) {
					
				}
	
				@Override
				public void update(BossBar bar, double remain) {
					String message = String.format("&r&7#%d &a%s&r sau %s", id, title, CountInstance.readableTime(remain));
					bar.setTitle(Countdown.colorize(message));
				}
	
				@Override
				public void complete(BossBar bar) {
					String message = String.format("&r&7#%d &a%s&r đã bắt đầu!", id, title);
					bar.setTitle(Countdown.colorize(message));
				}
			});
		}

		public void stop() {
			count.stop(String.format("Đã hủy bỏ &d%s", title));
			Bukkit.broadcastMessage(Countdown.colorize(
				String.format("Sự kiện &7(#%d) &d%s &fđã bị hủy!", id, title)));
		}
	}
	
	public static ArrayList<Instance> instances = new ArrayList<>();
	public CountdownCommand() { }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("countdown.countdown") && !(sender instanceof ConsoleCommandSender))
			return false;

		if (args.length < 1) {
			sender.sendMessage(Countdown.colorize("&fSử dụng &a/countdown &7<start|stop|stopall>"));
			return true;
		}

		switch (args[0]) {
			case "start":
				if (args.length < 2) {
					sender.sendMessage(Countdown.colorize("&fSử dụng &a/countdown start &7<length> [message]"));
					return true;
				}

				String title = "Sự Kiện Kết Thúc";
				int length = Countdown.parseTime(args[1]);

				if (length < 0) {
					sender.sendMessage(Countdown.colorize("&cThời gian không phải là một số hợp lệ: " + args[1]));
					return true;
				}
		
				if (args.length >= 3)
					title = StringUtils.join(Arrays.copyOfRange(args, 2, args.length), ' ');
				
				countdown(title, length);
				break;
		
			case "stop":
				if (args.length < 2) {
					sender.sendMessage(Countdown.colorize("&fSử dụng &a/countdown stop &7<id>"));
					return true;
				}

				int id = Integer.parseInt(args[1]);

				for (Instance instance : instances) {
					if (instance.id != id)
						continue;

					instance.stop();
					return true;
				}

				sender.sendMessage(Countdown.colorize("&cKhông tìm thấy countdown với ID " + id));
				return true;

			case "stopall":
				stopAll();
				
				break;

			default:
				sender.sendMessage(Countdown.colorize("&cHành động " + args[0] + " không tồn tại! &fSử dụng &a/countdown &7<start|stop|stopall>"));
				return true;
		}

		return true;
	}

	public void countdown(String title, int seconds) {
		Instance instance = new Instance(title);
		instances.add(instance);
		instance.id = instances.indexOf(instance);
		instance.start(seconds);
	}

	public static void stopAll() {
		for (Instance instance : instances)
			instance.stop();
	}
}
