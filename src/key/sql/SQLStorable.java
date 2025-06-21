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

package key.sql;

public interface SQLStorable
{
	public Object retrieveField( java.lang.reflect.Field f ) throws IllegalAccessException;
	
	/**
	  *  This routine should return -1 if the index has never been set.
	 */
	public int getSQLIndex();
	
	public void setSQLIndex( int i );
}
