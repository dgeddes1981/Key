/**
  *  DirectedObjectEffect
  *
  *  Class History
  *
  *  Date        Name         Description
  *  ---------|------------|-----------------------------------------------
  *  10Oct99     Noble       added this Effect
  *
 */

package key.effect;

import key.*;
import java.util.StringTokenizer;

/**
  *  A communication effect that goes to an entire
  *  medium, such as a room or channel.  (Technically,
  *  it goes to any scape)
 */
public class DirectedObjectEffect extends ObjectEffect
{
	
	protected String targetmessage;
	
	protected Player target;
	
	/**
	  *  Creates a new communication
	  *
	  * @param main the message to show to everyone in the location scape
	 */
	public DirectedObjectEffect( Thing sender, Player user, Player usedon, String main, String self, String them )
	{
		super( sender, user, main, self );
		
		target = usedon;
		targetmessage = them;
	}
	
	public String getMessage( Player receiver )
	{
		if( receiver == originator )
			return( usemessage );
		else if( receiver == target )
			return( targetmessage );
		else
			return( message );
	}

	public void cause()
	{
		Location().splashExcept( this, originatorObject, null );
		// suppressionlist
	}
}
