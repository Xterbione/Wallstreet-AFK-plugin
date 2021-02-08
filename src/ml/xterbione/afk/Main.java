package ml.xterbione.afk;

import java.io.ObjectInputFilter.Config;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin implements CommandExecutor, Listener {
	public static Main plugin;
	private ArrayList<AfkPlayer> players;
	public HashMap<Player, Integer> playersIdle = new HashMap<Player, Integer>(); // Player is a key, Integer is a value
	public Main() {
		

	}
	
	@Override
	public void onEnable() 
	{
		plugin					 = this;
		FileConfiguration config = this.getConfig();
		config.addDefault("afkTimer", 300);
		config.options().copyDefaults(true);
		saveConfig();
		System.out.println("wallstreetafk VER: 1.0 -> Enabled");
		System.out.println("afk plugin coded by xterbione.");
		getServer().getPluginManager().registerEvents(this, this);
	    players = new ArrayList<AfkPlayer>();
	   	for(Player p : this.getServer().getOnlinePlayers()) 
	   	{
	   		this.players.add(new AfkPlayer(p));
	    }	
	   	
	   	Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new IdleTask(),config.getInt("afkTimer") * 2L,config.getInt("afkTimer") * 2L); 
	}
	
	public void onDisable(){
		System.out.println("wallstreetafk VER: 1.0 -> disabled");
	}
	


	
	public ArrayList<AfkPlayer> getAfk (){
		return players;
	}
	
    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
    {
		e.getPlayer().setSleepingIgnored(false);
		this.players.add(new AfkPlayer(e.getPlayer()));
		
		e.getPlayer().sendMessage( ChatColor.GREEN + "Taking a break? Type '/afk'. Type '/afk help' for more info.");
    }
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
    	this.playersIdle.put(e.getPlayer(), 0);
    	for(AfkPlayer afkPlayer : this.players) 
    	{

    		if(e.getPlayer().getUniqueId() == afkPlayer.getPlayer().getUniqueId()) 
    		{
    			if(afkPlayer.getAfkState() == true) {
					this.SetAfkState(afkPlayer, false);		    
    			}

    		}
    	}
    }
	
    
    
    
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (cmd.getName().equalsIgnoreCase("afk")) 
		{
			if (args.length < 1) 
			{
				if(!(sender instanceof Player)) 
				{
					sender.sendMessage("JIJ BENT GEEN SPELER! HOE JIJ AKF ZIJN?");
					return true;
				}
				else {
					Player player = (Player) sender;
					player.setSleepingIgnored(true);
					for(AfkPlayer afkPlayer : this.players) 
					{
						if(afkPlayer.getPlayer() == player) 
						{
							if(afkPlayer.getAfkState() == false) 
							{
								this.SetAfkState(afkPlayer, true);
							}	
						}  
					}
				}
			} else if (args[0].equalsIgnoreCase("players")) 
			{
				sender.sendMessage(ChatColor.GREEN + "Players currently afk:");
				int counter = 0;
				for(AfkPlayer afkPlayer : this.players) 
			   	{
					if(afkPlayer.getAfkState()) 
					{
						String displayName = afkPlayer.getPlayer().getDisplayName().replace(" [AFK]", "");
						sender.sendMessage( ChatColor.GREEN + displayName);
						counter++;
					}
			   	}		
				sender.sendMessage( ChatColor.GREEN + "Total afk players: " + counter );
			} else if (args[0].equalsIgnoreCase("help")) 
			{
				if (args.length == 1 || args[1].equalsIgnoreCase("1") )
				{
				sender.sendMessage(ChatColor.GREEN + "help page 1/1");
				sender.sendMessage(ChatColor.GREEN + "'/afk help' OR '/afk help [PAGE NUMBER]' gives the command list");
				sender.sendMessage(ChatColor.GREEN + "'/afk' puts user on afk");
				sender.sendMessage(ChatColor.GREEN + "'/afk players' gives a list of afk players");
				sender.sendMessage(ChatColor.GREEN + "'/afk settimer [AMOUNT IN SECONDS]' Sets afk timer, reload required.");
				sender.sendMessage(ChatColor.GREEN + "END");
				}
			}else if (args[0].equalsIgnoreCase("settimer")) 
			{
				if (args.length == 2)
				{
				
					this.getConfig().set("afkTimer", Integer.valueOf(args[1]));
					saveConfig();
					sender.sendMessage(ChatColor.GREEN + "afk timer changed to " + args[1] + " seconds");
				} else {
					sender.sendMessage(ChatColor.RED + "[WALLSTREETAFK]: Too many, not enough or wrong arguments!");
					sender.sendMessage(ChatColor.RED + "Example: '/afk settimer 300' sets timer to 5 minutes");
				}
			}
			return true;
		} 	
		
		

	return false;	
	}
	
	public void SetAfkState(AfkPlayer afkPlayer, boolean state) {
		
		if(state == true)
		{
			afkPlayer.setAfkState(state);

			afkPlayer.getPlayer().setSleepingIgnored(true);
			getServer().broadcastMessage(ChatColor.GREEN + afkPlayer.getPlayer().getDisplayName() + ChatColor.GRAY + " is now afk. Sleep tight!");
			afkPlayer.getPlayer().setDisplayName(afkPlayer.getPlayer().getDisplayName() + " [AFK]");
			afkPlayer.getPlayer().setPlayerListName(afkPlayer.getPlayer().getPlayerListName() + " [AFK]");
		}
		
		if(state == false)
		{
			String listName = afkPlayer.getPlayer().getPlayerListName().replace(" [AFK]", "");
			String displayName = afkPlayer.getPlayer().getDisplayName().replace(" [AFK]", "");
			afkPlayer.setAfkState(state);
			afkPlayer.getPlayer().setDisplayName(listName);
			afkPlayer.getPlayer().setPlayerListName(displayName);
			afkPlayer.getPlayer().setSleepingIgnored(false);
			getServer().broadcastMessage(ChatColor.GREEN + afkPlayer.getPlayer().getDisplayName() + ChatColor.GRAY + " is no longer afk!");
		}
	}
	
	public void SetAfkStateByPlayer(Player player, boolean state) {
		for(AfkPlayer afkPlayer : this.players) 
		{
			if(afkPlayer.getPlayer() == player) 
			{
				if(state == true)
				{
					afkPlayer.setAfkState(state);
		
					afkPlayer.getPlayer().setSleepingIgnored(true);
					getServer().broadcastMessage(ChatColor.GREEN + afkPlayer.getPlayer().getDisplayName() + ChatColor.GRAY + " is now afk. Sleep tight!");
					afkPlayer.getPlayer().setDisplayName(afkPlayer.getPlayer().getDisplayName() + " [AFK]");
					afkPlayer.getPlayer().setPlayerListName(afkPlayer.getPlayer().getPlayerListName() + " [AFK]");
				}
				
				if(state == false)
				{
					String listName = afkPlayer.getPlayer().getPlayerListName().replace(" [AFK]", "");
					String displayName = afkPlayer.getPlayer().getDisplayName().replace(" [AFK]", "");
					afkPlayer.setAfkState(state);
					afkPlayer.getPlayer().setDisplayName(listName);
					afkPlayer.getPlayer().setPlayerListName(displayName);
					afkPlayer.getPlayer().setSleepingIgnored(false);
					getServer().broadcastMessage(ChatColor.GREEN + afkPlayer.getPlayer().getDisplayName() + ChatColor.GRAY + " is no longer afk!");
				}
			}
		}
	}
	public void issuecommand (Player player) {
		player.performCommand("afk");
	}

} 
