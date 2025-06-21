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
import key.primitive.*;

import java.io.*;
import java.util.StringTokenizer;

public class Email extends Command
{
private static final long serialVersionUID = 4598868694266351368L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Email.class, Paragraph.class, "warning",
			AtomicElement.PUBLIC_FIELD,
			"the paragraph sent just before they have to type their email" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final int MAX_LENGTH = 60;
	
	public Paragraph warning = new TextParagraph();
	
	public Email()
	{
		setKey( "email" );
		usage = "[public | private]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String email = args.nextToken();
			
			if( email.equalsIgnoreCase( "public" ) )
			{
				p.setProperty( "privateEmail", Boolean.FALSE );
				ic.sendFeedback( "Your email is now available for all to see" );
				return;
			}
			else if( email.equalsIgnoreCase( "private" ) )
			{
				p.setProperty( "privateEmail", Boolean.TRUE );
				ic.sendFeedback( "Your email is now hidden from everyone except admins, and those you have 'seePrivateInfo' for." );
				return;
			}
			else
				ic.sendError( "Please use 'email public' to make your email address publically available, or 'email private' to hide it so that only staff and people you've given 'seePrivateInfo' can see it.  'email' by itself will allow you to enter or change your email address." );
			
			return;
		}
		
		if( warning != null )
		{
			ic.send( warning );
		}
		
		String email;
		email = ic.input( "Enter your new email address: " );
		
		if( email.length() == 0 )
			;
		else if( email.length() <= MAX_LENGTH )
		{
			EmailAddress address = (EmailAddress) p.getProperty( "email" );
			
			address.set( email, ic, p );
		}
		else
		{
			ic.sendFeedback( "That is too long to be set as an email.  Please limit your aka to " + MAX_LENGTH + " characters." );
		}
	}
}
