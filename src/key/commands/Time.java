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
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.IOException;

public class Time extends Command
{
	public Time()
	{
		setKey( "time" );
		usage = "";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		boolean timeOnly = false;
		
		if( args.hasMoreTokens() )
		{
			String nt = args.nextToken();
			
			if( nt.equals( "-" ) )
				timeOnly = true;
		}
		
		Key key = Key.instance();
		
		StringBuffer sb = new StringBuffer();
		
		java.util.Date date = new java.util.Date();
		
		sb.append( date.toString() );
		
		Duration timezone;
		try
		{
			timezone = (Duration) p.getProperty( "timezone" );
		}
		catch( NoSuchPropertyException n )
		{
			ic.sendFeedback( "Your player does not contain a timezone attribute\n. Contact a director as soon as possible" );
			return;
		}
		
		if ( timezone.getTime() != 0 )
		{
			DateTime d = new DateTime( date.getTime(), timezone );
			sb.append( "\n<Your local time: " );
			sb.append( d.toString() );
			sb.append( ">" );
		}

		if( timeOnly )
		{
			ic.sendFeedback( sb.toString() );
			return;
		}
		else
			sb.append( '\n' );
		
		sb.append( '\n' );
		sb.append( key.getName() );
		sb.append( " has been running for " );
		sb.append( key.bootStats.getTimeSinceConnection().toTruncString() );
		
		int po = key.numberPlayers();
		
		sb.append( "\nThere " );
		sb.append( Grammar.isAre(po) );
		sb.append( ' ' );
		sb.append( po );
		sb.append( ' ' );
		sb.append( Grammar.personPeople(po) );
		sb.append( " online" );
		
		Enumeration e = key.players();
		Player topSpod = null;
		while( e.hasMoreElements() )
			topSpod = (Player) e.nextElement();
		
		if( topSpod != null )
		{
			Duration loginTime = topSpod.loginStats.getTimeSinceConnection();
			sb.append( '\n' );
			sb.append( topSpod.getName() );
			sb.append( " is top spod, with " );
			sb.append( Grammar.aAn( loginTime.getDescriptive() ) );
			sb.append( ' ' );
			sb.append( loginTime.toLimitedString( 2 ) );
			sb.append( " spod time" );
		}
		
		ic.send( new TextParagraph( TextParagraph.CENTERED, sb.toString() ) );
	}
}
