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
**  $Id: Book.java,v 1.10 2000/03/30 16:02:16 noble Exp $
**
**  $Log: Book.java,v $
**  Revision 1.10  2000/03/30 16:02:16  noble
**  add fieldlabel
**  add aspect
**  timenotwealth still needs to be fully removed
**
**  Revision 1.9  2000/03/22 21:17:57  subtle
**  Out finished and is no longer cd
**
**  Revision 1.8  2000/01/18 14:51:24  subtle
**  added a comment about compile errors in this class
**
**  Revision 1.7  1999/10/21 16:40:00  pdm
**  code prettifier
**
**  Revision 1.6  1999/10/21 15:39:13  noble
**  fixed version problem
**
**  Revision 1.3  1999/10/14 19:11:50  pdm
**  half converted to 1.1 API
**
**  Revision 1.2  1999/10/13 13:40:09  pdm
**  changed log entry (the Books were created by Noble)
**
**
*/

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import key.primitive.Duration;

/**
 */
public class Book extends Prop
{
	private static final long serialVersionUID = -1541314355578330420L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Book.class, String.class, "title",
			AtomicElement.PUBLIC_FIELD,
			"the title of the book" ),
		AtomicElement.construct( Book.class, Integer.TYPE, "pages",
			AtomicElement.PUBLIC_ACCESSORS,
			"the number of entries the book will store" ),
		AtomicElement.construct( Book.class, InformList.class, "inform",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"who this book records" ),
		AtomicElement.construct( Book.class, String.class, "field",
			AtomicElement.PUBLIC_ACCESSORS,
			"the field to do a comparison on.  integers, please" ),
		AtomicElement.construct( Book.class, String.class, "fieldlabel",
			AtomicElement.PUBLIC_FIELD,
			"the label of the field for display" ),			
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Prop.STRUCTURE, ELEMENTS );
	
	public final InformList inform = (InformList) Factory.makeAtom( InformList.class, "inform" );
	
	public String title = "";
	public int pages = 10;
	public boolean timeNotWealth = true;
	public String field = null;
	public String fieldlabel = "value";
	protected Vector entries = null;	
	
	public Book()
	{
		entries = new Vector( pages , 1 );
		getInform().setLimit( Player.DEFAULT_MAX_INFORM );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	protected void constructed()
	{
		super.constructed();
		init();
	}
	
	public void loaded()
	{
		super.loaded();
		init();
		
		if( field == null )
		{
			if( timeNotWealth )
				field = ".loginStats.totalConnectionTime";
			else
				field = ".florins";
		}
		
		if( fieldlabel == null )
		{
			fieldlabel = "";	
		}
	}
	
		//  needs to be called when loaded & when created
	private void init()
	{
			//  Books need to be notified when someone logs out
		try
		{
			LogoutNotification.register( this );
		}
		catch( Exception e )
		{
			Log.error( "during Book::init()", e );
		}
	}	
	public InformList getInform()
	{
		return( inform );
	}
	
	public void setPages( int amount )
	{
		if( amount < pages ) {
			entries.setSize( amount ); // if smaller , discards unwanted entries	
		}
		pages = amount ;
	}
	
	public int getPages()
	{
		return pages;
	}	
	
	public void setField( String field )
	{
		// changing type of book , so erase previous entries
		if( ! field.equalsIgnoreCase( this.field ) ) {
			entries.removeAllElements();
		}
		
		this.field = field;
	}
	
	public String getField()
	{
		return field;
	}
			
	/*
	public void setTimeNotWealth( boolean time )
	{
		// changing type of book , so erase previous entries
		if( time != timeNotWealth ) {
			entries.removeAllElements();
		}
		
		timeNotWealth = time;
	}
	
	public boolean getTimeNotWealth()
	{
		return timeNotWealth;
	}
	*/
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		ic.send( aspect() );
	}

	public Paragraph aspect()
	{
		TableParagraph.Column[] columns =
		{
			new TableParagraph.Column( "", 7 ),
			new TableParagraph.Column( "#", 4 ),
			new TableParagraph.Column( "name", Player.MAX_NAME ),
			new TableParagraph.Column( fieldlabel, 40 ),
			new TableParagraph.Column( "", 7 )
		};
		
		TableParagraph.Generator table = new TableParagraph.Generator( columns );
		
		String b[] = new String[5];
		b[0] = "|"; b[1] = ""; b[2] = ""; b[3] = ""; b[4] = "      |";
		table.appendRow( b ); // blank row
		
		int entry = 1;
		
		for( Enumeration e = entries.elements(); e.hasMoreElements(); )
		{
			Entry ent = (Entry) e.nextElement();
			if( ent == null ) break; // unfilled pages
			
			long det = ent.value;
			
			String s[] = new String[5];
			s[0] = "|";
			s[1] = "" + entry;
			s[2] = ent.ref.getName();
			s[3] = (  ( timeNotWealth ) ? ( new Duration( det ).toLimitedString(3) ) : ( "" + det + " silver florin" + ( (det == 1) ? "." : "s.") )  );
			s[4] = "      |";
			
			table.appendRow( s );
			entry++;
		}
	
		table.appendRow( b ); // blank row
		
		table.setFooter( "^h" + title + "^-" );
		
		return( table.getParagraph() );
	}
	
	public void splash( Effect e, SuppressionList s )
	{
		if( e instanceof key.effect.Logout )
		{
			Reference pRef = e.getOriginator().getThis();

			boolean found = false;
			
			for( Enumeration en = getInform().referenceElements(); en.hasMoreElements(); )
			{
				Reference a = (Reference) en.nextElement();
				
				if( a.isLoaded() ) // disconnecting players wont be unloaded yet
				{
					if( pRef.equals( a ) )
					{
						found = true;
					}
					else
					{
						Atom j = a.get();
						
						if( j instanceof Scape )
						{
							if( ((Scape)j).containsPlayer( (Player) pRef.get() ) )
							{	
								found = true;
							}
						}
					}
				}
			}
			
			if( !found )
				return;
			
			for(int x=0;x<entries.size();x++)
			{
				if( pRef.equals( ((Entry) entries.elementAt(x) ).ref) )
				{
					entries.removeElementAt(x); // already in book
				}
			}
			
			// check to add to vector list
			int pos = WhereAdd( getDeterminant( pRef ) );
			// add
			if( pos >= 0 ) AddEntry( pRef , pos );
		}				
	}
	
	protected long getDeterminant( Reference r )
	{
		if( field != null )
		{
			Object o = new Search( field, r.get() ).result;
			Class c = o.getClass();
			
			if( o instanceof Integer )
				return( ((Integer)o).longValue() );
			else if( o instanceof Duration )
				return( ((Duration)o).getTime() );
			else
				field = null;
		}
		return 0;
		
		/*  --  the old way, faster, but the above is more flexible.
		if( timeNotWealth )
			return ((Player) r.get()).loginStats.getTotalConnectionTime().getTime();
		else
			return (long) ((Player) r.get()).getFlorins();
		*/
	}
	
	// returns the position to which it should be inserted
	// 0 is the top entry
	protected int WhereAdd( long amount )
	{
		int y=0;
		for(y = entries.size();y>0;y--) {
		
			Entry ent = (Entry) entries.elementAt(y-1);
			if( ent.value >= amount) break;
		}
		
		if( y == entries.size() )
		{
			if( y >= pages ) return -1;  // isnt added
		}
		
		return y;
	}	
	
	protected void AddEntry( Reference pRef , int pos )
	{
		if( entries.size() == pages )
		{
			entries.removeElement( entries.lastElement() );  // bottom entry is removed
		}
		
		entries.insertElementAt( new Entry( pRef ), pos);
	}
	
	public class Entry implements java.io.Serializable
	{
			//  If you get a compile error on this line, you need a Java 1.2
			//  compiler.  (You don't need to RUN with 1.2, just compile)
			//
			//  Or, you can just delete this line.  It's only important if
			//  you change this class and want to retail compatibility with
			//  previously saved classes (like we need to with Forest).
			//
			//  (ie, get a 1.2 compiler, or delete the below line for your
			//  copy only - just be careful not to re-upload the deleted
			//  line one!!!)
			//
			//  Paul, Jan '00
		private static final long serialVersionUID = 7841314355574430420L;
		
		Reference ref = Reference.EMPTY;
		long value = 0;
		
		public Entry( Reference pRef )
		{
			ref = pRef;
			value = getDeterminant( pRef );
		}
	}	
}
