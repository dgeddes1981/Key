/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

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
public class Applet extends Terminal
{
	boolean inHyper = false;
	public Applet()
	{
		super();
		names = new String[1];
		names[0] = "applet";
	}
	
	public String codeLookup( char code )
	{
		switch( code )
		{
			case 'h':
			case 'H':
				return( "\033[1m" );
			
			case 'n':
			case 'N':
				normal = true;
				return( "\033[m" );

			case 'd':
				return( "\033[0;30m" );
			case 'D':
				return( "\033[1;30m" );

			case 'r':
				return( "\033[0;31m" );
			case 'R':
				return( "\033[1;31m" );

			case 'g':
				return( "\033[0;32m" );
			case 'G':
				return( "\033[1;32m" );

			case 'y':
				return( "\033[0;33m" );
			case 'Y':
				return( "\033[1;33m" );

			case 'b':
				return( "\033[0;34m" );
			case 'B':
				return( "\033[1;34m" );
			
			case 'm':
				return( "\033[0;35m" );
			case 'M':
				return( "\033[1;35m" );

			case 'c':
				return( "\033[0;36m" );
			case 'C':
				return( "\033[1;36m" );

			case 'w':
				return( "\033[0;37m" );
			case 'W':
				return( "\033[1;37m" );

			case '_':
				return( "\033[4m" );

			case '\021':
				return( "\033[5m" );

			case '\022':
				return( "\033[7m" );
			
			case '{':
				return( "\033{" );
			case '}':
				return( "\033}" );
			
				//  click-URL that isn't displayed to terminals
				//  intended use: ^[http://mylink.com|mylink^]
				//  (allows you to name the link)
			case '[':
				inHyper = true;
				return( "\033<" );
			case ']':
				inHyper = false;
				return( "\033>" );
			case '|':
				if( inHyper )
				{
					inHyper = false;
					return( "\033|" );
				}
				else
					return( "" );
			
			case '<':
				return( "\033<" );
			case '>':
				return( "\033>" );
			case '(':
				return( null );
		}
		
		return( "" );
	}
	
	private final static String until[] = { "^)" };
	public String[] getUntil()
	{
		return( until );
	}
	
	public String clearScreen()
	{
		return( "\033[0;0H\033[2J" );
	}
	
	public String beep()
	{
		return( new Character( (char) 7 ).toString() );
	}
}
