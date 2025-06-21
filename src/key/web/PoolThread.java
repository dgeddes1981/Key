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

package key.web;

import key.Animate;
import key.Animated;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

/**
  *  This is a subclass of thread that
  *  adds functionality to work out what,
  *  exactly, is running at the moment.
 */
public final class PoolThread extends Thread
{
	private Runnable code;
	key.Player player;
	
	public PoolThread()
	{
		super();
	}
	
	public PoolThread( Runnable r )
	{
		use( r );
	}
	
	public void use( Runnable r )
	{
		code = r;
	}
	
	void setPlayer( key.Player p )
	{
		player = p;
	}
	
	public void interrupt()
	{
	}
	
	public key.Player getPlayer()
	{
		return( player );
	}
	
	public void run()
	{
		code.run();
	}
}
