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
**  $Id: ReverseRank.java,v 1.1 1999/10/11 13:25:07 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.io.*;


/**
  * A specialised group that has 'implies' as
  * well as the ability to use a 'joined'
  * type effect for another scape.  elements()
  * and contains() are not overridden, so
  * they don't always return the _true_ values
  * that they could.
 */
public class ReverseRank extends Rank
{
	private static final long serialVersionUID = -8059397405403896887L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( ReverseRank.class, Rank.class, "reversedRank",
			AtomicElement.PUBLIC_FIELD,
			"the reversed rank" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Rank.STRUCTURE, ELEMENTS );
	public Reference reversedRank = Reference.EMPTY;
	
	public ReverseRank()
	{
		reversedRank = Reference.EMPTY;
		contained = key.collections.EmptyCollection.EMPTYCOLLECTION;
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void argument( String args )
	{
	}
	
	protected void addInternal( Reference added ) throws BadKeyException, NonUniqueKeyException
	{
	}
	
	protected void removeInternal( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
	}
	
	/**
	  *  This routine removes the atom 'removed' from this rank,
	  *  ensure that it isn't in this rank in any way, shape, or
	  *  form.  It will then re-add the player to any ranks that
	  *  this rank implies, except for rank s (which may be null).
	 */
	protected void removeInternal( Reference removed, Rank s ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
	}
	
	public boolean doesImply( Rank r )
	{
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Rank g = (Rank) e.nextElement();
			
			if( r == g )
				return( true );
			else if( g.doesImply( r ) )
				return( true );
		}

		return( false );
	}
	
	public boolean isOutRankedBy( Rank rank )
	{
		return( rank.getTargets().contains( this ) );
	}
	
	public Targets getTargets()
	{
		return( targets );
	}
	
	public Enumeration getImplications()
	{
		return( implies.elements() );
	}
	
	public Implications getImplies()
	{
		return( implies );
	}
	
	public void establish( Player p ) throws BadKeyException,NonUniqueKeyException
	{
	}

	private Rank getRank()
	{
		return( (Rank) reversedRank.get() );
	}
	
	public boolean containsPlayer( Player p )
	{
		Scape s = (Scape)reversedRank.get();
		
		if( s == null )
			return true;
		else
			return( !s.containsPlayer( p ) );
	}
	
	public boolean containsAtAll( Reference p )
	{
		Rank r = getRank();
		
			//  if there's no rank, then everyone is in it.
		if( r == null )
			return( true );
		
		if( r.shouldContain( p ) )
			return( false );
		else
			return( true );
	}
}
