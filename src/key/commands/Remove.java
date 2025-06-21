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
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Enumeration;

public class Remove extends Verb
{
	public Remove()
	{
		setKey( "remove" );
		usage = "<object-being-worn> | 'mail' <msg #>";
		verb = "remove";
		method = null;
		checkInventory = true;
		checkRoom = false;
	}
	
	protected boolean handleKeyword( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags, String keyword ) throws IOException
	{
		Object o = null;
		
		if( keyword.equalsIgnoreCase( "mail" ) )
		{
			o = p.getMailbox();
		}
		else if( keyword.equalsIgnoreCase( "news" ) )
		{
		}
		
		if( o != null )
		{
			if( o instanceof Container )
			{
				Container box = (Container) o;
				
				if( args.hasMoreTokens() )
				{
					int number;
					String strNumber = args.nextToken();
					try
					{
						number = Integer.parseInt( strNumber );
					}
					catch( NumberFormatException e )
					{
						ic.sendError( "'" + strNumber + "' is not a number." );
						return true;
					}
					
					if( number < 1 )
					{
						ic.sendError( "You can't remove negative numbers." );
						return true;
					}
					
					if( number > box.count() )
					{
						ic.sendError( "You don't have that many messages." );
						return true;
					}

					try
					{
						box.removeByNumber( number - 1 );
					}
					catch( BadKeyException e )
					{
						throw new UnexpectedResult( e.toString() + " while removing numbered atom." );
					}
					catch( NonUniqueKeyException e )
					{
						throw new UnexpectedResult( e.toString() + " while removing numbered atom." );
					}
					
					ic.sendFeedback( number + " has been removed from " + box.getName() );
				}
				else
				{
					ic.sendError( "You should use 'remove mail #', when # is the message number you wish to remove." );
				}
			}
			else
				ic.sendError( "'" + keyword + "' doesn't look like good place to go removing things from..." );
			
			return( true );
		}
		else
			return( super.handleKeyword( p, args, fullLine, caller, ic, flags, keyword ) );
	}
	
	public String getVerb()
	{
		return( "remove" );
	}
	
	public void setVerb( String v )
	{
		throw new InvalidArgumentException( "you may not set this property" );
	}
}
