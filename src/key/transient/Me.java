package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

/**
  * This is the main instance of a player on the program
 */
public final class Me extends TransitAtom
{
	private static final long serialVersionUID = 8029436954471027377L;
	
	/**  Constructs a me ;p */
	public Me()
	{
		setKey( "me" );
	}

	public static Player currentlyRunning()
	{
		return( Player.getCurrent() );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
