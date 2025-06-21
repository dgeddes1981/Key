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
**  $Id: Echo.java,v 1.2 2000/03/03 21:44:35 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class Echo extends Command
{
	private static final long serialVersionUID = 1521490484897290643L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Echo.class, String.class, "echo",
			AtomicElement.PUBLIC_FIELD,
			"what is echoed" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String echo = "";
	
	public Echo()
	{
		setKey( "echo" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( echo == null || echo.length() == 0 )
		{
			ic.sendError( "This echo is incorrectly set up" );
			return;
		}
		
		ic.sendFeedback( Grammar.substitute( echo ) );
	}
}
