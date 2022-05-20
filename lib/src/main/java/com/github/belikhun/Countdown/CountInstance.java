package com.github.belikhun.Countdown;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

public class CountInstance {
	public static ChatColor timeColor = ChatColor.of("#abcfff");
	public static ArrayList<CountInstance> instances = new ArrayList<>();

	public BossBar bar;
	public BukkitTask task;
	public boolean ended = false;
	public boolean cancelled = false;

	public interface CountdownCallback {
		void begin(BossBar bar);
		void update(BossBar bar, double remain);
		void complete(BossBar bar);
	}

	public CountInstance(int seconds, CountdownCallback callback) {
		long target = System.currentTimeMillis() + (seconds * 1000);
		bar = Bukkit.createBossBar("CountDown", BarColor.GREEN, BarStyle.SOLID);
		
		task = new BukkitRunnable() {
			public BarColor color = BarColor.GREEN;

			@Override
			public void run() {
				long current = System.currentTimeMillis();
				double remain = (double) (target - current) / 1000d;
				double progress = remain / seconds;
				
				if (progress >= 0) {
					bar.setProgress(progress);
	
					if (progress < .2)
						color = BarColor.RED;
					else if (progress < .6)
						color = BarColor.YELLOW;
	
					if (bar.getColor() != color)
						bar.setColor(color);

					callback.update(bar, remain);
				} else {
					if (!ended) {
						bar.setColor(BarColor.BLUE);
						bar.setProgress(1);
						callback.complete(bar);
					}

					if (remain < -4.8)
						bar.setProgress(0);

					ended = true;
					if (remain < -5) {
						cancel();
						bar.setVisible(false);
						bar.removeAll();
					}
				}
			}
		}.runTaskTimer(Countdown.instance, 0, 1);

		for (Player player : Bukkit.getOnlinePlayers())
			bar.addPlayer(player);

		instances.add(this);
		bar.setVisible(true);
		callback.begin(bar);
	}

	public void cancel() {
		if (cancelled)
			return;
		
		task.cancel();
		bar.setVisible(false);
		bar.removeAll();
		instances.remove(this);
		cancelled = true;
	}

	public void stop(String reason) {
		if (cancelled)
			return;

		task.cancel();
		bar.setTitle(Countdown.colorize(reason));
		bar.setColor(BarColor.PURPLE);
		instances.remove(this);
		cancelled = true;

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				bar.setVisible(false);
				bar.removeAll();
			}
		}, 5000);
	}

	public static String readableTime(double seconds) {
		return (seconds > 120d)
			? String.format("%s%.1fm&r", timeColor, (seconds / 60d))
			: String.format("%s%.3fs&r", timeColor, seconds);
	}

	public static void handlePlayerJoin(Player player) {
		for (CountInstance instace : instances)
			instace.bar.addPlayer(player);
	}

	public static void handlePlayerQuit(Player player) {
		for (CountInstance instace : instances)
			instace.bar.removePlayer(player);
	}
}
