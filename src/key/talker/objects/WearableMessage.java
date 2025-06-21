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
public class WearableMessage extends Message
{
	private static final long serialVersionUID = -1786353368415988069L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( WearableMessage.class, String.class, "wearLocations",
			AtomicElement.PUBLIC_ACCESSORS,
			"comma seperated list of locations that this object is worn" ),
		AtomicElement.construct( WearableMessage.class, String.class, "coverLocations",
			AtomicElement.PUBLIC_ACCESSORS,
			"comma seperated list of locations that this object covers" ),
		AtomicElement.construct( WearableMessage.class, Boolean.TYPE, "wieldToUse",
			AtomicElement.PUBLIC_FIELD,
			"true if the object must be wielded to be used" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Message.STRUCTURE, ELEMENTS );
	
	private String[] wearLocs = new String[0];
	private String[] coverLocs = new String[0];
	public boolean wieldToUse = false;
	
	public WearableMessage()
	{
	}
	
	public boolean wieldInsteadOfWear()
		{ return( wieldToUse ); }
	
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
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( wieldToUse )
		{
			if( !p.inventory.isWearing( ((Atom)item) ) )
			{
				ic.sendFailure( "You need to wield this item in order to use it." );
				return;
			}
		}
		
		super.use( p, args, ic, flags, item, originating );
	}
}
