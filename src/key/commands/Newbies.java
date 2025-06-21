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
**  $Id: Newbies.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Jul97     subtle      created this command
**  06Dec97     subtle      fixed the number of newbies online number
**
*/

package key.commands;

import key.*;
import key.primitive.DateTime;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Newbies extends Command
{
	public Newbies()
	{
		setKey( "newbies" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		displayTable( ic, Key.instance().players() );
	}
	
	private static final void displayTable( InteractiveConnection ic, Enumeration e )
	{
		Player scan;
		int count = 0;
		
		TableParagraph.Generator table = new TableParagraph.Generator( columns );
		
		DateTime now = new DateTime();
		
		while( e.hasMoreElements() )
		{
			scan = (Player) e.nextElement();
			
			if( scan.willSync() )
				continue;
			
			count++;
			
			String rowContents[] = new String[ columns.length ];
			
				//  the players name
			rowContents[0] = (String)scan.getName();
			
			Room current = scan.getLocation();
			
			if( current != null )
			{	
				if( current.getParent() instanceof Player )
					rowContents[1] = "-players-";
				else
					rowContents[1] = current.getName();
			}
			else
				rowContents[1] = "?nowhere?";
			
			try
			{
				rowContents[2] = scan.getConnection().getSiteName();
			}
			catch( PlayerNotConnectedException except )
			{
				rowContents[2] = "not online";
			}
			
				//  the idle time
			rowContents[3] = scan.getIdle( now ).toShortString();
			
			if( scan.hasPassword() )
				rowContents[4] = " # ";
			else
				rowContents[4] = "";

			if( scan.getCanSave() )
				rowContents[5] = " # ";
			else
				rowContents[5] = "";
			
				//  add the row
			table.appendRow( rowContents );
		}
		
		StringBuffer footer = new StringBuffer();
		footer.append( "There ");
		footer.append( Grammar.isAreCount( count ) );
		footer.append( " " );
		footer.append( Grammar.newbieNewbies( count ) );
		footer.append( " online." );
		table.setFooter( footer.toString() );
		
		ic.send( table.getParagraph() );
	}
	
	public static final TableParagraph.Column[] columns = 
	{
		new TableParagraph.Column( "name", Player.MAX_NAME ),
		new TableParagraph.Column( "location", 10 ),
		new TableParagraph.Column( "site", 40 ),
		new TableParagraph.Column( "idle", 5 ),
		new TableParagraph.Column( "pwd", 3 ),
		new TableParagraph.Column( "res", 3 )
	};
}
