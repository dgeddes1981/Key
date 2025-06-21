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
**  $Id: SurveyOption.java,v 1.1 2000/01/27 13:27:15 noble Exp $
**
**  $Log: SurveyOption.java,v $
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
*/

package key.te0;

import key.*;

public class SurveyOption extends Atom
{
	private static final long serialVersionUID = 4259738992195249580L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( SurveyOption.class, String.class, "value",
			AtomicElement.PUBLIC_FIELD,
			"description of this option" ),
		AtomicElement.construct( SurveyOption.class, Integer.TYPE, "votes",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"number of times this option has been chosen" ),			
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public String value = "";
	protected int votes;
	
	public SurveyOption()
	{
		votes = 0;
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public int getVotes()
	{
		return( votes );
	}
}
