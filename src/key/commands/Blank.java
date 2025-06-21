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
**  $Id: Blank.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       added support for Ranks in permissionlist
**
*/

package key.commands;
import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

/**
  *  This is a container class for several 'blank' routines.
 */
public abstract class Blank extends Command
{
	public Blank()
	{
	}
	
	public static class actions extends Blank
	{
		public actions()
		{
			setKey( "actions" );
			usage = "";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Atom context = p.getContext();
			PermissionList pl = context.getPermissionList();
			
			if( Grammar.getYesNo( "Are you sure you want to clear every entry from the permission list of '" + context.getId() + "'? ", false, ic ) )
			{
				pl.clear();
				ic.sendFeedback( "Blanked the permission list of '" + context.getId() + "'" );
			}
			else
				ic.sendFeedback( "Permission list left intact." );
		}
	}
	
	public static class friends extends Blank
	{
		public friends()
		{
			setKey( "friends" );
			usage = "";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Friends f = p.getFriends();
			
			if( Grammar.getYesNo( "Are you sure you want to clear your friends list? ", false, ic ) )
			{
				f.clearAllElements();
				ic.sendFeedback( "Blanked your friends list." );
			}
			else
				ic.sendFeedback( "Friends list left intact." );
		}
	}
	
	public static class informs extends Blank
	{
		public informs()
		{
			setKey( "informs" );
			usage = "";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			InformList f = p.getInform();
			
			if( Grammar.getYesNo( "Are you sure you want to clear your inform list? ", false, ic ) )
			{
				f.clearAllElements();
				ic.sendFeedback( "Blanked your inform list." );
			}
			else
				ic.sendFeedback( "Inform list left intact." );
		}
	}
	
	public static class prefers extends Blank
	{
		public prefers()
		{
			setKey( "prefers" );
			usage = "";
		}
		
		public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
		{
			Group f = p.getPrefer();
			
			if( Grammar.getYesNo( "Are you sure you want to clear your prefer list? ", false, ic ) )
			{
				f.clearAllElements();
				ic.sendFeedback( "Blanked your prefer list." );
			}
			else
				ic.sendFeedback( "Prefer list left intact." );
		}
	}
}
