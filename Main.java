package me.JoelyMo101.holeinthewall;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Main extends JavaPlugin implements Listener {
	
	final String prefix = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "HoleInTheWall" + ChatColor.DARK_PURPLE + "] " + ChatColor.GREEN;
	final String noperms = ChatColor.RED + "You do not have permission to perform this command!";
	
	public boolean ingame = false;
	
	ArrayList<String> queue = new ArrayList<String>();
	ArrayList<String> players = new ArrayList<String>();
	HashMap<String, String> playerno = new HashMap<String, String>();

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
			else if (args[0].equalsIgnoreCase("waiting") || args[0].equalsIgnoreCase("ingame") || args[0].equalsIgnoreCase("wall"))
			{
				if (p.hasPermission("hitw.admin"))
				{
					this.setspawn(args[0], p);
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
			p.sendMessage(ChatColor.RED + "/hitw wall" + ChatColor.DARK_RED + " - Set the wall with a world edit selection.");				
		}
		p.sendMessage(ChatColor.GREEN + "" + ChatColor.STRIKETHROUGH + "*************************************");
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
		else
		{
			boolean alreadyin = false;
			for (int i = 0; i < playerno.size(); i++)
			{
				Player t = Bukkit.getPlayer(playerno.get(i + ""));
				if (p == t)
				{
					alreadyin = true;
				}
			}
			if (alreadyin)
			{
				p.sendMessage(prefix + ChatColor.RED + "You are already in a game!");
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
				if (queue.size() >= 4 && !(ingame))
				{
					for (int i = 0; i < 4; i++)
					{
						Player t = Bukkit.getPlayer(queue.get(i));
						t.sendMessage(prefix + "Starting game...");
						playerno.put(i + "", t.getName());
					}
					ingame = true;
					this.gameLoop();
				}
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
	
	
	@SuppressWarnings("deprecation")
	public void setspawn(String type, Player p)
	{
		if (type.equalsIgnoreCase("waiting"))
		{
			Location loc = p.getLocation();
			getConfig().set("waitingset", true);
			getConfig().set("waiting.world", loc.getWorld().getName().toString());
			getConfig().set("waiting.x", loc.getX());
			getConfig().set("waiting.y", loc.getY());
			getConfig().set("waiting.z", loc.getZ());
			getConfig().set("waiting.yaw", loc.getYaw());
			getConfig().set("waiting.pitch", loc.getPitch());
			p.sendMessage(prefix + "Waiting room spawn set to your current location!");
			if (getConfig().getBoolean("ingameset") && getConfig().getBoolean("wallset"))
			{
				getConfig().set("setupcorrectly", true);
			}
		}
		
		
		else if (type.equalsIgnoreCase("ingame"))
		{
			Location loc = p.getLocation();
			getConfig().set("ingame.world", loc.getWorld().getName().toString());
			getConfig().set("ingame.x", loc.getX());
			getConfig().set("ingame.y", loc.getY());
			getConfig().set("ingame.z", loc.getZ());
			getConfig().set("ingame.yaw", loc.getYaw());
			getConfig().set("ingame.pitch", loc.getPitch());
			getConfig().set("ingameset", true);
			p.sendMessage(prefix + "In game spawn set to your current location!");
			if (getConfig().getBoolean("waitingset") && getConfig().getBoolean("wallset"))
			{
				getConfig().set("setupcorrectly", true);
			}
		}
		
		
		else if (type.equalsIgnoreCase("wall"))
		{
			WorldEditPlugin worldEditPlugin = null;
            worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            if (worldEditPlugin == null){
                p.sendMessage(prefix + ChatColor.RED + "Please install World Edit!");
            }
            else
            {
	            Selection sel = worldEditPlugin.getSelection(p);
	            if (sel == null) {
	            	p.sendMessage(prefix + ChatColor.RED + "Please select an area.");
	            }
	            else
	            {
		            if (sel instanceof CuboidSelection)
		            {
		                Vector min = sel.getNativeMinimumPoint();
		                Vector max = sel.getNativeMaximumPoint();
		                Material mat = Material.STAINED_CLAY;
		                for(int x = min.getBlockX();x <= max.getBlockX(); x=x+1){
		                    for(int y = min.getBlockY();y <= max.getBlockY(); y=y+1){
		                        for(int z = min.getBlockZ();z <= max.getBlockZ(); z=z+1){
		                            Location tmpblock = new Location(p.getWorld(), x, y, z);
		                           	tmpblock.getBlock().setType(mat);
		                           	tmpblock.getBlock().setData((byte) 5);
		                        }
		                    }
		                }
		                
		                int xmin = min.getBlockX();
		                int xmax = max.getBlockX();
		                int ymin = min.getBlockY();
		                int ymax = max.getBlockY();
		                int zmin = min.getBlockZ();
		                int zmax = max.getBlockZ();
		                
		                getConfig().set("wall.world", p.getWorld().getName());
		                
		                getConfig().set("wall.min.x", xmin);
		                getConfig().set("wall.min.y", ymin);
		                getConfig().set("wall.min.z", zmin);
		                
		                getConfig().set("wall.max.x", xmax);
		                getConfig().set("wall.max.y", ymax);
		                getConfig().set("wall.max.z", zmax);
		                
		                getConfig().set("wallset", true);
						p.sendMessage(prefix + "Successfully set the wall to your current selection");
		            }
		            else
		            {
		            	p.sendMessage(prefix + ChatColor.RED + "That is not a cuboid selection.");
		            }
	            }
            }
		}
		if (getConfig().getBoolean("setupcorrectly"))
		{
			p.sendMessage(prefix + "The game is now completely set up correctly and ready to play!");
		}
		saveConfig();
	}
	
	public void gameLoop()
	{
		World w = Bukkit.getWorld(getConfig().getString("waiting.world"));
		double x = getConfig().getDouble("waiting.x");
		double y = getConfig().getDouble("waiting.y");
		double z = getConfig().getDouble("waiting.z");
		float yaw = (float) getConfig().getDouble("waiting.yaw");
		float pitch = (float) getConfig().getDouble("waiting.pitch");
		Location loc = new Location(w, x, y, z, yaw, pitch);
		for (int i = 0; i < playerno.size(); i++)
		{
			Player t = Bukkit.getPlayer(playerno.get(i + ""));
			queue.remove(t.getName());
			t.teleport(loc);
			t.setGameMode(GameMode.ADVENTURE);
			t.getInventory().clear();
		}
		
		
		
		//At the end
		ingame = false;
		for (int i = 0; i < playerno.size(); i++)
		{
			Player t = Bukkit.getPlayer(playerno.get(i + ""));
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sudo " + t.getName() + " spawn");
		}
		playerno.clear();
	}
	
	
}
