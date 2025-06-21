/**
  *  Broadcast
  *
  *
 */

package key.effect.forest;

import key.*;
import java.util.StringTokenizer;

/**
  *  A communication effect that goes to an entire
  *  medium, such as a room or channel.  (Technically,
  *  it goes to any scape)
 */
public class Music extends key.effect.Broadcast
{
	/**
	  *  Creates a new communication
	  *
	  * @param from the player who sent it
	  * @param location where the local effect is to take place
	  * @param main the message to show to everyone in the location scape
	  * @param self the message to show to the player
	 */
	public Music( Scape location, Player from, String main, String self )
	{
		super( location, from, main, self );
	}
}
