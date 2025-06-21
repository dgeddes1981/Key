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
  *  This command is intended to change the players context to
  *  the room specified in the argument, or the current room,
  *  otherwise.
 */
public class RefMail extends CommandCategoryContainer
{
	public RefMail()
	{
		setKey( "mail" );
		usage = "<command>";
	}
	
	public void runCommand( Commandable c, Player p, StringTokenizer args, String fullLine, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom old = p.getContext();
		
		if( !(old instanceof MessageBox) )
		{
			p.setContext( p.getMailbox() );
			super.runCommand( c, p, args, fullLine, ic, flags );
			p.setContext( old );
		}
		else
			super.runCommand( c, p, args, fullLine, ic, flags );
	}
}
