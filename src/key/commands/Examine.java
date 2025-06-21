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
import java.util.Enumeration;

public class Examine extends Command
{
	public Examine()
	{
		setKey( "examine" );
		usage = "<player>";
	}
	
	//
	//  probably best to move the examine screen generation code to
	//  player.java
	//
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String targetPlayer;
		if( args.hasMoreTokens() )
		{
			targetPlayer = args.nextToken( " " );
			Object o;
			
			Player a = (Player) getOnlinePlayer( p, ic, targetPlayer );
			
			if( a != null )
			{
				a.sendExamineScreen( ic );
				
				/*
				boolean seeThrough = a.getPermissionList().permissionCheck( Player.seePrivateInfoAction, false, true );
				
				ic.sendLine();
				ic.send( a.getTitledName() );
				
					// show the description
				{
					Paragraph desc = a.getDescription();
					if( desc!= null )
						ic.send( desc );
				}
				
				{
					String aka = a.getAka();
					if( aka.length() > 0 ) 
						ic.sendFeedback( "Also known as: " + aka );
				}
				
				ic.sendLine();
				
				TimeStatistics loginStats = (TimeStatistics) a.loginStats;
				
				if( a.connected() )
				{
					ic.send( a.getName() + " has been logged in for " + loginStats.getTimeSinceConnection() );
				}
				else
				{
					DateTime dateTime = (DateTime) loginStats.getLastConnection();
					
					if( dateTime == null )
						ic.send( a.getName() + " has never logged in." );
					else
						ic.send( a.getName() + " was last seen at " + dateTime.toString( p ) );
				}

				Gender gender = a.getGender();
				
				ic.send( gender.HisHer() + " total login time is " + loginStats.getTotalConnectionTime() );
				
				int age = a.getAge();
				if( age !=  0 ) 
					ic.send( gender.HeShe() + " is " + age + " year" + (age==1?"":"s") + " old" );
				
				if( seeThrough )
				{
					int florins = a.getFlorins();
					ic.send( gender.HeShe() + " has " + florins + " silver florin" + (florins==1?"":"s") );
					
					ic.sendLine();
				}
				
					//  email addresses
				{
					String ea = a.getEmailAddress();
					
					if( ea != null && ( !a.isEmailPrivate() || seeThrough ) )
					{
						ic.sendFeedback( a.HisHer() + " email address is: " + ea + "\n" );
						ic.sendLine();
					}
				}
				*/
			}
		}
		else
			usage( ic );
	}
}
