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
**  $Id: Many.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
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
import key.util.SeperatedIdentifier;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Enumeration;

public class Many extends Command
{
	public Many()
	{
		setKey( "many" );
		usage = "<id> <alternate name> <alt. name> ...";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String type;
		
		if( !args.hasMoreTokens() )
			usage( ic );
		else
		{
			SeperatedIdentifier full = new SeperatedIdentifier( args.nextToken( " " ) );
			
			if( full.property == true )
			{
				ic.sendError( "Many will not alias properties, sorry" );
				return;
			}
			
			String id = full.id;
			String loc = full.location;
			
			Object o = new Search( loc, p.getContext() ).result;
			if( o != null )
			{
				if( o instanceof Container )
				{
					Container c = (Container)o;
					Object r;
					
					r = c.getExactElement( id );
					if( r == null )
						ic.sendError( "Could not find '" + id + "' in container " + c.getId() );
					else if( r instanceof Atom )
					{
						Object k = ((Atom)r).getKey();
						
								//  generate & add the new secondary names
						AliasListKey newKey = new AliasListKey();
						newKey.setPrimary( k.toString() );
						
						while( args.hasMoreTokens() )
							newKey.addSecondary( args.nextToken() );
						
						c.rekey( (Atom)r, newKey );
						StringBuffer sb = new StringBuffer( "Okay, " );
						sb.append( k.toString() );
						
						if( newKey.hasSecondaries() )
						{
							sb.append( " is now aliased to: " );
							
							for( Enumeration e = newKey.secondaryKeys(); e.hasMoreElements(); )
							{
								sb.append( (String) e.nextElement() );
								sb.append( ' ' );
							}
						}
						else
							sb.append( " is now not aliased." );
						
						ic.sendFeedback( sb.toString() );
					}
					else
						ic.sendError( "'" + id + "' is not an atom, it is " + Type.typeOf( r ).getName() );
				}
				else
					ic.sendError( "'" + Type.typeOf( o ).getName() + "' is not a container" );
			}
			else
				ic.sendError( "Could not find '" + loc + "'" );
		}
	}
}
