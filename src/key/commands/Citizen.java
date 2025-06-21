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
**  $Id: Citizen.java,v 1.4 2000/05/07 07:00:44 noble Exp $
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
import key.primitive.*;
import java.io.*;
import java.util.StringTokenizer;

public class Citizen extends Command
{
private static final long serialVersionUID = -5654733332853078867L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Citizen.class, String.class, "message",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the citizening worked" ),
		AtomicElement.construct( Citizen.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the superuser" ),
		AtomicElement.construct( Citizen.class, String.class, "splashback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the SU channel" ),
		AtomicElement.construct( Citizen.class, Scape.class, "splashTo",
			AtomicElement.PUBLIC_FIELD,
			"the SU channel to send the message to" ),
		AtomicElement.construct( Citizen.class, String.class, "revokeMessage",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player when the revoking worked" ),
		AtomicElement.construct( Citizen.class, String.class, "revokeFeedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the revoker" ),
		AtomicElement.construct( Citizen.class, String.class, "revokeSplashback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the channel" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char originatorCode = 'o';
	public static final char targetCode = 't';

	public String message = "--\n%o has made you a citizen!\n--";
	public String feedback = "You grant citizenship to %t.";
	public String splashback =  "%o grants citizenship to %t.";
	public String revokeMessage = "--\n%o has revoked your citizenship!\n--";
	public String revokeFeedback = "You revoke citizenship from %t.";
	public String revokeSplashback =  "%o revoke citizenship from %t.";
	public Reference splashTo = Reference.EMPTY;
	
	public Citizen()
	{
		setKey( "citizen" );
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
			targetPlayer = (Player) getOnlinePlayer( p, ic, playerName );

			String or = null;
			
			if( args.hasMoreTokens() )
				or = args.nextToken();
			
			if( targetPlayer == null )
				return;
			
				// check to see if they're in the rank
			if( targetPlayer.isCitizen() )
			{
				if( (or != null) && or.equalsIgnoreCase( "revoke" ) )
				{
					targetPlayer.setCitizen( false );
					p.putCode( originatorCode, p.getName() );
					p.putCode( targetCode, targetPlayer.getName() );
					
					String m = Grammar.substitute( message, p.getCodes() );
					String f = Grammar.substitute( feedback, p.getCodes() );
					String s = Grammar.substitute( splashback, p.getCodes() );
					
					(new key.effect.PrivilegedAction( p, targetPlayer, f, m, s, getSplashTo() ) ).cause();
					Log.log( "citizen", "REVOKED: " + s );
					return;
				}
				
				ic.sendError( targetPlayer.getName() + " is already a citizen. (use 'citizen <name> revoke' to take it away)" );
				return;
			}
			
			Duration dur = (Duration) targetPlayer.loginStats.getTotalConnectionTime();
			
			int login = (int)(dur.getTime() / Duration.SIH);
			
			if( login < 16 )
			{
				if( or == null || !or.equalsIgnoreCase( "override" ) )
				{
					ic.sendFeedback( "They require more than 16 hours login time to be a citizen.  (Use 'citizen <name> override' to override)" );
					return;
				}
			}
			
			targetPlayer.setCitizen( true );
			
			p.putCode( originatorCode, p.getName() );
			p.putCode( targetCode, targetPlayer.getName() );
			
			String m = Grammar.substitute( message, p.getCodes() );
			String f = Grammar.substitute( feedback, p.getCodes() );
			String s = Grammar.substitute( splashback, p.getCodes() );
			
			(new key.effect.PrivilegedAction( p, targetPlayer, f, m, s, getSplashTo() ) ).cause();
			Log.log( "citizen", s );
		}
		else
			usage( ic );
	}

	private final Scape getSplashTo()
	{
		try
		{
			return( (Scape) splashTo.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			splashTo = Reference.EMPTY;
			return( null );
		}
	}
}
