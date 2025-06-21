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
**  $Id: Group.java,v 1.2 2000/06/19 20:40:49 subtle Exp $
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
import java.io.IOException;
import java.io.ObjectInputStream;

/**
  *  A group is a specialised version of scape in that
  *  it has both online (currently linked in) members,
  *  and offline (not-currently linked in) members.
 */
public class Group extends Scape
{
	private static final long serialVersionUID = -2114633670388062310L;
	
	public Group()
	{
		super( true );
		setConstraint( Type.PLAYER );
	}
	
	protected void constructed()
	{
		super.constructed();
		init();
	}
	
	public void loaded()
	{
		super.loaded();
		init();
	}
	
		//  needs to be called when loaded & when created
	private void init()
	{
			//  groups need to be notified when someone logs in
		try
		{
			LoginNotification.register( this );
		}
		catch( Exception e )
		{
			Log.error( "during Group::init()", e );
		}
		
		rescan();
	}
	
	public void rescan()
	{
			//  we need to scan all online players to see
			//  if we should link anyone into this group
		for( Enumeration e = Key.instance().players(); e.hasMoreElements(); )
		{
			Player p = (Player) e.nextElement();
			
			if( contains( p ) )
			{
				try
				{
					if( !containsPlayer( p ) )
						establish( p );
				}
				catch( Exception ex )
				{
				}
			}
		}
	}
	
	public void clearTransient()
	{
		try
		{
			LoginNotification.unregister( this );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		super.clearTransient();
	}
	
	protected void addInternal( Reference added ) throws BadKeyException, NonUniqueKeyException
	{
		super.addInternal( added );
		
		//Log.debug( this, "add on Group " + getName() + " called, with added a " + Type.typeOf( added ).getName() );
		
		if( added.isLoaded() )
		{
			Atom a = (Atom) added.get();

			if( a instanceof Player && ((Player)a).connected() )
				establish( (Player) a );
		}
	}
	
	public void argument( String args ) throws IllegalArgumentException
	{
		throw new IllegalArgumentException( "no arguments to atom '" + getName() + "'" );
	}
	
	protected void noSideEffectRemove( Reference removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		super.noSideEffectRemove( removed );
		
			//  TODO:  deestablish scape link
		if( removed.isLoaded() )
		{
			Atom r = removed.get();
			
			if( r instanceof Player && ((Player)r).connected() )
			{
				//System.out.println( "(normal) removed online player from group " + getId() );
				unlinkPlayer( (Player) r );
			}
		}
	}
	
	public void splash( Effect t, SuppressionList s )
	{
		//if( t instanceof key.effect.Connection )
		//{
			if( t instanceof key.effect.Login )
			{
				Player p = (Player) t.getOriginator();
				
				if( contains( p ) )
				{
					try
					{
						establish( p );
					}
					catch( Exception e )
					{
						Log.error( e );
					}
				}
				
				if( !propagateLoginEffect() )
					return;
			}
		//}
		
		super.splash( t, s );
	}
	
	/**
	  *  If this scape should pass login effects through to
	  *  players, this function should return true.  In general,
	  *  this should only occur
	 */
	protected boolean propagateLoginEffect()
	{
		return( false );
	}
	
	/**
	  *  Given that the player is now linked into this Group,
	  *  he's online, so we're going to link him in
	 */
	public void establish( Player p ) throws BadKeyException,NonUniqueKeyException
	{
		linkPlayer( p );
		//System.out.println( "linking player " + p.getName() + " into group " + getId() );
	}
}
