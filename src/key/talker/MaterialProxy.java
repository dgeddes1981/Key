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

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
  *  MaterialProxy
  *
  *  A class implementing thing that subclasses may use when an object that
  *  contains data (that is not a container) may use.  For instance, this might
  *  be useful if, for instance, you wanted to make a purse that contained a
  *  number of florins.
 */
public class MaterialProxy extends Atom implements Thing
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( MaterialContainer.class, Material.class, "material",
			AtomicElement.PUBLIC_FIELD,
			"the material of this container - all requests will be passed to it" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public Reference material = Reference.EMPTY;
	
	public MaterialProxy()
	{
	}
	
	private final Material getMaterial()
	{
		try
		{
			return( (Material) material.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			material = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( getId() + ".material", e );
			material = Reference.EMPTY;
			return( null );
		}
	}
	
	public boolean isAvailableTo( Player p, Container r )
	{
		return( Material.defaultAvailability( this, p, r ) && Material.defaultAvailability( getMaterial(), p, r ) );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getFullPortrait( Player p )
	{
		try
		{
			return( getMaterial().getFullPortrait( p, this ) );
		}
		catch( NullPointerException e )
		{
			return( "the object" );
		}
	}
	
	public boolean wieldInsteadOfWear()
	{
		try
		{
			return( getMaterial().wieldInsteadOfWear() );
		}
		catch( NullPointerException e )
		{
			return( false );
		}
	}
	
	public boolean isMethodSpecial( Method m )
	{
		Class dc = m.getDeclaringClass();
		
		return( dc != MaterialProxy.class &&
				!Modifier.isFinal( m.getModifiers() ) &&
				!Modifier.isAbstract( m.getModifiers() )
			  );
	}
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing a, Container originating )
	{
		Material m = getMaterial();
		
		if( m != null )
			ic.send( new HeadingParagraph( "Proxy" ) );
		
		defaultInspect( p, args, ic, flags, this );
		
		if( m != null )
		{
			ic.send( new HeadingParagraph( "Material" ) );
			m.inspect( p, args, ic, flags, this, originating );
		}
	}
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().use( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void wear( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().wear( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void remove( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().remove( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void wield( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().wield( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void give( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().give( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().read( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().look( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	protected final void defaultInspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m )
	{
		Material.standardInspect( p, args, ic, flags, this );
	}
	
	public void get( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().get( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void drop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().drop( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void sit( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().sit( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void stand( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().stand( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void drink( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().drink( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void eat( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().eat( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void open( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().open( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void close( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().close( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void fill( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().fill( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void lock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().lock( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public void unlock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing m, Container originating )
	{
		try
		{
			getMaterial().unlock( p, args, ic, flags, this, originating );
		}
		catch( NullPointerException e )
		{
		}
	}
	
	public Reference build( Player p )
	{
		MaterialProxy mc = (MaterialProxy) Factory.makeAtom( getClass(), getName() );
		try
		{
			p.objects.add( mc );
		}
		catch( Exception e )
		{
			p.sendFeedback( "Could not create object: " + e.toString() );
			return( Reference.EMPTY );
		}
		
		return( mc.getThis() );
	}
	
	public int getValue()
	{
		try
		{
			return( getMaterial().getValue() );
		}
		catch( NullPointerException e )
		{
		}
		
		return( 0 );
	}
	
	public String[] wearLocations( Player p )
	{
		try
		{
			return( getMaterial().wearLocations( p ) );
		}
		catch( NullPointerException e )
		{
		}
		
		return( null );
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
