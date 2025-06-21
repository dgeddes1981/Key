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
**  $Id: Submissions.java,v 1.2 2000/01/31 16:39:29 noble Exp $
**
**  $Log: Submissions.java,v $
**  Revision 1.2  2000/01/31 16:39:29  noble
**  displays topic
**
**  Revision 1.1  2000/01/27 14:42:08  noble
**  creation
**
**
**
*/

package key.commands.te0;

import key.*;
import key.te0.*;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Submissions extends Command
{
	public Submissions()
	{
		setKey( "submissions" );
		usage = "<category>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
   			String category = args.nextToken();
				
				te0 te0 = (te0) Key.instance().getElement( "te0" );
				
				if( te0 == null )
				{
					ic.sendError( "There currently appears to be no te0" );
					return;					
				}
				
				Container catcon = (Container) te0.getSubmitted().getElement( category );
				
				if( catcon == null )
				{
					ic.sendError( "Could not find category '" + category + "'" );
					return;					
				}
				
				Atom a = null;
				TableParagraph.Generator table = new TableParagraph.Generator( columns );
				
				for( Enumeration e = catcon.referenceElements(); e.hasMoreElements(); )
				{
					Reference r = (Reference) e.nextElement();
					a = r.get();
					String s[] = new String[2];
					
					if( a instanceof Article )
					{
						s[0] = r.getName();
						s[1] = ((Article)a).title;
						
						table.appendRow( s );
					}
					else if( a instanceof Survey )
					{
						s[0] = r.getName();
						s[1] = ((Survey)a).topic;
						
						table.appendRow( s );
					}	
				}
				
				ic.send( table.getParagraph() );
				
		}
		else
			usage( ic );
	}
	
	private static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "id", 3 ),
		new TableParagraph.Column( "title", Article.MAX_TITLE_LENGTH )
	};	
}
