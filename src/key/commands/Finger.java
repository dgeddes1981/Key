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
**  $Id: Finger.java,v 1.4 1999/11/26 13:55:16 pdm Exp $
**
**  $Log: Finger.java,v $
**  Revision 1.4  1999/11/26 13:55:16  pdm
**  fix for Mordy's NullPtrException in finger friends (expect player supplemental).
**
**  Will log to debug when it breaks, so we can see which player doesn't have one.
**
**  Revision 1.3  1999/10/14 19:00:05  pdm
**  moved the clan clause up an if()
**
**  Revision 1.2  1999/10/14 18:54:11  noble
**  Added Finger clan
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Finger extends Command
{
	private static final long serialVersionUID = 511480019705872281L;
	
	public Finger()
	{
		setKey( "finger" );
		usage = "<player>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String targetPlayer;
		
		if( args.hasMoreTokens() )
		{
			targetPlayer = args.nextToken( " " );
			Object o;
			
			//Container c = (Container) Key.shortcuts();
			o = getElementInside( ic, targetPlayer, Key.shortcuts() );
			
			if( o == null )
				return;
			
			if( o instanceof Player )
			{
				Player a = (Player) o;
				a.sendFingerScreen( ic );
			}
			else if( o instanceof Clan )
				( (Clan) o ).sendFingerScreen( ic );
			else if( o instanceof Container )
			{
				Container c = (Container) o;
				
				if( c.getConstraint() == Type.PLAYER )
				{
					if( c.count() == 0 )
					{
							//  output an appropriate message
						if( c instanceof Friends )
							ic.sendFailure( "You have no friends." );
						else
							ic.sendFailure( "There is no-one in " + c.getName() );
	
						return;
					}
					
					if( args.hasMoreTokens() )
						sendLastLogin( c, ic, p, args.nextToken() );
					else
						sendLastLogin( c, ic, p, "a" );
				}
			}
			else
				ic.send( "'" + targetPlayer + "' is not something that is fingerable." );
		}
		else
			usage( ic );
	}
	
	public static void sendLastLogin( final Container box, final InteractiveConnection ic, final Player p, final String screen )
	{
		Grammar.pagedList( box, screen, new Grammar.PagedListStuffer()
			{
				public void stuffEntry( Reference r, TableParagraph.Generator tpg, int pageOffset, int totalOffset )
				{
					int in = r.getIndex();
					int ts = r.getTimestamp();
					
					PlayerSupplemental ps = (PlayerSupplemental) Registry.instance.getSupplemental( in, ts );
					
					if( ps == null )
					{
						Log.debug( this, "No PlayerSupplemental for " + r.getName() + " #" + r.getIndex() + ")" );
						return;
					}
					
					String rowContents[] = new String[ columns.length ];
					TimeStatistics loginStats = (TimeStatistics) ps.getLoginStats().get();
						
					Player f = null;
						
					if( r.isLoaded() )
						f = (Player) r.get();
			
					rowContents[0] = r.getName();
			
					if( f != null && f.connected() )
					{
						ic.send( f.getName() + " has been logged in for " + loginStats.getTimeSinceConnection() );
							
						rowContents[1] = "connected for " + loginStats.getTimeSinceConnection();
					}
					else
					{
						DateTime dateTime = (DateTime) loginStats.getLastConnection();
							
						if( dateTime == null )
							rowContents[1] = "<never logged in>";
						else
							rowContents[1] = dateTime.toString( p );
					}
					
					tpg.appendRow( rowContents );
				}
			}, ic, new TableParagraph.Generator( columns ), "player" );
	}
	
	public static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "Name", Player.MAX_NAME ),
		new TableParagraph.Column( "Last connected At", 50 )
	};
}
