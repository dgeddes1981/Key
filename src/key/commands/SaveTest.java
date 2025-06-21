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
**  $Id: SaveTest.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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

/**
  *  This command is dedicated to "Wizards First Rule", a novel
  *  by Terry Goodkind.  The Wizards First Rule, you see, can be
  *  summed up with "people are stupid".  More specifically, people
  *  can be made to believe something if they want it to be true,
  *  or if they're scared of it being true.  The application of the
  *  rule to this particular class is left as a (rather trivial)
  *  exercise for the reader.  And, for the cynical of heart, no,
  *  this wasn't the first command I ever wrote, it's nearly the last.
  *  (While converting the playerfiles).  The first command I wrote
  *  was 'quit'.  *smirk*.  - subtle, in one of his more verbose moods.
 */
public class SaveTest extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( SaveTest.class, String.class, "passwordWarning",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the player has not entered a password" ),
		AtomicElement.construct( SaveTest.class, String.class, "canSaveWarning",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the player does not have permission to save" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String passwordWarning = "You have not yet set a password.  You can type 'password' set one.";
	public String canSaveWarning = "You have not been given permission to save.  You can type 'staff' to get a list of the people online who can give you permission to save.  You need to ask one of these people to make you a 'resident'.";
	
	public SaveTest()
	{
		setKey( "save" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.getCanSave() )
		{
			if( p.hasPassword() )
			{
				ic.sendFeedback( "Character saved." );
			}
			else
				ic.send( passwordWarning );
		}
		else
			ic.send( canSaveWarning );
	}
}
