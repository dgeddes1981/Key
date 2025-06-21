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
**  $Id: LinkedList.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**  20Oct98     subtle       added serialisation
**  25Oct98     subtle       fixed a small bug in count()
**  02Nov98     subtle       moved to key.util package
**
*/

package key.util;

import java.io.*;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
  * Implementation of a doubly linked list
  *
  * @version 1.2, 02Nov98
  * @author Paul Mclachlan
  *
 */
public class LinkedList implements java.io.Serializable
{
	private static final long serialVersionUID = -377921300625278844L;
	private Element First;
	private Element Last;
	private int Count;
	
	private static int totalLists;
	
    /**
	  * creates the queue
     */
	public LinkedList()
	{
		init();
	}
	
	private void init()
	{
		totalLists++;
		
		First = null;
		Last = null;
		Count = 0;
	}
	
	private void readObject( ObjectInputStream from )
		throws IOException,StreamCorruptedException,ClassNotFoundException
	{
		init();
		
		int count;
		count = from.readInt();
		
		while( count-- > 0 )
			append( from.readObject() );
	}
	
	private void writeObject( ObjectOutputStream to )
		throws IOException
	{
		to.writeInt( Count );
		
		for( Enumeration e = elements(); e.hasMoreElements(); )
			to.writeObject( e.nextElement() );
	}
	
	/**
	  * Adds another object to the queue
	  *
	  * @param store the object to be added
	 */
	public final synchronized void append( Object store )
	{
		Element latest = new Element( store );
		
		if( Last != null )
		{
			latest.setPrevious( Last );
			Last.setNext( latest );
		}
		else
			First = latest;
		
		Last = latest;
		
		Count++;
	}
	
	public final synchronized void appendCycle( Object store, int max )
	{
		append( store );
		
		while( Count > max && First != null )
		{
				//  remove lines from the front until
				//  we have at most max lines.
			removeLinkedListElement( First );
		}
	}
	
	/**
	  * Adds another object to the queue
	  *
	  * @param store the object to be added
	 */
	public final synchronized void prepend( Object store )
	{
		Element latest = new Element( store );
		
		if( First != null )
		{
			latest.setNext( First );
			First.setPrevious( latest );
		}
		else
			Last = latest;
		
		First = latest;
		
		Count++;
	}
	
	/**
	  * Returns the number of objects in the queue
	  * @return the number of objects in the queue
	 */
	public final int count()
	{
		return( Count );
	}

	public final Object getFirstElement()
	{
		if( First != null )
			return( First.item() );
		else
			return( null );
	}

	public final Object getLastElement()
	{
		if( Last != null )
			return( Last.item() );
		else
			return( null );
	}
	
	public final Enumeration elements()
	{
		return( new Enumerator( First ) );
	}

	public final boolean contains( Object o )
	{
		for( Enumeration e = this.elements(); e.hasMoreElements(); )
			if( e.nextElement() == o )
				return true;
		
		return false;
	}

	public final boolean containsEqual( Object o )
	{
		for( Enumeration e = this.elements(); e.hasMoreElements(); )
			if( e.nextElement().equals( o ) )
				return true;
		
		return false;
	}

	public final void replaceEqual( Object o )
	{
		Element lle = First;
		
		while( lle != null )
		{
			if( lle.stored.equals( o ) )
			{
				lle.stored = o;
				return;
			}
			
			lle = lle.next;
		}
	}
	
	public final Object getElementAt( int c )
	{
		int i = 0;
		for( Enumeration e = this.elements(); e.hasMoreElements(); i++ )
		{
			Object o = e.nextElement();
			if( i == c )
				return o;
		}
		
		return( null );
	}

	private final synchronized void removeLinkedListElement( Element s )
	{
		if( s == First )
		{
			if( s == Last )
			{	//  only this element in the list
				First = null;
				Last = null;
			}
			else
			{	//  the first element in the list
				First = s.getNext();
				First.setPrevious( null );
			}
		}
		else
		{
			if( s == Last )
			{	//  last element in the list
				Last = s.getPrevious();
				Last.setNext( null );
			}
			else
			{	//  middle element in the list
				s.getPrevious().setNext( s.getNext() );
				s.getNext().setPrevious( s.getPrevious() );
			}
		}
		Count--;
		return;
	}
	
	public final synchronized void removeElementAt( int c )
	{
		Element s=First;
		int i = 0;
		while( s != null )
		{
			if( i == c )
			{
				removeLinkedListElement( s );
				return;
			}
			
			s = s.getNext();
			i++;
		}
	}

	/**
	  *  No action if o is not in the list
	 */
	public final synchronized void remove( Object o )
	{
		Element s=First;
		while( s != null )
		{
			if( s.item() == o )
			{
				removeLinkedListElement( s );
				return;
			}
			
			s = s.getNext();
		}
	}
	
	/**
	  *  Call if you wish to use the .equals() method
	  *  to compare objects.
	 */
	public final synchronized void removeEqual( Object o )
	{
		Element s=First;
		while( s != null )
		{
			if( s.item().equals( o ) )
			{
				removeLinkedListElement( s );
				return;
			}
			
			s = s.getNext();
		}
	}
	
	/**
	  * Erase all elements in this list
	 */
	public final synchronized void clear()
	{	//  ahh, the wonders of a garbage collector
		First = null;
		Last = null;
		Count = 0;
	}

	public final Iterator iterator()
	{
		return( new Iterator() );
	}

