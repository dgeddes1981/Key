/**
  *  Movement:
  *
  *    An effect that is caused when something 'moves' (switches
  *    rooms, etc.
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public abstract class Movement extends Effect
{
	/**
	  *  Creates a new movement
	  * @param from the player who sent it
	 */
	public Movement( Player from )
	{
		super( from );
	}
}
