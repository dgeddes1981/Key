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

package key.commands;

import key.*;
import java.io.IOException;

import java.util.StringTokenizer;

public class ContextChanger implements Commandable
{
	Commandable c;
	Atom ctxt;
	
	public ContextChanger( Commandable f, Atom context )
	{
		c = f;
		ctxt = context;
	}
	
	/**
	  *  Changes the players context.  The context is *always* specified
	  *  as an offset from the player class - regardless of what your current
	  *  reference is.  This is to stop you getting lost ;)
	 */
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom old = p.getContext();
		p.setContext( ctxt );
		c.run( p, args, fullLine, caller, ic, flags );
		p.setContext( old );
	}
	
	public boolean isDisabled()
	{
		return( c.isDisabled() );
	}
	
	public void disable()
	{
		c.disable();
	}
	
	public void usage( InteractiveConnection ic )
	{
		c.usage( ic );
	}
	
	public void usage( InteractiveConnection ic, CategoryCommand caller )
	{
		c.usage( ic, caller );
	}
	
	public String getWhichId()
	{
		return( c.getWhichId() );
	}
	
	public String getUsage()
	{
		return( c.getUsage() );
	}
	
	public Commandable getMatch( final Player p, key.util.StringTokenizer st )
	{
		return( this );
	}

	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st )
	{
		return( new Command.Match()
		{
			{
				match = ContextChanger.this;
			}
			
			public String getErrorString()
			{
				return( "" );
			}
		} );
	}
	
	public boolean recloneArgs()
	{
		return( c.recloneArgs() );
	}
}
