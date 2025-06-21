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
**  $Id: Effect.java,v 1.1 1999/10/11 13:25:05 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.StringTokenizer;

/**
  *  Represents the 'effect' of an action.  Don't ever store
  *  an effect - it should be a one shot object only.  If you
  *  *want* to store it, be sure to change the originator
  *  reference to a Tag, yes?
 */
public abstract class Effect
{
	protected Player originator;

	/**
	  *  The last atom this effect went to - you
	  *  should set it before resplashing any
	  *  effects.
	 */
	protected Atom resplasher;
	
	public Effect( Player by )
	{
		originator = by;
		resplasher = originator;
	}
	
	public Player getOriginator()
	{
		return( originator );
	}
	
	public abstract void cause();
	
	public Atom getSplasher()
	{
		return( resplasher );
	}
	
	public void setSplasher( Atom t )
	{
			//  setting the splasher to null needs to work
			//  for global events.
		//if( t != null )
			resplasher = t;
		//else
			//throw new UnexpectedResult( "attempting to set an effects resplasher to null" );
	}
	
	/**
	  *  Called just before the effect is actually output to
	  *  a players terminal.
	 */
	public void sending( String message, Player p )
	{
	}
	
	public abstract String getMessage( Player reciever );
}
