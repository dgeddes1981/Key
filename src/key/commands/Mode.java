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

public class Mode extends Command
{
	public Mode()
	{
		usage = "[<a prefix to your every typed command>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String mode = null;
		
		if( args.hasMoreTokens() )
		{
			//prompt = args.nextToken( "" );
				//  this allows leading & trailing spaces in prompts
			mode = fullLine.substring( fullLine.indexOf( ' ' ) + 1 );
			
			if( mode.length() > Player.MAX_MODE_LENGTH )
			{
				ic.sendFeedback( "That is too long to be set as a mode.  Please limit your mode to " + Player.MAX_MODE_LENGTH + " characters." );
				return;
			}

			if( !mode.endsWith( " " ) )
			{
				ic.sendFeedback( "Warning: Your mode does not end with a space.  You'll probably want to start out each line by typing a space first." );
			}
			
			p.setProperty( "mode", mode );
			ic.sendFeedback( "You change your mode so that '" + mode + "' is prepended to every line you type." );
		}
		else
		{
			ic.sendFeedback( "This command is used to save you typing the same thing over and over.  For instance, if you're setting up a lot for a particular exit, you might use 'mode exit track ' and then you can type your commands as if you'd already typed those three words at the start of each line.  Try it, you'll work it out.  Enter a blank line to clear your mode." );
		}
	}
}
