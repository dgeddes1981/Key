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

package key;

import key.collections.ShortcutCollection;
import key.collections.StringKeyCollection;

public class CommandList extends Container
{
	private static final long serialVersionUID = 7357197481333623369L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( CommandList.class, String.class,
		      "title",
			  AtomicElement.PUBLIC_FIELD,
			  "the printed title of this command list" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	String title;
	
	public CommandList()
	{
		contained = new ShortcutCollection();
		
		setConstraint( Type.COMMAND );
	}
	
	public CommandList( Object key )
	{
		this();
		setKey( key );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	protected void constructed()
	{
		super.constructed();
		
			//  default value of title is the name of this atom,
			//  that way we don't have to remember to set it, just
			//  change it, if we're that way inclined.
		title = getName();
	}
	
	public final String getTitle()
	{
		if( title == null )
			title = getName();
		
		return( title );
	}

	public void setTitle( String t )
	{
		title = t;
	}
}
