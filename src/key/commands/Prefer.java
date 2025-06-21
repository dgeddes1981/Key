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
**  05Jul97     subtle       created this command
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Prefer extends Command
{
	public Prefer()
	{
		usage = "<player>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
        if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String preferred = args.nextToken();
		Object o = getPlayer( ic, preferred );
		
		if( o == null )
			return;
		
		if( o == p )
		{
			ic.sendError( "Come now.  I hardly see a need.  After all, _we_ know how special you truly are already." );
			return;
		}
		
		Player f = (Player) o;
		Scape prefer = (Scape) p.getPrefer();
		
		if( prefer.contains( f ) )
		{
				//  this person is already preferred.. un'prefer' them
			try
			{
				prefer.remove( f );
			}
			catch( BadKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			catch( NonUniqueKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			
			ic.sendFeedback( f.getName() + " is no longer preferred." );
		}
		else
		{
			try
			{
				prefer.add( f );
			}
			catch( NonUniqueKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			catch( BadKeyException e )
			{
				throw new UnexpectedResult( e.toString() );
			}
			
			ic.sendFeedback( f.getName() + " is now preferred.  Multiple matches will go to them first." );
		}
	}
}
