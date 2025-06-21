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
  *  Prop.
  *
  *  A simple class that has no behaviour, only a particular appearance,
  *  just like a stage prop.
 */
public class Prop extends Material implements Thing
{
	private static final long serialVersionUID = 4509498017160514849L;
	
	public static final int MAX_DESCRIPTION_LENGTH = 160;
	public static final int MAX_FULLPORTRAIT_LENGTH = 80;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Prop.class, String.class,
			"description", "description",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to a person who looks at the object",
			AtomicSpecial.StringLengthLimit( MAX_DESCRIPTION_LENGTH, false, true ) ),
		AtomicElement.construct( Prop.class, String.class,
			"fullPortrait", "fullPortrait",
			AtomicElement.PUBLIC_FIELD,
			"a short description of the object, starting with a proposition",
			AtomicSpecial.StringLengthLimit( MAX_FULLPORTRAIT_LENGTH, false, true ) ),
		AtomicElement.construct( Prop.class, Integer.TYPE, "value",
			AtomicElement.PUBLIC_FIELD,
			"the value of this object in silver florins" ),
		AtomicElement.construct( Prop.class, Boolean.TYPE, "junk",
			AtomicElement.PUBLIC_FIELD,
			"true iff this object can be cleaned up at will" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public String description = "";
	public String fullPortrait = "a tiny grain of sand";
	public int value = 1;
	public boolean junk = false;
	
	public Prop()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	private static final HeadingParagraph INSPECT_HEADING = new HeadingParagraph( "basic", HeadingParagraph.RIGHT );
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		Material.defaultInspect( p, args, ic, flags, item );
		
		ic.send( INSPECT_HEADING );
		ic.send( "Junk: " + junk + "\t\tValue: " + value );
		
		if( getClass() == Prop.class )
			ic.sendLine();
	}
	
	public String getDescription()
		{ return( description ); }
	
	public void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( description != null && description.length() > 0 )
			ic.send( fullPortrait + ": " + description );
		else
			ic.send( fullPortrait );
	}
	
	public String getFullPortrait( Player p )
		{ return( fullPortrait ); }
	
	public int getValue()
		{ return( value ); }
}
