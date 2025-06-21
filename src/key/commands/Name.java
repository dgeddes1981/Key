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
**  $Id: Name.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  18Jul97     exile        created
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Name extends Command
{
	public static final int MAX_LENGTH = 40;

	public Name()
	{
		setKey( "name" );
		usage = "<name of the room>";
	}

	public void run( Player p,  StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.getContext() != p.getLocation() )
		{
			ic.sendFeedback( "The correct form of this command is 'room name <new name>'" );
			return;
		}
		
		try
		{
			String called = null;
			
			if( args.hasMoreTokens() )
			{
				called = args.nextToken( "" );
				
				if( called.length() <= MAX_LENGTH )
				{
						// do it
					p.getLocation().setCalled( called );
					ic.sendFeedback( "This room is now called " + called + "." );
				}
				else
				{
					ic.sendFeedback( "You must call the room something shorter than " + MAX_LENGTH + " characters. " );
					return;
				}
			}
			else
			{
				p.getLocation().setCalled( called );
				ic.sendFeedback( "You clear this rooms name.  If you want to set it to something, use '" + getQualifiedName( caller ) + " <new name>'" );
			}
		}
			//  this can't be called anything
		catch( NoSuchPropertyException e )
		{
			ic.sendError( p.getLocation().getName() + " cannot be named." );
		}
		
			// nop... you don't have the privs to do this
		catch( AccessViolationException e )
		{
			ic.sendError( "You do not have the authority to name " +  p.getLocation().getName() + "." );
		}
	}
}
