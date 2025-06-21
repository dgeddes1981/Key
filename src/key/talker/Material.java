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

import key.talker.objects.WearableMessage;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;

import java.lang.reflect.*;

/**
  *  Material
  *
  *  The base class for most user-manipulatable objects
 */
public abstract class Material extends Atom implements Thing
{
	private static final long serialVersionUID = -3366054986544050407L;
	
	public Material()
	{
			//  objects typically start out
			//  as publically moveable.
		getPermissionList().allow( Atom.moveAction );
	}
	
	public Reference build( Player p )
	{
		return( getThis() );
	}
	
	public String[] wearLocations( Player p )
	{
		return( new String[0] );
	}
	
	public boolean wieldInsteadOfWear()
	{
		return( false );
	}
	
	public boolean isAvailableTo( Player p, Container r )
	{
		return Material.defaultAvailability( this, p, r );
	}
	
	public static final boolean defaultAvailability( Atom thingDef, Player p, Container r )
	{
			//  this shortcut mainly exists for MaterialContainers - if
			//  there's no sub-item, we want the default behaviour to be
			//  to accept the value of the parent - it's an &&, so return true
		if( thingDef == null )
			return true;
		
		Reference thingOwner = thingDef.getOwnerReference();
		
			//  if it's null, this is a system-level object, available to all
		if( thingOwner == null || !thingOwner.isValid() )
		{
			return true;
		}
		
		if( p != null && p.getThis().equals( thingOwner ) )
		{
			if( r != null )
			{
				if( r.getOwnerReference().equals( thingOwner ) )
				{
					return true;
				}
				else
				{
					return false;
				}
			}
				//  this is a room independent query (for this player inventory, fi)
			else
			{
				return true;
			}
		}
		else
		{
				//  if this room is owned by the owner
			if( r != null && r.getOwnerReference().equals( thingOwner ) )
			{
				return true;
			}
		}
		
		return false;
	}
	
//---  default implementations  --------------------------------
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot use " + getFullPortrait( p ) + "." );
	}
	
	public void wear( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
			//  the default implementation is to allow the player to
			//  wear it
		if( item.wieldInsteadOfWear() )
			ic.send( "You cannot wield " + item.getFullPortrait( p ) + "." );
		else
			defaultWear( p, args, ic, flags, item );
	}
	
	protected final void defaultWear( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		String[] wl = wearLocations( p );
		
		if( wl == null || wl.length == 0 )
		{
			ic.send( "You may not wear " + getFullPortrait( p ) + "." );
			return;
		}
		
		Inventory i = p.getInventory();
		
			//  check all locations are free
		for( int j = 0; j < wl.length; j++ )
		{
			Thing o = (Thing) i.getProperty( wl[j] );
			
			if( o != null )
			{
				ic.sendFailure( "You are already wearing " + o.getFullPortrait( p ) + " on your " + wl[j] + "." );
				return;
			}
		}
		
			//  place it in all locations
		for( int j = 0; j < wl.length; j++ )
		{
			i.setProperty( wl[j], this );
		}
		
		ic.sendFeedback( "You are now wearing " + getFullPortrait( p ) + "." );
	}

	protected final void defaultWield( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		String[] wl = wearLocations( p );
		
		if( wl == null || wl.length == 0 )
		{
			ic.send( "You may not wield " + getFullPortrait( p ) + "." );
			return;
		}
		
		Inventory i = p.getInventory();
		
			//  check all locations are free
		for( int j = 0; j < wl.length; j++ )
		{
			Thing o = (Thing) i.getProperty( wl[j] );
			
			if( o != null )
			{
				ic.sendFailure( "You are already wielding " + o.getFullPortrait( p ) + " in your " + wl[j] + "." );
				return;
			}
		}
		
			//  place it in all locations
		for( int j = 0; j < wl.length; j++ )
		{
			i.setProperty( wl[j], this );
		}
		
		ic.sendFeedback( "You are now wielding " + getFullPortrait( p ) + "." );
	}
	
	protected final static void defaultRemove( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		String[] wl = item.wearLocations( p );
		
		if( wl == null || wl.length == 0 )
		{
			ic.send( "You may not wear " + item.getFullPortrait( p ) + ".  (Contact a mage)" );
			return;
		}
		
		Inventory i = p.getInventory();
		boolean didRem = false;
		
			//  check all locations are free
		for( int j = 0; j < wl.length; j++ )
		{
			Thing o = (Thing) i.getProperty( wl[j] );
			
			if( o == item )
			{
				i.setProperty( wl[j], null );
				didRem = true;
			}
		}
		
		if( didRem )
		{
			if( item.wieldInsteadOfWear() )
				ic.sendFeedback( "You are no longer wielding " + item.getFullPortrait( p ) + "." );
			else
				ic.sendFeedback( "You are no longer wearing " + item.getFullPortrait( p ) + "." );
		}
		else
		{
			if( item.wieldInsteadOfWear() )
				ic.sendFeedback( "But you aren't wielding " + item.getFullPortrait( p ) + "." );
			else
				ic.sendFeedback( "But you aren't wearing " + item.getFullPortrait( p ) + "." );
		}
	}
	
	public void remove( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		defaultRemove( p, args, ic, flags, item );
	}
	
	public void wield( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( item.wieldInsteadOfWear() )
			defaultWield( p, args, ic, flags, item );
		else
			ic.send( "You cannot wield " + item.getFullPortrait( p ) + "." );
	}
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot read " + item.getFullPortrait( p ) + "." );
	}
	
	public void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You don't see anything of note" );
	}
	
	/**
	  *  It is not real friendly to override this method in order
	  *  to hide the inspect information - it's very useful stuff.
	 */
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		//ic.send( "You do not descry anything of note about " + getFullPortrait( p ) + "." );
		defaultInspect( p, args, ic, flags, item );
		ic.sendLine();
	}
	
	public final static void anyAtomInspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Atom th )
	{
		Reference owner = th.getOwnerReference();
		StringBuffer sb = new StringBuffer( th.getId() );
		Type type = Type.typeOf( th );
		
		sb.append( " is" );
		
		if( type != null )
		{
			sb.append( ' ' );
			sb.append( Grammar.aAn( type.getName() ) );
		}
		
		if( owner.isValid() )
		{
			sb.append( " owned by " );
			sb.append( owner.getName() );
		}
		
		sb.append( '.' );
		
		ic.send( sb.toString() );
	}
	
	public final static void standardInspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing th )
	{
		ic.sendLine();
		anyAtomInspect( p, args, ic, flags, (Atom) th );
		ic.sendLine();
		
		ic.send( "Portrait: " + th.getFullPortrait( p ) );
		
		{
			Vector verbs = new Vector( 10, 10 );
			
				//  this is lots of fun.
				//  scan through the methods on this object comparing
				//  them to these defaults and see what has changed.
			Class ourClass = th.getClass();
			Method[] localMethods = ourClass.getMethods();
			
			for( int i = 0; i < localMethods.length; i++ )
			{
				Method m = localMethods[i];
				Class[] parameters = m.getParameterTypes();
				
				if( parametersEquivalent( parameters, verbParameters ) )
				{
					String mn = m.getName();
					
					if( th.isMethodSpecial( m ) )
					{
							//  this one is different
						verbs.addElement( mn );
					}
				}
			}
			
			if( verbs.size() == 0 )
				ic.send( "No active verbs" );
			else
				ic.send( "Verbs: ^h" + Grammar.enumerate( verbs.elements() ) + "^-" );
		}
	}
	
	protected final static void defaultInspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		standardInspect( p, args, ic, flags, item );
	}
	
	public boolean isMethodSpecial( Method m )
	{
		Class dc = m.getDeclaringClass();
		
		return( dc != Material.class &&
				!Modifier.isFinal( m.getModifiers() ) &&
				!Modifier.isAbstract( m.getModifiers() )
			  );
	}
	
	public void get( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		defaultGet( p, args, ic, flags, item );
	}
	
	protected static final void defaultGet( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		Room r = p.getLocation();
		Inventory i = p.getInventory();
		
			//  ensure that we won't have to back out
		r.checkRemoveFromPermission( ((Atom)item).getThis() );
		i.checkPermissionList( Container.addToAction );
		((Atom)item).checkPermissionList( Atom.moveAction );
		
		if( !i.canAdd() )
		{
			ic.sendFailure( "You are carrying too much already." );
			return;
		}
		
		try
		{
			r.remove( (Atom) item );
			i.add( (Atom) item );
			new key.effect.Broadcast( p.getLocation(), p, ( p.getName() + " picks up " + item.getFullPortrait( p ) + "." ), ( "You pick up " + item.getFullPortrait( p ) + "." ) ).cause();
		}
		catch( Exception e )
		{
			throw new UnexpectedResult( e );
		}
	}
	
	public void give( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		defaultGive( p, args, ic, flags, item );
	}
	
	protected static final void defaultGive( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		if( !args.hasMoreTokens() )
		{
			ic.sendFailure( "Who do you want to give " + item.getFullPortrait( p ) + " to?" );
			return;
		}
		
		String pname = args.nextToken();
		Player t = null;
		
		try
		{
			t = (Player) Command.getOnlinePlayer( p, ic, pname );
		}
		catch( ClassCastException e )
		{
			ic.sendFailure( "You may only give to single players" );
			return;
		}
		
		if( t == null )
		{
			ic.sendFailure( "No such player '" + pname + "'" );
			return;
		}
		
		Inventory i = p.getInventory();
		Inventory ti = t.getInventory();
		
			//  ensure that we won't have to back out
		i.checkRemoveFromPermission( ((Atom)item).getThis() );
		ti.checkPermissionList( Container.addToAction );
		((Atom)item).checkPermissionList( Atom.moveAction );
		
		if( !ti.canAdd() )
		{
			ic.sendFailure( p.HeShe() + " is carrying too much already." );
			return;
		}
		
		try
		{
			i.remove( (Atom) item );
			ti.add( (Atom) item );
			ic.sendFeedback( "You give " + item.getFullPortrait( p ) + " to " + t.getName() + "." );
			if( t.connected() )
			{
				t.send( p.getName() + " gives you " + item.getFullPortrait( p ) + "." );
			}
		}
		catch( CannotDropWhileWearingException e )
		{
			ic.sendFailure( "You may not give away objects that are currently being worn or wielded.  Use 'remove <object>' to stop wearing/wielding an object" );
		}
		catch( Exception e )
		{
			throw new UnexpectedResult( e );
		}
	}
	
	public void drop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		defaultDrop( p, args, ic, flags, item );
	}
	
	protected static final void defaultDrop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item )
	{
		Room r = p.getLocation();
		Inventory i = p.getInventory();
		
			//  ensure we won't have to back out
		r.checkPermissionList( Container.addToAction );
		i.checkRemoveFromPermission( ((Atom)item).getThis() );
		((Atom)item).checkPermissionList( Atom.moveAction );
		
		if( !r.canAdd() )
		{
			ic.sendFailure( "There is too much in this room to drop any more." );
			return;
		}
		
		try
		{
			i.remove( (Atom) item );
			r.add( (Atom) item );
			new key.effect.Broadcast( p.getLocation(), p, ( p.getName() + " puts down " + item.getFullPortrait( p ) + "." ), ( "You put down " + item.getFullPortrait( p ) + "." ) ).cause();
		}
		catch( CannotDropWhileWearingException e )
		{
			ic.sendFailure( "You cannot drop " + item.getFullPortrait( p ) + " while you are wearing it." );
		}
		catch( Exception e )
		{
			throw new UnexpectedResult( e );
		}
	}
	
		//  META: eventually do a default method for this
	public void sit( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot sit on " + getFullPortrait( p ) + "." );
	}
	
		//  META: eventually do a default method for this
	public void stand( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You are not sitting on " + getFullPortrait( p ) + "." );
	}
	
	public void drink( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot drink " + getFullPortrait( p ) + "." );
	} 
	public void eat( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot eat " + getFullPortrait( p ) + "." );
	}
	
	public void open( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot open " + getFullPortrait( p ) + "." );
	}
	
	public void close( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot close " + getFullPortrait( p ) + "." );
	}
	
	public void fill( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot fill " + getFullPortrait( p ) + "." );
	}
	
	public void lock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot lock " + getFullPortrait( p ) + "." );
	}
	
	public void unlock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( "You cannot unlock " + getFullPortrait( p ) + "." );
	}
	
