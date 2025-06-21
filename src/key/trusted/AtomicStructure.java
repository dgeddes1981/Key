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
**  $Id: AtomicStructure.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  28Jan98    subtle       created
**
*/

package key;

import key.util.MultiEnumeration;

import java.util.Vector;
import java.util.Enumeration;

/**
  *  This class contains the structure of the various subclasses
  *  of an Atom.  It is essentially a table which may be looked
  *  up to determine the 'visible' properties of an Atom.  A
  *  visible property may, for instance, be the atoms "Name", or
  *  a players "Title".
  *
  *  META: Efficiency: You could write an enumerator for a
  *  array and use that, instead of putting it into a vector
  *  and double storing everything.
 */
public final class AtomicStructure
{
	private AtomicStructure parent;
	private Vector elements;
	
	public AtomicStructure( AtomicStructure ourParent, AtomicElement[] ourElements )
	{
		parent = ourParent;
		
		elements = new Vector( ourElements.length, 1 );
		for( int i = 0; i < ourElements.length; i++ )
			elements.addElement( ourElements[i] );
	}
	
	public AtomicStructure getParent()
	{
		return( parent );
	}
	
	public Enumeration elements()
	{
		if( parent != null )
			return( new MultiEnumeration( parent.elements(), elements.elements() ) );
		else
			return( elements.elements() );
	}
	
	public AtomicElement getElement( String name )
	{
		name = name.toLowerCase();
		
		int hc = name.hashCode();
		
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			AtomicElement ae = (AtomicElement) e.nextElement();

			//System.out.println( "Scanning (for '" + name + "'): " + ae.getName() );
			
				//  only bother doing a string compare if these
				//  are equivalent
			if( ae.getHashCode() == hc )
			{
				if( ae.getLowerName().equalsIgnoreCase( name ) )
					return( ae );
				//else
					//System.out.println( "  but " + ae.getName() + " != " + name );
			}
		}
		
		return( null );
	}
	
	public int getElementIndex( String name )
	{
		name = name.toLowerCase();
		
		int hc = name.hashCode();
		
		for( int i = 0; i < elements.size(); i++)
		{
			AtomicElement ae = (AtomicElement) elements.elementAt( i );
		
			//System.out.println( "Scanning (for '" + name + "'): " + ae.getName() );
			
				//  only bother doing a string compare if these
				//  are equivalent
			if( ae.getHashCode() == hc )
			{
				if( ae.getLowerName().equalsIgnoreCase( name ) )
					return( i );
				//else
					//System.out.println( "  but " + ae.getName() + " != " + name );
			}
		}
		
		return( -1 );
	}	
}
