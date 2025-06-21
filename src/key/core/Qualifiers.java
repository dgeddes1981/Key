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
**  $Id: Qualifiers.java,v 1.1 1999/10/11 13:25:06 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.collections.StringKeyCollection;

import java.io.IOException;
import java.util.Enumeration;

/**
  *  Qualifier
  *
  *  This class it used to convert colour names into their
  *  respective codes, and vice versa.
  *
 */

public final class Qualifiers
{
	public static final String UNKNOWN_STRING = "unknown";
	public static final char UNKNOWN_CODE = ' ';

	public static final String SUPPRESSION_STRING = "suppressed";
	public static final char SUPPRESSION_CODE = '!';
	
	public static final String UNSUPPRESSION_STRING = "unsuppressed";
	public static final char UNSUPPRESSION_CODE = '#';
	
	/**
	  *  A StringKeyCollection containing all the possible
	  *  colour codes.
	 */
	private static StringKeyCollection colours;

	/**
	  *  A StringKeyCollection containing all the possible
	  *  colourable classes.  They're all subclasses
	  *  of communication.
	 */
	//private static StringKeyCollection classes;
	
	static
	{
		colours = new StringKeyCollection();

		try
		{
			colours.link( new QualifierCode( UNKNOWN_CODE, UNKNOWN_STRING ) );
			colours.link( new QualifierCode( SUPPRESSION_CODE, SUPPRESSION_STRING ) );
			colours.link( new QualifierCode( UNSUPPRESSION_CODE, UNSUPPRESSION_STRING ) );

				// iso 6429 defines

			colours.link( new QualifierCode( 'd', "dark") );
			colours.link( new QualifierCode( 'D', "grey" ) );
			colours.link( new QualifierCode( 'r', "red" ) );
			colours.link( new QualifierCode( 'R', "brightRed" ) );
			colours.link( new QualifierCode( 'g', "green" ) );
			colours.link( new QualifierCode( 'G', "brightGreen" ) );
			colours.link( new QualifierCode( 'y', "yellow" ) );
			colours.link( new QualifierCode( 'Y', "brightYellow" ) );
			colours.link( new QualifierCode( 'b', "blue" ) );
			colours.link( new QualifierCode( 'B', "brightBlue" ) );
			colours.link( new QualifierCode( 'm', "magenta" ) );
			colours.link( new QualifierCode( 'M', "brightMagenta" ) );
			colours.link( new QualifierCode( 'c', "cyan" ) );
			colours.link( new QualifierCode( 'C', "brightCyan" ) );
			colours.link( new QualifierCode( 'w', "white" ) );
			colours.link( new QualifierCode( 'W', "brightWhite" ) );
			
				// other defines
			
			colours.link( new QualifierCode( 'h', "hilight" ) );
			colours.link( new QualifierCode( '_', "underline" ) );
			colours.link( new QualifierCode( 'n', "normal" ) );
			colours.link( new QualifierCode( 'N', "brightNormal" ) );
			
				// defines that can't be used by users
			
			colours.link( new QualifierCode( '\021', "blink" ) );
			colours.link( new QualifierCode( '\022', "reverse" ) );
			
				//  feel free to add more colour codes here
		}
		catch( BadKeyException e )
		{
			Log.debug( "key.Qualifier", e.toString() + " while associating colours with their codes." );
		}
		catch( NonUniqueKeyException e )
		{
			Log.debug( "key.Qualifier", e.toString() + " while associating colours with their codes." );
		}
	}

	protected static QualifierCode getCode( char code )
	{
		for( Enumeration e = colours.elements(); e.hasMoreElements(); )
		{
			QualifierCode cc = (QualifierCode) e.nextElement();

			if( cc.code == code )
				return( cc );
		}

		return( null );
	}
	
	/**
	  *  Returns UNKNOWN_STRING if the 
	  *  colour can't be found.
	 */
	public static String getCodeName( char code )
	{
		QualifierCode cc = getCode( code );
		
		if( cc != null )
			return( cc.getName() );
		else
			return( UNKNOWN_STRING );
	}
	
	/**
	  *  Returns UNKNOWN_CODE if the colour
	  *  can't be found
	 */
	public static char getCodeCode( String name )
	{
		QualifierCode cc = (QualifierCode) colours.get( name );
		
		if( cc != null )
			return( cc.code );
		else
			return( UNKNOWN_CODE );
	}
}

final class QualifierCode implements Symbol
{
	char code;
	String name;

	public QualifierCode( char characterCode, String colourName )
	{
		code = characterCode;
		name = colourName;
	}

	public final String getName()
	{
		return( name );
	}

	public final Object getKey()
	{
		return( name );
	}
	
	public final void setKey( Object key )
	{
		name = (String) key;
	}

	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
}