//---  proxy methods  --------------------------------
	public String getFullPortrait( Player p, Atom proxy )
		{	return( getFullPortrait( p ) );	}
	
	public String toString()
		{	return( getFullPortrait( null ) );	}
	
//---  static section  -------------------------------------------------
//
//  contains some static constants that are useful for the manipulation
//  of objects.
//
	protected static transient Hashtable methods = new Hashtable();
	protected static transient String available_verbs;
	protected static transient String defaultVerb;
	protected static transient Method defaultMethod;
	
	public static final Class[] verbParameters =
	{
		Player.class,
		StringTokenizer.class,
		InteractiveConnection.class,
		Flags.class,
		Thing.class,
		Container.class
	};
	
	public static boolean parametersEquivalent( Class[] a, Class[] b )
	{
		if( a.length != b.length )
			return( false );
		
		for( int j = 0; j < a.length; j++ )
		{
			if( a[j] != b[j] )
				return( false );
		}
		
		return( true );
	}
	
		//  place the methods from Thing into the hashtable
	static
	{
		Vector v = new Vector( 5, 5 );
		
		try
		{
			Method m[] = Thing.class.getDeclaredMethods();
			
			for( int i = 0; i < m.length; i++ )
			{
				Class[] parameters = m[i].getParameterTypes();

				if( parametersEquivalent( parameters, verbParameters ) )
				{
					String name = m[i].getName();
					Method meth = m[i];
					
					methods.put( name, meth );
					v.addElement( name );
					
					if( defaultMethod == null )
					{
						defaultMethod = meth;
						defaultVerb = name;
					}
				}
			}
			
			available_verbs = Grammar.commaSeperate( v.elements() );
		}
		catch( Exception e )
		{
			Log.debug( Material.class, "Could not initialise fields from Thing: " + e.toString() );
			e.printStackTrace();
		}
	}
	
	public static final String getDefaultVerb()
	{
		return( defaultVerb );
	}
	
	public static final Method getMethod( String verb )
	{
		return( (Method) methods.get( verb ) );
	}

	public static final String getAvailableVerbs()
	{
		return( available_verbs );
	}
	
	public int getValue()
	{
		return( 0 );
	}
	
	public final void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { use( p, args, ic, flags, actual, null ); }
	public final void wear( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { wear( p, args, ic, flags, actual, null ); }
	public final void remove( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { remove( p, args, ic, flags, actual, null ); }
	public final void wield( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { wield( p, args, ic, flags, actual, null ); }
	public final void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { read( p, args, ic, flags, actual, null ); }
	public final void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { look( p, args, ic, flags, actual, null ); }
	public final void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { inspect( p, args, ic, flags, actual, null ); }
	public final void get( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { get( p, args, ic, flags, actual, null ); }
	public final void give( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { give( p, args, ic, flags, actual, null ); }
	public final void drop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { drop( p, args, ic, flags, actual, null ); }
	public final void sit( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { sit( p, args, ic, flags, actual, null ); }
	public final void stand( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { stand( p, args, ic, flags, actual, null ); }
	public final void drink( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { drink( p, args, ic, flags, actual, null ); }
	public final void eat( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { eat( p, args, ic, flags, actual, null ); }
	public final void open( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { open( p, args, ic, flags, actual, null ); }
	public final void close( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { close( p, args, ic, flags, actual, null ); }
	public final void fill( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { fill( p, args, ic, flags, actual, null ); }
	public final void lock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { lock( p, args, ic, flags, actual, null ); }
	public final void unlock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual ) { unlock( p, args, ic, flags, actual, null ); }
}
