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
**  $Id: DirectedBroadcast.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  05May99    symstrotic   added 'noMsgNeeded' property
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

public class DirectedBroadcast extends Broadcast implements Repeatable
{
	private static final long serialVersionUID = -1232110729709613305L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( DirectedBroadcast.class, String.class, "direct",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person" ),
		AtomicElement.construct( DirectedBroadcast.class, String.class, "directQuestion",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person if the string ends with a ?" ),
		AtomicElement.construct( DirectedBroadcast.class, String.class, "directExclaim",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person if the string ends with a !" ),
		AtomicElement.construct( DirectedBroadcast.class, String.class, "repeatUsage",
			AtomicElement.PUBLIC_FIELD,
			"the usage string when used with a repeat" ),
		AtomicElement.construct( DirectedBroadcast.class, String.class, "spoonClause",
			AtomicElement.PUBLIC_FIELD,
			"the string sent if you start talking yourself" ),
		AtomicElement.construct( DirectedBroadcast.class, Boolean.TYPE, "noMsgNeeded",
			AtomicElement.PUBLIC_FIELD,
			"Does this require a message?" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Broadcast.STRUCTURE, ELEMENTS );
	
	public static final char targetCode = 't';
	
	public String direct = "";
	public String directQuestion = null;
	public String directExclaim = null;
	
	public String repeatUsage = "<name>";
	public String spoonClause = "They say talking to yourself is the first sign of madness";

	public boolean noMsgNeeded = false;

	public DirectedBroadcast()
	{
		setKey( "dbroad" );
		
		if( noMsgNeeded == true )
			usage = "<name>";
		else
			usage = "<name> <message>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	/**
	  *  Used mainly so that whisper can override it
	  *  to make it the players location - might
	  *  speed up matching a little
	 */
	protected Scape limitation( Player p )
	{
		return( Key.instance() );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String name = args.nextToken();
			
			if( args.hasMoreTokens() )
			{
				String message = args.nextToken( "" ).trim();
				
				p.getRepeated().set( message, this );
				
				Splashable t = getPlayerInside( p, ic, name, limitation( p ) );
				if( t == p )
					ic.sendFeedback( spoonClause );
				else if( t == null )
					;
				else
					execute( p, message, t );

				return;
			}
			else if( noMsgNeeded == true )
			{
				Splashable t = getPlayerInside( p, ic, name, limitation( p ) );
				if( t == p )
					ic.sendFeedback( spoonClause );
				else if( t == null )
					;
				else
					execute( p, "", t );

				return;
			}
		}
		
		usage( ic );
	}
	
	protected void execute( Player p, String message, Splashable t )
	{
		String main;
		String self;
		String other;
		Scape to = getTo( p );
		
			//  getMessage() in the effect will actually
			//  do this over and over again to replace
			//  each players name with 'you'.  Here we
			//  do them all (no 'you's) to output to
			//  the scape as well as to the originator.
			//  (saves the effect doing it 20 odd times)

		if( t instanceof Scape )
			p.putCode( targetCode, ((Scape)t).allNames() );
		else
			p.putCode( targetCode, ((Player)t).getName() );
		
		p.putCode( originatorCode, p.getName() );
	
		/* 
		 * We don't want to consider the last message, so we don't
		 * want to get null warnings ;-) 
		 */

		if ( noMsgNeeded == false )
		{
			p.putCode( messageCode, message );
			
			if( message.charAt( 0 ) == '\'' )
				p.putCode( sometimesSpaceCode, "" );
			else
				p.putCode( sometimesSpaceCode, " " );
			
			if( message.endsWith( "?" ) )
			{
				main = broadcastQuestion != null ? broadcastQuestion : broadcast;
				other = directQuestion != null ? directQuestion : direct;
				self = feedbackQuestion != null ? feedbackQuestion : feedback;
			}
			else if( message.endsWith( "!" ) )
			{
				main = broadcastExclaim != null ? broadcastExclaim : broadcast;
				other = directExclaim != null ? directExclaim : direct;
				self = feedbackExclaim != null ? feedbackExclaim : feedback;
			}
			else
			{
				main = broadcast;
				other = direct;
				self = feedback;
			}
		}
		else
		{
			main = broadcast;
			other = direct;
			self = feedback;
		}
		
			//  put in the codes we can (this allows us
			//  to optimise the getMessage() in the effect
			//  somewhat
		main = Grammar.substitute( main, p.getCodes() );
		self = Grammar.substitute( self, p.getCodes() );
		
		p.aboutToBroadcast( throttleType );
		
		cause( (new key.effect.DirectedBroadcast( to, p, t, main, self, other, targetCode )) );
	}
	
	/**
	  *  Used mainly so that whisper can call and optimised
	  *  cause() method in DirectedBroadcast such that it
	  *  doesn't do a double splash.  (In whisper, we can
	  *  guarantee that if you splash the room, you've got
	  *  everyone you want)
	 */
	protected void cause( key.effect.DirectedBroadcast e )
	{
		e.cause();
	}
	
	public void run( Player p, StringTokenizer args, Repeated r, Command caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String message = r.getMessage();
		
		if( args.hasMoreTokens() )
		{
			Splashable t = getOnlinePlayer( p, ic, args.nextToken() );
			if( t != null )
			{
				if( t == p )
					ic.sendFeedback( spoonClause );
				else
					execute( p, message, t );
			}
			
			return;
		}
		
		ic.sendError( "Format: " + caller.getName() + " " + repeatUsage );
	}
}

/*
  the sideEffect property, if set, won't display the main message to the scape,
  allowing you to do standard whispers (subst 'something' instead of the message
  itself.  if its not set, then the message goes through as normal
*/
