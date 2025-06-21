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
**  $Id: Load.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
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
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class Load extends Command
{
	public Load()
	{
		setKey( "load" );
		usage = "<type> <identifier> [<argument>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;

		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			type = args.nextToken();

			if( !args.hasMoreTokens() )
				usage( ic );
			else
			{
				SeperatedIdentifier full = new SeperatedIdentifier( args.nextToken( " " ) );
				if( full.property == true )
				{
					ic.sendError( "Load will not create properties, sorry (try set)" );
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
						try
						{
							Atom r;
							
							if( id.equals( Key.nullString ) )
								id = "";
							
							r = (Atom) Factory.makeAtom( Class.forName( "key." + type ), id );
							
							if( args.hasMoreTokens() )
								r.argument( args.nextToken( "" ) );
							
							l.add( r );
								//  no longer needed, now called in setParent
							//r.stopBeingTemporary();
							ic.sendFeedback( "Added new " + Type.typeOf( r ).getName() + " '" + r.getName() +"' to " + l.getId() );
							
							if( l.getReferencesOnly() )
								ic.sendFeedback( "^RWARNING: The container you have loaded this atom into is a reference container; Your new atom is currently temporary and will be erased unless you move it into a non-reference container.^-" );
						}
						catch( ClassNotFoundException e )
						{
							ic.sendError( e.toString() );
						}
						catch( BadKeyException e )
						{
							ic.sendError( "'" + id + "' should contain only alphabetic characters" );
						}
						catch( NonUniqueKeyException e )
						{
							ic.sendError( "There is already an atom with this identifier ('" + id + "')" );
						}
					}
					else
						ic.sendError( "'" + Type.typeOf( o ).getName() + "' is not a container" );
				}
				else
					ic.sendError( "Could not find '" + loc + "'" );
			}
		}
	}
}
