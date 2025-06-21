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
**  $Id: Gender.java,v 1.1.1.1 1999/10/07 19:58:37 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key.primitive;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

/**
  *  This class is immutable, hence it is secure to allow public final
  *  access to it, or to allow it to be returned from a method.
 */
public final class Gender
implements Serializable, key.io.Replaceable
{
	public static final int MALE=1;
	public static final int FEMALE=2;
	public static final int NEUTER=0;
	
	public static final Gender MALE_GENDER = new Gender( MALE );
	public static final Gender FEMALE_GENDER = new Gender( FEMALE );
	public static final Gender NEUTER_GENDER = new Gender( NEUTER );
	
	int gender;
	
	public Gender()
	{
	}
	
	public Gender( int g )
	{
		gender = g;
	}
	
	public Object getReplacement()
	{
		switch( gender )
		{
			case MALE:
				return( MALE_GENDER );
			case FEMALE:
				return( FEMALE_GENDER );
			default:
				return( NEUTER_GENDER );
		}
	}
	
	public final String toString()
	{
		return( maleFemale() );
	}
	
	public final String MaleFemale()
	{
		switch( gender )
		{
			case MALE:
				return( "Male" );
			case FEMALE:
				return( "Female" );
			default:
				return( "Neuter" );
		}
	}
	
	public final String maleFemale()
	{
		switch( gender )
		{
			case MALE:
				return( "male" );
			case FEMALE:
				return( "female" );
			default:
				return( "neuter" );
		}
	}

	public final String HimHer()
	{
		switch( gender )
		{
			case MALE:
				return( "Him" );
			case FEMALE:
				return( "Her" );
			default:
				return( "It" );
		}
	}
	
	public final String himHer()
	{
		switch( gender )
		{
			case MALE:
				return( "him" );
			case FEMALE:
				return( "her" );
			default:
				return( "it" );
		}
	}
	
	public final String HisHer()
	{
		switch( gender )
		{
			case MALE:
				return( "His" );
			case FEMALE:
				return( "Her" );
			default:
				return( "Its" );
		}
	}
	
	public final String hisHer()
	{
		switch( gender )
		{
			case MALE:
				return( "his" );
			case FEMALE:
				return( "her" );
			default:
				return( "its" );
		}
	}
	
	public final String HeShe()
	{
		switch( gender )
		{
			case MALE:
				return( "He" );
			case FEMALE:
				return( "She" );
			default:
				return( "It" );
		}
	}
	
	public final String heShe()
	{
		switch( gender )
		{
			case MALE:
				return( "he" );
			case FEMALE:
				return( "she" );
			default:
				return( "it" );
		}
	}
}
