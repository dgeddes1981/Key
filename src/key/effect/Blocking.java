package key.effect;
import key.*;
import java.util.StringTokenizer;

public abstract class Blocking extends Effect
{
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public Blocking( Player from )
	{
		super( from );
	}
}
