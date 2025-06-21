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
**  $Id: QuickMove.java,v 1.1.1.1 1999/10/07 19:58:34 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key;

import key.util.Trie;
import java.io.IOException;

import java.util.Enumeration;
import java.util.StringTokenizer;

public class QuickMove extends Command
{
	public QuickMove()
	{
		setKey( "qmv" );
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

				Object o = new Search( id, p.getContext(), false, false ).result;
				if( o != null )
				{
					if( o instanceof Reference )
					{
						Object r = new Search( to, p.getContext() ).result;
						if( r == null )
							ic.sendError( "Could not find destination '" + to + "'" );
						else if( r instanceof Container )
						{
							Container t = (Container)r;
							
							try
							{
								
								if( t.isReference() )
								{
									t.contained.link( (Reference) o );
									ic.sendFeedback( "A reference (" + o.toString() + ") has been placed in " + t.getId() );
								}
								else
									ic.sendFeedback( "Operation not valid for non-reference containers" );
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
					else if( o instanceof Atom )
						ic.sendError( "Matched atom, not reference." );
					else
						ic.sendError( "'" + id + "' is not an atom, it is " + Type.typeOf( o ).getName() );
				}
				else
					ic.sendError( "Could not find '" + id + "'" );
			}
		}
	}
}
