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
**  $Id: SetGender.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import key.primitive.Gender;
import java.io.IOException;
import java.util.StringTokenizer;

public class SetGender extends Command
{
	public SetGender()
	{
		usage = "<m/f/n>";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String gender = args.nextToken( "" );
			if( gender.length() > 0 )
			{
				if( gender.charAt(0) == 'M' || gender.charAt(0) == 'm' )
					p.setGender( Gender.MALE_GENDER );
				else if( gender.charAt(0) == 'F' || gender.charAt(0) == 'f' )
					p.setGender( Gender.FEMALE_GENDER );
				else if( gender.charAt(0) == 'N' || gender.charAt(0) == 'n' )
					p.setGender( Gender.NEUTER_GENDER );
				else
				{
					usage( ic );
					return;
				}
				
				ic.sendFeedback( "You set your gender to " + p.maleFemale() + "." );
			}
		}
		else
			usage( ic );
	}
}
