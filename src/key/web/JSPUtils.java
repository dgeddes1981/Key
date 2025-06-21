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

package key.web;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.PrintWriter;

/**
  *  This is all a rush/hack job.  You're lucky you have it
  *  at all, don't complain. ;p~
  *
  *  This is all (paragraph system) due a rethink to take
  *  aligned images into account, but we can probably get away
  *  with an image colour code for now.
 */
public final class JSPUtils
{
	public static final String CRLF = "\r\n";
	
	public static Player getPlayer( PrintWriter out, String playerName )
	{
		if( playerName == null || playerName.length() <= 0 )
		{
			out.print( "<B>The 'player' parameter must be specified in order to view a profile.</B><P>\n" );
			return( null );
		}
		
		Player p = (Player) Key.instance().getResidents().getExactElement( playerName );
		if( p == null )
		{
			out.print( "<B>Cannot find player '" + playerName + "'</B><P>\n" );
			return( null );
		}
		
		return( p );
	}
	
	public static Player getPlayer( String playerName )
	{
		if( playerName == null || playerName.length() <= 0 )
		{
			return( null );
		}
		
		Player p = (Player) Key.instance().getResidents().getExactElement( playerName );
		if( p == null )
		{
			return( null );
		}
		
		return( p );		
	}
	
	public static int authenticateName( String name, String password )
	{
		if( name == null || password == null )
			return( -3 );
		
		Player p = (Player) Key.instance().getResidents().getExactElement( name );
		if( p != null )
		{
			if( p.authenticate( password ) )
			{
				return 1;
			}
			else  //  incorrect password
				return -1;
		}
		else  //  player not found
			return -2;
	}
	
	public static Clan getClan( PrintWriter out, String clanName )
	{
		if( clanName == null || clanName.length() <= 0 )
		{
			out.print( "<B>The 'clan' parameter must be specified in order to view a profile.</B><P>\n" );
			return( null );
		}
		
		Clan c = (Clan) Key.instance().getClans().getExactElement( clanName );
		if( c == null )
		{
			out.print( "<B>Cannot find clan '" + clanName + "'</B><P>\n" );
			return( null );
		}
		
		return( c );
	}
	
	public static Clan getClan( String clanName )
	{
		if( clanName == null || clanName.length() <= 0 )
		{
			return( null );
		}
		
		Clan c = (Clan) Key.instance().getClans().getExactElement( clanName );
		if( c == null )
		{
			return( null );
		}
		
		return( c );		
	}	
}
