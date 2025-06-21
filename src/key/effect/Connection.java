/**
  *  Connection:
  *
  *  Caused when a player logs in or out
  *
 */

package key.effect;
import key.*;
import java.util.StringTokenizer;

public abstract class Connection extends Effect
{
	/**
	  *  Creates a new connection effect
	  * @param from the player who is connecting
	 */
	public Connection( Player from )
	{
		super( from );
	}
}
