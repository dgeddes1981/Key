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
**  $Id: Symbol.java,v 1.1 1999/10/11 13:25:07 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  07Aug98     subtle       enhanced to support matching
**
*/

package key;

/**
  *  The symbol class is the basic interface that must be implemented by
  *  all symbolically (named) elements in the Key system.
  * <P>
  *  
  * @see key.Search
  */
public interface Symbol
{
	public Object getKey();
	public void setKey( Object key );
	public Object clone() throws CloneNotSupportedException;
	
	// META
	//public String getName();
}
