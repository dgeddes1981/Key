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
**  $Id: Message.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
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
import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class Message extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Message.class, Paragraph.class, "text",
			AtomicElement.PUBLIC_FIELD,
			"the message to be output" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Paragraph text = new TextParagraph();
	
	public Message()
	{
		setKey( "message" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( text == null )
		{
			ic.sendError( "This echo is incorrectly set up" );
			return;
		}
		
		ic.send( text );
	}
}
