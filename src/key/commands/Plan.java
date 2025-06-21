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
**  Class: plan
**
**  Class History
**
**  Date        Name         EditMotd
**  ---------|------------|-----------------------------------------------
**  ???????     subtle       creation
**  28Jul97     exile        changed so that plan edits a text paragraph
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Plan extends Command
{
	public Plan()
	{
		setKey( "plan" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Paragraph plan = null;
		
		plan = (Paragraph) p.getPlan();
		
		if( plan == null )
			plan = new TextParagraph();
		
		Paragraph para = Editor.edit( p, plan, ic, Player.MAX_PLAN_LINES, Player.MAX_PLAN_BYTES );
		
		if( para == plan )
		{
			ic.sendFeedback( "You decide it is best to leave you plan unchanged." );
		}
		else
		{
			p.setProperty( "plan", para );
			
			ic.sendFeedback( "You change your plan." );
		}
	}
}
