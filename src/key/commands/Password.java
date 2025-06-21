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
import java.io.*;
import java.util.StringTokenizer;

public class Password extends Command
{
private static final long serialVersionUID = -9039688251483920412L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Password.class, String.class, "noPasswordMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when there is no password at the moment" ),
		AtomicElement.construct( Password.class, String.class, "currentPasswordPrompt",
			AtomicElement.PUBLIC_FIELD,
			"the prompt for the users current password" ),
		AtomicElement.construct( Password.class, String.class, "incorrectMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the player gets the message wrong" ),
		AtomicElement.construct( Password.class, String.class, "newPasswordPrompt",
			AtomicElement.PUBLIC_FIELD,
			"the first prompt for the new password" ),
		AtomicElement.construct( Password.class, String.class, "retypePasswordPrompt",
			AtomicElement.PUBLIC_FIELD,
			"the second prompt for the new password" ),
		AtomicElement.construct( Password.class, String.class, "passwordChangedMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the password was successfully changed" ),
		AtomicElement.construct( Password.class, String.class, "differentPasswords",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the password was not changed" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	static final int MIN=3;
	
	
	public String noPasswordMessage = " You have no password at the moment.  " +
			"You will now be asked to enter a password twice for your character " +
			"(just to make sure you didn't type it wrong).\n";
	public String currentPasswordPrompt = "Please enter your current password: ";
	public String incorrectMessage = "That is incorrect.";
	public String newPasswordPrompt = "Please enter the new password: ";
	public String retypePasswordPrompt = "Re-type it, just in case: ";
	public String passwordChangedMessage = "Password changed.";
	public String differentPasswords = "Password NOT changed.  (You didn't type the same word both times)";
	
	public Password()
	{
		setKey( "password" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		key.primitive.Password cpass = p.getActualPassword();
		boolean ds = p.willSync();
		
		if( !cpass.isSet() )
		{
			ic.sendFeedback( noPasswordMessage );
		}
		else
		{
			Player doer = Player.getCurrent();
			if( doer != null && p != doer && doer.isBeyond() )
			{
				ic.sendFeedback( "(beyond used to changePassword)" );
			}
			else
			{
				String e = ic.hiddenInput( currentPasswordPrompt );
				if( !cpass.check( e ) )
				{
					ic.sendError( incorrectMessage );
					return;
				}
			}
		}
		
		String e = "";
		while( e.length() <= MIN )
		{
			e = ic.hiddenInput( newPasswordPrompt );
			if( e.length() <= MIN )
				ic.sendFeedback( "Password must be longer than 3 characters." );
		}
		
		String f = ic.hiddenInput( retypePasswordPrompt );
		if( e.equals( f ) )
		{
			p.setPassword( e );
			ic.sendFeedback( passwordChangedMessage );
		}
		else
			ic.sendFeedback( differentPasswords );
		
		if( !ds )
			p.sync();
	}
}
