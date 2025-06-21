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
**  $Id: AtomicElement.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  28Jan98    subtle       created
**  08Aug98    subtle       TODO: Make flag selection & verification
**                          tighter.  ie, 'REFERENCE' should only be
**                          settable iff f instanceof Reference.  Also,
**                          it can probably be automatically determined,
**                          without it needing to be stated.
**
*/

package key;

import java.lang.reflect.*;

/**
  *  This class represents the basic building block of an Atom in
  *  Key.  (An Atom, in turn, is the building block of the larger 
  *  structures).
  * <P>
  *  Each class of Atom has a static reference to an AtomicStructure,
  *  which holds all of the AtomicElements describing that Atom class.
  * <P>
  *  AtomicElements use Java Reflection to directly access the values
  *  held within the Atom, and as such, Atoms have the responsibilities
  *  to provide the correct fields and methods for their structure, (as
  *  held in their AtomicStructure).
  * <P>
  *  Each AtomicElement has a set of flags which determine the requisite
  *  support from Atom.  Specifically, for an AtomicElement with name
  *  <I>X</I> and type <I>classY</I>:
  * <P>
  * <TABLE>
  *
  * <TH>
  *   <TD>type</TD><TD>readonly?</TD><TD>reference?</TD><TD>Requirements</TD>
  * </TH>
  *
  * <TR>
  *   <TD>public field</TD><TD>n/a</TD><TD>no</TD>
  *     <TD><UL>
  *       <LI>public classY X;</LI>
  *     </UL></TD>
  * </TR>
  *
  * <TR>
  *   <TD>public field</TD><TD>n/a</TD><TD>yes</TD>
  *     <TD><UL>
  *       <LI>public Reference X;</LI>
  *     </UL></TD>
  * </TR>
  *
  * <TR>
  *   <TD>public accessors</TD><TD>yes</TD><TD>n/a</TD>
  *     <TD><UL>
  *       <LI>public classY getX();</LI>
  *     </UL></TD>
  * </TR>
  *
  * <TR>
  *   <TD>public accessors</TD><TD>no</TD><TD>no</TD>
  *     <TD><UL>
  *       <LI>public classY getX();</LI>
  *       <LI>public void setX( classY );</LI>
  *     </UL></TD>
  * </TR>
  *
  * <TR>
  *   <TD>public accessors</TD><TD>no</TD><TD>yes</TD>
  *     <TD><UL>
  *       <LI>public classY getX();</LI>
  *       <LI>public void setX( classY );</LI>
  *       <LI>public void setX( String, Atom relTo );</LI>
  *     </UL></TD>
  * </TR>
  * </TABLE>
  * <P>
  *  An AtomicElement may only be flagged "Reference" iff the
  *  type of the element is a subclass of Atom.  In other words,
  *  a Reference may only point to another Atom.
  * <P>
  *  The setX( String, Atom ) accessor (for references) accepts a
  *  string as the Id of an atom in the hierarchy.  This string is
  *  'relative' to the relTo parameter.  If you are using a
  *  "Reference" underlying the accessors, this is easy to implement
  *  by simply passing the call through to the set method in the
  *  underlying Reference object.
 */
public abstract class AtomicElement implements Searchable
{
	/**
	  * @see key.Container.PARENT_TYPE
	 */
	public static final int PARENT_TYPE = 1;
	
	/**
	  *  If (flags & PUBLIC_FIELD) == PUBLIC_FIELD, then
	  *  this element is accessable through a reflection
	  *  field.
	 */
	public static final int PUBLIC_FIELD = (1<<0);

	/**
	  *  If (flags & PUBLIC_ACCESSORS) == PUBLIC_ACCESSORS,
	  *  then this element is accessable through some
	  *  accessor (get/set) methods.
	 */
	public static final int PUBLIC_ACCESSORS = (1<<1);
	
	/**
	  *  If this flag is selected then you cannot call
	  *  'set' on this element.
	 */
	public static final int READ_ONLY = (1<<2);
	
	/**
	  *  If this flag is selected then this element is a
	  *  reference to an Atom somewhere else in the Key
	  *  hierarchy.  The class of the referenced element
	  *  _must_ be an Atom for this to be the case.
	 */
	public static final int REFERENCE = (1<<3);
	
