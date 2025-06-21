package key;
import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
  * This is the main instance of a player on the program
 */
public final class ClanRef extends TransitAtom
{
	private static final long serialVersionUID = -1740016534278938332L;
	
	/**  Constructs a me ;p */
	public ClanRef()
	{
		setKey( "clan" );
	}

	public static Clan currentlyRunning()
	{
		Player p = Player.getCurrent();
		if( p != null )
			return( p.getClan() );
		else
			return( null );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
