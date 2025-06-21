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
import key.util.LinkedList;

import java.util.Enumeration;

public final class LoginNotification
{
	private static LinkedList notifies;
	
	public static final void register( Atom t )
	{
		if( notifies == null )
			notifies = new LinkedList();
		
		notifies.append( t.getThis() );
	}
	
	public static final void unregister( Atom t )
	{
		if( notifies != null )
			notifies.removeEqual( t.getThis() );
	}
	
	public static final void notify( Effect t, SuppressionList s )
	{
		for( Enumeration e = notifies.elements(); e.hasMoreElements(); )
		{
			Reference r = (Reference) e.nextElement();
			
			if( !r.isValid() || !r.isLoaded() )
				notifies.remove( r );
			else
				((Atom)r.get()).splash( t, s );
		}
	}
}
