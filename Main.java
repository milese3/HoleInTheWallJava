package me.JoelyMo101.holeinthewall;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	
	final String prefix = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "HoleInTheWall" + ChatColor.DARK_PURPLE + "] " + ChatColor.GREEN;
	final String noperms = ChatColor.RED + "You do not have permission to perform this command!";
	
	ArrayList<String> queue = new ArrayList<String>();

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public void onDisable() {
		reloadConfig();
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "The console cannot use Hole In The Wall!");
		}
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("hitw"))
		{
			if (args.length != 1 || args[0].equalsIgnoreCase("help"))
			{
				this.helpScreen(p);
				return true;
			}
			else if (args[0].equalsIgnoreCase("join"))
			{
				this.join(p);
				return true;
			}
			else if (args[0].equalsIgnoreCase("leave"))
			{
				this.leave(p);
				return true;
			}
			else if (args[0].equalsIgnoreCase("waiting") || args[0].equalsIgnoreCase("ingame"))
			{
				if (p.hasPermission("hitw.admin"))
				{
					this.setspawn(args[0]);
					return true;
				}
				else
				{
					p.sendMessage(noperms);
					return true;
				}
			}
			else 
			{
				this.helpScreen(p);
				return true;
			}
		}
		return false;
	}
	
	
	public void helpScreen(Player p)
	{
		p.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "*******" + ChatColor.GREEN + " Hole In The Wall - Help " + ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "*******");
		p.sendMessage(ChatColor.DARK_GREEN + "/hitw help" + ChatColor.GREEN + " - Displays this help screen.");
		p.sendMessage(ChatColor.DARK_GREEN + "/hitw join" + ChatColor.GREEN + " - Join the queue.");
		p.sendMessage(ChatColor.DARK_GREEN + "/hitw leave" + ChatColor.GREEN + " - Leave the game or queue you are in.");
		if (p.hasPermission("hitw.admin"))
		{
			p.sendMessage(ChatColor.RED + "/hitw waiting" + ChatColor.DARK_RED + " - Set the waiting room spawn.");	
			p.sendMessage(ChatColor.RED + "/hitw ingame" + ChatColor.DARK_RED + " - Set the in game spawn.");				
		}
		p.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "**************************************");
	}
	
	public void join(Player p)
	{
		if (!(getConfig().getBoolean("setupcorrectly")))
		{
			p.sendMessage(prefix + ChatColor.RED + "Error: The game has not been setup correctly. Please ask an administrator to fix this.");
			
		}
		else if (queue.contains(p.getName()))
		{
			p.sendMessage(prefix + ChatColor.RED + "You are already in the queue!");
		}
		else if (queue.size() >= 4)
		{
			p.sendMessage(prefix + ChatColor.RED + "Error: The game has already started.");
		}
		else
		{
			queue.add(p.getName());
			p.sendMessage(prefix + "Successfully joined the queue! " + ChatColor.GOLD + queue.size() + ChatColor.YELLOW + "/" + ChatColor.GOLD + "4");
			for (int i = 0; i < queue.size(); i++)
			{
				Player t = Bukkit.getPlayer(queue.get(i));
				if (t != p)
				{
					t.sendMessage(prefix + ChatColor.GOLD + p.getName() + ChatColor.GREEN + " joined the queue. " + ChatColor.GOLD + queue.size() + ChatColor.YELLOW + "/" + ChatColor.GOLD + "4");
				}
			}
			if (queue.size() >= 4)
			{
				for (int i = 0; i < queue.size(); i++)
				{
					Player t = Bukkit.getPlayer(queue.get(i));
					t.sendMessage(prefix + "Starting game...");
				}
				this.gameLoop();
			}
		}
	}
	
	public void leave(Player p)
	{
		if (!(queue.contains(p.getName())))
		{
			p.sendMessage(prefix + ChatColor.RED + "You are not in the queue!");
		}
		else
		{
			queue.remove(p.getName());
			p.sendMessage(prefix + "Successfully left the queue!");
			for (int i = 0; i < queue.size(); i++)
			{
				Player t = Bukkit.getPlayer(queue.get(i));
				if (t != p)
				{
					t.sendMessage(prefix + ChatColor.GOLD + p.getName() + ChatColor.GREEN + " left the queue. " + ChatColor.GOLD + queue.size() + ChatColor.YELLOW + "/" + ChatColor.GOLD + "4");
				}
			}
		}
	}
	
	
	public void setspawn(String type)
	{
		if (type.equalsIgnoreCase("waiting"))
		{
			//Set waiting spawn
		}
		else if (type.equalsIgnoreCase("ingame"))
		{
			//Set in game spawn
		}
	}
	
	
	
	
	
	public void gameLoop()
	{
		for (int i = 0; i < queue.size(); i++)
		{
			Player t = Bukkit.getPlayer(queue.get(i));
		}
	}
	
	
}
