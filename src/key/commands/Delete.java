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
**  $Id: Delete.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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
import key.util.SeperatedIdentifier;
import java.io.IOException;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class Delete extends Command
{
	public Delete()
	{
		setKey( "delete" );
		usage = "<fully qualified identifier>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			SeperatedIdentifier full = new SeperatedIdentifier( args.nextToken( " " ) );
			
			if( full.property == true )
			{
				ic.sendError( "Delete will not remove properties, sorry" );
				return;
			}
			
			String id=full.id;
			String loc=full.location;
			
			Object o = new Search( loc, p.getContext() ).result;
			if( o != null )
			{
				if( o instanceof Container )
				{
					Container l = (Container)o;
					Object r;
					
					r = l.getExactElement( id );
					if( r == null )
						ic.sendError( "Could not find '" + id + "' in container " + l.getId() );
					else if( r instanceof Atom )
					{
						try
						{
							Atom a = (Atom) r;
							l.remove( a );
							
							if( !a.hasParent() )
							{
								a.dispose();
								ic.sendFeedback( "Deleted atom '" + a.getName() + "' from container " + l.getId() );
							}
							else
								ic.sendFeedback( "Removed reference to atom '" + a.getId() + "' from container " + l.getId() );
						}
						catch( BadKeyException e )
						{
							throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
						}
						catch( NonUniqueKeyException e )
						{
							throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
						}
					}
					else
						ic.sendError( "'" + id + "' is not an atom, it is " + Type.typeOf( r ).getName() );
				}
				else
					ic.sendError( "'" + Type.typeOf( o ).getName() + "' is not a container" );
			}
			else
				ic.sendError( "Could not find '" + loc + "'" );
		}
	}
}
