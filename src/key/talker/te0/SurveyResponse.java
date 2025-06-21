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
**  $Id: SurveyResponse.java,v 1.1 2000/01/27 13:27:15 noble Exp $
**
**  $Log: SurveyResponse.java,v $
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
*/

package key.te0;

import key.Reference;

public class SurveyResponse implements java.io.Serializable
{
	private static final long serialVersionUID = 2841324353574437420L;
	
	private Reference player;
	private int response;
	
	public SurveyResponse( Reference p, int r )
	{
		player = p;
		response = r;
	}

	public Reference getPlayer() { return( player ); }
	public int getResponse() { return( response ); }
}
