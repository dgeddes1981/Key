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
**  $id$
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  25Jul97     snapper      creation of command
**
*/

package key.commands;
import key.*;
import java.io.*;
import java.util.StringTokenizer;

public class Warn extends Command
{
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Warn.class, String.class, "defaultWarn",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the warned player" ),
		AtomicElement.construct( Warn.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the superuser" ),
		AtomicElement.construct( Warn.class, String.class, "splashback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the channel" ),
		AtomicElement.construct( Warn.class, Scape.class, "splashTo",
			AtomicElement.PUBLIC_FIELD,
			"the channel" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char originatorCode = 'o';
	public static final char targetCode = 't';
	public static final char messageCode = 'm';
	
	public String defaultWarn = "--\n\007You have conducted yourself in a way unbecoming to that detailed in: help law.\n\nYou have been cautioned for '%m'\n\nPlease abide by the Law in future.\n--\n";
	public String feedback = "You warn %t for '%m'";
	public String splashback = "%o warns %t for '%m' ";
	public Reference splashTo = Reference.EMPTY;
	
	public Warn()
	{
		setKey( "warn" );
		usage = "<name> [<message>]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			Player targetPlayer;
			
			String playerName = args.nextToken();
			
				//  get the target player if they are online
			targetPlayer = (Player)getOnlinePlayer( p, ic, playerName );
			
			if( targetPlayer == p )
			{
				ic.sendFeedback( "The voices, the voices, oh the voices!" );
				return;
			}
			else if( targetPlayer == null )
				return;
			

			if( !args.hasMoreTokens()  )
			{
				usage( ic );
				return;
			}
			
				// rock and roll lets warn!!
				// construct message
			String w,f,s;
			
				// insert them codes!
			p.putCode( originatorCode, p.getName() );
			p.putCode( targetCode, targetPlayer.getName() );
			p.putCode( messageCode, args.nextToken( "" ) );
			
			w = Grammar.substitute( defaultWarn, p.getCodes() );
			f = Grammar.substitute( feedback, p.getCodes() );
			s = Grammar.substitute( splashback, p.getCodes() );
			
				//  send the splash	
			(new key.effect.PrivilegedAction( p, targetPlayer, f, w, s, (Scape) splashTo.get() ) ).cause();
			Log.log( "warn", s );
		}
		else
			usage( ic );
	}
}
