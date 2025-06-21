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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul99     Noble        created this command
**
*/

package key.talker.objects;

import key.*;
import java.io.IOException;

import java.io.*;
import java.util.StringTokenizer;

/**
  *  The SlotMachine, as originally coded by Noble.
  *
  *  When treated as a object, the slot machine might be
  *  loaded like this:
  *
  *  <PRE>
		tavern
		cd ~here
		load talker.objects.SlotMachine machine
		edit machine.read
		.wipe
		A slot machine.  Type "use machine" to play.
		.end
		set machine.description "A slot machine, complete with flashing lights"
		set machine.fullportrait "the slot machine"
		cd machine
		deny move
  *  </PRE>
  *  <P>
  *  Notice the deny move, to stop people moving it around. :)
 */
 
 /*	TODO:
  *            room or global? broadcast on Jackpot
 */
public class SlotMachine extends Readable
{
	private static final long serialVersionUID = 224926487784024762L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( SlotMachine.class, Integer.TYPE, "profit",
			AtomicElement.PUBLIC_FIELD,
			"the amount of profit this machine has made" ),
		AtomicElement.construct( SlotMachine.class, Integer.TYPE, "cost",
			AtomicElement.PUBLIC_FIELD,
			"the amount it costs to use" ),
		AtomicElement.construct( SlotMachine.class, Integer.TYPE, "scale",
			AtomicElement.PUBLIC_FIELD,
			"how even the odds are per slotpic" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Readable.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public int profit = 0;
	public int cost = 1;
	public int scale = 4;
	
  	public static final String[] slotpic = { 
		"   ^Y$$$^-  ",
  		"^Cbedibeda^-", 
  		"  ^rfnord^- ",
  	 	" ^Bsnapper^-", 
  	 	"  ^cwoof^-  ", 
  	 	"   ^RKey^-  ",
		" ^Mfnarg!^- ",
		" ^GFOREST^- ",
		 };
	
	public SlotMachine()
	{
		setKey( "machine" );
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
		
			//  when a class is loaded & the object already exists,
			//  the constructor is never called.  So, we have to initialise it
			//  by hand in this case.
		if( cost == 0 )
			cost = 1;
		if( scale == 0 )
			scale = 4;
	}
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
			//  P.Mclachlan: Not needed
		//if( ! p.getLocation().getContainedId().equalsIgnoreCase("city/tavern") )
		//{
			//ic.sendFailure("You must be in the tavern to play Slots.");
			//return;
		//}
		
		if( p.getFlorins() < (cost+1) )
		{
			if( p.getFlorins()-cost == 0)
			{
				ic.sendFailure("You cant spend your last " + (( cost == 1 ) ? "florin" : (cost + " florins") ) + " on Slots. (Rainy Day Clause)");
				return;
			}
			
			ic.sendFailure("You cant afford to play Slots.");
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("\t");
		
		int[] wheel = new int[3];
		
		for(int i=0;i<3;i++)
		{
			float seed = (float) scale/10 + 1;
			
			int spinnum = (int) ( Math.random() * Math.pow(8,seed) ); // 0...8^seed
			
			wheel[i] = (int) Math.pow(spinnum,(1/seed)); 		
			
			sb.append( "\t[ " + (String) slotpic[ wheel[i] ] + " ]" );
			
		}
		ic.blankLine();
		ic.send( sb.toString() );
		ic.blankLine();
		
		p.subtractFlorins(cost); // money well spent?
		profit += cost;
		
		int win;
		
		// calculate results
		if( (wheel[0] == wheel[1]) || (wheel[0] == wheel[2]) || (wheel[1] == wheel[2]) ) {
				
			if( (wheel[0] == wheel[1]) && (wheel[1] == wheel[2]) ) {
				// 3 of a kind
				if(wheel[0]==0) win = 250; // Jackpot!! @@ 1/263406 chance
				else if(wheel[0]==1) win = 10;  //less then slot2 , as 2 slot2's also pays @@ 1/9693
				else if(wheel[0]==2) win = 50; // @@ 1/2097
				else win = 5;
				// if jackpot , global broadcast %o wins!
				
				win = win*cost;
				
				ic.beep();
				ic.send("CHA-Ching!!  3 " + slotpic[wheel[0]] + "'s. You win " + win + " silver florins.");
				
				p.subtractFlorins(-win);
				profit -= win;
			}
			else {
				// 2 of a kind
				int winningwheel;
				if( (wheel[0]==wheel[1]) || (wheel[0]==wheel[2]) ) winningwheel = 0; //first wheel is doubled
				else winningwheel = 1; // the second and third wheels
				
				if( wheel[winningwheel]==0 ) win = 20;
				else if( wheel[winningwheel]==1 ) win = 10;
				else win = 1;
				
				win = win*cost;
				
				if(win==1)
					ic.send( "CHA-Ching!!  2 " + slotpic[wheel[winningwheel]] + "'s.  You break even."  );
				else
					ic.send( "CHA-Ching!!  2 " + slotpic[wheel[winningwheel]] + "'s.  You win " + win + " silver florins." );
				
				p.subtractFlorins(-win);
				profit -= win;
			}
		}
		else
		{
			// loser
			ic.send("You lose " + cost + " silver florin" + (( cost != 1 ) ? "s." : ".") );
		}
	}
}
