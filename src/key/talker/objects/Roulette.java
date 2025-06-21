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
**  4Aug99      Noble        created
**
*/

package key.talker.objects;

import key.*;
import java.io.IOException;

import java.io.*;
import java.util.StringTokenizer;

/**
  *  The Roulette Wheel, as originally coded by Noble.
  *
  *  <PRE>
		tavern
		cd ~here
		load talker.objects.Roulette wheel
		edit wheel.read
		.wipe
		                             COLUMN		
		       1   2   3    4    5    6    7    8    9   10   11   12
		 		
		^n   1   ^r1   ^D4   ^r7   ^D10   ^D13   ^r16   ^r19   ^D22   ^r25   ^D28   ^D31   ^r34
		^n R
		^n O 2   ^D2   ^r5   ^D8   ^D11   ^r14   ^D17   ^D20   ^r23   ^D26   ^D29   ^r32   ^D35
		^n W
		^n   3   ^r3   ^D6   ^r9   ^r12   ^D15   ^r18   ^r21   ^D24   ^r27   ^r30   ^D33   ^r36
		
		                             ^g0   ^g00
		.centrealign
		.end
		set wheel.description "A roulette wheel, complete with ivory ball"
		set wheel.fullportrait "the roulette wheel"
		cd wheel
		deny move
  *  </PRE>
  *  <P>
 */
 
 /*	TODO:
  *            choose amount to bet , too many command args?
 */
public class Roulette extends Readable
{
	private static final long serialVersionUID = -4483182434623978682L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Roulette.class, Integer.TYPE, "profit",
			AtomicElement.PUBLIC_FIELD,
			"the amount of profit this wheel has made" ),
		AtomicElement.construct( Roulette.class, Integer.TYPE, "cost",
			AtomicElement.PUBLIC_FIELD,
			"the amount it costs to use" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Readable.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public int profit = 0;
	public int cost = 1;
	
	public static final int[] red = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
	
	
	public Roulette()
	{
		setKey( "wheel" );
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
		
		if( cost == 0 )
			cost = 1;
	}
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( args.hasMoreTokens() )
		{
			String bet = args.nextToken();
			
			if( p.getFlorins() < 2 )
			{
				if( p.getFlorins() == 1)
				{
					ic.sendFailure("You cant spend your last florin on Roulette. (Rainy Day Clause)");
					return;
				}
				
				ic.sendFailure("You cant afford to play Roulette.");
				return;
			}
			
			int win = 0;
			int x;
			
			if(bet.equalsIgnoreCase("high"))
			{
				x = Spin(p,ic);
				if( x>17 && x!=37) win = 1;
			}
			else if(bet.equalsIgnoreCase("low"))
			{
				x = Spin(p,ic);
				if( x<18 && x!=0) win = 1;
			}
			else if(bet.equalsIgnoreCase("red"))
			{
				if( isRed( Spin(p,ic) ) ) win = 1;
			}
			else if(bet.equalsIgnoreCase("black"))
			{
				if( isBlack( Spin(p,ic) ) ) win = 1;
			}
			else if(bet.equalsIgnoreCase("odd"))
			{
				x = Spin(p,ic);
				if( x%2 == 1 && x!=37 ) win = 1;
			}
			else if(bet.equalsIgnoreCase("even"))
			{
				x = Spin(p,ic);
				if( x%2 == 0 && x!=0 ) win = 1;
			}
			else if(bet.equalsIgnoreCase("corner"))
			{
				x = Spin(p,ic);
				if( x==1 || x==3 || x==34 || x==36) win = 8;
			}
			else if(bet.equalsIgnoreCase("col"))
			{
				// check next arg
				if( !args.hasMoreTokens() ) {
					ic.sendFailure( "You must state what column you are betting on." );
					return;
				}
				try
				{
					x = Integer.parseInt( args.nextToken() );
				}
				catch( NumberFormatException e )
				{
					x = 0;
				}
				if(x<1 || x>3) {
					ic.sendFailure( "That is not a valid column." );
					return;
				}
				
				if( getColumn( Spin(p,ic) )==getColumn( x ) ) win = 11;
								
			}
			else if(bet.equalsIgnoreCase("row"))
			{
				// check next arg
				if( !args.hasMoreTokens() ) {
					ic.sendFailure( "You must state what row you are betting on." );
					return;
				}
				try
				{
					x = Integer.parseInt( args.nextToken() );
				}
				catch( NumberFormatException e )
				{
					x = 0;
				}
				if(x<1 || x>12) {
					ic.sendFailure( "That is not a valid row." );
					return;
				}
				
				if( getColumn( Spin(p,ic) )==getColumn( x ) ) win = 2;
								
			}
			else // number
			{
				try
				{
					x = Integer.parseInt( bet );
				}
				catch( NumberFormatException e )
				{
					x = 0;
				}
				if(x<1 || x>36) {
					ic.sendFailure( "That is not a valid bet." );
					return;
				}

				if(x == Spin(p,ic)) win = 35;
				
			}

			if( win > 0 ) {
				
				win = win*cost;  
					
				ic.send( "WINNER!! You ^hwin^- " + win + " silver florin" + (( win > 1 ) ? "s." : "."));
				
				p.subtractFlorins(-(win+cost)); // + cost is your money back
				profit -= (win+cost);
			}
			else
			{ 
				// loser
				ic.send("You ^hlose^- " + cost + " silver florin" + (( cost != 1 ) ? "s." : ".") );
			}
		}
		else
			ic.sendFailure( "You must state what you are betting on." );
	}
	
	int Spin( Player p , InteractiveConnection ic )
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t\t");
		
		int spinnum = (int) (Math.random() * 38); // 0..37
		
		String colour = null;
		
		if(spinnum==0 || spinnum==37)
			colour = "^gGREEN  ";
		else if( isRed(spinnum) )
			colour = "^R RED   ";
		     else colour = "^hBLACK  ";
		
		sb.append( "( " + colour +  (( spinnum == 37 ) ? "00" : ("" + spinnum)) + "^- )" );
			
		ic.blankLine();
		ic.send( sb.toString() );
		ic.blankLine();
		
		p.subtractFlorins(cost); // no whammies! NO whammies!
		profit += cost;
		
		return spinnum;
	}
	
	boolean isRed( int x)
	{
		for(int y=0;y<18;y++) { // if red.contains(x)
			if(red[y] == x) return true;
		}
		return false;
	}
	
	boolean isBlack( int x)
	{
		if( !isRed(x) && x!=0 && x!=37) return true;
		else return false;
	}
	
	int getRow( int x )
	{
		return (x%3);
	}
	
	int getColumn( int x )
	{
		return ( (x/3) +1 );
	}
}
