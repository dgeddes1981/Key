/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
**
**  $Id: ISO6429.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       small tidy up
**
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
public class ISO6429 extends Terminal
{
	public ISO6429()
	{
		super();
		names = new String[10];
		names[0] = "ansi";
		names[1] = "iso6429";
		names[2] = "colour";
		names[3] = "color";
		names[4] = "xterm";
		names[5] = "linux";
		names[6] = "xterm-debian";
		names[7] = "vt100";
		names[8] = "vt220";
		names[9] = "vt102";
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
				return null;
			
			case '[':
				return null;
			
			case '<':
				return( "<\033[4m\033[1;34m" );
		}
		
		return( "" );
	}
	
	public String stringForCode( char code )
	{
		if( code == '>' )
			return( super.stringForCode( '-' ) + '>' );
		
		return( super.stringForCode( code ) );
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
