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
**  $Id: Inform.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       now allows scapes to be informed on
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Inform extends Command
{
	public Inform()
	{
		usage = "<player or scape>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String informed = args.nextToken();
			
			Object o;
			o = getElementInside( ic, informed, Key.shortcuts() );
			
			if( o != null )
			{
				if( o == p )
				{
					ic.sendError( "Excuse me if I think this is a little pointless..." );
				}
				else
				{
					boolean single = (o instanceof Player);
					
					if( single || o instanceof Scape )
					{
						InformList il = p.getInform();
						Atom a = (Atom) o;
						
						if( !(il.contains( a )) )
						{
							try
							{
								il.add( (Atom) o );
							}
							catch( NonUniqueKeyException e )
							{
								throw new UnexpectedResult( e.toString() + " on adding a uncontained inform entry" );
							}
							catch( BadKeyException e )
							{
								throw new UnexpectedResult( e.toString() + " on adding a uncontained inform entry" );
							}
							
							if( single )
							{
								ic.sendFeedback( "You will now be informed when " + ((Atom) o).getName() + " connects or disconnects." );
								
								if( p.getFriends().contains( (Player) o ) )
									ic.sendFeedback( "Remember, you can also use 'inform friends' if you want to be notified when any of your friends come online" );
							}
							else
							{
								if( o instanceof Friends )
									ic.sendFeedback( "You will now be informed when your friends connect or disconnect." );
								else
									ic.sendFeedback( "You will now be informed when people from '" + ((Atom) o).getName() + "' connect or disconnect." );
							}
						}
						else
						{
							try
							{
								il.remove( (Atom) o );
							}
							catch( BadKeyException t )
							{
								throw new UnexpectedResult( t.toString() + " on removing a contained inform entry" );
							}
							catch( NonUniqueKeyException t )
							{
								throw new UnexpectedResult( t.toString() + " on removing a contained inform entry" );	
							}
							
							if( single )
								ic.sendFeedback( "You will no longer be informed when " + ((Atom) o).getName() + " connects or disconnects." );
							else
							{
								if( o instanceof Friends )
									ic.sendFeedback( "You will no longer be informed when your friends connect or disconnect." );
								else
									ic.sendFeedback( "You will no longer be informed when people from '" + ((Atom) o).getName() + "' connect or disconnect." );
							}
						}
					}
					else
						ic.sendError( "You can't put that on your informlist, only players and scapes" );
				}
			}
			else
				ic.sendError( "Can't find '" + informed + "'." );
		}
		else
			usage( ic );
	}
}
