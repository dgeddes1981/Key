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
**  $Id: Approve.java,v 1.5 2000/02/16 08:53:49 noble Exp $
**
**  $Log: Approve.java,v $
**  Revision 1.5  2000/02/16 08:53:49  noble
**  summary no longer required
**
**  Revision 1.4  2000/02/11 14:56:15  noble
**  restores article permissions
**
**  Revision 1.3  2000/01/31 17:04:39  noble
**  added article summary editing
**
**  Revision 1.2  2000/01/27 14:55:39  noble
**  use getElement (not getElementAt)
**
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
**
*/

package key.commands.te0;

import key.*;
import key.te0.*;

import java.io.IOException;
import java.util.StringTokenizer;

public class Approve extends Command
{
	public Approve()
	{
		setKey( "approve" );
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
				
				Container t = (Container) te0.getApproved().getElement( category );
				
				if( t == null )
				{
					ic.sendError( "Could not find approval container for category '" + category + "'" );
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
				
				/*
				if( r instanceof Article )
				{
					ic.send( "Please enter a summary for '" + category + "/" + id + "'" );					
					
					Paragraph from = ((Article)r).summary;
					Paragraph para = Editor.edit( p, from, ic, 4, 200 );

				
					if( para != from && !para.isEmpty() )
						r.setProperty( "summary", para );
					else
					{
						ic.send( "You must enter a Summary. ('" + category + "/" + id + "' not approved)" );
						return;
					}
				}
				*/
				
				try
				{
					catcon.remove( r );
					
					try
					{
						t.add( r );
						ic.sendFeedback( "Approved '" + category + "/" + id + "'" );
					}
					catch( Exception e )
					{
						try
						{
							catcon.add( r );
						}
						catch( Exception e2 )
						{
								throw new UnexpectedResult( e2.toString() + " while attempting to recover from " + e.toString() + " while adding an atom after removing it during a move operation.  Atom #" + r.getIndex() + " could now be in an inconsistent state: notify an administrator if you don't want this atom automatically purged." );
						}
						
						throw new UnexpectedResult( e.toString() + " on adding the atom to the new container: restored to previous location" );
					}
				}
				catch( BadKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
				}
				catch( NonUniqueKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " on removing a matched atom" );
				}


				try
				{
					PermissionList pl = r.getPermissionList();
					
					pl.deny( Atom.moveAction );
					pl.deny( Atom.modifyAction );
					
					// restoring permissions for approved articles
				}
				catch( AccessViolationException e )
				{
					// should be alright, as the permissions are already intact
					Log.debug( "Approve", e.toString() );	
				}
				
			}
			else
				usage( ic );						
		}
		else
			usage( ic );
	}
}
