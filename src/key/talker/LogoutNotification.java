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
** $Id: LogoutNotification.java,v 1.2 1999/11/10 16:12:28 pdm Exp $
**
** $Log: LogoutNotification.java,v $
** Revision 1.2  1999/11/10 16:12:28  pdm
** fixed whisper bug & eject bug
**
** Revision 1.1  1999/10/14 17:47:40  noble
** Created
**
**
*/

package key;
import key.util.LinkedList;

import java.util.Enumeration;

public final class LogoutNotification
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
		if( notifies != null )
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
}
