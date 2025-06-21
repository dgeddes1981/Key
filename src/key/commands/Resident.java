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
**  Class: resident
**
**  Class History
**
**  Date        Name         description
**  ---------|------------|-----------------------------------------------
**  26Jul97     snapper      creation of the command
**  29Jul97     subtle       removed use of ranks
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

public class Resident extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Resident.class, String.class, "message",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player on success" ),
		AtomicElement.construct( Resident.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the superuser on success" ),
		AtomicElement.construct( Resident.class, String.class, "splashback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the channel on success" ),
		AtomicElement.construct( Resident.class, Scape.class, "splashTo",
			AtomicElement.PUBLIC_FIELD,
			"the channel notified on success" ),
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char originatorCode = 'o';
	public static final char targetCode = 't';
	
	public String message = "--\n%o has made you a resident!\nTo complete this action, you will need to use the register command.\n  Type: register\n--";
	public String feedback = "You grant residency to %t.";
	public String splashback = "%o grants residency to %t.";
	public Reference splashTo = Reference.EMPTY;
	
	public Resident()
	{
		setKey( "resident" );
		usage = "<player>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String playerName;
		Player targetPlayer;

		if( args.hasMoreTokens() )
		{
			playerName = args.nextToken();
			targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
			
			if( targetPlayer == null )
				return;
			
				// check to see if they're in the rank
			if( targetPlayer.getCanSave() )
			{
				ic.sendError( targetPlayer.getName() + " is already a resident." );
				return;
			}
			
			targetPlayer.setProperty( "canSave", Boolean.TRUE );
			
			String m;
			String f;
			String s;
			
			  // insert them codes!
			p.putCode( originatorCode, p.getName() );
			p.putCode( targetCode, targetPlayer.getName() );

			m = Grammar.substitute( message, p.getCodes() );
			f = Grammar.substitute( feedback, p.getCodes() );
			s = Grammar.substitute( splashback, p.getCodes() );
			
			(new key.effect.PrivilegedAction( p, targetPlayer, f, m, s, (Scape) splashTo.get() ) ).cause();
			Log.log( "resident", s );
		}
		else
			usage( ic );
	}
}
