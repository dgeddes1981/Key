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
**  $Id: Letter.java,v 1.1.1.1 1999/10/07 19:58:38 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import key.primitive.DateTime;
import key.util.MultiEnumeration;
import key.collections.*;

import java.util.StringTokenizer;
import java.util.Enumeration;
import java.io.*;

/**
  *  Some sort of note
 */
public class Letter extends Material
{
	private static final long serialVersionUID = 6963713531026101413L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Letter.class, Paragraph.class,
			"contents",
			AtomicElement.PUBLIC_FIELD,
			"the main text of the letter" ),
		AtomicElement.construct( Letter.class, String.class,
			"description",
			AtomicElement.PUBLIC_FIELD,
			"the subject or description of the letter" ),
		AtomicElement.construct( Letter.class, String.class,
			"from",
			AtomicElement.PUBLIC_FIELD,
			"the sender of the letter" ),
		AtomicElement.construct( Letter.class, DateTime.class,
			"when",
			AtomicElement.PUBLIC_FIELD,
			"the date the letter was sent" ),
		AtomicElement.construct( Letter.class, Integer.TYPE,
			"readCount",
			AtomicElement.PUBLIC_FIELD,
			"the number of times this letter has been read" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Atom.STRUCTURE, ELEMENTS );
	
	public Paragraph contents;
	public String description;
	public String from;
	public DateTime when;
	public int readCount;
	
	public static final int MAX_SUBJECT = 50;
	
	public Letter()
	{
		contents = null;
		description = "<no subject>";
		from = "";
		when = null;
		readCount = 0;
		
		setKey( new Integer( 0 ) );
		
		getPermissionList().allow( readAction );
	}
	
	private boolean sinceUpdate = true;
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
			//  a hack (you can remove) to keep the
			//  playerfiles accurate - we have this
			//  readAction, but we can't fix it
			//  in all places yet - it'll just have to
			//  be for new messages
		if( !sinceUpdate )
		{
			permissionList.allow( readAction );
			sinceUpdate = true;
		}
	}
	
	public void read( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container orig )
	{
		permissionCheck( readAction, true, false );
		readCount++;
		ic.sendLine();
		ic.send( " From: " + from );
		ic.send( " Date: " + when.toString( p ) );
		ic.send( " Subj: " + description );
		ic.blankLine();
		ic.send( contents );
		ic.sendLine();
	}
	
	public Paragraph getContents()
	{
		permissionCheck( readAction, true, false );
		return( contents );
	}
	
	public void setContents( Paragraph p )
	{
		permissionCheck( Atom.modifyAction, true, false );
		contents = p;
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getFullPortrait( Player p )
		{ return( "letter from " + from ); }
	
	//---  actions  ---//

	protected static StringKeyCollection staticActions;

		/**  is allowed to add objects to this container */
	public static Action readAction; 
	
	static
	{
		staticActions = new StringKeyCollection();
		
		readAction = newAction( Letter.class, staticActions, "read", true, true );
	}
	
	public Enumeration getActions()
	{
		return( new MultiEnumeration( staticActions.elements(), super.getActions() ) );
	}
	
	public boolean containsAction( Action a )
	{
		return( staticActions.contains( a ) || super.containsAction( a ) );
	}
	
	/**
	  *  Returns the action corresponding to the
	  *  supplied name.  This routine will need to
	  *  be overriden by sub-classes using actions.
	 */
	public Action getAction( String name )
	{
		Action a = (Action) staticActions.get( name );
		
		if( a == null )
			return( super.getAction( name ) );
		else
			return( a );
	}
}
