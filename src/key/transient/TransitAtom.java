package key;

import java.io.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Enumeration;
import key.effect.*;

/**
 */
public abstract class TransitAtom extends Atom
{
	private static final long serialVersionUID = -8728916863196191165L;
	public TransitAtom()
	{
	}
	
	public abstract Atom getRealAtom();
	
	public void search( Search s ) throws InvalidSearchException
	{
		s.dirty = true;
		s.result = getRealAtom();
	}

	String getId_imp()
	{
		Atom a = getRealAtom();

		if( a != null )
			return( a.getId() );
		else
			return( Key.nullString );
	}
}