	/* TEST SUITE
	final void dump()
	{
	    if( First != null )
			System.out.println( "First: " + First.toString() );
		else
			System.out.println( "First: null" );
		if( Last != null )
			System.out.println( "Last: " + Last.toString() );
		else
			System.out.println( "Last: null" );

		System.out.println( "----elements----" );
		Element t;
		t = First;
		while( t != null )
		{
			System.out.println( t.toString() + "---> " + t.item().toString() );
			t = t.getNext();
		}
	}

	public static void main( String args[] )
	{
		LinkedList main = new LinkedList();
		String entry;
		int count=0;

		do
		{
			System.out.println( "\nLinkedList contains " + main.count() + " elements" );
			entry = input( "append,prepend,dump: " );
			if( entry.length() == 0 )
			{
			}
			else if( entry.equals( "dump" ) )
			{
				System.out.println( "\nDUMPING:\n--------" );
				main.dump();
			}
			else if( entry.equals( "quit" ) )
			{
			}
			else if( entry.startsWith( "append " ) )
			{
				entry = entry.substring( 7 );
				System.out.println( "Appending " + entry + "..." );
				main.append( entry );
			}
			else if( entry.startsWith( "prepend " ) )
			{
				entry = entry.substring( 7 );
				System.out.println( "Prepending " + entry + "..." );
				main.prepend( entry );
			}
			else
			{
				System.out.println( "Unknown command '" + entry + "'" );
			}
		} while( !entry.equals( "quit" ) );
	}
	
	public static String input( String prompt )
	{
		StringBuffer buildInput = new StringBuffer();
		char b=' ';

		System.out.print( prompt );
		System.out.flush();

		do
		{
			try
			{
				b = (char) System.in.read();
			}
			catch( IOException e )
			{
					System.out.println( e.toString() );
					System.exit( 1 );
			}
			if( b != '\n' && b != 0 )
				buildInput.append( b );
		} while( b != '\n' && b != 0 );

		return( new String( buildInput ) );
	}
	*/
	
	public static int getTotalLists()
	{
		return( totalLists );
	}

	protected void finalize() throws Throwable
	{
		super.finalize();
		
		totalLists--;
	}
	
	private final static class Enumerator implements Enumeration
	{
		Element c;

		public Enumerator( Element first )
		{
			c = first;
		}

		public boolean hasMoreElements()
		{
			return( c != null );
		}

		public Object nextElement()
		{
			if( c != null )
			{
				Element result = c;
				c = c.getNext();
				return( result.item() );
			}
			else
				throw new NoSuchElementException( "iterating past end of linked list" );
		}
	}
	
	private final static class Element
	{
		Object stored;
		Element next;
		Element prev;
		
		/**
		  * Creates a new queue element to hold
		  * this object
		 */
		public Element( Object store )
		{
			stored = store;
			next = null;
			prev = null;
		}

		/**
		  * Sets the value of 'next' to the given value
		  @param next the next element in the queue
		 */
		public void setNext( Element Next )
		{
			next = Next;
		}

		public Element getNext()
		{
			return( next );
		}

		public void setPrevious( Element Previous )
		{
			prev = Previous;
		}

		public Element getPrevious()
		{
			return( prev );
		}

		public Object item()
		{
			return( stored );
		}

		public String toString()
		{
			if( prev != null )
			{
				if( next  != null)
					return(  String.valueOf( super.hashCode() ) + ": previous=" + prev.hashCode() + "  next=" + next.hashCode() );
				else
					return(  String.valueOf( super.hashCode() ) + ": previous=" + prev.hashCode() + "  next=null" );
			}
			else
			{
				if( next  != null)
					return( String.valueOf( super.hashCode() ) + ": previous=null  next=" + next.hashCode() );
				else
					return( String.valueOf( super.hashCode() ) + ": previous=null  next=null" );
			}
		}
	}
	
	public final class Iterator
	{
		Element c;

		public Iterator()
		{
			c = LinkedList.this.First;
		}

		public Object element()
		{
			return( c.item() );
		}
		
		public void next()
		{
			c = c.getNext();
		}
		
		public void previous()
		{
			c = c.getPrevious();
		}
		
		public boolean isFirst()
		{
			return( LinkedList.this.First == c );
		}
		
		/**
		  * Use this to iterate through the linked list.  eg:
		  *
		  * for( Iterator l = list.iterator(); l.isValid(); l.next() )
		  *    loop();
		 */
		public boolean isValid()
		{
			return( c != null );
		}
		
		public boolean isLast()
		{
			return( LinkedList.this.Last == c );
		}
		
		public void last()
		{
			c = LinkedList.this.Last;
		}
		
		public void first()
		{
			c = LinkedList.this.First;
		}
		
		public void insertBefore( Object o )
		{
			Element lle = new Element( o );

			lle.setPrevious( c.getPrevious() );
			lle.setNext( c );

			if( isFirst() )
				LinkedList.this.First = lle;
			
			if( c.getPrevious() != null )
				c.getPrevious().setNext( lle );
			
			c.setPrevious( lle );

			LinkedList.this.Count++;
		}
		
		public void insertAfter( Object o )
		{
			Element lle = new Element( o );

			lle.setNext( c.getNext() );
			lle.setPrevious( c );

			if( isLast() )
				LinkedList.this.Last = lle;

			if( c.getNext() != null )
				c.getNext().setPrevious( lle );
			
			c.setNext( lle );
			
			LinkedList.this.Count++;
		}

		public void remove()
		{
			if( isFirst() )
				LinkedList.this.First = c.getNext();
			if( isLast() )
				LinkedList.this.Last = c.getPrevious();

			if( c.getNext() != null )
				c.getNext().setPrevious( c.getPrevious() );

			if( c.getPrevious() != null )
				c.getPrevious().setNext( c.getNext() );

			LinkedList.this.Count--;
			
			c = c.getNext();
		}
	}
}
