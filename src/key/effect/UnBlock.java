/**
  *  UnBlock:
  *
  *    When a player leaves a room
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public class UnBlock extends Blocking
{
	protected Scape loc;
	protected String message;
	
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public UnBlock( Player from, Scape left, String format )
	{
		super( from );
		loc = left;
		message = Grammar.substitute( format, from.getCodes() );
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
