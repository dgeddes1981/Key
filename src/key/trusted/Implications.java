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
**  $Id: Implications.java,v 1.1.1.1 1999/10/07 19:58:39 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  19Aug98     subtle       start of recorded history
**
*/

package key;

/**
  *  Despite its ultra-trendy name, this is just a
  *  reference container which holds other groups
  *  that a specific group can 'imply'.
  *
  *  In the future, this is probably better done
  *  with a set of custom commands rather than a
  *  container - less resources.
 */
public class Implications extends Container
{
	public Implications()
	{
		super( true );
		setConstraint( Type.RANK );
	}

	protected void addInternal( Reference addedr ) throws BadKeyException,NonUniqueKeyException
	{
		Atom added = (Atom) addedr.get();
		
			//  it's important to ensure that the addition of
			//  this implication doesn't cause a circular
			//  reference...
		if( !(added instanceof Rank) )
			throw new TypeMismatchException( added.getName() + " isn't a rank, the required type for implications" );
		
		Atom p = getParent();
		
		if( p == added )
			throw new CircularException( "making an implication to ourselves is circular" );
		
		if( !(p instanceof Rank) )
			throw new UnexpectedResult( "implications doesn't seem to be linked to a rank" );
		
		Rank r = (Rank) p;
		
		if( ((Rank)added).doesImply( r ) )
			throw new CircularException( "making " + getName() + " imply " + r.getName() + " would cause a circular implication" );
		
		super.addInternal( addedr );
	}
}
