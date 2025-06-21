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
**  $Id: Sun.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  28Jul97    subtle       created
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
public class Sun extends Terminal
{
	public Sun()
	{
		super();
		names = new String[1];
		names[0] = "sun";
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
			return( super.stringForCode( '-' ) + '>' );
		
		return( super.stringForCode( code ) );
	}
	

	public String clearScreen()
	{
		return( "\014" );
	}
	
	public String beep()
	{
		return( new Character( (char) 7 ).toString() );
	}
}
