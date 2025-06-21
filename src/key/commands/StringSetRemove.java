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
**  16Jul97     merlin       created this command
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
  *  This command is intended to return the context of the player
  *  back to the player's own context - the player itself.    
 */
public class StringSetRemove extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( StringSetRemove.class, StringSet.class, "stringSet",
			AtomicElement.PUBLIC_FIELD,
			"the stringset the modify" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Reference stringSet = Reference.EMPTY;
	
	public StringSetRemove()
	{
		setKey( "stringsetremove" );
		usage = "<name>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		StringSet names = (StringSet) stringSet.get();
		
		if( names == null )
		{
			ic.sendError( "No string set" );
			return;
		}
		
		String name = args.nextToken( "" );
		
		try
		{
			names.removeString( name );
		}
		catch( Exception e )
		{
			ic.sendFailure( "Could not remove '" + name + "' from the set." );
		}
		
			//  name added successfully
		ic.sendFeedback( name + " added to the set." );
	}
}
