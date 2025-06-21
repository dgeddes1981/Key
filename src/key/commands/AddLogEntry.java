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
**  $Id: AddLogEntry.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Jul97     subtle      created this command
**
*/

package key.commands;

import key.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class AddLogEntry extends Command
{
	private static final long serialVersionUID = -6640470407966184447L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( AddLogEntry.class, String.class, "logName",
			AtomicElement.PUBLIC_FIELD,
			"the name of the log file" ),
		AtomicElement.construct( AddLogEntry.class, Paragraph.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message received when something is logged" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String logName = "bug";
	public Paragraph feedback = new TextParagraph( "Logged." );
	
	public AddLogEntry()
	{
		setKey( "log" );
		usage = "<message>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			if( logName != null && logName.length() > 0 && logName.indexOf( "/." ) == -1 )
			{
				Log.log( logName, p.getName() + " - " + args.nextToken( "" ) );
				
				ic.send( feedback );
			}
			else
				ic.sendFailure( "This command seems to be set up incorrectly." );
		}
	}
}
