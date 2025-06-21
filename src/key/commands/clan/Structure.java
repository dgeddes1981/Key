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

package key.commands.clan;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Structure extends Command
{
	public Structure()
	{
		setKey( "structure" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Clan clan = (Clan) p.getClan();
		
		ic.send( new HeadingParagraph( "^hclan " + clan.getName() + "^-", HeadingParagraph.CENTRE ) );
		
		int numRanks = 0;
		int numMembers = 0;
		
		for( Enumeration e = clan.ranks.elements(); e.hasMoreElements(); )
		{
			Rank r = (Rank) e.nextElement();
			{
				ic.send( new HeadingParagraph( "^h" + r.getName() + "^-", HeadingParagraph.LEFT ) );
				StringBuffer sb = new StringBuffer( "^himplies: " );
				for( Enumeration f = r.getImplies().elements(); f.hasMoreElements(); )
				{
					Rank imp = (Rank) f.nextElement();
					sb.append( imp.getName() + " " );
				}

				sb.append( "^-" );
				
				ic.send( new TextParagraph( sb.toString() ) );
				
				int c = r.count();
				if( c > 0 )
				{
					numMembers += c;
					String names[] = new String[ c ];
					int j=0;
					
					for( Enumeration f = r.referenceElements(); f.hasMoreElements(); )
					{
						//Player tempPlayer = (Player) f.nextElement();
						Reference tempPlayer = (Reference) f.nextElement();
						
						names[j++] = tempPlayer.getName();
					}
					
					ic.send( new TextParagraph( TextParagraph.LEFT, Grammar.commaSeperate( names ), 3, 3, 0, 0 ) );
				}
				
				numRanks++;
			}
		}
		
		ic.send( new HeadingParagraph( "^h[Ranks: " + numRanks + "] [Players: " + numMembers + "]^-" )  );
	}
}
