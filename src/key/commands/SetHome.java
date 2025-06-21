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
**  $Id: SetHome.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle      creation from key.commands.clan.SetHome
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class SetHome extends Command
{
	public SetHome()
	{
		setKey( "sethome" );
		usage = "<room name> ";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom a = p.getContext();
		
		if( a instanceof Room )
		{
			try
			{
				a.checkPermissionList( Room.setHomeAction );
				p.setHome( (Room) a );
				ic.sendFeedback( "You set your home room to be '" + a.getId() + "'" );
			}
			catch( AccessViolationException e )
			{
				ic.sendError( "You may only set a room you can modify as your home." );
			}
		}
		else
			ic.sendError( a.getId() + " is not a room" );
	}
}
