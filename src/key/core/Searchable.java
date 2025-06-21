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
**  $Id: Searchable.java,v 1.1 1999/10/11 13:25:07 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  22Aug98     subtle       created
**
*/

package key;

/**
  * @see key.Search
  */
public interface Searchable extends Symbol
{
	public void search( Search s ) throws InvalidSearchException;
}
