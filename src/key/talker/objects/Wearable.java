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

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

/**
 */
public class Wearable extends EW
{
	private static final long serialVersionUID = -8896879880878644302L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Wearable.class, String.class, "wearLocations",
			AtomicElement.PUBLIC_ACCESSORS,
			"comma seperated list of locations that this object is worn" ),
		AtomicElement.construct( Wearable.class, String.class, "coverLocations",
			AtomicElement.PUBLIC_ACCESSORS,
			"comma seperated list of locations that this object covers" ),
		AtomicElement.construct( Wearable.class, Boolean.TYPE, "wieldToWear",
			AtomicElement.PUBLIC_FIELD,
			"true if the object must be wielded to be used" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( EW.STRUCTURE, ELEMENTS );
	
	private String[] wearLocs = new String[0];
	private String[] coverLocs = new String[0];
	public boolean wieldToWear = false;
	
	public Wearable()
	{
	}

	public boolean wieldInsteadOfWear()
	{
		return( wieldToWear );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getWearLocations()
	{
		return( Grammar.commaSeperate( wearLocs ) );
	}
	
	public void setWearLocations( String s )
	{
		StringTokenizer st = new StringTokenizer( s, "," );
		Vector v = new Vector( 5, 20 );
		
		while( st.hasMoreTokens() )
		{
			v.addElement( st.nextToken() );
		}
		
		if( wearLocs.length != v.size() )
			wearLocs = new String[ v.size() ];
		v.copyInto( wearLocs );
		v.setSize( 0 );
	}
	
	public String[] wearLocations( Player p )
	{
		return( wearLocs );
	}
	
	public String getCoverLocations()
	{
		return( Grammar.commaSeperate( coverLocs ) );
	}
	
	public void setCoverLocations( String s )
	{
		StringTokenizer st = new StringTokenizer( s, "," );
		Vector v = new Vector( 5, 20 );
		
		while( st.hasMoreTokens() )
		{
			v.addElement( st.nextToken() );
		}
		
		if( coverLocs.length != v.size() )
			coverLocs = new String[ v.size() ];
		v.copyInto( coverLocs );
		v.setSize( 0 );
	}
	
	public String[] coverLocations( Player p )
	{
		return( coverLocs );
	}	
}
