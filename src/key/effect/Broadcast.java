/**
  *  Broadcast
  *
  * $Id: Broadcast.java,v 1.3 2000/06/19 20:40:49 subtle Exp $
  *
  * $Log: Broadcast.java,v $
  * Revision 1.3  2000/06/19 20:40:49  subtle
  * scan timeouts now deletes players
  * temporary atoms are now cleaned up regularly
  * added 'rescan' command to refresh friends lists (bug workaround)
  * added 'cancel' command to cancel a scheduled event
  *
  * Revision 1.2  2000/02/21 22:26:26  subtle
  * added Room history command, untested
  *
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
public class Broadcast extends Communication
{
	protected Scape loc;
	
	protected String mainMessage;
	protected String selfMessage;
	
	/**
	  *  If it echos to the scape, it must always re-echo
	  *  to the originator - this boolean is set if the
	  *  originator is splashed, so if its still false
	  *  after all, then re-splash them.
	 */
	protected boolean gotOriginator;
	
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param location where the local effect is to take place
	  * @param main the message to show to everyone in the location scape
	  * @param self the message to show to the player
	 */
	public Broadcast( Scape location, Player from, String main, String self )
	{
		super( from );
		mainMessage = main;
		selfMessage = self;
		loc = location;
	}

	public String getMessage( Player receiver )
	{
		if( receiver == originator )
		{
			gotOriginator = true;
			return( selfMessage );
		}
		else
			return( mainMessage );
	}
	
	public void cause()
	{
		gotOriginator = false;
		loc.splash( this, null );
		
		if( originator != null && !gotOriginator && originator.connected() )
			originator.splash( this, null );
	}
}
