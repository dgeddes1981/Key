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
**  $Id: WebAccessible.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  22Aug98     subtle       created
**
*/

package key;

public interface WebAccessible
{
	public String getContentType();
	public Paragraph getContent( String line );
}
