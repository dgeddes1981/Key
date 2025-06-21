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

import key.collections.*;

import java.util.Enumeration;
import java.io.*;
import java.util.StringTokenizer;

import key.util.Trie;

/**
  *  LightWeight Player Group - a collection of players
 */
public class LWPlayerGroup implements PlayerGroup
{
	transient Collection players;
	
	public LWPlayerGroup()
	{
	}
	
	public void setCollection( Collection c )
	{
		players = c;
	}
	
	/**
	  * Add this player to the list of players
	  *
	  * @param p the player to add to the list
	  * @exception NonUniqueKeyException if there is already a player in with this name
	  * @exception BadKeyException if this players name is malformed somehow
	 */
	public void linkPlayer( Player p ) throws NonUniqueKeyException,BadKeyException
	{
		if( !players.contains( p ) )
		{
			players.link( p );
		}
		else
		{
			Log.debug( this, "adding player " + p.getName() + " to scape when he's already in it" );
		}
	}
	
	/**
	  * Take this player out of the list of players
	  *
	  * @param p the player to remove from the list
	  * @exception NoSuchElementException if the player is not in the list
	  * @exception BadKeyException if the players name is malformed somehow
	 */
	public void unlinkPlayer( Player p ) throws NonUniqueKeyException,java.util.NoSuchElementException,BadKeyException
	{
		players.unlink( p );
	}
	
	/**
	  * Returns the player matched, or, possibly, an instance of
	  * a Trie object that contains all the matching players.
	  * <p>
	  * A null is returned if no matches were found at all.  The
	  * match string is searched until the end of the string or
	  * a non-alphabetical character is found.
	  * <p>
	  * If you specify a comma seperated list of players, a scape
	  * containing the players will be returned.  Such a scape can
	  * also contain errors when individual multiple matches are
	  * caught
	  *
	  * @param match the start or whole string to match from
	  * @return A player object, referring to the sole match, or a Trie
	 */
	public final Object getPlayer( Player p, String match )
	{
		//System.out.println( "players is " + players.getClass().getName() + " on " + getClass().getName() );
		StringTokenizer st = new StringTokenizer( match, ",", false );
		return( getPlayer( p, st ) );
	}
	
	public final Object getPlayer( String match )
	{
		return( players.get( match ) );
	}
	
	protected final Object getPlayer( Player p, StringTokenizer st )
	{
		Object single;
		
		if( st.hasMoreTokens() )
		{
			single = players.get( st.nextToken() );
			
			if( st.hasMoreTokens() )
			{	//  more than one player
				try
				{
					LWPlayerGroup group = new LWPlayerGroup();
					
						//  the use of this makes the reference persistant -
						//  it can survive the deletion and login/logout
						//  of players fairly easily.
					group.players = new ReferencingProxyCollection( new NoKeyCollection() );
					
					do
					{
						if( single instanceof Player )
						{
							group.linkPlayer( (Player)single );
						}
						else if( single instanceof Trie )
						{
								//  look for a prefer
							for( Enumeration e = ((Trie)single).elements(); e.hasMoreElements(); )
							{
								Player o = (Player) e.nextElement();
								
								if( p.getPrefer().containsPlayer( o ) )
									group.linkPlayer( o );
							}
						}
						
						single = players.get( st.nextToken() );
					} while( st.hasMoreTokens() );
					
					if( single instanceof Player )
					{
						group.linkPlayer( (Player)single );
					}
					else
						;  // in the future, link to a seperate linked
						   // linked list explicitly for multiple matches
					
					return( group );
				}
				catch( BadKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " occurred when creating temporary sub-group" );
				}
				catch( NonUniqueKeyException e )
				{
					throw new UnexpectedResult( e.toString() + " occurred when creating temporary sub-group" );
				}
			}
			else
			{
				return( single );
			}
		}
		else
			return( null );
	}
	
	public final Enumeration players()
	{
		return( players.elements() );
	}
	
	public final boolean containsPlayer( Player p )
	{
		return( players.contains( p ) );
	}
	
	public final String allNames()
	{
		String all[] = new String[numberPlayers()];
		
		int upto=0;
		for( Enumeration e = players(); e.hasMoreElements(); )
			all[upto++] = (String) ((Player)e.nextElement()).getName();

		return( Grammar.enumerate( all ) );
	}
	
	/**
	  *  All the names, except substitute 'you' for player p
	 */
	public final String allNames( Player p )
	{
		String all[] = new String[numberPlayers()];
		
		int upto=0;
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			Player t = (Player) e.nextElement();
			if( t == p )
				all[upto++] = "you";
			else
				all[upto++] = (String) t.getName();
		}
		
		return( Grammar.enumerate( all ) );
	}
	
	//  META: this one is deprecated
	public final int numberPlayers()
	{
		return( players.count() );
	}
	
	public final int getNumberPlayers()
	{
		return( players.count() );
	}
	
	//  this method replaced by allNames
	public String getAllNames() { return( allNames() ); }
	
	/**
	  *  A general way of saying that the people in
	  *  this scape should be notified of the
	  *  supplied Effect.
	 */
	public void splash( Effect t, SuppressionList s )
	{
		for( Enumeration e = players(); e.hasMoreElements(); )
			splashToPlayer( ((Player)e.nextElement()), t, s );
	}
	
	protected final void splashToPlayer( Player p, Effect t, SuppressionList sl )
	{
		try
		{
			p.splash( t, sl );
		}
		catch( Exception e )
		{
			Log.debug( this, e.toString() + ": during splashToPlayer( " + p.getName() + " ) [player unlinked]" );
			e.printStackTrace();
			try
			{
				players.unlink( p );
			}
			catch( Exception x )
			{
				Log.error( " during unlink after broken splashToPlayer", x );
			}
		}
	}
	
	/**
	  *  Sends the splash to everyone except the listed
	  *  atom
	 */
	public void splashExcept( Effect t, Splashable except, SuppressionList sl )
	{
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			Player s = (Player)e.nextElement();
			if( except instanceof Scape )
			{
				if( !((Scape)except).containsPlayer( s ) )
					splashToPlayer( s, t, sl );
			}
			else
			{
				if( s != except )
					splashToPlayer( s, t, sl );
			}
		}
	}
	
	/**
	  *  Sends the splash to everyone except the listed
	  *  atoms
	 */
	public void splashExcept( Effect t, Splashable[] except, SuppressionList sl )
	{
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			Atom s = (Atom)e.nextElement();
			boolean match;
			
			match=true;
			
			for( int i = 0; i < except.length; i++ )
			{
				if( s == except[i] )
				{
					match = false;
					break;
				}
			}
			
			if( match )
				s.splash( t, sl );
		}
	}
}