	/**
	  *  This flag does not need to be set explicitly.  If
	  *  this flag is set, it means that the class of the
	  *  value is atomic.  This means different things, dependant
	  *  upon whether this field is also a reference.
	  * <P>
	  *  If this element is not a reference, this means that atoms
	  *  that are assigned in here will have their parent, owner,
	  *  and name set correctly.  Atoms that are un-assigned from
	  *  here will be destroyed.
	  * <P>
	  *  An element cannot be a reference unless it is also an
	  *  atom.  (ie, REFERENCE implies ATOMIC)
	 */
	public static final int ATOMIC = (1<<4);
	
	/**
	  *  This flag indicates that this field is a calculated
	  *  value, derived from other properties.  Having this set
	  *  will prevent some commands (Dump) from listing it
	  *  explicitly.
	  * <P>
	  *  For instance, the Player class has both a 'name' field
	  *  and a 'title' field.  However, for user-interface reasons,
	  *  a 'titledName' read-only method is also available that
	  *  returns the concatenation of these two fields.  There is no
	  *  need, however, to output this field in a 'dump' of the entire
	  *  class: it is redundant information.
	 */
	public static final int GENERATED = (1<<5);
	
	private int hashCode;
	private String lowerName;
	private String fieldName;
	private int flags;
	private String description;
	
	protected Class classOf = null;
	protected Type typeOf = null;
	protected AtomicSpecial special = null;
	
	protected AtomicElement( String fName, String matchName, int ourFlags, String desc )
	{
		fieldName = fName;
		lowerName = matchName.toLowerCase();
		flags = ourFlags;
		description = desc;
		
			//  calculate a hashcode for slightly faster
			//  compares / finds.
		hashCode = lowerName.hashCode();
	}
	
	public final String getLowerName()
	{
		return( lowerName );
	}
	
	public final String getName()
	{
		return( fieldName );
	}
	
	public final Object getKey()
	{
		return( fieldName );
	}
	
	public final Type getTypeOf()
	{
		return( typeOf );
	}

	public final Class getClassOf()
	{
		return( classOf );
	}
	
	public final void setKey( Object o )
	{
		throw new UnexpectedResult( "cannot set the key of an element" );
	}
	
	public final void search( Search s ) throws InvalidSearchException
	{
			//  evaluate this result
		try
		{
			s.result = getValue( s.lastAtom );
		}
		catch( Exception e )
		{
				//  this could be enhanced to make error messages better,
				//  but lets not bother at the moment
			throw new InvalidSearchException( e.toString() );
		}
	}
	
	public final int getHashCode()
	{
		return( hashCode );
	}
	
	public final boolean isReadOnly()
	{
		return( (flags & READ_ONLY) == READ_ONLY );
	}
	
	public final boolean isReference()
	{
		return( (flags & REFERENCE) == REFERENCE );
	}

	public final boolean isAtomic()
	{
		return( (flags & ATOMIC) == ATOMIC );
	}
	
	public final boolean isGenerated()
	{
		return( (flags & GENERATED) == GENERATED );
	}
	
	public abstract Object getValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException;
	public abstract void setValue( Atom on, Object newValue, Atom relativeTo ) throws IllegalAccessException, IllegalArgumentException, NullPointerException;

