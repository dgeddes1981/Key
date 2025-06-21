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
**  $Id: Allow.java,v 1.2 2000/02/25 13:58:25 subtle Exp $
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

public class Allow extends Command
{
	public Allow()
	{
		setKey( "allow" );
		
		usage = "[<player | rank | @clanRank>] <action>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Atom context = p.getContext();
		PermissionList pl = context.getPermissionList();
		
		if( args.hasMoreTokens() )
		{
			String firstArg = args.nextToken();
			
			if( args.hasMoreTokens() )
			{
				String player = firstArg;
				String action = args.nextToken();
				
					//  first find the player
				Reference s = getActionReference( player, ic, p );
				
				if( s != null )
				{
					boolean isPlayer = false;
					boolean isFriends = false;
					boolean isRank = false;
					boolean isClan = false;
					
					if( s.isLoaded() )
					{
						Atom q = s.get();
						
						if( q instanceof TransitAtom )
						{
							q = ((TransitAtom)q).getRealAtom();
						
							if( q != null )
								s = q.getThis();
						}
						
						if( q instanceof Player )
							isPlayer = true;
						else if( q instanceof Friends )
							isFriends = true;
						else if( q instanceof Rank )
							isRank = true;
						else if( q instanceof Clan )
							isClan = true;
						else
						{
							if( q == null )
								ic.sendFailure( "null return from match" );
							else
								ic.sendFailure( "error determining match type of: " + q.getClass().getName() );
						}
					}
					else
					{
							//  only players can be offline atm.  in
							//  the future, this might become more of
							//  a problem.
						isPlayer = true;
					}
					
					if( isPlayer || isRank || isClan || isFriends )
					{
							//  now find the target action
						Action a = context.getAction( action );
						if( a != null )
						{
							pl.allow( s, a );
							
							if( isPlayer )
								ic.sendFeedback( s.getName() + " may now " + a.getName() + " " + context.getName() );
							else if( isFriends )
								ic.sendFeedback( "Your friends may now " + a.getName() + " " + context.getName() );
							else
								ic.sendFeedback( "Everyone in " + s.getName() + " may now " + a.getName() + " " + context.getName() );
						}
						else
							ic.sendError( "Could not find action '" + action + "' on " + context.getName() );
					}
					else
						ic.sendError( "That can't be put on a permission list." );
				}
				else
					ic.sendError( "Could not find anything answering to '" + firstArg + "'." );
			}
			else
			{
				Action a = context.getAction( firstArg );
				if( a != null )
				{
					pl.allow( a );
					
						//  "Everyone can now nuke snapper" - muahahahahahaha.
						//  sorry.
					ic.sendFeedback( "Everyone can now " + a.getName() + " " + context.getName() );
				}
				else
					ic.sendError( "Could not find action '" + firstArg + "' on " + context.getName() );
			}
		}
		else
			usage( ic );
	}
}
