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
**  Class: Through
**
**  Class History
**
**  Date        Name         Through
**  ---------|------------|-----------------------------------------------
**  28Jul97     exile        command created
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Through extends Command
{
    public static final int MAX_LINES = 6;
	public static final int MAX_BYTES = MAX_LINES * 80;
	
	public Through()
	{
		setKey( "through" );
		usage = "<exit>";
	}
	
	 public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
     {
		if( args.hasMoreTokens() )
		{
			String exitName = (String)args.nextToken();

			Paragraph through = null;
			
				// try to get the exit
			try
			{
				Exit e = (Exit) p.getLocation().getExactElement( exitName );

					// the exit doesn't exit
				if( e == null )
				{
					ic.sendFeedback( exitName + " is not an exit here!" );
					return;
				}
				
				through = (Paragraph) e.getProperty( "through" );
				
				if( through == null ) // new through
					through = new TextParagraph();
				
				Paragraph para = Editor.edit( p, through, ic, MAX_LINES, MAX_BYTES );
				
				if( para == through )
				{
					ic.sendFeedback( "You make no changes to the through description." );
				}
				else
				{
					e.setProperty( "through", para );
					ic.sendFeedback( "You change what people will see when they go through " + exitName + ".");
				}
			}
			catch( AccessViolationException e )
			{
				ic.sendFeedback( "You do not have permission to edit " + exitName );
				return;
			}

		}
		else
			usage( ic );
	}
}
