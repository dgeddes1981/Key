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
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  17Jul97     druss        Created this command ( Reply )
**  17Jul97     subtle       added quoting
**
*/

package key.commands;
import key.*;
import key.primitive.DateTime;
import key.util.Trie;
import java.util.StringTokenizer;
import java.io.*;

public class Reply extends Command
{
private static final long serialVersionUID = 6185137064316882760L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Reply.class, String.class, "spoonClause",
			AtomicElement.PUBLIC_FIELD,
			"the text sent to the player if the mail doesn't exist" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public static final int MAX_SUBJECT = 50;
	public static final int MAX_LINES = 100;
	public static final int MAX_BYTES = 50 * MAX_LINES;
	
	public String spoonClause = "You havent been sent that many letters!";
	
	public Reply()
	{
		setKey( "reply" );
		usage = "<service> <message number>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}

	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( args.hasMoreTokens() )
		{
			String service = args.nextToken();
			
			if( args.hasMoreTokens() )
			{
				int mailNumber = 0;
				
				try
				{
					mailNumber = Integer.parseInt( args.nextToken() );
				}
				catch( NumberFormatException e )
				{
					ic.sendError( "The message number you supplied dosent appear to be an integer \n" );
					usage( ic );
					return;
				}
				if( mailNumber == 0 )
				{
					ic.sendFeedback( "Message number must be greater than 0." );
					return;
				}	
				
					//  decrement mailNumber by one for container reference...
				mailNumber--;
				Object o;
				
				if( service.equalsIgnoreCase( "mail" ) )
				{
					try
					{
						o = p.getMailbox();
					}
					catch( NoSuchPropertyException e )
					{
						ic.sendError( "Umm... can we say bad?  I can't find your mailbox." );
						return;
					}
					
					if( o == null )
					{
						ic.sendError( "Hrmph.  You don't seem to _have_ a mailbox." );
						return;
					}
					else if( !( o instanceof Container ) )
					{
						ic.sendError( "'" + service + "' is not something you can send mail to." );
						return;
					}
				}
				else if( service.equalsIgnoreCase( "news" ) )
				{
					o = p.getRealm().news;
				}
				else
				{
					o = p.getLocation().getExactElement( service );
					if( o != null )
					{
						if( !( o instanceof Container ) )
						{
							ic.sendError( "'" + service + "' is not something you can send mail to." );
							return;
						}
					}
					else
					{
						ic.sendError( "You cant find '" + service + "' in this room." );
						return;
					}
				}		

					// we have their mailbox
				Container mailbox = (Container) o;
				Letter replyTo =(Letter)mailbox.getElementAt( mailNumber );
				if( replyTo == null )
				{
					ic.sendFeedback( spoonClause );
					return;
				}

				if( service.equalsIgnoreCase( "mail") )
				{
					String name = (String)replyTo.getProperty( "from" );
					Object t = new Search( name, Key.shortcuts() ).result;
					
					if( t == null )
					{
						ic.sendError( "Cannot find player '" + name + "'." );
						return;
					}
					else if( t instanceof Trie )
					{
						ic.sendError( "Multiple matches: " + ((Trie)t).contents() );
						return;
					}
					else if( t instanceof Player )
					{
						Letter toSend = createLetter( p, ic, replyTo );

						if( toSend == null )
						{
							ic.sendFeedback( "Reply aborted. Message NOT sent." );
							return;
						}

						try
						{
							((Player)t).addMail( toSend );
						}
						catch( NonUniqueKeyException e )
						{
							throw new UnexpectedResult( " while sending mail: " + e.toString() );
						}
						catch( BadKeyException e )
						{
							throw new UnexpectedResult( " while sending mail: " + e.toString() );
						}
					}	
					else
					{
						ic.sendError( "'" + name + "' is not something you can send mail to." );
						return;
					}	
				}
				else
				{
					Letter toSend = createLetter( p, ic, replyTo );
					if( toSend == null )
					{
						ic.sendFeedback( "Reply aborted. Message NOT posted." );
						return;
					}

					try
					{
						(( Container )o).add( toSend );
					}
					catch( NonUniqueKeyException e )
					{
						throw new UnexpectedResult( " while posting message: " + e.toString() );
					}
					catch( BadKeyException e )
					{
						throw new UnexpectedResult( " while posting message: " + e.toString() );
					}
				}	
			}
			else
				usage( ic );
		}
		else
			usage( ic );
	}
	
	private Letter createLetter( Player p, InteractiveConnection ic, Letter replyTo )
	{
		String header = "On " + replyTo.getProperty( "when" ).toString() + 
		                ", " + (String)replyTo.getProperty( "from" ) + " wrote: \n";
		
		String quote = ((TextParagraph)replyTo.getProperty( "contents" )).getText();

		{
			StringTokenizer st = new StringTokenizer( quote, "\n", true );
			StringBuffer newQuote = new StringBuffer();
			String t;
			
			while( st.hasMoreTokens() )
			{
				t = st.nextToken();
				
				if( !t.equals( "\n" ) )
					newQuote.append( "> " );
				
				newQuote.append( t );
			}
			
			newQuote.append( "\n\n" );
			
			quote = newQuote.toString();
		}
		
		Paragraph old = new TextParagraph( header + quote );
		Paragraph main = Editor.edit( p, old, ic, MAX_LINES, MAX_BYTES );

		if( old == main )
			return( null );

		Letter l = (Letter) Factory.makeAtom( Letter.class );
		String description = (String)replyTo.getProperty( "description" );
		if( !( description.startsWith( "Re:" ) ) )
		{
			if( description.length()+4 <= Letter.MAX_SUBJECT )
				l.description = "Re: " + description;
			else
				l.description = description;
		}
		else
			l.description = description;
		
		l.contents = main;
		l.from = p.getName();
		l.when = new DateTime();
		
		return( l );
	}
}
