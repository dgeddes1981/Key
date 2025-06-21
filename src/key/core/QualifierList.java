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

package key;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class QualifierList
implements java.io.Serializable
{
	private static final long serialVersionUID = 745328818192364172L;
	Vector entries;
	
	public QualifierList()
	{
		entries = new Vector( 0, 3 );

			//  set up some default colours:
		set( Type.PLAYER, 'M' );
		set( Type.CLAN, 'B' );
		set( Type.FRIENDS, 'G' );
		set( Type.SHOUT, 'W' );
		set( Type.ROOM, 'c' );
		set( Type.PUBLICROOM, 'c' );
		set( Type.CONNECTION, 'r' );
		set( Type.MOVEMENT, 'R' );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			Log.debug( this, e.toString() );
		}
		finally
		{
			for( int i = 0; i < entries.size(); i++ )
			{
				Entry entry = (Entry) entries.elementAt( i );
				
				if( entry == null || entry.entryFor == null ||
				    entry.code == 'n' || entry.code == 'N' )
				{
					entries.removeElementAt( i );
					i--;
				}
			}
		}
	}
	
	public Enumeration elements()
	{
		return( entries.elements() );
	}
	
	public int count()
	{
		return( entries.size() );
	}
	
	public void set( Type c, char code )
	{
		if( code == Qualifiers.UNKNOWN_CODE )
			clear( c );
		else
		{
			Entry cle = getEntryFor( c );
			
			if( cle == null )
			{
				cle = new Entry( c );
				newEntry( cle );
			}
			
			cle.set( code );
		}
	}
	
	public void clear( Type c )
	{
		Entry cle = getEntryFor( c );
		
		if( cle != null )
			entries.removeElement( cle );
	}
	
	Entry getEntryFor( Type c )
	{
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			Entry cle = (Entry) e.nextElement();
			if( cle.isFor( c ) )
				return( cle );
		}
		
		return( null );
	}
	
	public char check( Type c )
	{
		Entry cle = getEntryFor( c );
		
		if( cle != null )
		{
			return( cle.get() );
		}
		
		return( Qualifiers.UNKNOWN_CODE );
	}
	
	protected void newEntry( Entry ent )
	{
		entries.addElement( ent );
	}
	
	public String toString()
	{
		if( count() == 0 )
		{
			return( "No list entries" );
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			sb.append( Integer.toString( count() ) + " entr" + ((count()==1) ? "y" : "ies") + " in the list:\n" );
			for( Enumeration e = elements(); e.hasMoreElements(); )
			{
				Entry cle = (Entry) e.nextElement();
				sb.append( "  " );
				sb.append( cle.toString() );
				sb.append( "\n" );
			}
		
			return( sb.toString() );
		}
	}
	
	public QualifierList.Immutable getImmutable()
	{
		return( new Immutable() );
	}
	
	public static final class Entry implements Serializable
	{
		private static final long serialVersionUID = 5079643376687355139L;
		
		Type entryFor;
		char code;
		
		/**
		  *  A mark is mainly used for the 'suppression' code.  You
		  *  see, if a player suppresses a channel, it is often a
		  *  temporary assignment, at which point they'll want to
		  *  return to the unsuppressed colour.  The mark is used
		  *  to store the 'old' colour.  Call mark() just before
		  *  setting the suppression code, and call reset() to
		  *  return to the 'previous' colour.  (Whatever that is)
		 */
		char mark;
		
		public Entry( Type entry )
		{
			entryFor = entry;
			code = Qualifiers.UNKNOWN_CODE;
			mark = Qualifiers.UNKNOWN_CODE;
		}

		public boolean isFor( Type test )
		{
			return( test == entryFor );
		}

		public Type getTypeFor()
		{
			return( entryFor );
		}

		public void set( char newCode )
		{
			if( newCode == Qualifiers.UNSUPPRESSION_CODE )
			{
				code = mark;
				mark = Qualifiers.UNKNOWN_CODE;
			}
			else
			{
				if( newCode == Qualifiers.SUPPRESSION_CODE && mark != Qualifiers.SUPPRESSION_CODE )
					mark = code;
				code = newCode;
			}
		}

		public char get()
		{
			return( code );
		}
		
		public char getMark()
		{
			return( mark );
		}
		
		public String toString()
		{
			return( entryFor.getName() + ": ^" + code + Qualifiers.getCodeName( code ) + "^-" );
		}
		
		public String toMarkedString()
		{
			return( entryFor.getName() + ": ^" + mark + Qualifiers.getCodeName( mark ) + "^- (suppressed)" );
		}
		
		public Immutable getImmutable()
		{
			return( new Immutable() );
		}
		
		public final class Immutable
		{
			Immutable()
			{
			}
			
			public boolean isFor( Type test )
			{
				return( QualifierList.Entry.this.isFor( test ) );
			}

			public Type getTypeFor()
			{
				return( QualifierList.Entry.this.getTypeFor() );
			}
			
			public char get()
			{
				return( QualifierList.Entry.this.code );
			}
			
			public char getMark()
			{
				return( QualifierList.Entry.this.mark );
			}
			
			public String toString()
			{
				return( QualifierList.Entry.this.entryFor.getName() + ": ^" + QualifierList.Entry.this.code + Qualifiers.getCodeName( QualifierList.Entry.this.code ) + "^-" );
			}
			
			public String toMarkedString()
			{
				return( "(" + QualifierList.Entry.this.entryFor.getName() + ": ^" + QualifierList.Entry.this.mark + Qualifiers.getCodeName( QualifierList.Entry.this.mark ) + "^-)" );
			}
		}
	}
	
	public class Immutable
	{
		public final int count()
			{ return( QualifierList.this.count() ); }
		public final char check( Type c )
			{ return( QualifierList.this.check( c ) ); }
		public final String toString()
			{ return( QualifierList.this.toString() ); }
		
		public final Enumeration elements()
		{
			return( new key.util.FilteredEnumeration(
				QualifierList.this.elements(),
				new key.util.FilteredEnumeration.Filter()
				{
					public boolean isValid( Object e, Enumeration en )
						{	return( true );	}
					
					public Object replace( Object element, Enumeration en )
					{
						return( ((Entry)element). new Immutable() );
					}
				} ) );
		}
	}
}
