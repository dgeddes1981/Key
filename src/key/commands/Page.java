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
**  $Id: Page.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  22Jun97    subtle       made actions static
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.*;
import java.util.StringTokenizer;

public class Page extends Command
{
private static final long serialVersionUID = 1362596440273901280L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Page.class, String.class, "send",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the target" ),
		AtomicElement.construct( Page.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the feedback on success" ),
		AtomicElement.construct( Page.class, Boolean.TYPE, "enforceMessage",
			AtomicElement.PUBLIC_FIELD,
			"if true, a message must be supplied" ),
		AtomicElement.construct( Page.class, Duration.class, "pause",
			AtomicElement.PUBLIC_FIELD,
			"the cool-off delay between pages" )
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char originatorCode = 'o';
	public static final char targetCode = 't';
	public static final char messageCode = 'm';

		//  used for cookies: static means 'wake' and 'page' use the same one
	private static Object cookie = new Object();
	
	public String send = "A pageboy taps you on the shoulder and says '%o asked me to tell you: \"%m\"'";
	public String feedback = "You page %t";
	public boolean enforceMessage = true;
	public Duration pause = new Duration( 120000 );
	
	public Page()
	{
		setKey( "page" );
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
			targetPlayer = (Player) getOnlinePlayer( p, ic, playerName );
			
			if( targetPlayer == p )
			{
				ic.sendFeedback( "The page boy looks at you a little strangely, mutters something to himself, and returns to other duties." );
				return;
			}
			else if( targetPlayer == null )
				return;
			
			if( args.hasMoreTokens() != enforceMessage )
			{
				usage( ic );
				return;
			}
			
			if( pause.getTime() > 0 )
			{
				Object c = p.getCookie( cookie );
				
				if( c != null )
				{
					DateTime d = (DateTime)c;
					DateTime now = new DateTime();
					
					if( d.after( now ) )
					{
						Duration dur = now.difference( d );
						ic.sendFeedback( "The pageboy is still recovering from your last request.  Give him another " + dur.toString() + "." );
						return;
					}
				}
				
				DateTime dt = DateTime.nowPlus( pause );
				p.setCookie( cookie, dt );
			}
			
				// construct message
			String m;
			String f;
			
			p.putCode( originatorCode, p.getName() );
			p.putCode( targetCode, targetPlayer.getName() );
			
			if( args.hasMoreTokens() )
				p.putCode( messageCode, args.nextToken( "" ) );
			
			m = Grammar.substitute( send, p.getCodes() );
			f = Grammar.substitute( feedback, p.getCodes() );
			
				//  send the splash	
			(new key.effect.Page( p, targetPlayer, f, m ) ).cause();
		}
		else
			usage( ic );
	}
}
