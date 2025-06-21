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
**  $Id: Actions.java,v 1.1.1.1 1999/10/07 19:58:26 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       added support for Ranks in permissionlist
**  24Aug98    subtle       converted to new element system
**
*/

package key.commands;

import key.*;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.*;

/**
  *  Remember to turn your expert bit on if you want to see
  *  everything.
 */
public class Actions extends Command
{
	public Actions()
	{
		setKey( "actions" );
		
		usage = "[<player or rank>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom context = p.getContext();
		PermissionList pl = context.getPermissionList();
		
		boolean playerOnly = !p.isExpert() && (context instanceof Player);
		
		if( args.hasMoreTokens() )
		{
			String firstArg = args.nextToken();
			Reference s = getReferenceElementInside( ic, firstArg, Key.shortcuts() );
			
			if( s != null )
			{
				PermissionList.Entry ple = pl.getEntryFor( s );
				
				if( ple != null )
				{
					if( playerOnly )
						ic.sendFeedback( ple.toString( false ) );
					else
						ic.sendFeedback( ple.toString() );
				}
				else
					ic.sendFeedback( "No explicit entry for " + s.getName() );
			}
			else
				ic.sendError( "Could not find anything answering to '" + firstArg + "'." );
		}
		else
		{
			ic.send( new HeadingParagraph( "actions for " + context.getName(), HeadingParagraph.RIGHT ) );
			
			if( playerOnly )
				ic.sendFeedback( pl.toString( false ) );
			else
				ic.sendFeedback( pl.toString() );
			
			ic.sendLine();
			
			StringBuffer allowSb = new StringBuffer();
			boolean firstAllow=true;
			StringBuffer denySb = new StringBuffer();
			boolean firstDeny=true;
			
			for( Enumeration e = context.getActions(); e.hasMoreElements(); )
			{
				Action act = (Action) e.nextElement();
				
				if( !playerOnly || !act.getExpert() )
				{
					if( pl.isAllowingByDefault( act ) )
					{
						if( !firstAllow )
							allowSb.append( ", " );
						allowSb.append( act.getName() );
						firstAllow = false;
					}
					else
					{
						if( !firstDeny )
							denySb.append( ", " );
						denySb.append( act.getName() );
						firstDeny = false;
					}
				}
			}
			
			ic.sendFeedback( "Allow: [" + allowSb.toString() + "]" );
			ic.sendFeedback( "Deny: [" + denySb.toString() + "]" );
			ic.sendLine();
		}
	}
}
