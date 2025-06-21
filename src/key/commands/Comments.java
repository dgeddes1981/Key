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
import key.primitive.*;
import key.util.Trie;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Comments extends Command
{
	public Comments()
	{
		setKey( "comments" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Realm r = p.getRealm();
		
		if( r == null )
		{
			ic.sendFailure( "You're not in any realm." );
			return;
		}
		
		String title = r.sessionTitle;
		Reference by = r.sessionSetBy;

		if( title != null && title.length() > 0 )
		{
			if( by == null || !by.isValid() )
				by = Reference.EMPTY;
			
			displayTable( ic, Key.instance().players(), title, by );
			return;
		}
		
		ic.sendFailure( "No session set." );
	}
	
	public static final void displayTable( InteractiveConnection ic, Enumeration e, String title, Reference by )
	{
		Player scan;
		
		TableParagraph.Column[] columns = 
		{
			new TableParagraph.Column( "who", Player.MAX_NAME ),
			new TableParagraph.Column( "^@" + title + "^$", Player.MAX_COMMENT_LENGTH )
		};
		
		TableParagraph.Generator table = new TableParagraph.Generator( columns );
		
		while( e.hasMoreElements() )
		{
			scan = (Player)e.nextElement();
			String com = scan.getSessionComment();
			
			if( com != null && com.length() > 0 )
			{
					//  the players name
				String rowContents[] = new String[ 2 ];
				
				rowContents[0] = (String)scan.getName();
				
					//  eventually add a feature here
					//  to print a '*' for the person who
					//  set the comment
				//if( scan.getThis().equals( by ) )
					//rowContents[1] = com;
				//else
					rowContents[1] = "^@" + com + "^$";
				
					//  add the row
				table.appendRow( rowContents );
			}
		}
		
		table.setFooter( "session set by " + by.get().getName() );
		
		ic.send( table.getParagraph() );
	}
}
