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
**  $Id: Repeat.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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

public class Repeat extends Command
{
private static final long serialVersionUID = 8606798921686377144L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Repeat.class, String.class, "beforeClause",
			AtomicElement.PUBLIC_FIELD,
			"the text sent to the player if there is nothing to repeat" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String beforeClause = "You have nothing to repeat!";
	
	public Repeat()
	{
		setKey( "repeat" );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Repeated r = p.getRepeated();

		if( r.getCommand() != null )
			r.getCommand().run( p, args, r, this, ic, flags );
		else
			ic.sendError( beforeClause );
	}
}
