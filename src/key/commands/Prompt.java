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
**  $Id: Prompt.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Prompt extends Command
{
	public static final int MAX_LENGTH = 10;
	
	public Prompt()
	{
		usage = "[<a prompt>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String prompt = "-> ";
		
		if( args.hasMoreTokens() )
		{
			//prompt = args.nextToken( "" );
				//  this allows leading & trailing spaces in prompts
			prompt = fullLine.substring( fullLine.indexOf( ' ' ) + 1 );
			
			if( prompt.length() > MAX_LENGTH )
			{
				ic.sendFeedback( "That is too long to be set as a prompt trailer.  Please limit your prompt trailer to " + MAX_LENGTH + " characters." );
				return;
			}
		}
		
		p.setProperty( "prompt", prompt );
		ic.sendFeedback( "You change your prompt so that it will end with: '" + prompt + "'" );
	}
}
