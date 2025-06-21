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

import key.collections.ObjectCollection;
import key.talker.objects.Wearable;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Hashtable;
import java.io.*;

/**
  *  Nothing other than person location positions should be in this property
  *  list.
 */
public class Inventory extends Container
{
	private static final long serialVersionUID = 3171435380110162286L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Inventory.class, Atom.class, "personage",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "head",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "hair",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "ears",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "face",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "neck",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "body_outer",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "body_middle",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "body_inner",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "arm_left",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "arm_right",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "waist_accessory",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "waist",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "legs_outer",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "legs_middle",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "legs_inner",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "hands",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "wrist_left",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "wrist_right",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "finger_left",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "finger_right",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "feet_outer",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "feet_middle",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "feet_inner",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "miscellaneous",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "wield_left",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "wield_right",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "seat",
			AtomicElement.PUBLIC_FIELD,
			"object being worn on personage" ),
		AtomicElement.construct( Inventory.class, Atom.class, "pet",
			AtomicElement.PUBLIC_FIELD,
			"object is a pet" )
	};
	
	/**
	   * This routine is called by objects at initialisation time.  It assigns an integer (bitwise)
	   * mask to them that is used to determine what this item covers.  (ie, a cloak will cover a shirt).
	   *
	  */
	public static int assignMask( String[] locations )
	{
		int mask = 0;
		
		for( int i = 0; i < locations.length; i++ )
		{
			int idx = STRUCTURE.getElementIndex( locations[i] );
			
			if( idx == -1 )
				throw new UnexpectedResult( "Attempting to Inventory::assignMask() for an unknown location" );
			
			mask |= 1 << idx;
		}
		
		return( mask );
	}
	
	protected Reference personage = Reference.EMPTY;
	protected Reference head = Reference.EMPTY;
	protected Reference hair = Reference.EMPTY;
	protected Reference ears = Reference.EMPTY;
	protected Reference face = Reference.EMPTY;
	protected Reference neck = Reference.EMPTY;
	protected Reference body_outer = Reference.EMPTY;
	protected Reference body_middle = Reference.EMPTY;
	protected Reference body_inner = Reference.EMPTY;
	protected Reference arm_left = Reference.EMPTY;
	protected Reference arm_right = Reference.EMPTY;
	protected Reference waist_accessory = Reference.EMPTY;
	protected Reference waist = Reference.EMPTY;
	protected Reference legs_outer = Reference.EMPTY;
	protected Reference legs_middle = Reference.EMPTY;
	protected Reference legs_inner = Reference.EMPTY;
	protected Reference hands = Reference.EMPTY;
	protected Reference wrist_left = Reference.EMPTY;
	protected Reference wrist_right = Reference.EMPTY;
	protected Reference finger_left = Reference.EMPTY;
	protected Reference finger_right = Reference.EMPTY;
	protected Reference feet_outer = Reference.EMPTY;
	protected Reference feet_middle = Reference.EMPTY;
	protected Reference feet_inner = Reference.EMPTY;
	protected Reference miscellaneous = Reference.EMPTY;
	protected Reference wield_left = Reference.EMPTY;
	protected Reference wield_right = Reference.EMPTY;
	protected Reference seat = Reference.EMPTY;
	protected Reference pet = Reference.EMPTY;
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public Inventory()
	{
		super( false, "inventory", new ObjectCollection() );
		
		setConstraint( Type.THING );
		setLimit( 10 );
	}
	
	public void remove( Atom removed ) throws NonUniqueKeyException,NoSuchElementException,BadKeyException
	{
		if( isWearing( removed ) )
			throw new CannotDropWhileWearingException();
		
		super.remove( removed );
	}

	/**
	  *  Harmless if you're not wearing the item
	 */
	public void stopWearing( Atom a )
	{
			//  quickly scan our fields & determine if we're wearing
			//  this or not
		for( int i = 0; i < ELEMENTS.length; i++ )
		{
			try
			{
				if( ((Reference) ELEMENTS[i].getBasicValue( this )).contains( a ) )
				{
					ELEMENTS[i].setValue( this, null, null );
				}
			}
			catch( Exception e )
			{
				Log.debug( this, e.toString() + " while stopWearing" );
				e.printStackTrace();
			}
		}
	}
	
	public boolean isWearing( Atom a )
	{
			//  quickly scan our fields & determine if we're wearing
			//  this or not
		for( int i = 0; i < ELEMENTS.length; i++ )
		{
			try
			{
				if( ((Reference) ELEMENTS[i].getBasicValue( this )).contains( a ) )
					return( true );
			}
			catch( Exception e )
			{
				Log.debug( this, e.toString() + " while checking if wearing" );
				e.printStackTrace();
			}
		}
		
		return( false );
	}
	
	public Paragraph look( Player p )
	{
		Hashtable ht = new Hashtable();
		StringBuffer sb = new StringBuffer();
		
		int mask = 0;
		
			//  prescan once
		for( int i = 0; i < ELEMENTS.length; i++ )
		{
			//sb.append( "i=" + i + ", mask=" + mask + " " );
			if( (mask & (1 << i)) == 0 )
			{
				try
				{
					AtomicElement ae = ELEMENTS[i];
					Reference r = (Reference) ae.getBasicValue( this );
					
					if( r.isValid() )
					{
						Object m = ht.get( r );
						
						if( m == null )
						{
									//  It'd be nice to have a reference to the current player here so we can do a proper
									//  getFullPortrait()...  20/20 Hindsight. ;)
							Atom a = (Atom) r.get();
							
							if( a instanceof Thing && !((Thing)a).isAvailableTo( p, p.getLocation() ) )
								continue;
							
							if( a instanceof Wearable )
							{
								mask |= assignMask( ((Wearable)a).coverLocations( null ) );
							}
							
							sb.append( "    " );
							sb.append( a.toString() );
							sb.append( '\n' );
							ht.put( r, Boolean.TRUE );
						}
					}
				}
				catch( Exception e )
				{
					Log.debug( this, e.toString() + " while checking if wearing" );
					e.printStackTrace();
				}
			}
			/*
			else	//  remove this
			{
				try
				{
					AtomicElement ae = ELEMENTS[i];
					Reference r = (Reference) ae.getBasicValue( this );
					
					if( r.isValid() )
					{
						Object m = ht.get( r );
						
						if( m == null )
						{
									//  It'd be nice to have a reference to the current player here so we can do a proper
									//  getFullPortrait()...  20/20 Hindsight. ;)
							Atom a = (Atom) r.get();
							sb.append( "(HIDDEN) " );
							sb.append( a.toString() );
							sb.append( '\n' );
							ht.put( r, Boolean.TRUE );
						}
					}
				}
				catch( Exception e )
				{
					Log.debug( this, e.toString() + " while debug if wearing" );
					e.printStackTrace();
				}
			}
			*/
		}
		
		String s = sb.toString();
		
		if( s.length() > 0 )
			return( new TextParagraph( s ) );
		else
			return( null );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}

		if( pet == null )
			pet = Reference.EMPTY;
	}
	
		//  here we prevent things from being added to the room
	protected void checkAdd_imp( Atom a )
	{
		if( a instanceof Thing )
		{
				//  check to see if this object is available in this room
				//  if not, don't allow it to be added
			((Thing)a).isAvailableTo( null, this );
		}
	}
	
	public Paragraph contents( Player p )
	{
		Hashtable ht = new Hashtable();
		
			//  prescan once
		for( int i = 0; i < ELEMENTS.length; i++ )
		{
			try
			{
				AtomicElement ae = ELEMENTS[i];
				Reference r = (Reference) ae.getBasicValue( this );
				
				if( r.isValid() )
				{
					Map m = (Map) ht.get( r );
					
					if( m == null )
					{
						m = new Map( ae.getName(), r );
						ht.put( r, m );
					}
					else
					{
						m.sb.append( ", " );
						m.sb.append( ae.getName() );
					}
				}
			}
			catch( Exception e )
			{
				Log.debug( this, e.toString() + " while checking if wearing" );
				e.printStackTrace();
			}
		}
		
			//  now output everything
		TableParagraph.Generator table = new TableParagraph.Generator( columns );
		
		for( Enumeration e = referenceElements(); e.hasMoreElements(); )
		{
			Reference r = (Reference) e.nextElement();
			Map m = (Map) ht.get( r );
			
				//  this line is necessary - if we are carrying more
				//  than one of the same thing, we still only list it
				//  as being worn once - as this is indeed the case: 
				//  it can only be worn once.
			ht.remove( r );
			
			Thing t = (Thing) r.get();

			String s[] = new String[2];
			
			if( t.isAvailableTo( p, p.getLocation() ) )
			{
				s[0] = r.getName() + ": " + t.getFullPortrait( null );
				
				if( m != null )
					s[1] = m.sb.toString();
				else
					s[1] = "";
			}
			else
			{
				s[0] = "^h{n/a here}^- " + r.getName() + ": " + t.getFullPortrait( null );
				
				if( m != null )
					s[1] = m.sb.toString();
				else
					s[1] = "";
			}
			
			table.appendRow( s );
		}
		
		int c = count();
		StringBuffer footer = new StringBuffer();
		
		footer.append( c );
		
		if( c > 0 )
		{
			footer.append( '/' );
			footer.append( limit );
		}
		
		footer.append( " objects in your inventory" );
		
		table.setFooter( footer.toString() );
		
		ht.clear();
		
		return( table.getParagraph() );
	}
	
	class Map
	{
		Reference fr;
		StringBuffer sb;
		
		Map( String first, Reference r )
		{
			fr = r;
			sb = new StringBuffer( first );
		}
	}
	
	public static final TableParagraph.Column[] columns = 
	{
		new TableParagraph.Column( "name", 50  ),
		new TableParagraph.Column( "being worn at", 25 )
	};
}
