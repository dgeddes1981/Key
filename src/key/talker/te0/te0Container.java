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
**  $Id: te0Container.java,v 1.1 2000/01/27 13:27:15 noble Exp $
**
**  $Log: te0Container.java,v $
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
*/

package key.te0;

import key.*;

public class te0Container extends Container
{		
	private static final long serialVersionUID = -7555779863081354147L;

	public te0Container()
	{
		setConstraint( Type.NUMBEREDCONTAINER );
		
		try
		{		
			NumberedContainer articles = (NumberedContainer) Factory.makeAtom( NumberedContainer.class, "articles" );
			articles.argument( "te0Article" );
			add( articles );
			
			NumberedContainer surveys = (NumberedContainer) Factory.makeAtom( NumberedContainer.class, "surveys" );
			surveys.argument( "Survey" );
			add( surveys );
			
			NumberedContainer people = (NumberedContainer) Factory.makeAtom( NumberedContainer.class, "people" );
			people.argument( "te0Article" );
			add( people );
			
			NumberedContainer clans = (NumberedContainer) Factory.makeAtom( NumberedContainer.class, "clans" );
			clans.argument( "te0Article" );
			add( clans );				
			
			NumberedContainer objects = (NumberedContainer) Factory.makeAtom( NumberedContainer.class, "objects" );
			objects.argument( "te0Article" );
			add( objects );			
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "couldn't initialise te0 sub-containers", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "couldn't initialise te0 sub-containers", e );
		}
		catch( IllegalArgumentException e )
		{
			Log.error( "couldn't initialise te0 sub-containers", e );	
		}	
	}
}
