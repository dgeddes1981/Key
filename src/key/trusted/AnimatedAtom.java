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
**  $Id: AnimatedAtom.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

/**
  * Is basically an animated atom ;)
 */
public abstract class AnimatedAtom extends Atom implements Animate
{
	private static final long serialVersionUID = 4049403736574501121L;
	transient Thread animated;
	boolean daemon=false;
	
	public AnimatedAtom()
	{
	}
	
	public AnimatedAtom( boolean isDaemon )
	{
		this();
		
		daemon = isDaemon;
	}
	
	public void interrupt()
	{
		if( animated != null && animated.isAlive() )
			animated.interrupt();
	}
	
	public abstract void run();
	
	public void start()
	{
		if( animated != null && animated.isAlive() )
		{
			animated.stop();
		}
		
		animated = new Animated( this );
		animated.setDaemon( daemon );
		animated.setName( getName() );
		animated.start();
	}
	
	public void stop()
	{
		if( animated != null )
		{
			animated.stop();
			
			try
			{
				animated.join();
			}
			catch( InterruptedException e )
			{
			}
			
			animated = null;
		}
	}
	
	public boolean isAlive()
	{
		if( animated == null )
			return( false );
		else
			return( animated.isAlive() );
	}
	
	boolean canSwap()
	{
		return( !isAlive() );
	}
	
	public final boolean isRunning()
	{
		return( Thread.currentThread() == animated );
	}
	
	public final void yield()
	{
		animated.yield();
	}
	
	public final boolean isDaemon()
	{
		return( animated.isDaemon() );
	}
	
	public final void setPriority( int pri )
	{
		animated.setPriority( pri );
	}
	
	public final int getPriority()
	{
		return( animated.getPriority() );
	}
	
	void setKey_imp( Object newkey )
	{
		super.setKey_imp( newkey );
		
		if( animated != null && newkey instanceof String )
			animated.setName( (String) newkey );
	}
}
