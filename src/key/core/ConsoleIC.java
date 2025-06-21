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
**  $Id: ConsoleIC.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.io.*;
import java.util.Stack;

/**
 */
public class ConsoleIC
extends DumbIC
{
	transient DataInputStream br;
	
	/**
	  *  The constructor must take a socket as its sole
	  *  argument
	  *
	  * @param s The socket that the connection is on
	 */
	public ConsoleIC()
	{
		super();
		setPrintStream( System.out );
		
		br = new DataInputStream( System.in );
	}
	
	public String getName()
	{
		return( "console" );
	}
	
	public final String getSiteName()
	{
		return( "local console" );
	}
	
	public String input( String prompt )
	{
		unIdle();
		
		ps.print( prompt );
		
		try
		{
			String s = br.readLine();
			if( s != null )
				return( s );
			else
				throw new IOException( "End Of File" );
		}
		catch( IOException e )
		{
			throw new NetworkException( e );
		}
	}
	
	public String hiddenInput( String prompt )
	{
		return( input( prompt ) );
	}
}
