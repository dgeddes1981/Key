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
  *  Shows a players email address
 */
public class CheckEmail extends Command
{
	public CheckEmail()
	{
		setKey( "email" );
		usage = "";
	}

	/**
	  *  Changes the players context.  The context is *always* specified
	  *  as an offset from the player class - regardless of what your current
	  *  reference is.  This is to stop you getting lost ;)
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		
		ic.sendFeedback( "Your email address is ^h" + p.getEmailAddress() + "^-" );
		if( p.isEmailPrivate() )
		{
			ic.sendFeedback( "Your email address ^his not visible^- to people who you haven't allowed to 'seePrivateInfo'.  You can list your current permission list with the 'actions' command.  You can allow someone to see your private info with 'allow <name> seePrivateInfo'.  You can stop someone from seeing your private info with 'default <name> seePrivateInfo'.  You can make your email visible with 'email public'." );
		}
		else
			ic.sendFeedback( "Your email address ^his publically visible^-.  You can make you email private with 'email private'." );
	}
}
