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
**  $Id: Scape.java,v 1.4 1999/10/18 13:00:08 pdm Exp $
**
**  $Log: Scape.java,v $
**  Revision 1.4  1999/10/18 13:00:08  pdm
**  moved object splashes to rooms only
**
**  Revision 1.3  1999/10/14 18:58:25  pdm
**  changed history to $log$
**
**
*/

package key;

import key.collections.*;

import java.util.Enumeration;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  Scape is a general way of saying 'players exist here for
  *  and communication can be sent to them all in a uniform
  *  way.  The basis for all channels & rooms.
  *
  *  Shouldn't be necessary to store players in the linkedTrie
  *  as Tags, as players can't be swapped out while they're online.
  *
  *  *right*?
 */
public class Scape extends Container implements CommandContainer, PlayerGroup
{
	private static final long serialVersionUID = -2480334989348845021L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Scape.class, Integer.TYPE,
		    "numberPlayers",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the number of online players in this scape" ),
		AtomicElement.construct( Scape.class, String.class,
		    "allNames",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"all the names of the players in this scape" ),
		AtomicElement.construct( Scape.class, CommandList.class,
			"commands",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the scapes custom command list" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	transient PlayerGroup playergroup;
	
	/**
	  *  Commands that are specific for this particular
	  *  atom.  Examples might be custom commands in
	  *  rooms, such as 'look' (if its overridden to
	  *  give different messages each time you type
	  *  it)
	 */
	public Reference commands = Reference.EMPTY;
	
	public Scape()
	{
		this( false );
	}
	
	/**
	  *  For the rare case of a reference scape - 
	  *  key baseclass only, atm
	 */
	Scape( boolean reference )
	{
		super( reference );
		init();
	}

	private void init()
	{
		totalScapes++;
		
		playergroup = new LWPlayerGroup();
		setupPlayersCollection();
	}
	
	public void setupPlayersCollection()
	{
		playergroup.setCollection( new NoKeyCollection() );
	}

	public void setCollection( Collection c )
	{
		playergroup.setCollection( c );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void loaded()
	{
		super.loaded();
		init();
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
		try
		{
			playergroup.linkPlayer( p );
		}
		catch( NonUniqueKeyException e )
		{
			//  for a scape, this is non-sensical, really.  Just ignore it?
		}
		p.linkToScape( this );
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
		playergroup.unlinkPlayer( p );
		p.unlinkFromScape( this );
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
		return( playergroup.getPlayer( p, match ) );
	}
	
	public final Object getPlayer( String match )
	{
		return( playergroup.getPlayer( match ) );
	}
	
	public final Enumeration players()
	{
		return( playergroup.players() );
	}
	
	public boolean containsPlayer( Player p )
	{
		return( playergroup.containsPlayer( p ) );
	}
	
	protected void notifyUponClearTransient( Player p )
	{
		try
		{
			p.sendSystem( "You discern that " + getName() + " no longer exists." );
		}
		catch( PlayerNotConnectedException ex )
		{
		}
	}
	
	public void clearTransient()
	{
		for( Enumeration e = players(); e.hasMoreElements(); )
		{
			Player p = (Player) e.nextElement();
			
			notifyUponClearTransient( p );
			
			try
			{
				unlinkPlayer( p );
			}
			catch( NonUniqueKeyException t )
			{
				throw new UnexpectedResult( t.toString() );
			}
			catch( BadKeyException t )
			{
				throw new UnexpectedResult( t.toString() );
			}
		}
		
		contained.deallocate();
		
		super.clearTransient();
	}
	
	public final String allNames()
	{
		return( playergroup.allNames() );
	}
	
	/**
	  *  All the names, except substitute 'you' for player p
	 */
	public final String allNames( Player p )
	{
		return( playergroup.allNames( p ) );
	}
	
	public final int numberPlayers()
	{
		return( playergroup.numberPlayers() );
	}
	
	public final int getNumberPlayers()
	{
		return( playergroup.getNumberPlayers() );
	}
	
	public String getAllNames() { return( playergroup.allNames() ); }
	
	/**
	  *  A general way of saying that the people in
	  *  this scape should be notified of the
	  *  supplied Effect.
	 */
	public void splash( Effect t, SuppressionList s )
	{
		super.splash( t, s );
		
		Atom oldsplasher = t.getSplasher();
		t.setSplasher( this );
		playergroup.splash( t, s ); //splash all Players in the scape
		t.setSplasher( oldsplasher );
	}
	
	/**
	  *  Sends the splash to everyone except the listed
	  *  atom
	 */
	public void splashExcept( Effect t, Splashable except, SuppressionList sl )
	{
		super.splash( t, sl );
		
		Atom oldsplasher = t.getSplasher();
		t.setSplasher( this );
		
		playergroup.splashExcept( t, except, sl ); //splash all Players in the scape
		
		t.setSplasher( oldsplasher );
	}
	
	/**
	  *  Sends the splash to everyone except the listed
	  *  atoms
	 */
	public void splashExcept( Effect t, Splashable[] except, SuppressionList sl )
	{
		super.splash( t, sl );

		Atom oldsplasher = t.getSplasher();
		t.setSplasher( this );
		
		playergroup.splashExcept( t, except, sl ); //splash all Players in the scape
		
		for( Enumeration e = elements(); e.hasMoreElements(); ) //splash all Things in the scape. (Noble 09Oct99)
		{
			Atom a = (Atom) e.nextElement();
			
			if( a instanceof Thing ) {
				
				boolean match;
				
				match=true;
				
				for( int i = 0; i < except.length; i++ )
				{
					if( a == except[i] )
					{
						match = false;
						break;
					}
				}
				
				if( match )
					a.splash( t, sl );			
			}
		}		
		
		t.setSplasher( oldsplasher );
	}
	
	/**
	  *  A shortcut, since matching it from the
	  *  string could be prohibitively slow
	 */
	public final CommandList getCommandList()
	{
		try
		{
			return( (CommandList) commands.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			Log.debug( this, e.toString() + " in scape " + getName() + ":getCommandList()" );
			commands = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( getId() + ".commands", e );
			commands = Reference.EMPTY;
			return( null );
		}
	}
	
	private static int totalScapes;
	
	public static int getTotalScapes()
	{
		return( totalScapes );
	}
	
	protected void finalize() throws Throwable
	{
		super.finalize();
		
		totalScapes--;
	}
	
	boolean canSwap()
	{
		return( numberPlayers() == 0 );
	}
}
