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

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Toggle extends Set
{
	private static final long serialVersionUID = 7734753125517683224L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Toggle.class, String.class, "toggleField",
			AtomicElement.PUBLIC_FIELD,
			"a string representing the string to toggle" ),
		AtomicElement.construct( Toggle.class, String.class, "onFeedback",
			AtomicElement.PUBLIC_FIELD,
			"a message to send when the toggle is turned on" ),
		AtomicElement.construct( Toggle.class, String.class, "offFeedback",
			AtomicElement.PUBLIC_FIELD,
			"a message to send when the toggle is turned off" ),
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String toggleField = "";
	public String onFeedback = "Toggle on";
	public String offFeedback = "Toggle off";
	
	public Toggle()
	{
		setKey( "toggle" );
		usage = "[on|off]";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( toggleField == null || toggleField.length() == 0 )
		{
			ic.sendFailure( "This command is incorrectly set up" );
			return;
		}

		{
			StringTokenizer st = new StringTokenizer( toggleField );
			
			if( st.hasMoreTokens() )
			{
				st.nextToken();
				if( st.hasMoreTokens() )
				{
					ic.sendFailure( "This command is incorrectly set up (spaces in toggle field)" );
					return;
				}
			}
		}
		
		String toggle;
		
		if( args.hasMoreTokens() )
		{
			toggle = args.nextToken();
			
			if( toggle.equalsIgnoreCase( "on" ) )
			{
					//  the player wants to hide
				super.run( p, new StringTokenizer( toggleField + " true" ), fullLine, caller, ic, flags  );
				ic.sendFeedback( onFeedback );
			}
			else if( toggle.equalsIgnoreCase( "off" ) )
			{
					//  the player doesn't want to hide
				super.run( p, new StringTokenizer( toggleField + " false" ), fullLine, caller, ic, flags );
				ic.sendFeedback( offFeedback );
			}
			else
				usage( ic );
		}
		else
		{
				//  no options passed so just invert what their current state is
			Search s = new Search( toggleField, p.getContext() );
			if( s.result instanceof Boolean )
			{
				if( ((Boolean)s.result).booleanValue() )
				{
						// they are hidden and want to be 'unhidden'
					super.run( p, new StringTokenizer( toggleField + " false" ), fullLine, caller, ic, flags );
					ic.sendFeedback( offFeedback );
				}
				else
				{
						// hide them
					super.run( p, new StringTokenizer( toggleField + " true" ), fullLine, caller, ic, flags );
					ic.sendFeedback( onFeedback );
				}
			}
			else
			{
				ic.sendFeedback( "This command is not correctly set up (field doesn't match a boolean)" );
			}
		}
	}
	
	protected void sendOkay( InteractiveConnection ic )
	{
	}
}
