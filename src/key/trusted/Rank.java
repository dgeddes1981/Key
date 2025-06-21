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

import key.util.FilteredEnumeration;

import java.util.Vector;
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
public class Rank extends Group implements Targetable
{
	private static final long serialVersionUID = -2727379890374629964L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Rank.class, Implications.class, "implies",
			AtomicElement.PUBLIC_FIELD,
			"the inherited rank" ),
		AtomicElement.construct( Rank.class, Targets.class, "targets",
			AtomicElement.PUBLIC_FIELD,
			"the ranks that can be targetted" ),
		AtomicElement.construct( Rank.class, Scape.class, "joinedTo",
			AtomicElement.PUBLIC_FIELD,
			"a scape to link players from this group into as they come online" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Group.STRUCTURE, ELEMENTS );
	
	public final Implications implies = (Implications) Factory.makeAtom( Implications.class, "implies" );
	public final Targets targets = (Targets) Factory.makeAtom( Targets.class, "targets" );
	
	private Vector ranksThatImplyUs = new Vector( 0, 5 );
	
	/**
	  *  This property is deliberately not
	  *  added to the group. We don't
	  *  want it to be settable, but
	  *  we can do with the reference
	  *  routines to point it at things
	  *  and storing
	 */
	Reference joinedTo = Reference.EMPTY;
	
	public Rank()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	//public void argument( String args )
	//{
		//joinedTo = Reference.to( args );
	//}

	public void setJoinedTo( Scape s )
	{
		joinedTo = s.getThis();
	}
	
	public final Enumeration reverseImplications()
	{
		return( new FilteredEnumeration( 
				ranksThatImplyUs.elements(),
				new ReferenceEnumeratorFilter(
					new ReferenceEnumeratorFilter.EnumeratedThing()
					{
						public void noSideEffectRemove( Reference r )
							throws NonUniqueKeyException, java.util.NoSuchElementException,BadKeyException
						{
							Rank.this.ranksThatImplyUs.removeElement( r );
						}
					}
				, true ) ) );
	}
	
	/**
	  *  This is a notification routine that is called when a reference
	  *  container adds this atom to it.  (A non-reference container
	  *  calls set_parent).  The default implementation does nothing,
	  *  but some atoms (such as Rank or Player) may be interested.
	 */
	public void addSymbolicParent( Container c )
	{
		super.addSymbolicParent( c );
		
		if( c instanceof Implications )
			ranksThatImplyUs.addElement( c.getThis() );
	}
	
	/**
	  *  This is a notification routine that is called when a reference
	  *  container removes this atom from it.  (A non-reference container
	  *  calls set_parent).  The default implementation does nothing,
	  *  but some atoms (such as Rank or Player) may be interested.
	 */
	public void removeSymbolicParent( Container c )
	{
		super.removeSymbolicParent( c );
		
		if( c instanceof Implications )
		{
				//  it's okay to pass in a different reference,
				//  the Vector class calls .equals to determine
				//  equality in the Sun Java implementation.
			ranksThatImplyUs.removeElement( c.getThis() );
			
			System.out.println( "  " + getId() + " rank is no longer implied by: " + c.getId() );
		}
	}
	
	protected void addInternal( Reference addedr ) throws BadKeyException, NonUniqueKeyException
	{
		if( shouldContain( addedr ) )
			throw new RedundantException( "redundant add to rank " + getName() + " for atom " + addedr.getName() + " as it is already in a rank which implies us" );
		
			//  ultimately - if you're here, we want to remove you from any
			//  groups that we imply.
		
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Rank r = (Rank) e.nextElement();
			
			r.strip( addedr );
		}
		
			//  this call (in group) will establish() the player
		super.addInternal( addedr );
		
		Player p = (Player) addedr.get();
		p.addReverseRank( this );
	}
	
	protected void removeInternal( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		removeInternal( removed, null );
	}
	
	/**
	  *  This routine removes the atom 'removed' from this rank,
	  *  ensure that it isn't in this rank in any way, shape, or
	  *  form.  It will then re-add the player to any ranks that
	  *  this rank implies, except for rank s (which may be null).
	 */
	protected void removeInternal( Reference removed, Rank s ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		if( !removed.isValid() )
		{
			noSideEffectRemove( removed );
			return;
		}
		
			//  we can skip a few cases since a Rank is always
			//  a reference container
		if( !super.containsReference( removed ) )
		{
			boolean didRemove = false;
			
				//  this whole algorithm is a little convoluted,
				//  as it skips around the hierarchy quite a bit.
				//
				//  if it works, I suggest we just leave it as is.
				
				//  remove them from the ranks which imply this
				//  one, as well. - this is like 'exile
				//  <name> member" when the person is a leader -
				//  we want to remove leader as well.
			for( Enumeration e = reverseImplications(); e.hasMoreElements(); )
			{
				Implications i = (Implications) e.nextElement();
				Rank r = (Rank) i.getParent();
				
				Log.currentPlayer( "Removing '" + removed.getName() + "' from " + r.getName() + " because it implies " + getName() + " and we're removing from that." );
				r.checkRemoveFromPermission( removed );
				r.removeInternal( removed, this );
				didRemove = true;
			}
			
			if( !didRemove )
			{
				if( s == null )
					throw new NoSuchElementException( removed.getName() + " is not in " + getName() );
				else
					return;
			}
		}
		else
			noSideEffectRemove( removed );
		
			//  remove you from the joinedTo group
			//  order is important here - its possible that
			//  one of the other groups (below) require that
			//  you're in this same scape - they'll add you
			//  back in, if this is the case, and we hardly
			//  want you there twice. ;)
		Scape jTo = (Scape) joinedTo.get();
		if( jTo != null )
			jTo.unlinkPlayer( (Player) removed.get() );
		
		if( super.containsReference( removed ) )
		{
			throw new UnexpectedResult( "didn't manage to remove " + removed.getName() + " from " + getName() );
		}
		
			//  it isn't essential that we do this here, as the
			//  Player class has enough intelligence to remove the
			//  rank itself next time it is loaded if !rank.contains( player )
		if( removed.isLoaded() )
			((Player)removed.get()).removeReverseRank( this );
		
			//  re-add the player to any groups we imply, except
			//  to the rank which called us.
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Rank r = (Rank) e.nextElement();
			
			if( r != s )
			{
				Log.currentPlayer( "Adding " + removed.getName() + " to " + r.getName() + " because its implied by " + getName() + " and we're removing from that" );
				if( !r.containsReference( removed ) )
					r.addInternal( removed );
			}
			else
				Log.currentPlayer( "NOT adding " + removed.getName() + " to " + r.getName() + " because, while its implied by " + getName() + " and we're removing from that, its the rank which called us." );
		}
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
		if( containsPlayer( p ) )
			return;
		
		super.establish( p );
		
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Group g = (Group) e.nextElement();
			
			if( !g.containsPlayer( (Player) p ) )
			{
				g.establish( p );
			}
		}
		
			//  add you to the joinedTo group
			//  order is important here - its possible that
			//  one of the other groups (above) removed you
			//  from this scape - if we checked first, you
			//  wouldn't be in it now ;)
			//
			//  (the way I figure, anyway ;p)
		Scape jTo = (Scape) joinedTo.get();
		if( jTo != null )
		{
			if( !jTo.containsPlayer( p ) )
			{
				jTo.linkPlayer( p );
			}
		}
	}
	
	private void strip( Reference stripped )
	{
		try
		{
				//  used to be noSideEffectRemove, but it isn't
				//  removing reverseRanks
			super.noSideEffectRemove( stripped );
		}
		catch( BadKeyException e )
		{
		}
		catch( NonUniqueKeyException e )
		{
		}
		
			//  it isn't essential that we do this here, as the
			//  Player class has enough intelligence to remove the
			//  rank itself next time it is loaded if !rank.contains( player )
		if( stripped.isLoaded() )
			((Player)stripped.get()).removeReverseRank( this );
		
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Rank r = (Rank) e.nextElement();
			
			r.strip( stripped );
		}
	}
	
	public boolean containsAtAll( Reference p )
	{
		if( containsReference( p ) )
			return( true );
		
		for( Enumeration e = getImplications(); e.hasMoreElements(); )
		{
			Rank g = (Rank) e.nextElement();
			
			if( g.containsAtAll( p ) )
				return true;
		}
		
		return false;
	}

	public boolean shouldContain( Reference p )
	{
		for( Enumeration e = reverseImplications(); e.hasMoreElements(); )
		{
			Implications i = (Implications) e.nextElement();
			
			if( i != null )
			{
				if( i.containsReference( p ) )
					return( true );
			}
		}
		
		return( false );
	}
}
