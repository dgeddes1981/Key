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
**  $Id: Broadcast.java,v 1.1.1.1 1999/10/07 19:58:27 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  20Jul97     subtle       modified so that you need to be in the scape
**                           in order to send broadcasts to it.
**
*/

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Hashtable;

public class Broadcast extends Command
{
	private static final long serialVersionUID = -1234778516480130846L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Broadcast.class, String.class, "broadcast",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the broadcast channel" ),
		AtomicElement.construct( Broadcast.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the player" ),
		AtomicElement.construct( Broadcast.class, String.class, "broadcastQuestion",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the broadcast channel if it ends with a ?" ),
		AtomicElement.construct( Broadcast.class, String.class, "feedbackQuestion",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the player if it ends with a ?" ),
		AtomicElement.construct( Broadcast.class, String.class, "broadcastExclaim",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the broadcast channel if it ends with a !" ),
		AtomicElement.construct( Broadcast.class, String.class, "feedbackExclaim",
			AtomicElement.PUBLIC_FIELD,
			"the message format sent to the player if it ends with a !" ),
		AtomicElement.construct( Broadcast.class, String.class, "noone",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the player if no-one is at the receiving channel" ),
		AtomicElement.construct( Broadcast.class, Scape.class, "scapeFor",
			AtomicElement.PUBLIC_FIELD,
			"the receiving channel" ),
		AtomicElement.construct( Broadcast.class, Integer.TYPE, "throttleType",
			AtomicElement.PUBLIC_ACCESSORS,
			"the throttle type (0 or 1)" )//,
		/*AtomicElement.construct( Broadcast.class, String.class, "effectClass",
			AtomicElement.PUBLIC_ACCESSORS,
			"the classname of the effect to use" ),*/
	};

	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	/**
	  *  scapeFor represents the scape which the output of this
	  *  command should go.  If this is not specified, the players
	  *  location is used by default.  (as opposed to coding it to
	  *  use 'here', which would be correct, but less efficient, and
	  *  I mean, hey.. we are talking about *say* here, not some
	  *  priv checking command.)
	 */
	public Reference scapeFor = Reference.EMPTY;
	
	public String broadcast = "";
	public String broadcastQuestion = null;
	public String broadcastExclaim = null;
	public String feedback = "";
	public String feedbackQuestion = null;
	public String feedbackExclaim = null;
	public String noone = "But there isn't anyone there";
	public int throttleType = 0;
	
	public transient Class effectClass = key.effect.Broadcast.class;
	
	public static final char originatorCode = 'o';
	public static final char prefixNameCode = 'p';
	public static final char messageCode = 'm';
	public static final char sometimesSpaceCode = 's';
	
	public Broadcast()
	{
		setKey( "broadcast" );

			// text for commands
		setProperty( "usage", "<message>" );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void setScapeFor( Scape s )
	{
		scapeFor = s.getThis();
	}
	
	public void argument( String args )
	{
		scapeFor = Reference.to( args );
	}
	
	public synchronized void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			Scape to = getTo( p );
			
			if( to == null )
				ic.sendError( "Can't find scape to send this message" );
			else if( to.numberPlayers() > 0 )
			{
				{
					QualifierList ql = p.getQualifierList();
					Type t = Type.typeOf( to );
					
					if( ql.check( t ) == Qualifiers.SUPPRESSION_CODE )
					{
						ic.sendError( "You can't talk in " + to.getName() + " when you're blocking it." );
						return;
					}
				}
				
				String main;
				String self;
				String message = args.nextToken( "" ).trim();
				
				if( message.endsWith( "?" ) )
				{
					main = broadcastQuestion != null ? broadcastQuestion : broadcast;
					self = feedbackQuestion != null ? feedbackQuestion : feedback;
				}
				else if( message.endsWith( "!" ) )
				{
					main = broadcastExclaim != null ? broadcastExclaim : broadcast;
					self = feedbackExclaim != null ? feedbackExclaim : feedback;
				}
				else
				{
					main = broadcast;
					self = feedback;
				}
				
				placeCodes( p, message );
				
				main = Grammar.substitute( main, p.getCodes() );
				self = Grammar.substitute( self, p.getCodes() );
				
				generateEffect( to, p, main, self ).cause();
				p.aboutToBroadcast( throttleType );
			}
			else
				ic.sendError( noone );
		}
		else
			usage( ic );
	}
	
	protected Effect generateEffect( Scape to, Player p, String main, String self )
	{
		return( new key.effect.Broadcast( to, p, main, self ) );
	}
	
	protected void placeCodes( Player p, String message )
	{
		p.putCode( originatorCode, p.getName() );
		p.putCode( prefixNameCode, p.getFullName() );
		p.putCode( messageCode, message );
		
		if( message.charAt( 0 ) == '\'' )
			p.putCode( sometimesSpaceCode, "" );
		else
			p.putCode( sometimesSpaceCode, " " );
	}
	
	protected Scape getTo( Player p )
	{
		Scape to = (Scape) scapeFor.get();
		
		if( to == null )
			return( p.getLocation() );
		else
			return( to );
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
	
		/*
		
			% codes for this command:

			  %o - name of the originator of the message
			  %m - the message itself
		*/
		/* (eventually return something like):
		 
		    commands.Broadcast:
			
		    This command sends a message to everyone in a scape.  If the
			sending player is also in the scape, the message they recieve
			can be tailored.

			The format used for the message sent to everyone is:
			
			  <prepend> <name> <middle> <message> <append>

			where prepend,middle and append are properties, name is the players
			name, and message is the command line typed to this command.

		    To modify the message for the sending player, if you set the self*
			properties to a non-null string (ie, not Key.nullString, as
			opposed to the empty string ""), then they will be used instead of
			the main ones.  If you set the boolean 'substituteName', then instead
			of the players name being inserted, "You" will be used.
		*/
}
