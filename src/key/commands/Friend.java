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
**  Class: friend
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  02Jul97     exile        fix so that friending a player that is
**                           already a friend doesn't throw an exception
**  05Jul97     subtle       code restructure & additional sarcasm.
**  05Jul97     exile        tidied up some stuff such as adding a setKey etc.
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Friend extends Command
{
	public Friend()
	{
		setKey( "friend" );
		usage = "<player>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
        if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String friended = args.nextToken();
		Object o = getPlayer( ic, friended );
		
		if( o == null )
			return;
		
		Player f = (Player) o;
		
		Scape friends = (Scape) p.friends;
		
		if( friends.contains( f ) )
		{
				//  this person is already friended.. unfriend them
			try
			{
				friends.remove( f );
			}
			catch( BadKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			catch( NonUniqueKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			
				//  forgive the sarcasm.  I'm not a big
				//  fan of friend channels. ;)
			ic.sendFeedback( f.getName() + " is no longer a friend.  (I'm sure they're heartbroken)" );
		}
		else
		{
			if( o == p )
			{
				ic.sendError( "Hmmm.  That sort of friend, eh?" );
				return;
			}
			
			if( !f.canMakeFriend( p ) )
			{
				ic.sendFailure( f.getName() + " doesn't want to be friended." );
				return;
			}
			
			try
			{
				friends.add( f );
			}
			catch( NonUniqueKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			catch( BadKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			
			ic.sendFeedback( f.getName() + " has been added to your friends list." );
		}
	}
}
