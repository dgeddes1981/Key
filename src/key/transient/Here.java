package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

/**
  * This is the main instance of a player on the program
 */
public final class Here extends TransitAtom
{
	private static final long serialVersionUID = 459589495888067979L;
	/**  Constructs a me ;p */
	public Here()
	{
		setKey( "here" );
	}

	public static Room currentlyRunning()
	{
		Player p = Player.getCurrent();
		if( p != null )
			return( p.getLocation() );
		else
			return( null );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
