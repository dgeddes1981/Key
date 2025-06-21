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
**  $Id: PermissionList.java,v 1.2 2000/06/23 18:51:46 subtle Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;
import key.util.EmptyEnumeration;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class PermissionList implements java.io.Serializable
{
	private static final long serialVersionUID = 6961040118922549304L;
	
	Vector entries;
	Atom parent;
	Entry defaults;
	
	public static final int DEFAULT_LIMIT = 20;
	
	private int limit;
	
	public PermissionList( Atom p )
	{
		entries = null;
		parent = p;
		defaults = new Entry( null );
		limit = DEFAULT_LIMIT;
	}
	
	private final void ensureList()
	{
		if( entries == null )
			entries = new Vector( 2, 5 );
	}
	
	public final void deallocate()
	{
		if( entries != null )
		{
			if( entries.size() == 0 )
				entries = null;
			else
				entries.trimToSize();
		}
	}
	
	public final void clear()
	{
		entries.setSize( 0 );
	}
	
	public final void setLimit( int i )
	{
		limit = i;
	}
	
	public final int getLimit()
	{
		return( limit );
	}
	
	public final boolean actionRegistered( Action a )
	{
		return( parent.containsAction( a ) );
	}
	
	public Enumeration elements()
	{
		if( entries != null )
			return( entries.elements() );
		else
			return( new EmptyEnumeration() );
	}

	public int count()
	{
		if( entries != null )
			return( entries.size() );
		else
			return( 0 );
	}
	
	public void allow( Action action )
	{
		defaults.allow( action );
	}
	
	public void deny( Action action )
	{
		defaults.deny( action );
	}
	
	public void allow( Reference ref, Action action )
	{
		if( !actionRegistered( action ) )
			throw new UnexpectedResult( "action '" + action + "' not registered for permission list belonging to " + parent.getName() );
		
		Entry ple = getEntryFor( ref );
		
		if( ple == null )
		{
			ple = new Entry( ref );
			newEntry( ple );
		}
		
		ple.allow( action );
	}
	
	public void deny( Reference ref, Action action )
	{
		if( !actionRegistered( action ) )
			throw new UnexpectedResult( "action '" + action + "' not registered for permission list belonging to " + parent.getName() );
		
		Entry ple = getEntryFor( ref );
		
		if( ple == null )
		{
			ple = new Entry( ref );
			newEntry( ple );
		}
		
		ple.deny( action );
	}
	
	public void clear( Reference ref, Action action )
	{
		if( entries != null )
		{
			if( !actionRegistered( action ) )
				throw new UnexpectedResult( "action '" + action + "' not registered for permission list belonging to " + parent.getName() );
		
			Entry ple = getEntryFor( ref );
		
			if( ple == null )
				return;
			
			if( ple.clear( action ) )
			{
				entries.removeElement( ple );  //  if that was the last thing in it,
											//  tidy it up.
				if( entries.size() == 0 )
					entries = null;
			}
		}
	}

	public void deleteEntryFor( Reference ref )
	{
		if( entries != null )
		{
			Entry ple = getEntryFor( ref );
			
			if( ple != null )
			{
				entries.removeElement( ple );
				
				if( entries.size() == 0 )
					entries = null;
			}
		}
	}
	
	public Entry getEntryFor( Reference ref )
	{
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Entry ple = (Entry) e.nextElement();
			if( ple.isFor( ref ) )
				return( ple );
		}
		
		return( null );
	}
	
	public boolean isAllowingByDefault( Action action )
	{
		return( defaults.isAllowing( action ) );
	}
	
	protected boolean check( Reference ref, Action action )
	{
		if( !actionRegistered( action ) )
			throw new UnexpectedResult( "action '" + action + "' not registered for permission list belonging to " + parent.getName() );
		
		Entry ple = getEntryFor( ref );
		
		if( ple != null )
		{
			//System.out.println( " found permission entry for tag '" + tag.getName() + "'" );
			if( ple.isAllowing( action ) )
			{
				//System.out.println( " action " + action.getName() + " is explicitly allowed" );
				return( true );
			}
			else if( ple.isDenying( action ) )
			{
				//System.out.println( this, " action " + action.getName() + " is explicitly denied" );
				return( false );
			}
		}
		//else
			//System.out.println( " no entry for tag '" + tag.getName() + "'" );
		
		//System.out.println( "Using default for action " + action.getName() + " which is " + (action.allowByDefault() ? "to allow" : "to deny" ) );
		return( defaults.isAllowing( action ) );
	}

	void violation( Action action, String s )
	{
		if( action.getSerious() )
			throw new AccessViolationException( parent, s );
		else
			throw new PermissionDeniedException( parent, s );
	}
	
	public void check( Action action )
	{
		if( permissionCheck( action, true, true ) )
			return;
		else
		{
			try
			{
				Animated s = (Animated) Thread.currentThread();
				if( !( s.is() instanceof Daemon ) )
					violation( action, s.getName() + " trying to " + action.getName() + " " + parent.getId() );
			}
			catch( ClassCastException e )
			{
				violation( action, "trying to " + action.getName() + " " + parent.getId() );
			}
		}
	}
	
	/**
	  *  Checks the permissions for the currently running
	  *  thread.  This is (possibly), a very inefficient
	  *  routine.  This is permitted because priv checks
	  *  are relatively rare things.  (Aside from the
	  *  first 'list lookup')
	 */
	public boolean remotePermissionCheck( Player a, Action action, boolean noisy, boolean checkRanks )
	{
		try
		{
			if( a != null )
			{
				if( a.getNullConnection() == parent )
				{
					//Log.debug( this, "permission check on ourselves shortcutted for action '" + action.getName() + " " + parent.getName() + "'" );
					return true;
				}
				
					//  this is an 'efficiency' hack for playerfiles
					//  it'll just shortcut if we're trying to modify
					//  ourselves
				if( a == parent )
				{
					//Log.debug( this, "permission check on ourselves shortcutted for action '" + action.getName() + " " + parent.getName() + "'" );
					return true;
				}
				
				//System.out.println( "PermissionList.check, targeter " + ((Atom)s).getName() + ", action " + action.getName() );
				
					//  META: remove this bit - there's a better search
					//  for beyond further down
				//if( a.isBeyond() )
					//return true;
				
					//  first off - if they own us, they can do
					//  whatever they want.  A bit scary, otherwise.
				if( parent.isOwner( a ) )
				{
					//System.out.println( "permission check on atom " + parent.getName() + ", owner " + a.getName() + " suceeded to " + action.getName() + " it" );
					return true;
				}
				/*
				else
				{
					System.out.println( a.getId() + " is not the owner of " + parent.getId() );
					Atom p = parent.getOwner();
					if( p != null )
						System.out.println( "  , " + p.getId() + " is." );
					else
						System.out.println( "  , it is null" );
				}
				*/
				
					//  now do the standard permissions check
				//System.out.println( a.getName() + " wants to " + action.getName() + " " + parent.getName() );
				if( check( a.getThis(), action ) )
					return true;
				else
				{
						//  check the permissions of the container its in
						//  (this is a way of saying "anything in the 'groups'
						//  container can do x")  It is *NOT* recursive at
						//  the moment.
						//
						//  as an efficiency consideration, just use
						//  ranks at the moment - that is, don't allow
						//  entries to work for any arbitrary container,
						//  just ranks and animated atoms
						//
						//  that means we use 's' instead of a, since its
						//  a player.  it also means we'll need to notice
						//  and change this when we allow people to write
						//  their own code.
						//
						//  this is more efficient, as, generally, people
						//  are only in one or two ranks.
						//
						//  now, while we're searching ranks, we might as
						//  well do the targetting stuff - that is,
						//  check that if the rank of the target allows
						//  us to target, well, all the rank's we're in,
						//  blah, then its okay, you know?
						//
						//  You might want to read the documentation
						//  (hopefully we've produced some and I've found
						//  the design I did) if you don't understand
						//  how ranks are checked.
						//
						//  here we are stepping through the ranks of the
						//  player _doing_ the action to the current
						//  permission list.
						//  
					Targetable rent = null;
					
					Atom new_rent = parent;
					
					while( new_rent != null && !(new_rent instanceof Targetable) )
					{
						new_rent = new_rent.getParent();
					} 
					
					rent = (Targetable) new_rent;
					
					/* -- META this does something, surely
					if( rent != null && ((Atom)rent).isLoading() )
						return true;
					*/
					
						//  check clan and friends permissions
					if( rent instanceof Player )
					{
						Player p = (Player) rent;
						Friends f = p.getFriends();
						Clan c = p.getClan();
						
						//System.out.println( "checking friends list: " + f.getId() + " against player " + a.getId() );
						if( f.containsPlayer( a ) && check( f.getThis(), action ) )
							return true;
						
						//System.out.println( "checking clan..." );
						
						if( c != null && c.containsPlayer( (Player) a ) && check( c.getThis(), action ) )
							return true;
					}
					
					if( checkRanks )
					{
							//  if any of the ranks that the player is in
							//  we have a list entry allowing them to do it, or
							//  has rent (us or our parent) in its target list
						for( Enumeration e = a.ranks(); e.hasMoreElements(); )
						{
							Rank rank = (Rank) e.nextElement();
							
							if( check( rank.getThis(), action ) )
							{
								InteractiveConnection ic = ((Player)a).getNullConnection();
								if( ic != null && noisy )
									ic.sendSystem( "(" + rank.getName() + " used to '" + action.getName() + " " + parent.getName() + "')" );
								return true;
							}
							
								//  this call is another loop -
								//  this is a very inefficient place to be
								//  a loop
							if( rent != null && rent.isOutRankedBy( rank ) )
							{
								InteractiveConnection ic = ((Player)a).getNullConnection();
								if( ic != null && noisy )
									ic.sendSystem( "(" + rank.getName() + " used to '" + action.getName() + " " + parent.getName() + "')" );
								return true;
							}
						}
					}
					
							//  as a last resort - see if they've got beyond
					if( a instanceof Player && ((Player)a).isBeyond() )
					{
						InteractiveConnection ic = ((Player)a).getNullConnection();
						if( ic != null && noisy )
							ic.sendSystem( "(beyond used to '" + action.getName() + " " + parent.getName() + "')" );
						
						return true;
					}
					
					return( false );
				}
			}
			else
			{
					//  META
				//System.out.println( "perm check when IC not set..." );
			}
		}
		catch( Exception e )
		{
			Log.error( e );
			return( false );
		}
		
		return( true );
	}
	
	public boolean permissionCheck( Action action, boolean noisy, boolean checkRanks )
	{
		return( remotePermissionCheck( Player.getCurrent(), action, noisy, checkRanks ) );
	}
	
	protected void newEntry( Entry ent )
	{
		ensureList();
		entries.addElement( ent );
	}
	
	public String toString()
	{
		return( toString( true ) );
	}
	
	public String toString( boolean expert )
	{
		if( count() == 0 )
		{
			return( "No list entries" );
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			sb.append( Integer.toString( count() ) + " entr" + ((count()==1) ? "y" : "ies") + " in the list:\n" );
			for( Enumeration e = elements(); e.hasMoreElements(); )
			{
				Entry ple = (Entry) e.nextElement();
				sb.append( "  " );
				sb.append( ple.toString( expert ) );
				sb.append( "\n" );
			}
		
			return( sb.toString() );
		}
	}
	
	public final Atom getParent()
	{
		return( parent );
	}
	
	public Immutable getImmutable()
	{
		return( new Immutable() );
	}
	
	public static final class Entry implements java.io.Serializable
	{
		Vector allowActions;
		Vector denyActions;
		Reference entryFor;
		
		public Entry( Reference entry )
		{
			if( entry == null )
				entryFor = null;
			else
				entryFor = entry;
			
			allowActions = new Vector( 0, 3 );
			denyActions = new Vector( 0, 3 );
		}
		
		public int allowCount()
		{
			return( allowActions.size() );
		}
		
		public int denyCount()
		{
			return( denyActions.size() );
		}
		
		public int count()
		{
			return( allowCount() + denyCount() );
		}
		
		public boolean isAllowing( Action action )
		{
			//Key.instance().debug( this, "  isAllowing called: entryFor " + entryFor.getName() + " contains " + count() + " (" + allowCount() + "/" + denyCount() + ") entries" );
			if( allowActions.contains( action ) )
				return( true );
			else
				return( false );
		}

		public boolean isDenying( Action action )
		{
			//Key.instance().debug( this, "  isDenying called: entryFor " + entryFor.getName() + " contains " + count() + " (" + allowCount() + "/" + denyCount() + ") entries" );
			if( denyActions.contains( action ) )
				return( true );
			else
				return( false );
		}
		
		public Enumeration allowing()
		{
			return( allowActions.elements() );
		}

		public Enumeration denying()
		{
			return( denyActions.elements() );
		}
		
		public boolean isFor( Reference test )
		{
			return( test.equals( entryFor ) );
		}
		
		public void allow( Action toAllow )
		{
			if( allowActions.contains( toAllow ) )
				return;
			if( denyActions.contains( toAllow ) )
				denyActions.removeElement( toAllow );
			
			allowActions.addElement( toAllow );
		}
		
		public void deny( Action toDeny )
		{
			if( denyActions.contains( toDeny ) )
				return;
			if( allowActions.contains( toDeny ) )
				allowActions.removeElement( toDeny );
			
			denyActions.addElement( toDeny );
		}

		/**
		  *  Returns true if this entry now
		  *  contains nothing.
		 */
		public boolean clear( Action toDefault )
		{
			if( allowActions.contains( toDefault ) )
				allowActions.removeElement( toDefault );
			if( denyActions.contains( toDefault ) )
				denyActions.removeElement( toDefault );
			
			if( count() == 0 )
				return true;
			
			return false;
		}
		
		public String toString()
		{
			return( toString( true ) );
		}
		
		public String toString( boolean expert )
		{
			StringBuffer sb = new StringBuffer();
			boolean empty = true;
			
			if( entryFor == null )
				sb.append( "defaults" );
			else
			{
				sb.append( entryFor.getName() );
				sb.append( " (#" );
				sb.append( entryFor.getIndex() );
				sb.append( ")" );
			}
			
			sb.append( " -" );
			
			if( allowCount() > 0 )
			{
				boolean first = true;
				
				sb.append( "  [Allow: " );
				
				for( Enumeration e = allowing(); e.hasMoreElements(); )
				{
					Action a = (Action)e.nextElement();
					boolean exp = a.getExpert();
					
					if( (exp && expert) || !exp )
					{
						if( !first )
							sb.append( ", " );
						
						first = false;
						sb.append( a.getName() );
					}
				}
				
				sb.append( "]" );
				empty = false;
			}

			if( denyCount() > 0 )
			{
				boolean first = true;
				sb.append( "  [Deny: " );
				for( Enumeration e = denying(); e.hasMoreElements(); )
				{
					Action a = (Action)e.nextElement();
					boolean exp = a.getExpert();
					
					if( (exp && expert) || !exp )
					{
						if( !first )
							sb.append( ", " );
						
						first = false;
						
						sb.append( a.getName() );
					}
				}
				sb.append( "]" );
				empty = false;
			}

				//  this shouldn't ever happen, but you never know
			if( empty )
				sb.append( "[]" );

			return( sb.toString() );
		}
	}
	
	public class Immutable
	{
		public final int getLimit()
			{ return( PermissionList.this.getLimit() ); }
		public final boolean actionRegistered( Action a )
			{ return( PermissionList.this.actionRegistered( a ) ); }
		public final int count()
			{ return( PermissionList.this.count() ); }
		public final boolean isAllowingByDefault( Action action )
			{ return( PermissionList.this.isAllowingByDefault( action ) ); }
		public final boolean check( Reference ref, Action action )
			{ return( PermissionList.this.check( ref, action ) ); }
		public final void check( Action action )
			{ PermissionList.this.check( action ); }
		public final boolean permissionCheck( Action action, boolean noisy, boolean checkRanks )
			{ return( PermissionList.this.permissionCheck( action, noisy, checkRanks ) ); }
		public final String toString( boolean expert )
			{ return( PermissionList.this.toString( expert ) ); }
		public final String toString()
			{ return( PermissionList.this.toString() ); }
		public final Atom getParent()
			{ return( PermissionList.this.getParent() ); }
	}
}
