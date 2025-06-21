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
**  $Id: Qualify.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import java.util.StringTokenizer;
import java.io.*;

public class Qualify extends Command
{
private static final long serialVersionUID = -6972065623187981763L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Qualify.class, Paragraph.class, "help",
			AtomicElement.PUBLIC_FIELD,
			"the message output when the command is executed by itself" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Paragraph help = defaultHelp;
	
	public Qualify()
	{
		setKey( "colour" );
		
		usage = "[<something> <colour>]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		QualifierList ql = p.getQualifierList();
		
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}

		String classId = args.nextToken();
			
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		Type c = null;
		
		try
		{
			c = Type.forName( classId );
		}
		catch( ClassNotFoundException e )
		{
			ic.sendError( "Unknown type '" + classId + "'" );
			return;
		}
		
		String qualifyId = args.nextToken();
		
		char code = Qualifiers.getCodeCode( qualifyId );
		
		if( code == Qualifiers.UNKNOWN_CODE && !qualifyId.equalsIgnoreCase( Qualifiers.UNKNOWN_STRING ) )
		{
			ic.sendError( "Could not find a colour for '" + qualifyId + "'" );
			ic.sendFeedback( "Use: '" + getName() + " " + classId + " normal' to stop colouring something." );
			return;
		}
				
		ql.set( c, code );
		if( code != ' ' )
			ic.sendFeedback( "Effects from " + c.getName() + " will now be ^" + code + Qualifiers.getCodeName( code ) + "^-." );
		else
			ic.sendFeedback( "Effects from " + c.getName() + " will now be " + Qualifiers.getCodeName( code ) + "." );
	}
	
	public void usage( InteractiveConnection ic )
	{
		super.usage( ic );
		
		ic.blankLine();
		ic.send( help );
	}
	
	private static TextParagraph defaultHelp = new TextParagraph(
		TextParagraph.CENTERALIGNED,
	"For example:\n" +
	"  colour player brightMagenta -- (to colour tells ^Mbright pink^-)\n" +
	"  colour friends red          -- (to colour friend tells ^rred^-)\n" +
	"  colour room cyan            -- (to colour says and emotes ^ccyan^-)\n" +
	"  colour shouts brightBlue    -- (to colour shouts ^Bbright blue^-)\n" +
	"  colour clan hilite          -- (to ^hhilite^- your clan channel)\n" +
	"  colour connection yellow    -- (the colour of 'name has connected')\n" +
	"  colour movement green       -- (the colour of room enter/exit messages)\n" +
	"  colour blocking reverse     -- (the colour of 'name blocks the clan')\n" +
	"  colour feedback underline   -- (the colour of 'You tell X 'Y' messages')\n" +
	"\n" +
	"The full list of available colours can be obtained by typing 'iscolour'\n" +
	"      You can see your current colour scheme by typing 'colours'" );
}
