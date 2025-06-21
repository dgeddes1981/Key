/**
  *  Broadcast
  *
  *  Class History
  *
  *  Date        Name         Description
  *  ---------|------------|-----------------------------------------------
  *  29Sep99     Noble       added this Effect
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
public class ObjectEffect extends Communication
{
	protected Thing originatorObject;

	protected String message;
	
	protected String usemessage;
	
	/**
	  *  Creates a new communication
	  *
	  * @param location where the local effect is to take place
	  * @param main the message to show to everyone in the location scape
	 */
	public ObjectEffect( Thing sender, Player user, String main, String self )
	{
		super( user );
		
		originatorObject = sender;
		
		message = main;
		usemessage = self;
	}
	
	public Thing getOriginatorObject()
	{
		return( originatorObject );
	}
	
	public String getMessage( Player receiver )
	{
		if( receiver == originator )
			return( usemessage );
		else
			return( message );
	}

	public Scape Location() {
		
		Scape loc = null;
		
		if( originator == null) {
			try {
				loc = (Room) ( (Atom) originatorObject).getParent();
			}
			catch( Exception e ) {
				// object is somewhere it shouldnt be
			}
		}
		else
			loc = originator.getLocation();
			
		return loc;
	}

	public void cause()
	{
		Location().splashExcept( this, originatorObject, null );
	}
}
