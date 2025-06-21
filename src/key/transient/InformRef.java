package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

/**
  * This is the main instance of a player on the program
 */
public final class InformRef extends TransitAtom
{
	private static final long serialVersionUID = 1589985663518582838L;
	
	/**  Constructs a me ;p */
	public InformRef()
	{
		setKey( "informs" );
	}

	public static InformList currentlyRunning()
	{
		Player p = Player.getCurrent();
		if( p != null )
			return( p.getInform() );
		else
			return( null );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
