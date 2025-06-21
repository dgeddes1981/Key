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
**  $Id: Post.java,v 1.1.1.1 1999/10/07 19:58:28 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       uses shortcuts now
**
*/

package key.commands;

import key.*;
import key.primitive.DateTime;
import key.util.Trie;
import java.util.StringTokenizer;
import java.io.*;

public class Post extends Command
{
private static final long serialVersionUID = 6966230298988995503L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Post.class, String.class, "spoonClause",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when messages are sent to yourself" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final int MAX_LINES = 100;
	public static final int MAX_BYTES = 50 * MAX_LINES;
	
	public String spoonClause = "Try a post-it note.  They work better";
	
	public Post()
	{
		setKey( "post" );
		
			// text for command
		usage = "<'news' or playername> <subject>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String name = args.nextToken();
			boolean publicI = false;
			
			if( args.hasMoreTokens() )
			{
				Object t;
				
				if( name.equalsIgnoreCase( "news" ) )
				{
					t = p.getRealm().news;
					publicI = true;
				}
				else
					t = p.getLocation().getExactElement( name );
				
				if( t != null )
				{
					if( t instanceof Container )
					{
						String subject = args.nextToken( "" );
						
						if( subject.length() > Letter.MAX_SUBJECT )
						{
							ic.sendError( "Please use a subject of less than " + Letter.MAX_SUBJECT + " characters." );
							return;
						}
						
						Letter l = createLetter( p, ic, subject );

						if( l == null )
						{
							ic.sendFeedback( "Post aborted.  Message NOT posted." );
							return;
						}
						
						if( publicI )
						{
							l.getPermissionList().allow( Letter.readAction );
						}
						
						try
						{
							((Container)t).add( l );
						}
						catch( NonUniqueKeyException e )
						{
							throw new UnexpectedResult( "while sending mail: " + e.toString() );
						}
						catch( BadKeyException e )
						{
							throw new UnexpectedResult( "while sending mail: " + e.toString() );
						}
						
						return;
					}
				}
				
				t = new Search( name, Key.shortcuts() ).result;
				
				if( t == null )
				{
					ic.sendError( "Cannot find object or player '" + name + "'" );
					return;
				}
				else if( t instanceof Trie )
				{
					ic.sendError( "Multiple matches: " + ((Trie)t).contents() );
					return;
				}
				else if( t == p )
				{
					ic.sendFeedback( spoonClause );
					return;
				}
				else if( t instanceof Player )
				{
					Letter l = createLetter( p, ic, args.nextToken( "" ) );
					
					if( l != null )
					{
						try
						{
							((Player)t).addMail( l );
						}
						catch( NonUniqueKeyException e )
						{
							throw new UnexpectedResult( "while sending mail: " + e.toString() );
						}
						catch( BadKeyException e )
						{
							throw new UnexpectedResult( "while sending mail: " + e.toString() );
						}
					}
					else
						ic.sendError( "No message body, letter not sent." );
					
					return;
				}
				else
				{
					ic.sendError( "'" + name + "' is not something you can send mail to." );
					return;
				}
			}
		}
		
		usage( ic );
	}
	
	private Letter createLetter( Player p, InteractiveConnection ic, String subject )
	{
		Paragraph main = Editor.edit( p, new TextParagraph(), ic, MAX_LINES, MAX_BYTES );
		
		if( ((TextParagraph)main).getText().length() == 0 )
			return( null );
		
		Letter l = (Letter) Factory.makeAtom( Letter.class );
		l.setProperty( "description", subject );
		l.setProperty( "contents", main );
		l.setProperty( "from", p.getName() );
		l.setProperty( "when", new DateTime() );
		
		return( l );
	}
}
