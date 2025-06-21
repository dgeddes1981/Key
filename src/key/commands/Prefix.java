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
**  $Id: Prefix.java,v 1.6 2000/06/12 15:34:32 subtle Exp $
**
**  $Log: Prefix.java,v $
**  Revision 1.6  2000/06/12 15:34:32  subtle
**  now only matches prefix conflicts with -exact- name matches of players
**
**  Revision 1.5  2000/03/07 14:31:11  subtle
**  added log
**
**
*/

package key.commands;

import key.*;
import key.util.Trie;
import java.io.IOException;
import java.util.StringTokenizer;

public class Prefix extends Command
{
	public static final int MAX_LENGTH = 16;
	
	public Prefix()
	{
		setKey( "prefix" );
		usage = "[<prefix>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String newPrefix = args.nextToken();
			
			if( newPrefix.length() <= MAX_LENGTH )
			{
				Object o = Key.instance().getResidents().getReferenceElement( newPrefix );
				if( o != null && !(o instanceof Trie) )
				{
					if( o instanceof Reference )
					{
						Player p1 = (Player) ((Reference)o).get();
						if( p1.getName().equalsIgnoreCase( newPrefix ) )
						{
							ic.sendError( "You may not set your prefix to that..." );
							return;
						}
					}
					else
					{
						ic.sendError( "You may not set your prefix to that..." );
						return;
					}
				}
				
					//  basically, if the first character isn't in the provided set
				p.getContext().setProperty( "prefix", newPrefix );
				
				if( args.hasMoreTokens() )
					ic.sendFeedback( "A prefix can only be a single word..." );
			}
			else
			{
				ic.sendFeedback( "That word is too long to be set as a prefix.  Please limit your prefix's to " + MAX_LENGTH + " characters." );
				return;
			}
		}
		else
			p.getContext().setProperty( "prefix", "" );
		
		ic.sendFeedback( "You set the prefix of " + p.getContext().getName() + " to '" + (String) p.getContext().getProperty( "prefix" ) + "'" );
	}
}
