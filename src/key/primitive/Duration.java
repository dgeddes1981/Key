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

package key.primitive;

import key.Grammar;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.StringTokenizer;
/**
  *  Represents an amount of time, such as 4 hours, or
  *  3 days, or whatever
 */
public final class Duration implements java.io.Serializable
{
	private static final long serialVersionUID = 7398203181405879584L;
	
	public static final String[] describe = { "measly", "measly", "pitiful", "mere", "total of", "total of", "total of", "total of", "total of", "amazing", "astounding", "shocking", "shocking" };
	
	long milliseconds;
	
	public Duration()
	{
		milliseconds = 0;
	}

	/**
	  *  Constructs a new time with the specified duration
	  *  in milliseconds
	 */
	public Duration( long duration )
	{
		milliseconds = duration;
	}

	public Object Clone()
	{
		return( new Duration( milliseconds ) );
	}

	public long getTime()
	{
		return( milliseconds );
	}

	public final long getHours()
	{
		return( milliseconds / SIH );
	}
	
	public String getDescriptive()
	{
		int measure = (int) Math.pow( (long)(milliseconds / SIM), 0.4 );
		
		if( measure >= describe.length )
			return describe[describe.length-1];
		else
			return describe[measure];
	}
	
	/*
	public void setTime( long duration )
	{
		milliseconds = duration;
	}
	*/

	/*  - find the places that do this and change the
	    - add method to be functional in nature
	public void add( Duration d )
	{
		milliseconds += d.milliseconds;
	}
	*/

	/**
	  * Adds the supplied duration number times
	 */
	public Duration addMultiple( Duration d, int number )
	{
		return( new Duration( milliseconds + (d.milliseconds * number) ) );
	}
	
	public void subtract( Duration d )
	{
		milliseconds -= d.milliseconds;
	}
	
