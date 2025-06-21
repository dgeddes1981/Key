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
**  Class: describe
**
**  Class History
**
**  Date        Name         Describe
**  ---------|------------|-----------------------------------------------
**  18Jul97     snapper      creation
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

public class Describe extends Command
{
	/**
	  *  The default maximum number of lines for this command.  Can be
	  *  overridden by specifications in the editted class, to be either
	  *  larger or smaller.
	 */
	public static final int MAX_LINES = 40;
	
	/**
	  *  The default maximum number of bytes for this command.  Can be
	  *  overridden by specifications in the editted class, to be either
	  *  larger or smaller.
	 */
	public static final int MAX_BYTES = MAX_LINES * TelnetIC.DEFAULT_WIDTH;
	
	public Describe()
	{
		setKey( "describe" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Paragraph desc = null;
		Atom context = p.getContext();
		
		try
		{
			desc = (Paragraph) context.getProperty( "description" );
		}
		catch( NoSuchPropertyException e )
		{
			ic.sendFeedback( "There is nothing in the current context for you to describe. (" + context.getId() + ")" );
			return;
		}
		
			//  attempt to determine if there are any limits for this
			//  edit.
		AtomicStructure structure = context.getStructure();
		AtomicElement ae = structure.getElement( "description" );
		
		int max_lines = MAX_LINES;
		int max_bytes = MAX_BYTES;
		
		if( ae != null )
		{
			AtomicSpecial special = ae.getSpecial();
			
			if( special instanceof TextParagraphLengthWrapper )
			{
				TextParagraphLengthWrapper wrapper = (TextParagraphLengthWrapper) special;
				max_lines = wrapper.getLineLimit();
				max_bytes = wrapper.getByteLimit();
			}
		}
		
		if( desc == null )
			desc = new TextParagraph();
		
		Paragraph newd = Editor.edit( p, desc, ic, max_lines, max_bytes );
		context.setProperty( "description", newd );
		
		if( newd == desc )
		{
			ic.sendFeedback( "No changes made to the description of '" + context.getName() + "'" );
		}
		else
			ic.sendFeedback( "You change the description of '" + context.getName() + "'" );
	}
}
