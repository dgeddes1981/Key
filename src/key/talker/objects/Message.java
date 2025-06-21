/*
**               j###t  ########## ####   ####
**              j###t   ########## ####   ####
**             j###T               "###L J###"
**          ######P'    ##########  #########
**          ######k,    ##########   T######T
**          ####~###L   ####
**          #### q###L  ##########   .#####
**          ####  \###L ##########   #####"
*/

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

/**
  *  Prop.
  *
  *  A simple class that has no behaviour, only a particular appearance,
  *  just like a stage prop.
 */
public class Message extends EW
{
	private static final long serialVersionUID = 7395956924306542674L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Message.class, String.class, "direct",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the recipient of the use command" ),
		AtomicElement.construct( Message.class, String.class, "feedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the user" ),
		AtomicElement.construct( Message.class, String.class, "room",
			AtomicElement.PUBLIC_FIELD,
			"the message sent to the current room (if set, the target must be in the same room as the originator)" )
	};
	
	public static final char originatorCode = 'o';
	public static final char prefixNameCode = 'p';
	public static final char targetCode = 't';
	public static final char portraitCode = 'f';
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( EW.STRUCTURE, ELEMENTS );
	
	public String direct = null;
	public String feedback = null;
	public String room = null;
	
	public Message()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	private static final HeadingParagraph INSPECT_HEADING = new HeadingParagraph( "messages", HeadingParagraph.RIGHT );
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		super.inspect( p, args, ic, flags, item, originating );
		
		ic.send( INSPECT_HEADING );

		p.putCode( originatorCode, p.getName() );
		p.putCode( prefixNameCode, p.getFullName() );
		p.putCode( targetCode, "Koschei" );
		p.putCode( portraitCode, getFullPortrait( p ) );
		
		if( direct != null )
			ic.sendFeedback( "Direct: " + Grammar.substitute( direct, p.getCodes() ) );
		
		if( room != null )
			ic.sendFeedback( "Room: " + Grammar.substitute( room, p.getCodes() ) );
		
		if( feedback != null )
			ic.sendFeedback( "Feedback: " + Grammar.substitute( feedback, p.getCodes() ) );
		
		ic.sendLine();
	}
	
	public String getDirect()
		{ return( direct ); }
	
	public String getFeedback()
		{ return( feedback ); }
	
	public String getRoom()
		{ return( room ); }
	
	public void use( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		if( direct != null )
		{
			if( args.hasMoreTokens() )
			{
				String name = args.nextToken();
				
				Player target = (Player) Command.getOnlinePlayer( p, ic, name );
				if( target == null )
					return;
				
				if( !target.isLocation( p.getLocation() ) )
				{
					ic.sendError( target.getName() + " is not in the same room as you." );
				}
				
				p.putCode( originatorCode, p.getName() );
				p.putCode( prefixNameCode, p.getFullName() );
				p.putCode( targetCode, target.getName() );
				p.putCode( portraitCode, getFullPortrait( p ) );
				
				String self = "";
				String rest = "";
				
				String main = Grammar.substitute( direct, p.getCodes() );
				
				if( room != null ) {
					
					if( feedback != null )
						self = Grammar.substitute( feedback, p.getCodes() );
					else
						self = Grammar.substitute( room, p.getCodes() );
					
					rest = Grammar.substitute( room, p.getCodes() );					
					
				}
				else if( feedback != null )
				{
					self = Grammar.substitute( feedback, p.getCodes() );
					
				}
									
				new key.effect.DirectedObjectEffect( this, p, target, rest, self, main ).cause();
			
				
				itemWasUsed( p, ic, flags, item, originating );
			}
			else
				ic.sendError( "Usage: use <object> <player>" );
		}
		else
		{
			if( room != null )
			{
				String self = room;
				
				p.putCode( originatorCode, p.getName() );
				p.putCode( prefixNameCode, p.getFullName() );
				p.putCode( portraitCode, getFullPortrait( p ) );
				
				if( feedback != null )
					self = Grammar.substitute( feedback, p.getCodes() );
				else
					self = Grammar.substitute( room, p.getCodes() );
				
				String main = Grammar.substitute( room, p.getCodes() );
				new key.effect.ObjectEffect( this, p, main, self ).cause();
				itemWasUsed( p, ic, flags, item, originating );
			}
			else if( feedback != null )
			{
				p.putCode( originatorCode, p.getName() );
				p.putCode( prefixNameCode, p.getFullName() );
				p.putCode( portraitCode, getFullPortrait( p ) );
				
				String self = Grammar.substitute( feedback, p.getCodes() );
				ic.sendFeedback( self );
				itemWasUsed( p, ic, flags, item, originating );
			}
			else
				ic.sendFailure( "You cannot use this object." );
		}
	}
}
