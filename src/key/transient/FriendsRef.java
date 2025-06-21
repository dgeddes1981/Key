package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

public final class FriendsRef extends TransitAtom
{
	private static final long serialVersionUID = -4620866573393636704L;
	
	public FriendsRef()
	{
		setKey( "friends" );
	}

	public static Scape currentlyRunning()
	{
		Player p = Player.getCurrent();
		if( p != null )
			return( p.getFriends() );
		else
			return( null );
	}

	public Atom getRealAtom()
	{
		return( currentlyRunning() );
	}
}
