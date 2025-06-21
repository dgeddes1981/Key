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
**  $Id: EmailAddress.java,v 1.2 2000/06/23 18:51:46 subtle Exp $
**
**  $Log: EmailAddress.java,v $
**  Revision 1.2  2000/06/23 18:51:46  subtle
**  Added cool timeout code
**
*/

package key.primitive;

import key.util.SMTP;
import key.util.HTUU;
import key.Event;
import key.Reference;
import key.EmailSetter;
import key.Player;
import key.Factory;
import key.Key;
import key.Daemon;
import key.Daemon;
import key.InteractiveConnection;
import key.InvalidEmailException;

import java.io.*;
import java.net.UnknownHostException;
import java.net.ProtocolException;
import java.util.Random;
import java.util.StringTokenizer;

public class EmailAddress implements java.io.Serializable
{
	private static final long serialVersionUID = -2285329608433520970L;
	
	private String validationCode;
	protected String validAddress;
	protected String address;
	protected Reference setter;
	
	public EmailAddress()
	{
		validationCode = null;
		validAddress = null;
		address = null;
		setter = null;
	}
	
	public String toString()
	{
		if( validAddress != null )
			return( validAddress );
		else
			return( "<not set>" );
	}
	
	public String get()
	{
		return( validAddress );
	}

	public String getInvalid()
	{
		return( address );
	}
	
	public boolean isValid()
	{
		return( validAddress != null );
	}
	
	public boolean isSet()
	{
		return( address != null || validAddress != null );
	}
	
	public void set( String newAddress, InteractiveConnection ic, Player p )
	{
			//  simply checks that there is something between the seperators
			//  and that both of them (@ and .) exist in the string, somewhere.
		try
		{
			boolean containsAt = false;
			boolean containsDot = false;
			boolean lastWasSeperator = true;
			boolean firstseperator = true;
			
			StringTokenizer st = new StringTokenizer( newAddress, "@.", true );
			
			while( st.hasMoreTokens() )
			{
				String s = st.nextToken();
				
				if( s.charAt( 0 ) == '@' )
				{
					if( lastWasSeperator )
						throw new InvalidEmailException();
						
					if( !firstseperator )
						throw new InvalidEmailException();

					firstseperator = false;
					lastWasSeperator = true;
					containsAt = true;
				}
				else if( s.charAt( 0 ) == '.' )
				{
					if( lastWasSeperator )
						throw new InvalidEmailException();
						
					if( firstseperator )
						lastWasSeperator = false;
					else
					{
						lastWasSeperator = true;
						containsDot = true;
					}
				}
				else
				{
					lastWasSeperator = false;
				}
			}
			
			if( !containsAt || !containsDot || lastWasSeperator )
				throw new InvalidEmailException();
		}
		catch( InvalidEmailException e )
		{
			ic.sendError( "That is not the correct form of an email address.  Please do not use this command frivolously" );
			return;
		}
		
		if( address != null && (setter == null || !setter.isValid()) )
		{
			//  some sort of message here saying the previous
			//  email was never registered?
			ic.sendFeedback( "\nThe previous email address you set was never validated.  Please do not use this command frivolously.\n" );
		}
		
			//  check for invalid characters
		if( newAddress.indexOf( ' ' ) != -1 ||
			newAddress.indexOf( '^' ) != -1 ||
			newAddress.indexOf( '\"' ) != -1 ||
			newAddress.indexOf( '<' ) != -1 ||
			newAddress.indexOf( '>' ) != -1 ||
			newAddress.indexOf( '(' ) != -1 ||
			newAddress.indexOf( ')' ) != -1 )
		{
			ic.sendError( "Email addresses do not usually contain some of the characters that you've typed.  If you're sure '" + newAddress + "' is your correct email address, email " + (String) Key.instance().getConfig().getProperty( "returnAddress" ) );
			return;
		}
		
		validationCode = generateRandomCode();
		address = newAddress;
		
		send( ic, p );
		
		ic.sendFeedback( "\nAn email will be sent to " + address + " shortly, containing the verification code that you need to make this new email address active.  If you have made a mistake, please set your email address again immediately." );
	}

	private String generateRandomCode()
	{
		return( HTUU.encodeTypable( Integer.toString( new Random().nextInt() ) ).substring( 0, 5 ) );
	}
	
	public void send( InteractiveConnection ic, Player p )
	{
		if( setter == null || !setter.isValid() )
		{
			EmailSetter es = (EmailSetter) Factory.makeAtom( EmailSetter.class, p.getName() );
			try
			{
				es.ensureInKey();
			}
			catch( Exception e )
			{
				ic.sendError( "An error occurred while attempting to send your email: " + e.toString() );
				return;
			}
			
			setter = es.getThis();
			es = null;
		}
		
		if( address == null && validAddress != null )
			address = validAddress;
		if( validationCode == null )
			validationCode = generateRandomCode();
		
		((EmailSetter)setter.get()).set( p.getThis(), address, validationCode );
	}
	
	public boolean verify( String code, InteractiveConnection ic, Player p )
	{
		if( address == null )
			ic.sendError( "You don't have a pending email verification code" );
		else if( code.equalsIgnoreCase( validationCode ) )
		{
			validAddress = address;
			address = null;
			validationCode = null;
			ic.sendFeedback( "Verification code correct, your email address has been set to: " + validAddress );
			key.Log.log( "email", p.getName() + ":" + validAddress );
			return true;
		}
		else
			ic.sendError( "Invalid verification code.  Make sure you type it exactly as it appears in the email that was sent to you.  If you got more than one email sent, the code WILL change - you have to use the code from the LAST email that was sent.  (So yes, it's much better to wait for the email than request lots of them, :*)" );
		
		return( false );
	}
	
	public boolean isAwaitingVerification()
	{
		return( address != null );
	}
}
