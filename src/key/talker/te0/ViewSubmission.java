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
**  $Id: ViewSubmission.java,v 1.1 2000/01/31 17:07:08 noble Exp $
**
**  $Log: ViewSubmission.java,v $
**  Revision 1.1  2000/01/31 17:07:08  noble
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

public class ViewSubmission extends Command
{
	public ViewSubmission()
	{
		setKey( "viewsub" );
		usage = "<category> <id>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
   			String category = args.nextToken();
   				
   			if( args.hasMoreTokens() )
			{
				String id = args.nextToken();
				
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
				
				Atom r = null;
				
				try
				{
					r = (Atom) catcon.getElement( id );
				}
				catch( ClassCastException cc )  // not an Atom
				{
					ic.sendError( "That is not an atom" );
					return;							
				}				

				if( r == null )
				{
					ic.sendError( "Could not find id '" + id + "'" );
					return;					
				}
				
				if( r instanceof Article )
					ic.send( ((Article)r).aspect() );
				else if( r instanceof Survey )
					ic.send( ((Survey)r).aspect() );

			}
			else
				usage( ic );						
		}
		else
			usage( ic );
	}
}