	/**
	  *  Given a string that represents a duration, parse it into a
	  *  value that can be used to construct a Duration.  (Don't
	  *  tell anyone, but it returns the duration in milliseconds).
	 */
	public static long parse( String s ) throws NumberFormatException
	{
		StringTokenizer st = new StringTokenizer( s, "hmsMwdfyq", true );

		long millisecs = 0;
		Double length;
		char periodType;
		boolean negative = false;
		long working;

		while( st.hasMoreTokens() )
		{	
			length = Double.valueOf( st.nextToken() );
			if( st.hasMoreTokens() )
			{
				periodType = st.nextToken().charAt(0);
				switch( periodType )
				{
					case 'y':
						working = (long)( SIY * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 's':
						working = (long)( SIS * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'w':
						working = (long)( SIW * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'M':
						working = (long)( SIMTH * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'f':
						working = (long)( SIF * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'q':
						working = (long)( SIQ * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'd':
						working = (long)( SID * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'm':
						working = (long)( SIM * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
					case 'h': 
					default:
						working = (long)( SIH * length.doubleValue() );
						if( working < 0 )
						{
							negative = true;
							working *= -1;
						}
						millisecs += working;
						break;
				}
			}
		}

		if ( negative )
			millisecs *= -1;

		return( millisecs );
	}

	public static final long SIS=1000;			// milliseconds in a Second
	public static final long SIM=60 * SIS;		// milliseconds in a Minute
	public static final long SIH=60 * SIM;		// milliseconds in a Hour
	public static final long SID=24 * SIH;		// milliseconds in a Day
	public static final long SIW=7 * SID;		// milliseconds in a Week
	public static final long SIF=2 * SIW;		// milliseconds in a Fortnight
	public static final long SIMTH = 4 * SIW;  // milliseconds in a Month
	public static final long SIQ= 3 * SIMTH;   // milliseconds in a Quarter
	public static final long SIY=365 * SID;	// milliseconds in a Year


        /**
          *Return short version of time (ETB)
        */
	public String toShortString()
	{
		return( toShortString( milliseconds ) );
	}

	public static String toShortString( long milliseconds )
	{
		String theTime = "";
		String seconds = "";
		String minutes = "";
		long secs = 0;

		long yrs=0;
		long wks=0;
		long days=0;
		long hrs=0;
		long mins=0;

		secs = milliseconds - ( ( yrs = getYears( milliseconds ) ) * SIY );
		secs = secs - ( ( wks=getWeeks( secs ) ) * SIW );
		secs = secs - ( ( days=getDays( secs ) ) * SID );
		secs = secs - ( ( hrs=getHours( secs ) ) * SIH );
		secs = secs - ( ( mins=getMinutes( secs ) ) * SIM );
		secs = secs / 1000;

		if( secs < 10 )
			seconds = "0";
		else
			seconds = "";
		seconds += Long.toString( secs );

		if ( mins < 10 )
			minutes = " ";
		else 
			minutes = "";
		minutes += Long.toString( mins );
	
		theTime = minutes + ":" + seconds ;
		return theTime;
	}
	
	/**
	  *  This routine courtesy of Adam Teague (brann)
	 */
	public String toString()
	{
		return( toString( milliseconds, 6 ) );
	}

	public String toTruncString()
	{
		return( toString( milliseconds, 3 ) );
	}

	public String toLimitedString( int maxfields )
	{
		return( toString( milliseconds, maxfields ) );
	}
	
	public String toStdString( int maxfields )
	{
		return( toStdString( milliseconds, maxfields ) );
	}

	public static String toString( long milliseconds )
	{
		return( toString( milliseconds, 6 ) );
	}
	
	public static String toString( long milliseconds, int shrt )
	{
        String TheTime[];
        int count = 0;
        long secs = 0;
		
        long yrs = 0;
        long wks = 0;
        long days = 0;
        long hrs = 0;
        long mins = 0;
		
		if ( milliseconds < 0 )
			milliseconds *= -1;
		
        secs = milliseconds - ( ( yrs = getYears( milliseconds ) ) * SIY );
        secs = secs - ( ( wks = getWeeks( secs ) ) * SIW );
        secs = secs - ( ( days = getDays( secs ) ) * SID );
        secs = secs - ( ( hrs = getHours( secs ) ) * SIH );
        secs = secs - ( ( mins = getMinutes( secs ) ) * SIM );
        secs = secs / 1000;
		
		TheTime = new String[ 7 ];
		
		count=0;
		
		/*
		if( shrt < 6 )
		{
				//  this is a very rough time-rounding
				//  function.  What it does is round time
				//  to the nearest unit when a smaller 
				//  number of fields is specified.
			if( yrs != 0 && count < shrt )
				count++;
			if( wks != 0 )
			{
				if( wks > 26 && count < shrt )
					yrs++;
				
				count++;
			}
			if( days != 0 )
			{
				if( days > 3 && count < shrt )
					wks++;
				
				count++;
			}
			if( hrs != 0 )
			{
				if( hrs > 12 && count < shrt )
					days++;
			
				count++;
			}
			if( mins != 0 )
			{
				if( mins > 30 && count < shrt )
					hrs++;
				
				count++;
			}
		}
		*/
		
		count=0;
		
        if( yrs != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( yrs ) + " year" + ( yrs != 1 ? "s" : "" );
        if( wks != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( wks ) + " week" + ( wks != 1 ? "s" : "" );
        if( days != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( days ) + " day" + ( days != 1 ?
"s" : "" );
        if( hrs != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( hrs ) + " hour" + ( hrs != 1 ? "s" : "" );
        if( mins != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( mins ) + " minute" + ( mins != 1 ? "s" : "" );
        if( secs != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( secs ) + " second" + ( secs != 1 ? "s" : "" );
		
		if( count == 0 )
			return( "no time at all" );
		
		return( Grammar.enumerate( TheTime, count ) );
	}
	
	public static String toStdString( long milliseconds, int shrt )
	{
        String TheTime[];
        int count = 0;
        long secs = 0;
		
        long days = 0;
        long hrs = 0;
        long mins = 0;
		
		if ( milliseconds < 0 )
			milliseconds *= -1;
		
        secs = milliseconds - ( ( days = getDays( milliseconds ) ) * SID );
        secs = secs - ( ( hrs = getHours( secs ) ) * SIH );
        secs = secs - ( ( mins = getMinutes( secs ) ) * SIM );
        secs = secs / 1000;
		
		TheTime = new String[ 7 ];
		
		count=0;
		
		/*
		if( shrt < 4 )
		{
				//  this is a very rough time-rounding
				//  function.  What it does is round time
				//  to the nearest unit when a smaller 
				//  number of fields is specified.
			if( days != 0 )
			{
				count++;
			}
			if( hrs != 0 )
			{
				if( hrs > 12 && count < shrt )
					days++;
			
				count++;
			}
			if( mins != 0 )
			{
				if( mins > 30 && count < shrt )
					hrs++;
				
				count++;
			}
		}
		*/
		
		count=0;
		
        if( days != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( days ) + " day" + ( days != 1 ?
"s" : "" );
        if( hrs != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( hrs ) + " hour" + ( hrs != 1 ? "s" : "" );
        if( mins != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( mins ) + " minute" + ( mins != 1 ? "s" : "" );
        if( secs != 0 && count < shrt )
            TheTime[ count++ ] = Long.toString( secs ) + " second" + ( secs != 1 ? "s" : "" );
		
		if( count == 0 )
			return( "no time at all" );
		
		return( Grammar.enumerate( TheTime, count ) );
	}
	
	static private int getYears( long milliseconds )
	{
		return( (int)( milliseconds / SIY) );
	}

	static private int getWeeks( long milliseconds )
	{
		return( (int)( milliseconds / SIW) );
	}

	static private int getDays( long milliseconds )
	{
		return( (int)( milliseconds / SID) );
	}

	static private int getHours( long milliseconds )
	{
		return( (int)( milliseconds / SIH) );
	}

	static private int getMinutes( long milliseconds )
	{
		return( (int)( milliseconds / SIM) );
	}
}
