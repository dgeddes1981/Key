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
**  $Id: Quit.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Aug98    subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

public class Quit extends Command
{
	private static final long serialVersionUID = 8506478616464130492L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Quit.class, String.class, "passwordWarning",
			AtomicElement.PUBLIC_FIELD,
			"The warning output when no password has been set" ),
		AtomicElement.construct( Quit.class, String.class, "canSaveWarning",
			AtomicElement.PUBLIC_FIELD,
			"The warning output when the user doesn't have permission to save" ),
		AtomicElement.construct( Quit.class, String.class, "quitPrompt",
			AtomicElement.PUBLIC_FIELD,
			"The 'are you sure' style prompt" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String passwordWarning = "You have not yet set a password.  If you quit now, your character will be deleted.  If you type 'no' now, then you can type 'password' when you get back to the prompt to set one.";
	
	public String canSaveWarning = "You have not been given permission to save.  If you quit now, your character will be deleted.  If you type 'no' now, then you can type 'staff' when you get back to the prompt to get a list of the people online who can give you permission to save.  You need to ask one of these people to make you a 'resident'.";
	
	public String quitPrompt = "Do you still wish to quit? ";
	
	public Quit()
	{
		setKey( "quit" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.connected() )
		{
			if( p.getCanSave() )
			{
				if( p.hasPassword() )
				{
					p.disconnect();
				}
				else
				{
					ic.send( passwordWarning );
					if( Grammar.getYesNo( quitPrompt, false, ic ) )
						p.disconnect();
				}
			}
			else
			{
				ic.send( canSaveWarning );
				if( Grammar.getYesNo( quitPrompt, false, ic ) )
					p.disconnect();
			}
		}
	}
}
