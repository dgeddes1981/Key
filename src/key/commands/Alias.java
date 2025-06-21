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
** $Log: Alias.java,v $
** Revision 1.4  2000/07/12 16:23:02  pdm
** syntax error on 87 fixed
**
** Revision 1.3  2000/07/11 13:52:24  noble
** %m now substitutes all args
**
** Revision 1.2  2000/05/02 14:25:11  noble
** add positional args (note: comments need to be updated to reflect this)
**
*/


package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

/**
  *  This command implements *basic* alias handling.  Ultimately
  *  I'd like shell style substitution, with %1, %2 and all of
  *  those handled correctly to remap badly placed words and
  *  so forth.
  *  <p>
  *  Maybe consider command combinations, too?  Or the ability
  *  to put inline \n's somehow...
  *  <p>
  *  Going any *further* than that very basic, simple script
  *  requires java code, of course.
 */
public class Alias extends Command
{
	private static final long serialVersionUID = -9110932562654654779L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Alias.class, String.class, "command",
			AtomicElement.PUBLIC_FIELD,
			"the command to be executed by the alias (use %1->%12 for positioned args,%m for all args) (can be pipe seperated)" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char messageCode = 'm';
	
	public String command = "";
	
	public Alias()
	{
		setKey( "alias" );
		usage = "";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getWhichId()
	{
		return( getId() + " (command alias for '" + command + "')" );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( command == null || command.length() == 0 )
		{
			ic.sendError( "This alias is incorrectly set up" );
			return;
		}
		
		String[] codes = new String[13];	// a blank set of codes - using Player.codes would
							// mean having to wipe the previous contents each time
							// in case of less args then required substitutions  
				
		if( args.hasMoreTokens() )
		{
			int code = 0;
			String allArgs = "";
	
			try
			{
				while( args.hasMoreTokens() && ( code < 12 ) )
				{
					String arg = args.nextToken();
					codes[ code ] = arg;
					
					allArgs = allArgs + " " + arg;
					
					code++;
				}
				
				codes[ messageCode - 'a' ] = allArgs; // for backwards compatibility with %m
			}
			catch( Exception e )
			{
				e.printStackTrace();
				Log.error( "During Alias::putCode()", e );
			}
		}		
	
		StringTokenizer st = new StringTokenizer( command, "|" );
		
		while( st.hasMoreTokens() )
			p.command( Grammar.substitute( st.nextToken(), codes ), ic, false );
	}
}
