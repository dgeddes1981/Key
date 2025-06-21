/**
  *  EnterRoom:
  *
  *    When a player comes into a room
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public class Enter extends Movement
{
	protected Scape loc;
	protected String message;
	
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public Enter( Player from, Scape entered, String format )
	{
		super( from );
		loc = entered;
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
