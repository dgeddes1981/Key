/**
  *  Block:
  *
  *    When a player leaves a room
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public class BlockNotification extends Blocking
{
	protected PlayerGroup loc;
	protected String message;
	
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public BlockNotification( Player from, PlayerGroup left, String msg )
	{
		super( from );
		loc = left;
		message = msg;
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
