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
**  $Id: Look.java,v 1.8 2000/07/12 13:41:00 pdm Exp $
**
**  $Log: Look.java,v $
**  Revision 1.8  2000/07/12 13:41:00  pdm
**  added log tags
**
**
*/

package key.commands;

import key.*;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Look extends Command
{
	public Look()
	{
		setKey( "look" );
		usage = "[<object in room> | my <object in inventory>]";
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String nt = args.nextToken();
			
			if( nt.equals( "-" ) )
			{
				if( p.connected() && p.located() )
					ic.send( p.getLocation().who( p ), false );
				else
					ic.sendError( "Darkness." );
			}
			else
			{
				Thing t = null;
				Container orig = null;
				
				if( nt.equalsIgnoreCase( "my" ) && args.hasMoreTokens() )
				{
					nt = args.nextToken();
					t = getObjectFromInventory( p, null, nt );
					if( t == null )
					{
						ic.sendFailure( "You don't seem to be holding that object." );
						return;
					}
					orig = p.getInventory();
				}
				else
				{
					t = getObjectFromLocation( p, null, nt );
					orig = p.getLocation();
				}
				
				if( t != null )
					t.look( p, args, ic, flags, t, orig );
				else
				{
					Object l = getOnlinePlayer( p, null, nt );
					Player g = null;
					
					if( l instanceof PlayerGroup )
					{
						Enumeration e = ((PlayerGroup) l).players();
						if( e.hasMoreElements() )
						{
							do
							{
								g = (Player) e.nextElement();
								if( g.isLocation( p.getLocation() ) )
								{
									ic.send( new HeadingParagraph( g.getFullName() ) );
									g.sendLookScreen( p, ic );
								}
								else
									ic.sendFailure( g.getName() + " is not in this room." );
							} while( e.hasMoreElements() );
							
							ic.sendLine();
						}
						else
							ic.sendError( "No-one in that group" );
					}
					else if( l instanceof Player )
					{
						g = (Player) l;
						//ic.send( g.getName() + " is wearing:" );
						if( g.isLocation( p.getLocation() ) )
						{
							new key.effect.DirectedBroadcast( p.getLocation(), p, g, ( p.getName() + " looks at " + g.getName() + "."), ( "You look at " + g.getName() + "."), ( p.getName() + " looks at %t." ), 't' ).cause();
							g.sendLookScreen( p, ic );
						}
						else
							ic.sendFailure( "That player is not in this room." );
					}
					else
					{
						ic.sendFailure( "You can't see anything here called '" + nt + "'." );
						ic.sendFeedback( "(You can use 'look my <obj>' to look at something in your inventory)" );
					}
				}
			}
		}
		else
		{
			if( p.connected() && p.located() )
				ic.send( p.getLocation().aspect( p ), false );
			else
				ic.sendError( "Darkness." );
		}
	}
}
