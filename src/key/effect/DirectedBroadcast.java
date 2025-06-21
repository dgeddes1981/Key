/**
  *  DirectedBroadcast
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
public class DirectedBroadcast extends Broadcast
{
	Splashable directedAt;
	String otherMessage;
	char targetCode;
	
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param location where the local effect is to take place
	  * @param main the message to show to everyone in the location scape
	  * @param self the message to show to the player
	  * @param other the UNPARSED other message
	 */
	public DirectedBroadcast( Scape location, Player from, Splashable to, String main, String self, String other, char tCode )
	{
		super( location, from, main, self );
		
		directedAt = to;
		otherMessage = other;
		targetCode = tCode;
	}
	
	public String getMessage( Player receiver )
	{
		if( originator == receiver )
			return( super.getMessage( receiver ) );
		else
		{
			if( directedAt instanceof PlayerGroup )
			{
				if( ((PlayerGroup)directedAt).containsPlayer( receiver ) )
					originator.putCode( targetCode, ((PlayerGroup)directedAt).allNames( receiver ) );
				else
					return( super.getMessage( receiver ) );
			}
			else
			{
				if( receiver == directedAt )
					originator.putCode( targetCode, "you" );
				else
					return( super.getMessage( receiver ) );
			}
			
			return( Grammar.substitute( otherMessage, originator.getCodes() ) );
		}
	}

	/**
	  *  Directly splashes the location, with the exception of those
	  *  in the directedAt, who are splashed directly themselves
	 */
	public void cause()
	{
		loc.splashExcept( this, directedAt, null );
		directedAt.splash( this, null );
	}

	/**
	  *  Doesn't splash the directedAt people directly,
	  *  instead, this routine is used when we can
	  *  guarantee that the players will be in the location
	  *  scape.
	 */
	public void causeExclusive()
	{
		loc.splash( this, null );
	}
}
