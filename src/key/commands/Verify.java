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
import key.primitive.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Verify extends Command
{
	public static final int MAX_LENGTH = 60;
	
	public Verify()
	{
		setKey( "verify" );
		usage = "<verification code>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String code = args.nextToken( "" );
			
			EmailAddress address = p.getEmail();
			
			address.verify( code, ic, p );
		}
		else
			usage( ic );
	}
}
