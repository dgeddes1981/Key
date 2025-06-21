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
**  $Id: DateTime.java,v 1.3 2000/01/03 14:02:40 subtle Exp $
**
**  $Log: DateTime.java,v $
**  Revision 1.3  2000/01/03 14:02:40  subtle
**  Fixed small AM/PM bug pointed out by goddess
**
**  Revision 1.2  1999/12/10 03:24:29  noble
**  tidied up toShortString()
**
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**  09Sep98     subtle       some optimisations
**
*/


package key.primitive;

import key.Player;
import key.AccessViolationException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public final class DateTime extends java.util.Date implements java.io.Serializable
{
	private static final long serialVersionUID = -8585425790209139587L;
	
	public DateTime()
	{
	}

	/**
	  *  Constructs a new time with the specified duration
	  *  in seconds
	 */
	public DateTime( long date )
	{
		super( date );
	}
	
	public DateTime( long date, Duration add )
	{
		super( date + add.getTime() );
	}
	
	/**
	  *  Calculates the difference between this datetime
	  *  and the supplied datetime
	 */
	public Duration difference( DateTime dt )
	{
		if( after( dt ) )
			return( new Duration( getTime() - dt.getTime() ) );
		else
			return( new Duration( dt.getTime() - getTime() ) );
	}
	
	/**
	  *  Increments this DateTime by the supplied Duration
	 */
	private DateTime add( Duration d )
	{
		return( new DateTime( d.getTime() + getTime() ) );
	}
	
	public String toString()
	{
		return( toString( null ) );
	}
	
	public String toString( Player observer )
	{
		DateTime n = this;
		String a = "";
		
		if( observer != null )
		{
			Duration d = observer.getTimezone();
			
			if( d.getTime() != 0 )
			{
				n = new DateTime( getTime() );
				n = n.add( d );
				a = " (your time)";
			}
		}

		String ampm = " AM";
		int hours = n.getHours();
		if( hours > 12 )
		{
			hours -= 12;
			ampm = " PM";
		}
		else if( hours == 12 )
			ampm = " PM";
		
		return( getDayStr( n.getDay() ) + " " + getMonthStr( n.getMonth() ) + " " + Integer.toString( n.getDate() ) + ", " + getYearStr( n.getYear() ) + " " + Integer.toString( n.getHours() ) + ":" + getMinuteStr( n.getMinutes() ) + ":" + getSecondStr( n.getSeconds() ) + ampm + a );
	}
	
	public String toShortString()
	{
		return( toShortString( null ) );
	}

	public String toShortString( Player observer )
	{
		return( toString( observer ).substring( 4, 16 ).trim() );
	}
	
	private static String days[] =
		{ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
	
	private static String getDayStr( int d )
	{
		return( days[d] );
	}
	
	private static String months[] =
		{ "Jan", "Feb", "Mar", "Apr", "May", "Jun",
		  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	private static String getMonthStr( int m )
	{
		return( months[ m ] );
	}
	
	private static String getYearStr( int y )
	{
		return( new String( Integer.toString( 1900 + y ) ) );
	}

	private static String getMinuteStr( int m )
	{
		if ( m < 10 )
			return( "0" + Integer.toString( m ) );
		else
			return( Integer.toString( m ) );
	}

	private static String getSecondStr( int s )
	{
		if ( s < 10 )
			return( "0" + Integer.toString( s ) );
		else
			return( Integer.toString( s ) );
	}
	
	private static String error = "cannot 'set' in immutable DateTime object";
	
	/*
	public void setTime( long time ) { throw new AccessViolationException( this, error ); }
	public void setYear( int year ) { throw new AccessViolationException( this, error ); }
    public void setMonth(int month) { throw new AccessViolationException( this, error ); }
    public void setDate(int date) { throw new AccessViolationException( this, error ); }
    public void setHours(int hours) { throw new AccessViolationException( this, error ); }
    public void setMinutes(int minutes) { throw new AccessViolationException( this, error ); }
    public void setSeconds(int seconds) { throw new AccessViolationException( this, error ); }
	*/
	
	/**
	  *  Returns a DateTime representing the current time + the duration
	 */
	public static DateTime nowPlus( Duration d )
	{
		return( new DateTime( d.getTime() + System.currentTimeMillis() ) );
	}
}
