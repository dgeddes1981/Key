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
import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Enumeration;

/**
  *  A remarkable scripting tool, and the start of a simple expression
  *  parser (all this will go a long way, one day), the print class
  *  accepts, as it's argument, what is to be printed.  If the
  *  argument is inclosed in ""'s, the quotes are stripped and it is
  *  printed as is.  if enclosed in ()'s, the brackets are stripped,
  *  and a search is performed based on the current context, the value
  *  of the result is printed (using toString()).  otherwise, the
  *  arguments are just printed.  mainly useful for alias & the scripting
  *  commands, such as the loginscript.
 */
public class Print extends Command
{
	public Print()
	{
		setKey( "print" );
		usage = "[<identifier>]";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		String id;
		
		if( args.hasMoreTokens() )
			id = args.nextToken( "" );
		else
		{
			ic.blankLine();
			return;
		}
		
		Object o;
		char c = id.charAt( 0 );
		switch( c )
		{
				//  include for future compatibility
			case '\"':
			{
				int i = id.lastIndexOf( '\"' );
				if( i != -1 )
					id = id.substring( 1, i );
				
				ic.send( id );
				break;
			}
			case '(':
			{
				int i = id.lastIndexOf( ')' );
				if( i != -1 )
					id = id.substring( 1, i );
				
				try
				{
					o = new Search( id, p.getContext() ).result;
				}
				catch( InvalidSearchException e )
				{
					ic.sendError( e.getMessage() );
					return;
				}
				
				if( o == null )
					ic.sendFeedback( Key.nullString );
				else if( o instanceof Paragraph )
				{
					ic.send( (Paragraph) o );
				}
				else if( o instanceof Screen )
				{
					ic.send( (Screen) o );
				}
				else if( o instanceof Trie )
				{
					ic.sendError( "Multiple matches: " + ((Trie)o).contents() );
				}
				else
					ic.sendFeedback( o.toString() );

				break;
			}
			default:
			{
				if( Character.isDigit( c ) )
				{
						//  a number, probably
					ic.send( id );
				}
			}
		}
	}
}
