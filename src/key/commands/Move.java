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
**  $Id: Move.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
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

public class Move extends Command
{
	public Move()
	{
		setKey( "mv" );
		usage = "<fully qualified identifier> <destination>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		SeperatedIdentifier full = new SeperatedIdentifier( nextArgument( args, ic ) );
		String targetId = nextArgument( args, ic );
		
		if( full.property == true )
		{
			ic.sendError( "Move will not move properties, sorry" );
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
					Object t = new Search( targetId, p.getContext() ).result;
					
					if( t instanceof Container )
					{
						try
						{
							l.remove( (Atom) r );
							
							try
							{
								((Container)t).add( (Atom) r );
								ic.sendFeedback( "Moved atom '" + ((Atom)r).getName() + "' from " + l.getId() + " to " + ((Atom)t).getId() );
							}
							catch( Exception e )
							{
								try
								{
									l.add( (Atom) r );
								}
								catch( Exception e2 )
								{
									throw new UnexpectedResult( e2.toString() + " while attempting to recover from " + e.toString() + " while adding an atom after removing it during a move operation.  Atom #" + ((Atom)r).getIndex() + " could now be in an inconsistent state: notify an administrator if you don't want this atom automatically purged." );
								}
								
								throw new UnexpectedResult( e.toString() + " on adding the atom to the new container: restored to previous location" );
							}
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
