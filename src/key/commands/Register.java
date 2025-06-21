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
**  $Id: Register.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
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
import key.primitive.*;
import java.io.IOException;

import java.util.StringTokenizer;

public class Register extends Command
{
	public Register()
	{
		setKey( "register" );
		usage = "";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( p.willSync() )
		{
			ic.sendFeedback( "This character is already registered." );
		}
		else
		{
			if( !p.getCanSave() )
			{
				ic.sendFeedback( "You will first need to ask a superuser to grant you 'residency'.  When they do so, you will be instructed to run this command again in order to enter the rest of your details." );
			}
			else
			{
				if( !p.hasPassword() )
				{
					p.command( "password", ic, false );
				}
				
				EmailAddress ea = p.getEmail();
				
				if( !ea.isSet() )
				{
					p.command( "email", ic, false );
				}
				else if( !ea.isValid() )
				{
					ic.sendFeedback( "You have already set an email address, but have not yet verified that you recieved the email we sent you.  If you wish to change it, use the 'email' command." );
				}
				else
					ic.sendFeedback( "You have already set an email address and verified it." );
			}
		}
	}
}
