package key.terminals;

import java.util.Enumeration;
import java.util.StringTokenizer;
import key.*;

/**
  *  This class defines the basic abstract implementation of
  *  a terminal type.  It's specific to a TelnetConnection,
  *  since the other (custom) terminals will need changes at
  *  a much higher level.
 */
public class Dumb extends Terminal
{
	public Dumb()
	{
		super();
		names = new String[2];
		names[0] = "none";
		names[1] = "dumb";
	}
	
	public String codeLookup( char code )
	{
		switch( code )
		{
			case 'n':
			case 'N':
				normal = true;
				return "";
			
			case '{':
				return null;
			
			case '<':
				return( "<" );
		}
		
		return( "" );
	}

	public String stringForCode( char code )
	{
		if( code == '>' )
			return( ">" );
		
		return( super.stringForCode( code ) );
	}
	
	public String beep()
	{
		return( new Character( (char) 7 ).toString() );
	}
}
