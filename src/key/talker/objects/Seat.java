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
  *  Seat.
  *
  *  Contains no behaviour at present, just an encapsulation for the incoming EW data.
 */
public class Seat extends Prop
{
	private static final long serialVersionUID = -8589507750870993275l;
	
	public static final AtomicElement[] ELEMENTS =
	{
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Prop.STRUCTURE, ELEMENTS );
		
	public Seat()
	{
	}
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		super.inspect( p, args, ic, flags, item, originating );
		ic.sendLine();
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
}
