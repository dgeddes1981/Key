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
**  $Id: BaseConfiguration.java,v 1.1.1.1 1999/10/07 19:58:31 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul97     subtle       created this class
**
*/

package key.config;

import key.*;
import key.primitive.*;
import java.io.*;

public final class BaseConfiguration extends Container implements Configuration
{
	private static final long serialVersionUID = 8705312328428737545L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( BaseConfiguration.class, String.class, "loginMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message displayed by default when someone logs in" ),
		AtomicElement.construct( BaseConfiguration.class, String.class, "logoutMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message displayed by default when someone logs out" ),
		AtomicElement.construct( BaseConfiguration.class, String.class, "emailServer",
			AtomicElement.PUBLIC_FIELD,
			"the place to send outgoing email" ),
		AtomicElement.construct( BaseConfiguration.class, Paragraph.class, "emailForm",
			AtomicElement.PUBLIC_FIELD,
			"the email form letter to verify an address" ),
		AtomicElement.construct( BaseConfiguration.class, String.class, "returnAddress",
			AtomicElement.PUBLIC_FIELD,
			"the address of return email" ),
		AtomicElement.construct( BaseConfiguration.class, Duration.class, "emailPause",
			AtomicElement.PUBLIC_FIELD,
			"the delay between a player setting their email address and the email being sent" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	public String loginMessage = "%o appears.";
	public String logoutMessage = "%o disappears.";
	public String emailServer = "realm.progsoc.uts.edu.au";
	public String returnAddress = "forest@realm.progsoc.uts.edu.au";
	public Duration emailPause = new Duration( 60000 );
	
	public Paragraph emailForm = new TextParagraph( 
		"This is an automated email verification message.\n" +
		"\n\n" +
		"Character name: %p\n" +
		"Date email registered: %d\n" +
		"Verification code: %v\n" +
		"\n\n" +
		"In order to complete the registration of your %c character, you will\n" +
		"need to issue the command (on %c):  verify %v\n" +
		"\n" +
		"------------------------------------------------------------------------------\n" +
		"If you have recieved this email and have never heard of %c, please\n" +
		"ignore this message.  If you are getting lots of these messages, mail\n" +
		"%r, and we'll list your email address as\n" +
		"permanently invalid, so that people can't mailbomb you with our system.\n" +
		"\n\n------------------------------------------------------------------------------\n" +
		"                              Key - trial edition\n" +
		"                  http://realm.progsoc.uts.edu.au/~subtle/key\n" +
		"                    telnet  realm.progsoc.uts.edu.au 2809\n" );
	
	public BaseConfiguration()
	{
		setConstraint( Type.CONFIGURATION );
		setKey( "config" );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
}
