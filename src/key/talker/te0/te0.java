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
**  $Id: te0.java,v 1.4 2000/07/05 14:13:11 pdm Exp $
**
**  $Log: te0.java,v $
**  Revision 1.4  2000/07/05 14:13:11  pdm
**  blah
**
**  Revision 1.2  2000/05/05 17:55:58  noble
**  add footline
**
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
*/

package key.te0;

import key.*;

public class te0 extends Container
{
	private static final long serialVersionUID = 670816061119829015L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( te0.class, Integer.TYPE, "hits",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the total number of players logins ever" ),
		AtomicElement.construct( te0.class, Screen.class, "headline",
			AtomicElement.PUBLIC_FIELD,
			"upper front page message" ),
		AtomicElement.construct( te0.class, Screen.class, "footline",
			AtomicElement.PUBLIC_FIELD,
			"lower front page message" )		
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	public Screen headline = (Screen) Factory.makeAtom( Screen.class, "headline" );
	public Screen footline = (Screen) Factory.makeAtom( Screen.class, "footline" );
	
	protected int hits;
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}			
			
	public te0()
	{
		setConstraint( Type.TE0CONTAINER );
		
		hits = 0;
		
		// setup the required sub containers
		
		try
		{		
			te0Container approved = (te0Container) Factory.makeAtom( te0Container.class, "approved" );
			add( approved );
			
			te0Container submitted = (te0Container) Factory.makeAtom( te0Container.class, "submitted" );
			add( submitted );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "couldn't initialise te0 containers", e );
		}
		catch( BadKeyException e )
		{
			Log.error( "couldn't initialise te0 containers", e );
		}		
	}
	
	public void login( Player p )
	{
	
		// possibility of linking Player into te0 if it later becomes a scape
		// or a 'loggedIn' flag counting number -currently- viewing te0
		
		hits++;
		
		Log.log( "te0", p.getName() + " logged into te0" );
	}

	public int getHits()
	{
		return hits;
	}
	
	public Realm getTe0Realm()
	{
		return Key.instance().getDefaultRealm();
	}

	public Container getSubmitted()
	{
		return ( (Container) getElement( "submitted" ) );	
	}

	public Container getApproved()
	{
		return ( (Container) getElement( "approved" ) );
	}
	
	public Survey getCurrentSurvey()
	{
		return( (Survey) ( (Container) getApproved().getElement( "surveys" ) ).getElementAt( 0 )  );
	}	
	
}
