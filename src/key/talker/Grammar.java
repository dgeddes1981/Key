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
**  $Id: Grammar.java,v 1.6 2000/05/02 14:22:14 noble Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.io.IOException;
import java.util.Enumeration;

/**
  * A simple grammar class.  Add routines to this
  * as they are required.
 */
public final class Grammar
{
	/**
	  *  There is 1 other person here
	  *  There are 3 other people here
	 */
	public static final String isAre( int n )
	{
		if( n == 1 )
			return( "is" );
		else
			return( "are" );
	}
	
	public static final String isAreCount( int n )
	{
		if( n == 0 )
			return( "aren't any" );
		else if( n == 1 )
			return( "is one" );
		else
			return( "are " + n );
	}
	
	public static final String noneOneCount( int n )
	{
		if( n == 0 )
			return( "none" );
		else if( n == 1 )
			return( "one" );
		else
			return( Integer.toString( n ) );
	}

	public static final String wasWere( int n )
	{
		if( n == 1 )
			return( "was" );
		else
			return( "were" );
	}
	
	public static final String vowels = "aeiouAEIOU";
	
	public static final String aAn( String w )
	{
		char a = w.charAt( 0 );
		
		if( vowels.indexOf( a ) == -1 )
			return( "a " + w );
		else
			return( "an " + w );
	}
	
	public static final String aAn( int number )
	{
		return( aAn( Integer.toString( number ) ) );
	}
	
	public static final String IsAre( int n )
	{
		if( n == 1 )
			return( "Is" );
		else
			return( "Are" );
	}

	public static final String personPeople( int n )
	{
		if( n == 1 )
			return( "person" );
		else
			return( "people" );
	}
	
	public static final String newbieNewbies( int n )
	{
		if( n == 1 )
			return( "newbie" );
		else
			return( "newbies" );
	}
	
	public static final String objectObjects( int n )
	{
		if( n == 1 )
			return( "object" );
		else
			return( "objects" );
	}
	
	public static final String PersonPeople( int n )
	{
		if( n == 1 )
			return( "person" );
		else
			return( "people" );
	}
	
	public static final String enumerate( String strings[], int count )
	{
		StringBuffer sb = new StringBuffer();
		int upto = 0;
		
		while( count > 2 )
		{
			sb.append( strings[upto] );
			sb.append( ", " );
			count--;
			upto++;
		}
		if( count > 0 )
		{
			sb.append( strings[upto] );
			count--;
			upto++;
		}
		if( count > 0 )
		{
			sb.append( " and " );
			sb.append( strings[upto] );
		}
		
		return( sb.toString() );
	}
	
	public static final String enumerate( String strings[] )
	{
		return( enumerate( strings, strings.length ) );
	}

	/**
	 */
	public static final String commaSeperate( Enumeration e )
	{
		if( e.hasMoreElements() )
		{
			StringBuffer sb = new StringBuffer( (String) e.nextElement() );
			
			while( e.hasMoreElements() )
			{
				sb.append( ", " );
				sb.append( (String) e.nextElement() );
			}
			
			return( sb.toString() );
		}
		else
			return( "" );
	}

	public static final String enumerate( Enumeration e )
	{
		if( e.hasMoreElements() )
		{
			String next = e.nextElement().toString();
			
			if( e.hasMoreElements() )
			{
				StringBuffer sb = new StringBuffer( next );
				next = e.nextElement().toString();
				
				while( e.hasMoreElements() )
				{
					sb.append( ", " );
					sb.append( next );
					next = e.nextElement().toString();
				}
				
				sb.append( " and " );
				sb.append( next );
				
				return( sb.toString() );
			}
			else
				return( next );
		}
		else
			return( "" );
	}

	public static final String onOff( boolean t )
	{
		if( t )
			return( "on" );
		else
			return( "off" );
	}
	
	public static final String commaSeperate( String[] strings )
	{
		StringBuffer sb = new StringBuffer();
		int count = strings.length;
		int upto = 0;
		
		while( count > 1 )
		{
			sb.append( strings[upto] );
			sb.append( ", " );
			count--;
			upto++;
		}
		
		if( count > 0 )
		{
			sb.append( strings[upto] );
			count--;
			upto++;
		}

		return( sb.toString() );
	}
	
	/**
	  * @param prompt the prompt to use
	  * @param def if they just hit return, assume yes?
	  * @param ic the interactive connection to prompt
	 */
	public static boolean getYesNo( String prompt, boolean def, InteractiveConnection ic ) throws IOException
	{
		String confirm;
		int v=0;
		
		do
		{
			confirm = ic.input( prompt );
			
			if( confirm.length() > 0 )
			{
				char c = confirm.charAt(0);
				
				if( c == 'y' || c == 'Y' )
					v = 1;
				else if( c == 'n' || c == 'N' )
					v = 2;
				else
					ic.send( "Please use 'y' or 'n' to respond." );
			}
			else
				v = def ? 1 : 2;
		} while( v == 0 );

		if( v == 1 )
			return( true );
		else
			return( false );
	}
	
