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

import java.lang.SecurityManager;
import java.io.FileDescriptor;

/**
  * Implementation of a security manager for Key
 */
public final class Security extends java.lang.SecurityManager //extends java.rmi.RMISecurityManager
{
	public static final Security instance = new Security();
	
	private Security()
	{
	}
	
	public void checkAccept( String host, int port )
	{
		System.out.println( "checkAccept( " + host + ", " + port + " )" );
//		super.checkAccept( host, port );
	}

	public void checkPropertiesAccess()
	{
		System.out.println( "checkPropertiesAccess()" );
//		super.checkPropertiesAccess();
	}

	public void checkPropertyAccess( String key )
	{
		System.out.println( "checkPropertyAccess( " + key + " )" );
//		super.checkPropertyAccess( key );
	}

	public void checkPropertyAccess( String key, String def )
	{
		System.out.println( "checkPropertyAccess( " + key + ", " + def + " )" );
//		super.checkPropertyAccess( key, def );
	}

	public boolean checkTopLevelWindow( Object window )
	{
		System.out.println( "checkTopLevelWindow( " + window.toString() + " )" );
		return( true );
//		return( super.checkTopLevelWindow( window ) );
	}

	public void checkPackageAccess( String pkg )
	{
		System.out.println( "checkPackageAccess( " + pkg + " )" );
//		super.checkPackageAccess( pkg );
	}

	public void checkPackageDefinition( String pkg )
	{
		System.out.println( "checkPackageDefinition( " + pkg + " )" );
//		super.checkPackageDefinition( pkg );
	}
	
	public void checkSetFactory()
	{
		System.out.println( "checkSetFactory()" );
//		super.checkSetFactory();
	}

	public void checkListen( int port )
	{
		System.out.println( "checkListen( " + port + " )" );
//		super.checkListen( port );
	}

	public void checkConnect( String host, int port, Object context )
	{
		System.out.println( "checkConnect( " + host + ", " + port + ", " + context.toString() + " )" );
//		super.checkConnect( host, port, context );
	}

	public void checkConnect( String host, int port )
	{
		System.out.println( "checkConnect( " + host + ", " + port + " )" );
//		super.checkConnect( host, port );
	}
	
	public void checkDelete( String file )
	{
		System.out.println( "checkDelete( " + file + " )" );
//		super.checkDelete( file );
	}

	public void checkWrite( String file )
	{
		System.out.println( "checkWrite( " + file + " )" );
//		super.checkWrite( file );
	}

	public void checkWrite( FileDescriptor fd )
	{
		System.out.println( "checkWrite( " + fd.toString() + " )" );
//		super.checkWrite( fd );
	}

	public void checkRead( String file )
	{
		System.out.println( "checkRead( " + file + " )" );
//		super.checkRead( file );
	}

	public void checkRead( String file, Object context )
	{
		System.out.println( "checkRead( " + file + ", " + context.toString() + " )" );
//		super.checkRead( file, context );
	}
	
	public void checkRead( FileDescriptor fd )
	{
		System.out.println( "checkRead( " + fd.toString() + " )" );
//		super.checkRead( fd );
	}

	public void checkLink( String lib )
	{
		System.out.println( "checkLink( " + lib + " )" );
//		super.checkLink( lib );
	}

	public void checkExec( String cmd )
	{
		System.out.println( "checkExec( " + cmd + " )" );
//		super.checkExec( cmd );
	}

	public void checkExit( int status )
	{
		System.out.println( "checkExit( " + status + " )" );
//		super.checkExit( status );
	}

	public void checkAccess( ThreadGroup g )
	{
		System.out.println( "checkAccess( " + g.toString() + " )" );
//		super.checkAccess( g );
	}
	
	public void checkAccess( Thread g )
	{
		System.out.println( "checkAccess( " + g.toString() + " )" );
//		super.checkAccess( g );
	}

	public void checkCreateClassLoader()
	{
		System.out.println( "checkCreateClassLoader()" );
//		super.checkCreateClassLoader();
	}

	public void checkMemberAccess(Class clazz, int which)
	{
		System.out.println( "checkMemberAccess( " + clazz.getName() + ", " + which + " )" );
//		super.checkMemberAccess( clazz, which );
	}
	
	/**
	  *  Checks the permissions for the currently running
	  *  thread.  This is (possibly), a very inefficient
	  *  routine.  This is permitted because priv checks
	  *  are relatively rare things.  (Aside from the
	  *  first 'list lookup')
	  * META: still to be placed here
	public boolean permissionCheck( Action action, boolean noisy, boolean checkRanks )
	{
		try
		{
			Thread t = Thread.currentThread();
			
			if( t instanceof Animated )
			{
				Animate s = ((Animated)t).is();
				
					//  this is an 'efficiency' hack for playerfiles
					//  it'll just shortcut if we're trying to modify
					//  ourselves
				if( s == parent )
				{
					//Log.debug( this, "permission check on ourselves shortcutted for action '" + action.getName() + " " + parent.getName() + "'" );
					return true;
				}
				
					//  this can be changed back to Atom when we
					//  allow people to write their own code.
				if( s instanceof InteractiveConnection )
				{
					Player a = ((InteractiveConnection)s).getPlayer();
					
					if( a != null )
					{
						//System.out.println( "PermissionList.check, targeter " + ((Atom)s).getName() + ", action " + action.getName() );
						
							//  META: remove this bit - there's a better search
							//  for beyond further down
						if( a.isBeyond() )
							return true;
						
							//  first off - if they own us, they can do
							//  whatever they want.  A bit scary, otherwise.
						if( parent.isOwner( a ) )
						{
							//System.out.println( "permission check on atom " + parent.getName() + ", owner " + a.getName() + " suceeded to " + action.getName() + " it" );
							return true;
						}
						
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
							
							// -- META this does something, surely
							//if( rent != null && ((Atom)rent).isLoading() )
							//	return true;
							//
							
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
										InteractiveConnection ic = ((Player)a).getConnection();
										if( ic != null && noisy )
											ic.sendSystem( "(" + rank.getName() + " used to '" + action.getName() + " " + parent.getName() + "')" );
										return true;
									}
									
										//  this call is another loop -
										//  this is a very inefficient place to be
										//  a loop
									if( rent != null && rent.isOutRankedBy( rank ) )
									{
										InteractiveConnection ic = ((Player)a).getConnection();
										if( ic != null && noisy )
											ic.sendSystem( "(" + rank.getName() + " used to '" + action.getName() + " " + parent.getName() + "')" );
										return true;
									}
								}
								
							}
							
								//  as a last resort - see if they've got beyond
							if( a instanceof Player && ((Player)a).isBeyond() )
							{
								InteractiveConnection ic = ((Player)a).getConnection();
								if( ic != null && noisy )
									ic.sendSystem( "(beyond used to '" + action.getName() + " " + parent.getName() + "')" );
								
								return true;
							}
							
							return( false );
						}
					}
				}
			}
		}
		catch( Exception e )
		{
			Log.debug( this, e.toString() );
			return( false );
		}
		
		return( true );
	}
	 */
}
