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

package key.commands;

import key.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class ConnectRoom extends Command
{
	public ConnectRoom()
	{
		setKey( "connectRoom" );
		usage = "<location>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String id = args.nextToken();
			Atom a = getSymbolInside( ic, id, p.getContext() );
			
			if( a instanceof Room )
			{
				p.setProperty( "loginRoom", a );
				ic.sendFeedback( "You will now log in to " + a.getId() );
			}
		}
		else
		{
			ic.sendFeedback( "You will now log in to the last public room you were in." );
			p.setProperty( "loginRoom", null );
		}
	}
}
