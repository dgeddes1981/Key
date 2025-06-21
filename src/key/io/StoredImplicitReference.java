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

package key.io;

import key.Atom;
import key.Registry;
import key.OutOfDateReferenceException;

import java.io.*;

/**
  *  This is an object that is written to a serialized stream
  *  when implicit references are replaced.  It allows the input
  *  stream to detect an implicit reference and replace it with
  *  the loaded actual object.
  *
  *  If class is made un-final, change KeyInputStream to use instanceof
  *  instead of o.getClass() == StoredImplicitReference.class
  *
  *  This class is trusted - it calls Registry.get() which is not
  *  public.
 */
public final class StoredImplicitReference
	implements Serializable, Replaceable
{
	int index;
	int timestamp;
	
	public StoredImplicitReference( Atom a )
	{
		if( a == null )
		{
			index = -1;
			timestamp = -1;
		}
		else
		{
			index = a.getIndex();
			timestamp = a.getTimestamp();
		}
	}
	
	public Object getReplacement()
	{
			//  this code will never be executed for the initial
			//  database load - storedimplicitreferences are only
			//  written by the output stream when writing a distinct
			//  atom.  because of this we can guarantee that the other
			//  atom will already be in memory, or can be loaded from disk
			//
			//  A problem occurs if this other atom references this one
			//  with a stored implicit reference - in this case, we are
			//  saved only because an Atom calls 'makeTemporarilyAvailabile'
			//  as it is loaded, but before it's fields are loaded (including
			//  this field), which makes it available to get()s from circular
			//  references.
		try
		{
			return( Registry.instance.get( index, timestamp ) );
		}
		catch( OutOfDateReferenceException e )
		{
			return( null );
		}
	}
}
