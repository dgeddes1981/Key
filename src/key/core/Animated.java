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
**  $Id: Animated.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/


package key;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

/**
  *  This is a subclass of thread that
  *  adds functionality to work out what,
  *  exactly, is running at the moment.
 */
public class Animated extends Thread
{
	protected Animate use;
	protected Runnable code;
	
	public Animated( Animate using )
	{
		super( using );
		
		use = using;
		code = using;
	}
	
	public void run()
	{
		if( code != null )
			code.run();
	}
	
	protected void useAnimate( Animate u )
	{
		use = u;
		
		if( code == null && !isAlive() )
			code = u;
	}

	protected void useCode( Runnable c )
	{
		if( code == null && !isAlive() )
			code = c;
		else
			throw new UnexpectedResult( "useCode() called on running (or pre-used) thread" );
	}
	
	public Animate is()
	{
		return( use );
	}
}
