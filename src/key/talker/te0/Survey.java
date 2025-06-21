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
**  $Id: Survey.java,v 1.2 2000/01/31 16:37:42 noble Exp $
**
**  $Log: Survey.java,v $
**  Revision 1.2  2000/01/31 16:37:42  noble
**  added aspect
**
**  Revision 1.1  2000/01/27 13:27:15  noble
**  creation
**
*/

package key.te0;

import key.*;

import java.util.Enumeration;
import java.util.Hashtable;

public class Survey extends NumberedContainer
{
	private static final long serialVersionUID = -1677160029700176849L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Survey.class, String.class, "topic",
			AtomicElement.PUBLIC_FIELD,
			"the topic of the survey" ),
		AtomicElement.construct( Survey.class, String.class, "author",
			AtomicElement.PUBLIC_FIELD,
			"the author of the survey" ),
		AtomicElement.construct( Survey.class, Integer.TYPE, "totalOptions",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"total number of options available" ),						
		AtomicElement.construct( Survey.class, Integer.TYPE, "totalComments",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"total number of comments by people" ),
		AtomicElement.construct( Survey.class, Paragraph.class, "description",
			AtomicElement.PUBLIC_FIELD,
			"a description of the survey" ),
		AtomicElement.construct( Survey.class, Boolean.TYPE, "voteOnce",
			AtomicElement.PUBLIC_FIELD,
			"true if you are not allowed to change your vote" )			
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public String topic;
	public String author;
	public Paragraph description = new TextParagraph( "" );
	public boolean voteOnce;
	
	Hashtable responses;
	Hashtable options;
	
	public Survey()
	{
		topic = "none";
		author = "";
		voteOnce = false;
		responses = new Hashtable();
		setConstraint( Type.SURVEYOPTION );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getTopic()
	{
		return( topic );
	}
	
	public void comment( Player p, int resp )
	{

			//  makes a comment for p
		Reference r = p.getThis();
		
		if( responses.containsKey( r ) && !voteOnce )  // player has voted already and is allowed to change
		{
			SurveyResponse sr = ( (SurveyResponse) responses.get( r ) );
			
			if( sr.getResponse() != resp ) // changing their vote
			{
				( (SurveyOption) getElementAt( sr.getResponse() ) ).votes--;
				responses.put( r, new SurveyResponse( r, resp ) );
				( (SurveyOption) getElementAt( resp ) ).votes++;
			}
			// else repeated vote, do nothing
		}
		else // first vote
		{
			responses.put( r, new SurveyResponse( r, resp ) );
			( (SurveyOption) getElementAt( resp ) ).votes++;
		}
	}
	
	public int getPlayerVote( Player p )
	{
		Reference r = p.getThis();
		
		if( responses.containsKey( r ) )
		{
			return( ( (SurveyResponse) responses.get( r ) ).getResponse() ) ;
		}
		
		return( -1 );  // hasnt voted
	}
	
	public void addOption( String option )
	{
		SurveyOption so = (SurveyOption) Factory.makeAtom( SurveyOption.class );
		so.value = option;
		
		try
		{
			add( so );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( "error adding new SurveyOption", e );
		}		
		catch( BadKeyException e )
		{
			Log.error( "invalid SurveyOption being added", e );
		}
		
		
	}
	
	public Enumeration responses()
	{
		return( responses.elements() );
	}
	
	public Enumeration options()
	{
		return( elements() );
	}	

	public int getTotalComments()
	{
		return( responses.size() );
	}
	
	public int getTotalOptions()
	{
		return( count() );
	}	
	
	/*
	public boolean assert()
	{
		int c = 0;
		SurveyOption so = null;
		
		for( Enumeration e = options(); e.hasMoreElements(); )
		{
			so = (SurveyOption) e.nextElement();
			
			c += so.votes;
		}
		
		return( getTotalComments() == c );
	}
	*/
	
	public Paragraph aspect()
	{
		MultiParagraph.Generator p = new MultiParagraph.Generator();
		
		p.append( new HeadingParagraph( topic ) );
		p.append( BlankLineParagraph.BLANKLINE );
		p.append( description );
		p.append( BlankLineParagraph.BLANKLINE );
		p.append( LineParagraph.LINE );
		
		StringBuffer sb = new StringBuffer();
		
		for( Enumeration e = options(); e.hasMoreElements(); )
		{
			SurveyOption so = (SurveyOption) e.nextElement();
			sb.append( "* " + so.value );
		}
		
		p.append( new TextParagraph( sb.toString() ) );
		p.append( new HeadingParagraph( "author: " + author, HeadingParagraph.RIGHT ) );
		
		return( p.getParagraph() );		
	}
}
