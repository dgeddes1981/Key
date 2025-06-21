/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key.commands;
import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

/**
  *  Allows a player to write the rooms portrait
 */
public class Portrait extends Command
{
	public Portrait()
	{
		setKey( "portrait" );
		usage = "<portrait>";
	}
	
    public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String portraitText = args.nextToken( "" );
		Room r = (Room) p.getContext();
		
		try
		{
			r.setProperty( "portrait", portraitText );
			ic.sendFeedback( "The portrait now reads: [" + p.getName() + " is " + r.relation() + "] " + r.portrait() );
		}
		catch( ClassCastException e )
		{
			ic.sendError( r.getName() + " doesn't appear to be a room." );
		}
		catch( AccessViolationException e )
		{
			ic.sendError( "You are not allowed to set the portrait for " + r.getName() + "." );
		}
	}
}
