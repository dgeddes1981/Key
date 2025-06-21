package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

/**
  * This is the main instance of a player on the program
 */
public final class Context extends TransitAtom
{
	private static final long serialVersionUID = 5041996534603235259L;
	
	/**  Constructs a me ;p */
	public Context()
	{
		setKey( "context" );
	}

	public static Atom currentlyRunning()
	{
		Player p = Player.getCurrent();
		if( p != null )
			return( p.getContext() );
		else
			return( null );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
