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

package key;

import key.primitive.DateTime;
import key.primitive.Duration;
import key.util.SMTP;

import java.io.*;
import java.net.UnknownHostException;
import java.net.ProtocolException;
import java.util.Random;
import java.util.StringTokenizer;

public class EmailSetter extends Event
{
	public static final String VAR_ENTRY_NAME = "emailsetters";
	public final static String EMAIL_LOG = "email";
	//public static final int EMAIL_DELAY = 5 * 1000 * 60; // 5 minutes
	public static final int EMAIL_DELAY = 50;

	private static final long serialVersionUID = -2383473746954299699L;
	
	DateTime created;
	boolean resetSince;
	String validationCode;
	String address;
	Reference player;
	String ip = "";
	
	public EmailSetter()
	{
	}

	public void ensureInKey() throws NonUniqueKeyException,BadKeyException
	{
		Key.instance().ensureAndGetVarEntry( VAR_ENTRY_NAME ).addInternal( getThis() );
	}

	public String getName()
	{
		return( "EmailSetter for: " + getKey().toString() );
	}
	
	public void set( Reference p, String ad, String vc )
	{
		player = p;
		created = new DateTime();
		resetSince = true;
		
		try
		{
			Player pl = (Player) p.get();
			ip = pl.getLastConnectFrom();
		}
		catch( OutOfDateReferenceException e )
		{
			if( scheduledFor != null )
			{
					//  remove from old schedule first
				Key.instance().getScheduler().remove( this );
				scheduledFor = null;
			}
			
			return;
		}
		
		address = ad;
		validationCode = vc;
		
		if( scheduledFor != null )
		{
				//  remove from old schedule first
			Key.instance().getScheduler().remove( this );
		}
		
		scheduledFor = DateTime.nowPlus( new Duration( EMAIL_DELAY ) );  //  5 minutes
		Key.instance().getScheduler().add( this );
	}
	
	public void run( Daemon scheduler )
	{
		Player p;

		try
		{
			p = (Player) player.get();
			
			p.putCode( 'p', p.getName() );
			p.putCode( 'i', ip );
			p.putCode( 'd', created.toString() );
			p.putCode( 'v', validationCode );
			
			String cn = Key.instance().getName();
			p.putCode( 'c', cn );
			
			String ra = (String) Key.instance().getConfig().getProperty( "returnAddress" );
			p.putCode( 'r', ra );
			
			TextParagraph para = (TextParagraph) Key.instance().getConfig().getProperty( "emailForm" );
			
			if( para != null )
			{
				para = (TextParagraph) Grammar.substitute( para, p.getCodes() );
				
				try
				{
					SMTP mailer = new SMTP( (String) Key.instance().getConfig().getProperty( "emailServer" ) );
					Log.log( EMAIL_LOG, "sending to '" + address + "', for " + player.getKey() + ", code '" + validationCode + "'" );
					mailer.send( ra, address, "[automated] Verification of " + cn + " email", para.getText() );
					if( p.connected() )
						p.send( "^H%% Successfully sent verification email to " + address + " %%^-" );
				}
				catch( ProtocolException e )
				{
					Log.log( EMAIL_LOG, e.toString() + " while sending email verification for player " + player.getKey() + ", address: " + address );
					if( p.connected() )
						p.send( "^H%% Error sending verification email: " + e.getMessage() + " %%^-" );
				}
				catch( UnknownHostException e )
				{
					Log.log( EMAIL_LOG, e.toString() + " while sending email verification for player " + player.getKey() + ", address: " + address );
					if( p.connected() )
						p.send( "^H%% Error sending verification email: " + e.getMessage() + " %%^-" );
				}
				catch( IOException e )
				{
					Log.log( EMAIL_LOG, e.toString() + " while sending email verification for player " + player.getKey() + ", address: " + address );
					if( p.connected() )
						p.send( "^H%% Error sending verification email: " + e.getMessage() + " %%^-" );
				}
				finally
				{
					cleanup();
				}
			}
			else
				cleanup();
		}
		catch( Exception e )
		{
			cleanup();
			return;
		}
	}
	
	private void cleanup()
	{
		player = null;
		
		try
		{
			Key.instance().ensureAndGetVarEntry( VAR_ENTRY_NAME ).removeInternal( this.getThis() );
		}
		catch( Exception e )
		{
			System.out.println( "while removing emailsetters: " + e.toString() );
		}
	}
}
