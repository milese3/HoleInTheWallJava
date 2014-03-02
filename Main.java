package me.JoelyMo101.holeinthewall;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

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
			if (args.length < 1 || args[0].equalsIgnoreCase("help"))
			{
				this.helpScreen(p);
				return true;
			}
			else if (args[0].equalsIgnoreCase("join"))
			{
				if (queue.contains(p.getName()))
				{
					p.sendMessage(ChatColor.RED + "You are already in the queue!");
				}
				else
				{
					queue.add(p.getName());
					p.sendMessage(ChatColor.GREEN + "Successfully joined the queue!");
				}
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
		p.sendMessage(ChatColor.DARK_GREEN + "/hitw " + ChatColor.GREEN + " - Leave the game or queue you are in.");		
	}
}
