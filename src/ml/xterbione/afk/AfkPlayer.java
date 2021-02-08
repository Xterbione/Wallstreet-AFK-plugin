package ml.xterbione.afk;
import org.bukkit.entity.Player;

public class AfkPlayer {
	
	private Player player;
	private boolean afkState;
	
	public AfkPlayer(Player player)
	{
		this.player = player;
		this.afkState = false;
	}
	
	public Player getPlayer() 
	{
		return this.player;
	}
	
	public boolean getAfkState() 
	{
		return this.afkState;
	}
	
	public void setAfkState(boolean newState) 
	{
		this.afkState = newState;
	}
	
}