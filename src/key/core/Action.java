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
**  $Id: Action.java,v 1.1 1999/10/11 13:25:03 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;
import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;


/**
  *  Action names should be verbs, such that "subtle may now nuke snapper",
  *  makes sense, where 'nuke' is the action name.  (I don't want to give
  *  anyone ideas.. I would never do such a thing to J ;p~)
  *
  *  If class is made un-final, change KeyInputStream to use instanceof
  *  instead of o.getClass() == Action.class
 */
public final class Action
implements Symbol, Serializable, key.io.Replaceable
{
	private static final long serialVersionUID = -9062224339411127098L;
	String name;
	boolean expert;
	boolean serious;
	Class in;  // the class this action is from
	
	public Action( Class from, String theName, boolean e, boolean s )
	{
		in = from;
		name = theName;
		expert = e;
		serious = s;
	}
	
	public String toString()
	{
		return( "(Action) " + getName() + ": expert is " + expert );
	}
	
	public final String getName()
	{
		return( name );
	}
	
	public final Object getKey()
	{
		return( name );
	}

	public final boolean getExpert()
	{
		return( expert );
	}

	public final boolean getSerious()
	{
		return( serious );
	}
	
	public final void setKey( Object key )
	{
		name = (String) key;
	}

	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	public final Object getReplacement()
	{
		try
		{
			java.lang.reflect.Field f = in.getDeclaredField( name + "Action" );
			return( f.get( null ) );
		}
		catch( Exception e )
		{
			throw new UnexpectedResult( e.toString() + " while resolving " + toString() );
		}
	}
}
