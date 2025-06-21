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
**  $Id: Landscape.java,v 1.1 1999/10/11 13:25:05 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;

public class Landscape extends Scape
{
	public Landscape()
	{
		this( false );
	}
	
	public Landscape( boolean reference )
	{
		super( reference );
		
		setConstraint( Type.LANDSCAPE );
	}

	public Landscape( Object key )
	{
		this();
		setKey( key );
	}
	
	public Enumeration locations()
	{
		return( elements() );
	}
}
