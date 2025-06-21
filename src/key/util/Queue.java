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
**  $Id: Queue.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.util;

import java.io.IOException;

/**
  * Implementation of a simple FIFO queue
  *
  * @version 1.00, 23Apr96
  * @author Paul Mclachlan
  *
 */
public class Queue
{
	Element First;
	Element Last;
	int Count;

    /**
	  * creates the queue
     */
	public Queue()
	{
		First = null;
		Last = null;
		Count = 0;
	}

	/**
	  * Adds another object to the queue
	  *
	  * @param store the object to be added
	 */
	public void push( Object store )
	{
		Element latest = new Element( store );

		if( Last != null )
			Last.setNext( latest );
		else
			First = latest;
		
		Last = latest;

		Count++;
	}
	
	/**
	  * Takes the first object off the queue and returns it
	  * @return the first object from the queue
	 */
	public Object pop() throws java.util.NoSuchElementException
	{
		if( First == null )
			throw new java.util.NoSuchElementException( "trying to pop past end of queue" );

		Object result=First.item();
		First = First.getNext();
		if( First == null )
			Last = null;
		Count--;

		return( result );
	}

	/**
	  * Returns the number of objects in the queue
	  * @return the number of objects in the queue
	 */
	public int count()
	{
		return( Count );
	}

	/* TEST SUITE */
/*
	void dump()
	{
		Element t;
		t = First;
		while( t != null )
		{
			System.out.println( t.item().toString() );
			t = t.getNext();
		}
	}

	public static void main( String args[] )
	{
		Queue main = new Queue();
		String entry;
		int count=0;

		do
		{
			System.out.println( "\nQueue contains " + main.count() + " elements" );
			entry = input( "dump,quit,push <item>,pop: " );
			if( entry.length() == 0 )
			{
			}
			else if( entry.equals( "dump" ) )
			{
				System.out.println( "\nDUMPING:\n--------" );
				main.dump();
			}
			else if( entry.equals( "pop" ) )
			{
				System.out.println( "Popping '" + main.pop().toString() +"'..." );
			}
			else if( entry.equals( "quit" ) )
			{
			}
			else if( entry.startsWith( "push " ) )
			{
				entry = entry.substring( 5 );
				System.out.println( "Pushing " + entry + "..." );
				main.push( entry );
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
				b = (char) System.in.read();
			catch( IOException e )
				{
						//  generally an IOException here means that
						//  the player in question has disconnected
					System.out.println( e.toString() );
					System.exit( 1 );
				}
			if( b != '\n' && b != 0 )
				buildInput.append( b );
			} while( b != '\n' && b != 0 );

		return( new String( buildInput ) );
	}
*/
	
	private static class Element
	{
		Object Stored;
		Element Next;
		
		/**
		  * Creates a new queue element to hold
		  * this object
		 */
		public Element( Object store )
		{
			Stored = store;
			Next = null;
		}

		/**
		  * Sets the value of 'next' to the given value
		  @param next the next element in the queue
		 */
		public void setNext( Element next )
		{
			Next = next;
		}

		public Element getNext()
		{
			return( Next );
		}

		public Object item()
		{
			return( Stored );
		}
	}
}
