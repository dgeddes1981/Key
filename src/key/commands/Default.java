/**
  *  Command: Default
  *
  *
 */

package key.commands;
import key.*;
import java.util.StringTokenizer;
import java.io.IOException;

public class Default extends Command
{
	public Default()
	{
		setKey( "default" );
		usage = "<player | rank | @clanRank> [<action>]";
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
				String action = args.nextToken();
				
					//  first find the player
				Reference s = getActionReference( firstArg, ic, p );
				
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
							pl.clear( s, a );
							
							if( isPlayer )
								ic.sendFeedback( s.getName() + " no longer has a specific entry for '" + a.getName() + " " + context.getName() + "'" );
							else if( isFriends )
								ic.sendFeedback( "Your friends no longer have a specific entry for '" + a.getName() + " " + context.getName() + "'" );
							else
								ic.sendFeedback( s.getName() + " no longer has a specific entry for " + a.getName() + " " + context.getName() );
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
				Reference s = getActionReference( firstArg, ic, p );
				
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
						pl.deleteEntryFor( s );
						
						if( isPlayer )
							ic.sendFeedback( s.getName() + " no longer has a specific entry on " + context.getName() );
						else if( isFriends )
							ic.sendFeedback( "Your friends no longer have a specific entry on " + context.getName() );
						else
							ic.sendFeedback( s.getName() + " no longer has a specific entry on " + context.getName() );
					}
					else
						ic.sendError( "That can't be put on a permission list." );
				}
				else
					ic.sendError( "Could not find anything answering to '" + firstArg + "'." );
			}
		}
		else
			usage( ic );
	}
}
