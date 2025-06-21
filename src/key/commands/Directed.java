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
**  $Id: Directed.java,v 1.2 1999/11/23 14:25:19 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.io.*;

public class Directed extends Command implements Repeatable
{
	private static final long serialVersionUID = 2397330230954817944L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Directed.class, String.class, "direct",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person" ),
		AtomicElement.construct( Directed.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent back to the player" ),
		AtomicElement.construct( Directed.class, String.class, "directQuestion",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person if the string ends with a ?" ),
		AtomicElement.construct( Directed.class, String.class, "feedbackQuestion",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent back to the player if the string ends with a ?" ),
		AtomicElement.construct( Directed.class, String.class, "directExclaim",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the person if the string ends with a !" ),
		AtomicElement.construct( Directed.class, String.class, "feedbackExclaim",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent back to the player if the string ends with a !" ),
		AtomicElement.construct( Directed.class, String.class, "repeatUsage",
			AtomicElement.PUBLIC_FIELD,
			"the usage string when used with a repeat" ),
		AtomicElement.construct( Directed.class, String.class, "spoonClause",
			AtomicElement.PUBLIC_FIELD,
			"the string sent if you start talking yourself" ),
		AtomicElement.construct( Directed.class, Integer.TYPE, "throttleType",
			AtomicElement.PUBLIC_ACCESSORS,
			"the throttle type (0 or 1)" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final char originatorCode = 'o';
	public static final char prefixNameCode = 'p';
	public static final char messageCode = 'm';
	public static final char sometimesSpaceCode = 's';
	public static final char targetCode = 't';

	public String direct = "";
	public String feedback = "";
	public String directQuestion = null;
	public String feedbackQuestion = null;
	public String directExclaim = null;
	public String feedbackExclaim = null;
	public int throttleType = 0;
	
	public String repeatUsage = "<name>";
	public String spoonClause = "They say talking to yourself is the first sign of madness";
	
	public Directed()
	{
		setKey( "directed" );
		
			// text for command
		usage = "<name> <message>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
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
				
				Object t = getOnlinePlayer( p, ic, name );
				if( t != null )
				{
					if( t == p )
						ic.sendFeedback( spoonClause );
					else
						execute( p, message, (Splashable) t );
				}
				
				return;
			}
		}
		
		usage( ic );
	}

	protected void execute( Player p, String message, Splashable t )
	{
		String other;
		String self;
		
		p.putCode( originatorCode, p.getName() );
		p.putCode( prefixNameCode, p.getFullName() );
		p.putCode( messageCode, message );
		
		if( message.charAt( 0 ) == '\'' )
			p.putCode( sometimesSpaceCode, "" );
		else
			p.putCode( sometimesSpaceCode, " " );
		
		if( message.endsWith( "?" ) )
		{
			other = directQuestion != null ? directQuestion : direct;
			self = feedbackQuestion != null ? feedbackQuestion : feedback;
		}
		else if( message.endsWith( "!" ) )
		{
			other = directExclaim != null ? directExclaim : direct;
			self = feedbackExclaim != null ? feedbackExclaim : feedback;
		}
		else
		{
			other = direct;
			self = feedback;
		}
		
		p.aboutToBroadcast( throttleType );
		(new key.effect.Directed( p, t, self, other, targetCode )).cause();
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
	
	public void setThrottleType( int i )
	{
		if( i < Player.NUMBER_OF_THROTTLES )
			throttleType = i;
		else
			throw new InvalidArgumentException( "bad throttle type: must be < " + Player.NUMBER_OF_THROTTLES );
	}

	public int getThrottleType()
	{
		return( throttleType );
	}
}
