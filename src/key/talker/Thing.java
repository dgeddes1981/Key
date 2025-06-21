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

import java.util.StringTokenizer;
import java.io.File;

/*
**  The base class for all 'objects' that can be placed
**  in rooms or manipulated by players.
*/
public interface Thing extends Splashable
{
	/**
	  *  eg, "pink teddy bear", as in "You pick up a pink teddy bear".
	 */
	public String getFullPortrait( Player p );
	public String getName();
	public String[] wearLocations( Player p );
	public boolean wieldInsteadOfWear();
	
	public boolean isAvailableTo( Player p, Container r );
	
	/**
	  *  eg, "use sword kill snapper". [ Hi J ;) ]
	 */
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void wear( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void remove( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void wield( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void look( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void get( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void give( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void drop( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void sit( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );

	/**
	  *  Unlike the others, stand does not require the object 'argument' to be
	  *  specified - it is automatically determined.  It doesn't effect the
	  *  parameters of this call, though.
	 */
	public void stand( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void drink( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void eat( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void open( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void close( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void fill( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void lock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	public void unlock( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing actual, Container originating );
	
	public int getValue();
	
	/**
	  *  This routine builds a new type of this Thing.
	  *
	  *  To explain, most things don't require a seperate instantiation
	  *  of the actual Atom being built, so it is acceptable to just
	  *  return a reference to itself (which will generally be something
	  *  in /realm.objects).
	  *
	  *  However, some objects, such as a purse or backpack (which contain
	  *  things) require a seperate instantiation for each player that
	  *  owns it.  In this situation, it is necessary to create a new atom
	  *  in ~player.objects and return a reference to that.
	  *
	  *  There may be other situations in the future, as well.
	 */
	public Reference build( Player p );
	
	public boolean isMethodSpecial( java.lang.reflect.Method m );
	
	public void splash( Effect t, SuppressionList s );	
}