	public Object getBasicValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		return( getValue( on ) );
	}
	
	public AtomicSpecial getSpecial()
	{
		return( special );
	}
	
	/**
	  *  This routine makes sure that, when an Atom atomic element is
	  *  being used on an Atom, the parent, name, and owner are
	  *  set correctly to avoid permissions problems.
	 */
	public void atomicFrontend( Atom on, Object newValue )
	{
		if( isAtomic() && !isReference() )
		{
			try
			{
				Atom old = (Atom) getValue( on );
				
				if( old != null )
				{
					old.dispose();
					old = null;
				}
			}
			catch( IllegalAccessException e )
			{
				Log.error( e );
			}
			
			if( newValue != null )
			{
					//  we need to set up the parent, name & owner
				Atom a = (Atom) newValue;
				
				a.setKey( fieldName );
				a.setOwner( on.getOwner() );
				
					// META:  Review parent structure here
				a.setParent( on, PARENT_TYPE );
			}
		}
	}
	
	/**
	  *  This routine is called before the 'set' routine
	  *  in any subclass to check the preconditions for a
	  *  call to 'set' (such as this field not being read-only)
	 */
	protected final Object checkSet( Atom on, Object newValue )
	{
		if( isReadOnly() )
			throw new AccessViolationException( this, "attempting to set read only property: " + getName() + " with " + newValue );
		
		on.permissionList.check( Player.modifyAction );
		
		if( special != null )
			return( special.validateNewValue( newValue ) );
		else
			return( newValue );
	}
	
	/**
	  *  Standard abstract factory to create the correct implementation
	  *  subclass based on the provided flags.
	 */
	public static AtomicElement construct( Class in, Class type, String name, int flags, String desc )
	{
		return( construct( in, type, name, name, flags, desc, null ) );
	}
	
	public static AtomicElement construct( Class in, Class type, String name, String matchName, int flags, String desc )
	{
		return( construct( in, type, name, matchName, flags, desc, null ) );
	}
	
	public static AtomicElement construct( Class in, Class type, String name, String matchName, int flags, String desc, AtomicSpecial special )
	{
		AtomicElement ae = null;
		
		try
		{
			if( (flags & PUBLIC_FIELD) == PUBLIC_FIELD )
			{
				Field f = in.getDeclaredField( name );
				
				if( f == null )
				{
					Log.error( "AtomicElement: unknown field type, '" + name + "' in " + in.getName() );
					return null;
				}
				else
				{
					if( Reference.class.isAssignableFrom( f.getType() ) )
					{
						flags |= REFERENCE;
						
							//  just a quick sanity check
						if( !Atom.class.isAssignableFrom( type ) )
							System.out.println( "  !!" + type.getName() + " is not a subclass of Atom - FAILURE condition" );
						
						ae = new ReferenceFieldAtomicElement( in, type, name, matchName, flags, desc );
					}
					else
					{
						if( (flags & REFERENCE) == REFERENCE )
						{
							System.out.println( "incorrectly set REFERENCE flag in field " + f.getName() + " on " + in.getName() );
							flags &= ~REFERENCE;
						}
						ae = new FieldAtomicElement( in, type, name, matchName, flags, desc );
					}
				}
				
				if( Atom.class.isAssignableFrom( type ) )
					ae.flags |= ATOMIC;
			}
			else if( (flags & PUBLIC_ACCESSORS) == PUBLIC_ACCESSORS )
			{
				ae = new AccessorAtomicElement( in, type, name, matchName, flags, desc );
			}
			else
			{
				Log.error( "AtomicElement: problem with field '" + name + "' in " + in.getName() );
				return null;
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
			Log.error( "AtomicElement: while building atomic structure for " + in.getName() + " (field " + name + ")", e );
		}
		
		if( !(Atom.class.isAssignableFrom( type )) && ae.isReference() )
		{
			throw new UnexpectedResult( "reference to a non-atomic property attempted in class " + in.getName() );
		}
		
		if( special != null )
		{
			if( special.canUseWith( ae ) )
				ae.special = special;
			else
				throw new UnexpectedResult( "invalid special in " + ae.getName() + ", class " + in.getName() );
		}
		
		return( ae );
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		if( isReadOnly() )
			sb.append( "[readonly] " );
		
		sb.append( asString() );
		
		return( sb.toString() );
	}

	public abstract String asString();

	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
}

class FieldAtomicElement extends AtomicElement
{
	Field theField;
	
	public FieldAtomicElement( Class in, Class type, String name, String matchName, int flags, String desc ) throws NoSuchFieldException, SecurityException
	{
		super( name, matchName, flags, desc );
		
		theField = in.getDeclaredField( name );
		classOf = theField.getType();
		typeOf = Type.typeFor( classOf );
	}
	
	public Object getValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		return( theField.get( on ) );
	}

	public void setValue( Atom on, Object newValue, Atom relTo ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		newValue = checkSet( on, newValue );
		atomicFrontend( on, newValue );
		
			//  the VM will check the type for us, here - we don't
			//  have to do it.
		theField.set( on, newValue );
	}
	
	public String asString()
	{
		return( theField.toString() );
	}
}

