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
**  $Id: Orient.java,v 1.4 2000/01/18 14:48:27 subtle Exp $
**
**  $Log: Orient.java,v $
**  Revision 1.4  2000/01/18 14:48:27  subtle
**  compile errors
**
**
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

/**
  *  "Orient", as in "Orientate" - to get ones bearings.  This is useful
  *  as a log-in command, and just displays some statistics.
 */
public class Orient extends Command
{
	public Orient()
	{
		setKey( "orient" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Key key = Key.instance();
		Room r = p.getLocation();
		StringBuffer sb;
		
		{
			sb = new StringBuffer( "You are " );
			sb.append( r.getFullPortrait() );
			
			sb.append( ".  [" );
			sb.append( r.getContainedId() );
			sb.append( "]" );
			
			ic.sendFeedback( sb.toString() );
		}
		
		int po = key.numberPlayers() - 1;
		
		if( po == 0 )
			ic.sendFeedback( "There is no-one online but you." );
		else
		{
			sb = new StringBuffer( "There " );
			sb.append( Grammar.isAre( po ) );
			sb.append( " " );
			sb.append( Integer.toString( po ) );
			sb.append( " other " );
			sb.append( Grammar.personPeople( po ) );
			sb.append( " online" );
			
				//  one of them in this room
				//  none of them in this room
				//  5 of them in this room
			int rp = r.numberPlayers() - 1;

			if( po != 1 )
			{
				sb.append( ", " );
				sb.append( Grammar.noneOneCount( rp ) );
				sb.append( " of them in this room." );
			}
			else
			{
					//  find this mysterious player
				for( Enumeration e = key.players(); e.hasMoreElements(); )
				{
					Player z = (Player) e.nextElement();

					if( z != p )
					{
						if( z.isLocation( r ) )
						{
							sb.append( ", " );
							sb.append( z.heShe() );
							sb.append( " is with you in this room." );
						}
						else
						{
							sb.append( " but " );
							sb.append( z.heShe() );
							sb.append( " is not in this room." );
						}
						
						break;
					}
				}
			}
			
			ic.sendFeedback( sb.toString() );
		}
		
		{
				//  use the blocking command for the next output
			ColumnParagraph blocking = Blocking.buildColumnParagraph( p );
			
			if( blocking.count() != 0 )
			{
				sb = new StringBuffer( "You are blocking " );
				sb.append( Grammar.commaSeperate( blocking.elements() ) );
				sb.append( "." );
				ic.sendFeedback( sb.toString() );
			}
		}
		
		if( p.checkNewMail() )
		{
			MessageBox m = p.getMailbox();
			int c = m.unread;
			
			if( c > 1 )
				ic.sendFeedback( "^hYou have " + c + " unread messages.^-" );
			else if( c == 1 )
				ic.sendFeedback( "^hYou have one unread message.^-" );
		}
	}
}
