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
**  $Id: Save.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import java.io.IOException;
import java.util.StringTokenizer;

public class Save extends Command
{
	public Save()
	{
		setKey( "save" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.getCanSave() )
		{
			if( p.hasPassword() )
			{
				try
				{
						//  deliberate - it stops them over-using it,
						//  I hope.
					Thread.sleep( 3000 );
					p.sync();
				}
				catch( IOException e )
				{
					ic.sendError( "An error occurred trying to save your character" );
					Log.debug( this, e.toString() + " while saving player " + p.getName() );
					return;
				}
				catch( InterruptedException e )
				{
				}
			}
			else
				ic.sendError( "You don't have a password set." );
		}
		else
			ic.sendError( "You haven't been given permission to save." );
	}
}
