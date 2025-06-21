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
**  22Jun97     subtle       seperated SuppressionListEntry to its own
**                           public class
**  24Jul97     subtle       some small optimisations (not creating the
**                           vector unless something is actually added)
**  29Jul97     subtle       added idle msg support
**
*/

package key;

import key.util.EmptyEnumeration;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
  *  This class is optionally passed around to splash()
  *  routines, if it is (and not null), then the caller
  *  is interested when an effect they send is suppressed.
  * <br>
  *  This routine basically keeps a list of those atoms
  *  which have suppressed the effect.
 */
public class SuppressionList
{
	Vector components = null;

	/**
	  *  Specifies that the block was blocking
	  *  effects of this type.
	 */
	public static final int GENERAL = 0;

	/**
	  *  Specifies that the block was blocking
	  *  effects from this originator.
	 */
	public static final int SPECIFIC = 1;

	/**
	  *  Specified that its not a block,
	  *  that we're just idling.
	 */
	public static final int IDLING = 2;
	
	public SuppressionList()
	{
	}
	
	public void add( Atom c, int t, String msg )
	{
		String m = c.getName();

		if( t == GENERAL )
			m += Entry.GENERAL_DEFAULT;
		else if( t == SPECIFIC )
			m += Entry.SPECIFIC_DEFAULT;
		else if( t == IDLING )
			m += Entry.IDLING_DEFAULT;
		
		ensureComponents();
		components.addElement( new Entry( c, t, m + msg ) );
	}

	private void ensureComponents()
	{
		if( components == null )
			components = new Vector( 5 );
	}

	public int count()
	{
		if( components == null )
			return( 0 );
		else
			return( components.size() );
	}

	public Enumeration elements()
	{
		if( components == null )
			return( new EmptyEnumeration() );
		else
			return( components.elements() );
	}
	
	public static class Entry
	{
		public static final String GENERAL_DEFAULT = " is blocking: ";
		public static final String SPECIFIC_DEFAULT = " is blocking you: ";
		public static final String IDLING_DEFAULT = " is idle: ";
		
		Atom from;
		int type;
		String message;
		
		Entry( Atom a, int t, String msg )
		{
			from = a;
			type = t;
			message = msg;
		}

		public String getText()
		{
			return( message );
		}

		public Atom getFrom()
		{
			return( from );
		}

		public int getType()
		{
			return( type );
		}

		public boolean isActuallyBlocked()
		{
			return( type != SuppressionList.IDLING );
		}
	}
}
