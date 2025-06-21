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
import key.util.Trie;

import java.io.IOException;
import java.util.StringTokenizer;

public class Transfer extends Command
{
	public Transfer()
	{
		setKey( "transfer" );
		usage = "<object> <new-owner> ";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String targetName = args.nextToken();

			if( args.hasMoreTokens() )
			{
				String ownerName = args.nextToken();
				
				Atom newOwner;
				if( ownerName.equalsIgnoreCase( Key.nullString ) )
					newOwner = null;
				else
				{
					Object owner;
					owner = new Search( ownerName, p.getContext() ).result;
					
					if( owner == null )
					{
						ic.sendError( "Could not find '" + ownerName + "'" );
						return;
					}
					
					if( owner instanceof Atom )
						newOwner = (Atom) owner;
					else
					{
						ic.sendError( "Non-atomic get-symbol match on '" + ownerName + "'" );
						return;
					}
				}
				
				Object o = new Search( targetName, p.getContext() ).result;
				if( o != null )
				{
					if( o instanceof Atom )
					{
						Atom target = (Atom) o;
						
						if( target.getOwner() == newOwner )
						{
							if( newOwner != null )
								ic.sendError( newOwner.getName() + " already owns " + target.getId() );
							else
								ic.sendError( target.getId() + " is already not owned" );
						}
						else
						{
							target.setRecursiveOwner( newOwner );
							
							if( newOwner != null )
								ic.sendFeedback( newOwner.getName() + " now owns " + target.getId() );
							else
								ic.sendFeedback( target.getId() + " is no longer owned" );
						}
					}
					else if( o instanceof Trie )
						ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
					else
						ic.sendError( "Non-atomic get-symbol match on '" + targetName + "'" );
				}
				else
					ic.sendError( "Could not find '" + targetName + "'" );
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
}
