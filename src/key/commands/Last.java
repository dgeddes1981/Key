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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul99     Noble        created this command
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Last extends Command
{
	private static final long serialVersionUID = -2597720726018962532L;
	
	public Last()
	{
		setKey( "last" );
		usage = "<player>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String targetPlayer;
		
		if( args.hasMoreTokens() )
		{
			targetPlayer = args.nextToken( " " );
			Object o;
			
			o = getElementInside( ic, targetPlayer, Key.shortcuts() );
			
			if( o == null )
				return;
			
			if( o instanceof Player )
			{
				Reference r = (Reference) ((Player)o).getThis();
				sendLastLogin( r , ic, p );
			}
			else if( o instanceof Container && ((Container)o).getConstraint() == Type.PLAYER )
			{
				sendContainerLastLogin( (Container) o, ic, p );
			}
			else
				ic.sendFailure( "'" + targetPlayer + "' is not something that is lastable." );
		}
		else
			usage( ic );
	}
	
	public static void sendContainerLastLogin( Container c, InteractiveConnection ic, Player p )
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
		
		if( c.count() > 20 )
		{
			ic.sendFailure( "Too many in " + c.getName() + " to list them all." );
			return;
		}
		
			//  this is a group of some sort containing players, we can
			//  finger each one of them, quickly.
		for( Enumeration e = c.referenceElements(); e.hasMoreElements(); )
			sendLastLogin( (Reference) e.nextElement(), ic, p );		
	}
	
	public static void sendLastLogin( Reference r, InteractiveConnection ic, Player p )
	{
		int in = r.getIndex();
		int ts = r.getTimestamp();

		PlayerSupplemental ps = (PlayerSupplemental) Registry.instance.getSupplemental( in, ts );
					
		if( ps == null )
		{
			Log.debug( "Last.java", "No PlayerSupplemental for " + r.getName() + " #" + r.getIndex() + ")" );
			return;
		}
					
		TimeStatistics loginStats = (TimeStatistics) ps.getLoginStats().get();
		
		Player f = null;
					
		if( r.isLoaded() )
			f = (Player) r.get();
		
		if( f != null && f.connected() )
		{
			ic.send( f.getName() + " has been logged in for " + loginStats.getTimeSinceConnection() );
		}
		else
		{
			DateTime dateTime = (DateTime) loginStats.getLastConnection();
			DateTime now = new DateTime();
						
			if( dateTime == null )
				ic.send( f.getName() + " has never logged in." );
			else
				ic.send( f.getName() + " was last seen at " + dateTime.toString( p ) + " (^h" + dateTime.difference( now ).toStdString( 1 ) + " ago^-)");
		}
		
				
	}	
}
