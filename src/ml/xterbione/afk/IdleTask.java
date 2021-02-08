package ml.xterbione.afk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IdleTask implements Runnable {

    public void run() { 
        for (Player player : Bukkit.getOnlinePlayers()) 
        {
            if (Main.plugin.playersIdle.containsKey(player)) 
            { 
            	Main.plugin.playersIdle.put(player, Main.plugin.playersIdle.get(player) + 1);
            } else {
            	Main.plugin.playersIdle.put(player, 1);
            }
            if (Main.plugin.playersIdle.get(player) >= 12) 
            {
                Main.plugin.issuecommand(player);
            }
        }
    }
}