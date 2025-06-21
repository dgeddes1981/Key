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
**  $Id: Link.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
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
import key.util.Trie;
import java.io.IOException;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class Link extends Command
{
	public Link()
	{
		setKey( "ln" );
		usage = "<object> <destination container>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;

		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			String id=args.nextToken();
			if( !args.hasMoreTokens() )
				usage( ic );
			else
			{
				String to=args.nextToken();

				Object o = new Search( id, p.getContext() ).result;
				if( o != null )
				{
					if( o instanceof Atom )
					{
						Object r = new Search( to, p.getContext() ).result;
						if( r == null )
							ic.sendError( "Could not find destination '" + to + "'" );
						else if( r instanceof Container )
						{
							Container t = (Container)r;
							
							try
							{
								t.add( (Atom)o );
								
								if( t.isReference() )
									ic.sendFeedback( "A reference to " + ((Atom)o).getId() + " has been placed in " + t.getId() );
								else
									ic.sendFeedback( ((Atom)o).getName() + " has been linked into " + t.getId() );
								//Log.log( "level", p.getName() + ", from context '" + p.getContext().getId() + "': moved " + id + " " + to );
							}
							catch( BadKeyException e )
							{
								throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
							}
							catch( NonUniqueKeyException e )
							{
								ic.sendError( "There already exists an atom with this name in the destination container" );
								return;
							}
						}
						else
							ic.sendError( "'" + to + "' is not a Container, it is a " + Type.typeOf( r ).getName() );
					}
					else if( o instanceof Trie )
						ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
					else
						ic.sendError( "'" + id + "' is not an atom, it is " + Type.typeOf( o ).getName() );
				}
				else
					ic.sendError( "Could not find '" + id + "'" );
			}
		}
	}
}
