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
import java.util.Vector;
import java.util.Enumeration;
import java.io.IOException;

public class CommandCategoryReferenceContainer extends CommandCategory
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( CommandCategoryReferenceContainer.class, CommandList.class, "commands",
			AtomicElement.PUBLIC_FIELD,
			"the sub-commands for this command" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Reference commands = Reference.EMPTY;
	
	public CommandCategoryReferenceContainer()
	{
	}
	
	public final CommandList getCommandList()
	{
		try
		{
			return( (CommandList) commands.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			commands = Reference.EMPTY;
			return( null );
		}
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	/**
	  * @see key.Command.getMatch
	 */
	public Commandable getMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			String command = st.nextToken();
			
			return( (Command) getCommandList().getExactElement( command ) );
		}
		else
			return( this );
	}
	
	public Command.Match getFinalMatch( final Player p, key.util.StringTokenizer st )
	{
		if( st.hasMoreTokens() )
		{
			final String command = st.nextToken();
			
			return( new Command.Match()
			{
				{
					match = (Commandable) getCommandList().getExactElement( command );
				}
				
				public String getErrorString()
				{
					return( "No such command '" + command + "' in " + CommandCategoryReferenceContainer.this.getName() );
				}
			} );
		}
		else
			return( super.getFinalMatch( p, st ) );
		
	}
}
