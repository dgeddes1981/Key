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

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;

/**
  *  Prop.
  *
  *  A simple class that has no behaviour, only a particular appearance,
  *  just like a stage prop.
 */
public class EW extends Prop implements Thing
{
	private static final long serialVersionUID = 5519580046563915867L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( EW.class, Integer.TYPE, "useCount",
			AtomicElement.PUBLIC_FIELD,
			"if > 0, specifies number of times this object may be used" ),
		AtomicElement.construct( EW.class, Material.class, "morphTo",
			AtomicElement.PUBLIC_FIELD,
			"when usecount is up, morphs to this" ),
		AtomicElement.construct( EW.class, Boolean.TYPE, "prize",
			AtomicElement.PUBLIC_FIELD,
			"true iff this object gives its value when used" ),
		AtomicElement.construct( EW.class, Boolean.TYPE, "spawn",
			AtomicElement.PUBLIC_FIELD,
			"true iff we don't remove the original object after morph" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Prop.STRUCTURE, ELEMENTS );
	
	public boolean prize = false;
	public boolean spawn = false;
	public int useCount = 0;
	public Reference morphTo = Reference.EMPTY;
	
	public EW()
	{
	}
	
	protected final boolean requiresState()
	{
		return( useCount > 0 );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void inspect( Player p, StringTokenizer args, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		super.inspect( p, args, ic, flags, item, originating );
		
		StringBuffer flagsb = new StringBuffer( "Flags: " );
		if( prize )
			flagsb.append( "[PRIZE] " );
		
		if( spawn )
		{
			if( morphTo.isValid() && !morphTo.equals( getThis() ) )
			{
				Atom a = null;
				
				try
				{
					a = morphTo.get();
					ic.send( "Spawns #" + a.getIndex() + ": " + ((Thing)a).getFullPortrait( p ) );
					flagsb.append( "[SPAWN] " );
				}
				catch( OutOfDateReferenceException e )
				{
					morphTo = Reference.EMPTY;
				}
			}
		}
		else
		{
			if( morphTo.isValid() && !morphTo.equals( getThis() ) )
			{
				Atom a = null;
				
				try
				{
					a = morphTo.get();
					ic.send( "MorphTo #" + a.getIndex() + ": " + ((Thing)a).getFullPortrait( p ) );
					flagsb.append( "[MORPH] " );
				}
				catch( OutOfDateReferenceException e )
				{
					morphTo = Reference.EMPTY;
				}
			}
		}
		
		if( useCount > 0 )
		{
			flagsb.append( "[USECOUNT=" );
			flagsb.append( useCount );
			flagsb.append( "]" );
		}
		
		ic.send( flagsb.toString() );

		if( getClass() == EW.class )
			ic.sendLine();
	}
	
	public boolean itemWasUsed( Player p, InteractiveConnection ic, Flags flags, Thing item, Container originating )
	{
		boolean didSomething = false;
		
		if( prize )
		{
			p.transferFlorins( this );
			didSomething = true;
		}
		
		if( useCount > 1 )
		{
			ic.send( "WARNING: State objects not implemented" );
			didSomething = true;
		}
		else
		{
				//  okay, if our morphTo is not empty, morph the object
			if( morphTo.isValid() && !morphTo.equals( getThis() ) )
			{
				Atom a = null;
				
				didSomething = true;
				
				try
				{
					a = morphTo.get();
				}
				catch( OutOfDateReferenceException e )
				{
					morphTo = Reference.EMPTY;
					ic.sendFailure( "Couldn't spawn, object is set up incorrectly" );
					return true;
				}
				
				if( spawn )
				{
						// charge player for florin value of spawned
					if( a instanceof Prop )
						p.subtractFlorins( ((Prop)a).getValue() );
				}
				else
				{
					try
					{
						if( originating instanceof Inventory )
							((Inventory)originating).stopWearing( (Atom) item );
						
						originating.remove( (Atom) item );
					}
					catch( Exception ex )
					{
						Log.error( ex );
						ic.sendFailure( "An error occurred while morphing your object.  Please email forest@realm.progsoc.uts.edu.au and tell them what happened, including the current time: " + new java.util.Date().toString() );
						return true;
					}
				}
				
				try
				{
					originating.add( a );
				}
				catch( Exception ex )
				{
					Log.error( ex );
				}
			}
				//  if morphTo -is- empty, but our useCount is 1,
				//  delete the object.
			else if( useCount == 1 && !spawn )
			{
				didSomething = true;
				
				try
				{
					if( originating instanceof Inventory )
						((Inventory)originating).stopWearing( (Atom) item );
						
					originating.remove( (Atom) item );
				}
				catch( Exception ex )
				{
					Log.error( ex );
				}
			}
		}
		
		return( didSomething );
	}
}
