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

package key.primitive;

import key.Key;
import key.Log;
import key.util.HTUU;
import key.InteractiveConnection;
import key.PasswordEntryCancelled;

import java.io.*;
import java.net.UnknownHostException;
import java.net.ProtocolException;
import java.util.Random;
import java.util.StringTokenizer;

/**
  *  This class is not immutable and should not be given
  *  out to people you don't want to be able to modify it.
 */
public final class Password implements java.io.Serializable
{
	private static final long serialVersionUID = -362026314546050465l;
	public static final String FAIL_PASSWORD_LOG = "failPassword";
	public static final int MAX_PASSWORD = 10;
	
	private String password;
	
	public Password()
	{
		password = null;
	}
	
	public String toString()
	{
		if( password != null )
			return( "<set>" );
		else
			return( "<not set>" );
	}
	
	public boolean isSet()
	{
		return( (password != null) && (password.length() != 0) );
	}
	
	public boolean check( String enteredPassword )
	{
		enteredPassword = enteredPassword.substring( 0, Math.min( enteredPassword.length(), MAX_PASSWORD ) );
		
		try
		{
			if( password.equals( HTUU.encode( enteredPassword ) ) )
				return true;
			else
				return false;
		}
		catch( Exception e )
		{
			return( false );
		}
	}
	
	public String checkGetPassword( String name, InteractiveConnection ic ) throws PasswordEntryCancelled
	{
		String in = ic.hiddenInput( "Enter your password: " );
		if( in.length() == 0 )
			throw new PasswordEntryCancelled();
		
		return( in );
	}
	
	public boolean check( String name, InteractiveConnection ic )
		throws IOException, PasswordEntryCancelled
	{
		if( password != null && password.length() > 0 )
		{
			String in = checkGetPassword( name, ic );
			
			if( check( in ) )
			{
				ic.blankLine();
				return( true );
			}
			else
			{
				ic.send( "Password incorrect." );
				ic.blankLine();
				
				Log.log( FAIL_PASSWORD_LOG, name + " from " + ic.getFullSiteName() );
				return( false );
			}
		}
		else
		{
			//ic.error( "Cannot authenticate - no password" );
			return( true );
		}
	}
	
	public void set( String newValue )
	{
		password = HTUU.encode( newValue.substring( 0, Math.min( newValue.length(), MAX_PASSWORD ) ) );
	}
	
	public void clear()
	{
		password = null;
	}
}
