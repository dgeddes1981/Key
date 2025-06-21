/**
  *  Communication
  *
  *
 */

package key.effect;

import key.*;
import java.util.StringTokenizer;

public abstract class Communication extends Effect
{
	/**
	  *  Creates a new communication
	  * @param from the player who sent it
	 */
	public Communication( Player from )
	{
		super( from );
	}
}
