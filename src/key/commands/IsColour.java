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
**  $Id: IsColour.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
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

public class IsColour extends Command
{
	public IsColour()
	{
		setKey( "iscolour" );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		TelnetIC tc = null;
		
		try
		{
			tc = (TelnetIC) ic;
		}
		catch( ClassCastException e )
		{
			ic.sendFailure( "This command only works with telnet sessions" );
			return;
		}

		tc.send( topParagraph );
		tc.sendRaw( "\033[0;34mblue, \033[0;32mgreen, \033[0;36mcyan, \033[0;31mred, \033[0;35mmagenta, \033[0;33mbrown, \033[1;30mdark" );
		tc.sendRaw( "\033[1;34mbrightBlue, \033[1;32mbrightGreen, \033[1;36mbrightCyan, \033[1;31mbrightRed," );
		tc.sendRaw( "\033[1;35mbrightMagenta, \033[1;33mbrightYellow, \033[1;37mwhite\033[0m" );
		tc.sendRaw( "\033[4munderline\033[m, \033[7mreverse\033[m" );
		tc.send( endParagraph );
	}
	
	private static HeadingParagraph headingParagraph = new HeadingParagraph( "ANSI colour test pattern" );
	
	private TextParagraph topParagraph = new TextParagraph(
		TextParagraph.CENTERALIGNED,
	"The words on the lines below should be displayed in colour\n" );
	
	private TextParagraph endParagraph = new TextParagraph(
	"\n" +
	"If you can see the above colours, you have an ANSI colour terminal, " +
	"and you can type 'term ansi' to force it on.  If some of the words " +
	"appear bold, you probably have a vt100-style terminal.  Use " + 
	"'term vt100' to force that on." );
}