	public static void getReturn( InteractiveConnection ic ) throws IOException
	{
		ic.input( "Press enter to continue: " );
	}
	
	public static final String[] emptyArray = new String[26];
	
	public static final String substitute( String format )
	{
		return( substitute( format, emptyArray ) );
	}
	
	public static final Paragraph substitute( Paragraph p )
	{
		return( p.substitute( emptyArray ) );
	}
	
	public static final Paragraph substitute( Paragraph p, String[] codes )
	{
		return( p.substitute( codes ) );
	}
	
	/**
	  *  The codes hashtable should be hashed on the 'character'
	  *  class, and contain strings
	 */
	public static String substitute( String format, String[] codes )
	{
		StringBuffer sb = new StringBuffer();
		
		int from = 0;
		while( true )
		{
			int i = format.indexOf( '%', from );
			if( i != -1 )
			{
				sb.append( format.substring( from, i ) );
				//Log.debug( "key.Grammar", "appended '" + format.substring( from, i ) + "'" );
				if( format.length() > i+1 )
				{
					char c = format.charAt( i+1 );
					if( c == '%' )
					{
						sb.append( '%' );
						from = i+2;
					}
					else if( c == '(' )
					{
							//  found a major substitute code: print the value of
							//  the property.
						String ts = format.substring( i+2 );
						int j = ts.indexOf( ')' );
						String ss = ts.substring( 0, j );
						Object o = null;
						
						//System.out.println( "Grammer found search string '" + ss + "' at index " + (i+2) + "," + (j+1) );
						
						try
						{
							o = new Search( ss, Key.instance() ).result;
							
							if( o == null )
								sb.append( "(" + Key.nullString + ")" );
							else
								sb.append( o.toString() );
						}
						catch( Exception e )
						{
							sb.append( "(" + e.toString() + ")" );
						}
						
						from = i+2+j+1;
					}
					else
					{
						//Log.debug( "key.Grammar", "found code '" + c.toString() + "'" );
						try
						{
							if( Character.isDigit(c) )
								c = (char) ( c + 48 );   // '1' becomes 'a' , '2' becomes 'b' ...
							
							String s = (String) codes[ c - 'a' ];
							if( s != null )
							{
								//Log.debug( "key.Grammar", "placing value '" + s + "'" );
								sb.append( s );
							}
						}
						catch( Exception e )
						{
							Log.error( "key.Grammar: attempted to place invalid code '" + c + "' (ignored) : ", e );
						}
						
						from = i+2;
					}
				}
				else
				{
					Log.debug( "key.Grammar", "skipping out - final string is '" + sb.toString() + "'" );
					break;
				}
			}
			else
			{
					//  shortcut to make nothing substitutes more efficient
				if( from == 0 )
					return( format );
				
				sb.append( format.substring( from ) );
				//Log.debug( "key.Grammar", "appending final '" + format.substring( from ) + "'" );
				//Log.debug( "key.Grammar", "clean finish - final string is '" + sb.toString() + "'" );
				break;
			}
		}
		
		return( sb.toString() );
	}

	/**
	  * @return true iff the string is of the form [a-zA-z]*
	 */
	public static boolean isStringCompletelyAlphabetical( String s )
	{
		for( int i = 0; i < s.length(); i++ )
		{
			if( !Character.isLetter( s.charAt( i ) ) )
				return( false );
		}

		return( true );
	}
	
	public static interface PagedListStuffer
	{
		public void stuffEntry( Reference r, TableParagraph.Generator tpg, int pageOffset, int totalOffset );
	}
	
	public static void pagedList( Container box, String screen, PagedListStuffer pls, InteractiveConnection ic, TableParagraph.Generator tpg, String contentType )
	{
		int count = box.count();
		if( count == 0 )
		{
			ic.send( "There aren't any messages in " + box.getName() );
			return;
		}
		
		char c;
		
		if( screen == null || screen.length() == 0 )
			c = 'a';
		else
			c = screen.toLowerCase().charAt( 0 );
		
		int page = c - 'a';
		int height = 20;
		int startAt = page*height;
		int endAt = ((page+1)*height);

		if( startAt < 0 || startAt > count )
		{
			ic.sendFailure( "That is not a valid page to view.  For example, use 'news read b' to view the second page.  (This will only work if you have more than one page)" );
			return;
		}
		
		int i = 0;
		
		for( Enumeration e = box.referenceElements(); i < endAt && e.hasMoreElements(); i++ )
		{
			Reference r = (Reference) e.nextElement();
			
			if( i < startAt )
				continue;
			
			pls.stuffEntry( r, tpg, i - startAt, i );
		}
		
		if( count < endAt )
			tpg.setFooter( i + " " + contentType + ((i==1)?"":"s") );
		else
			tpg.setFooter( contentType + " " + (startAt+1) + "-" + endAt + " of " + count );
		
		ic.send( tpg.getParagraph() );
	}
}
