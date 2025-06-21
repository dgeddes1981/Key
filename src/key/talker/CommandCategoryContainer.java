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

public class CommandCategoryContainer extends CommandCategory
{
	private static final long serialVersionUID = 7497991691973702663L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( CommandCategoryContainer.class, CommandList.class, "commands",
			AtomicElement.PUBLIC_FIELD,
			"the sub-commands for this command" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	public final CommandList commands = (CommandList) Factory.makeAtom( CommandList.class, "commands" );
	
	public CommandCategoryContainer()
	{
	}
	
	public final CommandList getCommandList()
	{
		return( commands );
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
			
			return( (Command) commands.getExactElement( command ) );
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
					match = (Commandable) commands.getExactElement( command );
				}
				
				public String getErrorString()
				{
					return( "No such command '" + command + "' in " + CommandCategoryContainer.this.getName() );
				}
			} );
		}
		else
			return( super.getFinalMatch( p, st ) );
	}
}
