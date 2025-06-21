/**
  *  Leave:
  *
  *    When a player leaves a room
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public class Leave extends Movement
{
	protected Scape loc;
	protected String message;
	
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public Leave( Player from, Scape left, String format )
	{
		super( from );
		loc = left;
		message = format;
	}

	public String getMessage( Player receiver )
	{
		return( message );
	}
	
	public void cause()
	{
		loc.splashExcept( this, originator, null );
	}
}
