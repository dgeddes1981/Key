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
**  $Id: Webpage.java,v 1.1.1.1 1999/10/07 19:58:37 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.primitive;

import key.InteractiveConnection;
import key.Symbol;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.net.UnknownHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

public class Webpage implements java.io.Serializable
{
	URL page;
	
	public Webpage()
	{
		page = null;
	}

	public String toString()
	{
		if( page != null )
			return( page.toString() );
		else
			return( "not set" );
	}

	public String get()
	{
		if( page != null )
			return( page.toString() );
		else
			return( null );
	}

	public void set( String newAddress, InteractiveConnection ic )
	{
		URL newPage;

		try
		{
			newPage = new URL( newAddress );
		}
		catch( MalformedURLException e )
		{
			ic.sendError( "'" + newAddress + "' is not a valid URL: " + e.getMessage() );
			return;
		}

		/*
			//  try to do some validation to check it's accurate...
		URLConnection uc;
		
		try
		{
			uc = newPage.openConnection();
		}
		catch( IOException e )
		{
			ic.sendError( "An error occurred trying to examine this URL." );
			return;
		}

		ic.sendFeedback( "Please wait while your URL is validated... (if the time this takes is unbearable, think about reconnecting and trying later)" );
		ic.flush();
		
		try
		{
			uc.connect();
		}
		catch( IOException e )
		{
			ic.sendError( "An error occurred trying to examine this URL." );
			return;
		}
			
		String contentType = uc.getContentType();

		if( contentType != null )
		{
		*/
			page = newPage;
			ic.sendFeedback( "Your homepage has been set to the URL: " + page.toString() );
		/*
			ic.sendFeedback( "Your homepage has been set to the " + contentType + " URL: " + page.toString() );
		}
		else
		{
			ic.sendFeedback( "I couldn't determine the content type of this URL." );
		}
		*/
	}
}
