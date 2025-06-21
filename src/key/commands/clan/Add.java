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
**  Class: add
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  14Jul97     snapper      creation
**  20Jul97     snapper      added logging
**  21Jul97     subtle       added limit exception catching
*/

package key.commands.clan;

import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

import java.util.Enumeration;

public class Add extends Command
{
	public Add()
	{
		setKey( "add" );
		usage = "<command> [<alternate name>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
			// if they supply any arguments.
		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
				// get the current context, off the player, it should be a rank
				// so a command can be created within.  If not, spit.
			Object o = p.getContext();
			if( o != null )
			{
					// is the current context a rank?
				if( o instanceof Rank )
				{
						// the token they supply is the command they wish
						// to insert into the rank!
					String commandName = args.nextToken();
					Container l = (Container)o;
					String newId = "";
					try
					{
							// create the new command
						Atom r = (Atom) Factory.makeAtom( Class.forName( "key.commands.clan." + commandName ) );
						
						if( args.hasMoreTokens() )
							newId = (String) args.nextToken();
						else
							newId = commandName.toLowerCase();
						
						r.setKey( newId );
						
						Rank rank = (Rank) o;
						CommandList cl = (CommandList) new Search( ".commands/clan.commands", rank ).result;
						if( cl == null )
						{
							ic.sendFailure( "Could not find clan command in rank.  Please re-run 'create rank' for this rank, it should help.  If you continue to have problems, contact a mage." );
							
							return;
						}
						
						cl.add( r );
						
						String out = Type.typeOf( r ).getName() + " '" + r.getName() +"' to " + l.getId();
						ic.sendFeedback( "Added new " + out );
						
							//logging
						Log.log( "clans/" + p.getClan().getName() + ".notes", "'" + p.getName() + "' added command '" + out); 
					}
					catch( ClassNotFoundException e )
					{
						ic.sendError( "Could not find that clan command (don't forget to capitalise the first letter!)" );
					}
					catch( BadKeyException e )
					{
						ic.sendError( "'" + commandName + "' should contain only alphabetic characters" );
					}
					catch( NonUniqueKeyException e )
					{
						ic.sendError( "There is already an atom with this identifier ('" + newId + "')" );
					}
					catch( LimitExceededException e )
					{
						ic.sendError( "There is actually a limit on the number of ranks you can have, and this would be one too many." );
					}
				}
				else
					ic.sendError( "You are not referencing a rank.  Use: clan rank <rank> first!" );
			}
			else
				ic.sendError( "Could not find the rank?" );
		}
	}
}
