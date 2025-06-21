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
public class Shopkeeper extends MaterialContainer
{
	private static final long serialVersionUID = -3864297319810349528L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Shopkeeper.class, Integer.TYPE, "markup",
			AtomicElement.PUBLIC_FIELD,
			"the value increase of items sold here" ),
		AtomicElement.construct( Shopkeeper.class, String.class, "notFound",
			AtomicElement.PUBLIC_FIELD,
			"the message send when the item wasn't found" ),
		AtomicElement.construct( Shopkeeper.class, String.class, "sold",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when sold" ),
		AtomicElement.construct( Shopkeeper.class, Material.class, "proxydata",
			AtomicElement.PUBLIC_FIELD,
			"descriptions of this object" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( MaterialContainer.STRUCTURE, ELEMENTS );
	
	public final Material proxydata = (Material) Factory.makeAtom( Prop.class, "proxydata" );
	
	public int markup = 2;
	
	public String notFound = "I do not have any of those to sell.";
	public String sold = "Sold!";
	
	public Shopkeeper()
	{
		material = proxydata.getThis();
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	private static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "object", 50 ),
		new TableParagraph.Column( "price", 8 )
	};
	
	/**
	  *  Override the default value of build - we don't want a
	  *  copy of this object, we want the real schzam.
	 */
	public Reference build( Player p )
	{
		return( getThis() );
	}
	
	private static final HeadingParagraph INSPECT_HEADING = new HeadingParagraph( "shoppe", HeadingParagraph.RIGHT );
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		super.inspect( p, args, ic, flags, item, originating );
		
		ic.send( INSPECT_HEADING );
		
		ic.send( "Markup: " + markup );
		ic.send( "Not Found: " + notFound );
		ic.send( "Sold: " + sold );
		
		if( getClass() == Shopkeeper.class )
			ic.sendLine();
	}
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( !args.hasMoreElements() )
		{
				//  List things to buy
			TableParagraph.Generator table = new TableParagraph.Generator( columns );
			
			for( Enumeration e = elements(); e.hasMoreElements(); )
			{
				Thing t = (Thing) e.nextElement();
				String s[] = new String[2];
				s[0] = t.getName() + ": " + t.getFullPortrait( p );
				s[1] = Integer.toString( t.getValue() + markup ) + " sf";
				table.appendRow( s );
			}
			
			ic.send( table.getParagraph() );
			return;
		}
		
		String boughtName = args.nextToken();
		Thing t = Command.getObjectIn( p, this, ic, boughtName, notFound );
		if( t != null )
		{
			int v = t.getValue();
			if( v <= 0 )
				v = 0;
			
			v += markup;
			
			p.subtractFlorins( v );
			
			try
			{
				p.getInventory().add( t.build( p ).get() );
				ic.send( sold );
			}
			catch( Exception e )
			{
				if( e instanceof UserOutputException )
					((UserOutputException)e).send( ic );
				else
				{
					Log.error( "during buy item", e );
					ic.sendFailure( e.getMessage() );
				}
			}
		}
	}
}