final class ReferenceFieldAtomicElement extends FieldAtomicElement
{
	public ReferenceFieldAtomicElement( Class in, Class type, String name, String matchName, int flags, String desc ) throws NoSuchFieldException, SecurityException
	{
		super( in, type, name, matchName, flags, desc );
		
		classOf = type;
		typeOf = Type.typeFor( type );
	}
	
	public Object getValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		Reference r = (Reference) super.getValue( on );
		
		if( r != null )
		{
			try
			{
				return( r.get() );
			}
			catch( OutOfDateReferenceException e )
			{
				theField.set( on, Reference.EMPTY );
				return( null );
			}
		}
		else
			return( null );
	}

	public Object getBasicValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		return( super.getValue( on ) );
	}
	
	public void setValue( Atom on, Object newValue, Atom relTo ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		newValue = checkSet( on, newValue );
		
		if( newValue == null )
		{
			theField.set( on, Reference.EMPTY );
		}
		else if( classOf.isInstance( newValue ) )
		{
			theField.set( on, ((Atom)newValue).getThis() );
		}
		else if( newValue instanceof String )
		{
				//  the new way does the resolving in the later call
			theField.set( on, Reference.to( (String) newValue, relTo ) );
		}
		else
			throw new IllegalArgumentException( "attempting to set reference with incompatible type." );
	}
}

/**
  *  Permits key to use accessor methods to get and set a property.
  *
  *  Accessor methods must be declared:
  *
  *    public <whatever> get<Property>()
  *    public void set<Property>( Object newValue )
 */
final class AccessorAtomicElement extends AtomicElement
{
	private static final Object[] emptyObjectArray = new Object[0];
	private static final Class[] emptyClassArray = new Class[0];
	Method getMethod;
	Method setMethod;
	Method setIdMethod = null;
	
	public AccessorAtomicElement( Class in, Class type, String name, String matchName, int flags, String desc ) throws NoSuchMethodException, SecurityException
	{
		super( name, matchName, flags, desc );
		
		String cappedName = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );
		
		getMethod = in.getDeclaredMethod( "get" + cappedName, emptyClassArray );
		
		if( !isReadOnly() )
		{
			Class[] setclasses = new Class[1];
			setclasses[0] = type;
			setMethod = in.getDeclaredMethod( "set" + cappedName, setclasses );
			
			if( isReference() )
			{
				setclasses = new Class[2];
				setclasses[0] = String.class;
				setclasses[1] = Atom.class;
				
				setIdMethod = in.getDeclaredMethod( "set" + cappedName, setclasses );
			}
		}
		else
			setMethod = null;
		
		classOf = getMethod.getReturnType();
		typeOf = Type.typeFor( classOf );
	}
	
	public Object getValue( Atom on ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		try
		{
			return( getMethod.invoke( on, emptyObjectArray ) );
		}
		catch( InvocationTargetException e )
		{
			Throwable x = e.getTargetException();
			
			if( x instanceof RuntimeException )
				throw (RuntimeException) x;
			else
				throw new UnexpectedResult( x.toString() );
		}
	}
	
	public void setValue( Atom on, Object newValue, Atom relTo ) throws IllegalAccessException, IllegalArgumentException, NullPointerException
	{
		try
		{
			newValue = checkSet( on, newValue );
			atomicFrontend( on, newValue );
			
				//  setIdMethod is != null iff isReference()
			if( setIdMethod != null && newValue instanceof String )
			{
				Object[] setobjects = new Object[2];
				setobjects[0] = newValue;
				setobjects[1] = relTo;
				setIdMethod.invoke( on, setobjects );
			}
			else
			{
				Object[] setobjects = new Object[1];
				setobjects[0] = newValue;
				setMethod.invoke( on, setobjects );
			}
		}
		catch( InvocationTargetException e )
		{
			Throwable x = e.getTargetException();
			
			if( x instanceof RuntimeException )
				throw (RuntimeException) x;
			else
				throw new UnexpectedResult( x.toString() );
		}
	}
	
	public String asString()
	{
		return( getMethod.toString() );
	}
}
