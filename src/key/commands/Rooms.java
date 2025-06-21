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
**  $Id: Rooms.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97     subtle       created this command
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Rooms extends Command
{
	public Rooms()
	{
		setKey( "rooms" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom context = p.getContext();
		
		if( context instanceof Container )
		{
			TableParagraph.Generator table = new TableParagraph.Generator( columns );
			
			int i = 0;
			
			for( Enumeration e = ((Container)context).elements(); e.hasMoreElements(); )
			{
				Object o = e.nextElement();

				if( o instanceof Room )
				{
					Room r = (Room) o;
					String rowContents[] = new String[ columns.length ];
					
					rowContents[ 0 ] = r.getName();
					rowContents[ 1 ] = "Someone is " + r.getFullPortrait();
					rowContents[ 2 ] = Integer.toString( r.numberPlayers() );
					rowContents[ 3 ] = Integer.toString( r.count() );
					rowContents[ 4 ] = r.getCalled();
					
					table.appendRow( rowContents );
					i++;
				}
			}

			if( i > 1 )
				table.setFooter( Integer.toString( i ) + " rooms" );
			else if( i == 1 )
				table.setFooter( "one room" );
			else
			{
				ic.sendFailure( "There aren't any rooms in " + context.getName() + ".  Use 'create room <id>' if you want to create one." );
				return;
			}
			
			ic.send( table.getParagraph() );
		}
	}
	
	public static final TableParagraph.Column[] columns = 
	{
		new TableParagraph.Column( "name", Atom.MAX_KEY_LENGTH ),
		new TableParagraph.Column( "portrait", 40 ),
		new TableParagraph.Column( "pl#", 3  ),
		new TableParagraph.Column( "ob#", 3  ),
		new TableParagraph.Column( "called", 40 )
	};
}
