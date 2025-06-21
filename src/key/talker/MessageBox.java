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
** $Id: MessageBox.java,v 1.4 2000/02/17 15:48:48 subtle Exp $
**
** $Log: MessageBox.java,v $
** Revision 1.4  2000/02/17 15:48:48  subtle
** goddess reported a bug in the unread - basically that it gets out of sync if he
** deletes a message before reading it (or some similar type of behaviour).  This
** causes the unread to never be decremented, which means he gets told he has new
** mail -every- single time he logs in.  Annoying
**
** This is a problem in the unread cache, and I can't think of a good solution off
** the top of my head.  What I've done for now is added a reflective unread field
** so that we can manually reset it to -1 online.  This will tidy up the cache
** at next load, I guess.
**
*/

package key;

import key.collections.NumberedCollection;
import key.primitive.DateTime;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.*;

/**
  *  A messageBox is a type of specialised container for
  *  containing letters
 */
public class MessageBox extends MaterialContainer
{
	private static final long serialVersionUID = -1619021870941708968L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( MessageBox.class, Integer.TYPE, "unread",
			AtomicElement.PUBLIC_FIELD,
			"unread" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( MaterialContainer.STRUCTURE, ELEMENTS );
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public static final TableParagraph.Column[] columns =
	{
		new TableParagraph.Column( "[##]", 4 ),
		new TableParagraph.Column( "From", Player.MAX_NAME ),
		new TableParagraph.Column( "Subject", Letter.MAX_SUBJECT )
	};
	
	public int unread = -1;
	
	public MessageBox()
	{
		contained = new NumberedCollection();
		
		unread = 0;
		
		setConstraint( Type.LETTER );
	}
	
	public MessageBox( int newLimit )
	{
		this();
		
		setLimit( newLimit );	
	}

	public void loaded()
	{
			//  when a class is loaded & the object already exists,
			//  the constructor is never called.  So, we have to initialise it
			//  by hand in this case.
		if( unread == -1 )
			unread = countUnread();
	}
	
// replaced by more effiecient method (using member field)
// which stores number of unread Letters - as opposed to calculating each time
// (and loading each Letter)
// This method remains to be used while Player files update themselves
	public int countUnread()
	{
		int c = 0;
		
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Letter t = (Letter) e.nextElement();
			
			if( t.readCount == 0 )
				c++;
		}
		
		return( c );
	}
	
	public void readIndividual( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, int number )
	{
		if( number < 1 )
		{
			ic.send( "You want to read *which* number?" );
		}
		else
		{
			Letter t = (Letter) getElementAt( number - 1 );
			
			if( t == null )
				ic.send( "Can't find that message..." );
			else
			{
				t.read( p, args, ic, flags, t );
				if( t.readCount == 1 )  // that was the first read
					unread--;
			}
		}
	}
	
	public void readBox( final Player p, final InteractiveConnection ic, final String screen )
	{
		Grammar.pagedList( this, screen, new Grammar.PagedListStuffer()
			{
				public void stuffEntry( Reference r, TableParagraph.Generator tpg, int pageOffset, int totalOffset )
				{
					Letter t = (Letter) r.get();
					
					String rowContents[] = new String[ columns.length ];
					
					rowContents[0] = Integer.toString( totalOffset+1 );
					rowContents[1] = t.from;
					rowContents[2] = t.description;
					
					tpg.appendRow( rowContents );
				}
			}, ic, new TableParagraph.Generator( columns ), "message" );
/*			
		if( count() == 0 )
		{
			ic.send( "There aren't any messages in " + getName() );
			return;
		}
		
		char c = screen.toLowerCase().charAt( 0 );
		int page = c - 'a';
		int height = 20;
		int startAt = page*height;
		int endAt = ((page+1)*height);

		if( startAt < 0 || startAt > count() )
		{
			ic.sendFailure( "That is not a valid page to view.  For example, use 'news read b' to view the second page.  (This will only work if you have more than one page)" );
			return;
		}

		//ic.send( "Page " + page );
		
		TableParagraph.Generator tp = new TableParagraph.Generator( columns );
		
		int i = 0;
		
		for( Enumeration e = referenceElements(); i < endAt && e.hasMoreElements(); i++ )
		{
			Reference r = (Reference) e.nextElement();
			
			if( i < startAt )
				continue;
			
			Letter t = (Letter) r.get();
			
			String rowContents[] = new String[ columns.length ];
			
			rowContents[0] = Integer.toString( i+1 );
			rowContents[1] = t.from;
			rowContents[2] = t.description;
			
			tp.appendRow( rowContents );
		}
		
		if( count() < endAt )
			tp.setFooter( i + " message" + ((i==1)?"":"s") );
		else
			tp.setFooter( "Message " + (startAt+1) + "-" + endAt + " of " + count() );
		
		ic.send( tp.getParagraph() );
		*/
	}
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing th, Container originating )
	{
		if( args.hasMoreTokens() )
		{
			String fa = args.nextToken();
			
			int number = 0;
			
			try
			{
				number = Integer.parseInt( fa );
				readIndividual( p, args, ic, flags, number );
				return;
			}
			catch( NumberFormatException e )
			{
			}
			
			readBox( p, ic, fa );
		}
		else
		{
			if( count() > 20 )
				ic.send( useParagraph );
			else
				readBox( p, ic, "a" );
		}
	}
	
	public void add( Atom letter ) throws BadKeyException, NonUniqueKeyException
	{
		super.add( letter );
		unread++;	
	}
	
	public String[] wearLocations( Player p )
	{
		return( null );
	}

	private static final TextParagraph useParagraph =
		new TextParagraph( TextParagraph.CENTERALIGNED,
"  This message box has been revised to limit the number of messages\n" +
"  displayed at once.  It is similar to the old EW style of checking\n" +
"  news, except that we use 'a-z' instead of numbers to refer to\n" +
"  individual pages.\n" +
"\n" +
"For instance, '^hnews read a^-' will display the first page, '^hnews read b^-'\n" +
"the second, and so on.  <^gfootnote 19^->\n" );

}
