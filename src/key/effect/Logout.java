/**
  *  Logout:
  *
  *    When a player comes onto the program
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public class Logout extends Connection
{
	protected String message;
	
	/**
	  *  Creates a new 
	  * @param from the player who sent it
	 */
	public Logout( Player from, String format )
	{
		super( from );
		message = Grammar.substitute( format, from.getCodes() );
	}
	
	public String getMessage( Player receiver )
	{
		return( message );
	}
	
	public void cause()
	{
			//  notify everyone who requested to be notified
		LogoutNotification.notify( this, null );
		
			//  notify everyone online		
		Key.instance().splashExcept( this, originator, null );
	}
}
