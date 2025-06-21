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
**  $Id: Age.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  29Jul97     exile        fix so that it doesn't throwup when someone
**                           enters a non-number values for their age.
**  24Aug98     subtle       converted to new element system
*/

package key.commands;

import key.*;
import java.util.StringTokenizer;
import java.io.*;

public class Age extends Command
{
	private static final long serialVersionUID = 4200460639236027854L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Age.class, String.class, "invalidAge",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the entered age is out of range" ),
		AtomicElement.construct( Age.class, String.class, "incorrectType",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when no numbers are entered" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public String invalidAge = "Come on now, thats a wee bit ridiculous for an age...";
	public String incorrectType = "So how many years is that exactly? Use numbers please!";
	
	public Age()
	{
		setKey( "age" );
		usage = "<age in years>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
   			String ageString = args.nextToken( "" );
			try
			{
				int age = Integer.parseInt( ageString );

				if( ( age > -1 ) && ( age < 120 ) )
				{          
					if( age != 0 )
					{
						p.setAge( age );
						ic.sendFeedback( "Your age is now set to " + age + " year" + (age==1?"":"s") + " old." );
					}
					else
					{
						p.setAge( 0 );
						ic.sendFeedback( "You reset your age." );
					}
				}
				else
				{
					ic.sendFeedback( invalidAge );
				}
			}
			catch( NumberFormatException e )
			{
				ic.sendFeedback( incorrectType );
			}
		}
		else
			usage( ic );
	}
}
