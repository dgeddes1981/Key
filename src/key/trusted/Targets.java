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
**  $Id: Targets.java,v 1.1.1.1 1999/10/07 19:58:40 pdm Exp $
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

/**
  *  Used to ... hold the ranks which this
  *  ranks can target.
  *
  *  In the future, this is probably better done
  *  with a set of custom commands rather than a
  *  container - less resources.
 */
public class Targets extends Container
{
	public Targets()
	{
		super( true );
		
		setConstraint( Type.TARGETABLE );
	}
	
	public boolean canTarget( Targetable t )
	{
		if( t instanceof Atom )
		{
			if( contains( (Atom) t ) )
				return( true );
			else if( t instanceof Rank )
			{
				for( Enumeration e = elements(); e.hasMoreElements(); )
				{
					Targetable a = (Targetable) e.nextElement();
					Rank r = (Rank) t;

					if( a instanceof Rank )
					{
						if( ((Rank)a).doesImply( r ) )
							return( true );
					}
				}
			}
		}
		
		return( false );
	}
}
