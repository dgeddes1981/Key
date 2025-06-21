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
import key.primitive.*;
import key.util.Trie;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class SysMessage extends Command
{
	public SysMessage()
	{
		setKey( "wall" );
		usage = "<message>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String message = args.nextToken( "" );
			
			for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
			{
				Player t = (Player) e.nextElement();
				
				try
				{
					t.sendSystem( message );
				}
				catch( Exception x )
				{
				}
			}
		}
		else
			usage( ic );
	}
}
