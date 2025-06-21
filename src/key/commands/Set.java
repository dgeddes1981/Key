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
**  $Id: Set.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  24Aug98     subtle       start of recorded history
**
*/

package key.commands;

import key.*;
import key.primitive.*;
import java.io.IOException;
import java.util.StringTokenizer;

public class Set extends Command
{
	private static final long serialVersionUID = -5068724951236294607L;
	
	public Set()
	{
		setKey( "set" );
		usage = "<property> <value>";
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String propertyId = args.nextToken();
		
		if( !args.hasMoreTokens() )
		{
			usage( ic );
			return;
		}
		
		String valueString = args.nextToken( "" ).trim();
		
			// search for propertyId, set it to valueString
		Search search = new Search( propertyId, p.getContext() );
		
		Object o = search.matchedResult;
		
		try
		{
			if( o instanceof AtomicElement )
			{
				AtomicElement ae = (AtomicElement) o;
				
				if( ae.isReadOnly() )
				{
					ic.sendFailure( "That property is read-only." );
					return;
				}
				else if( ae.isReference() )
				{
					ae.setValue( search.lastAtom, valueString, p.getContext() );
				}
				else if( ae.isAtomic() )
				{
					if( valueString.equalsIgnoreCase( Key.nullString ) )
						ae.setValue( search.lastAtom, null, p.getContext() );
					else
					{
						StringTokenizer st = new StringTokenizer( valueString );
						
						if( st.hasMoreTokens() )
						{
							String className = st.nextToken();
							
							try
							{
								Atom r = (Atom) Factory.makeAtom( Class.forName( "key." + className ) );
								if( st.hasMoreTokens() )
									r.argument( st.nextToken() );
								
								ae.setValue( search.lastAtom, r, p.getContext() );
							}
							catch( ClassNotFoundException e )
							{
								ic.sendFailure( "Type not found: " + e.getMessage() );
								return;
							}
							catch( IllegalAccessException e )
							{
								ic.sendFailure( "Error accessing type: " + e.getMessage() );
								return;
							}
						}
						else
						{
							ic.sendFailure( "No value supplied for atomic set." );
							return;
						}
					}
				}
				else
				{
					if( valueString.equalsIgnoreCase( Key.nullString ) )
						ae.setValue( search.lastAtom, null, p.getContext() );
					else
					{
							//  special case for string assigns so we can
							//  have leading and trailing spaces
						if( ae.getClassOf() == String.class )
						{
							if( valueString.length() > 1 )
							{
								if( valueString.charAt( 0 ) == '\"' )
								{
									int i = valueString.lastIndexOf( '\"' );
									if( i != -1 )
										valueString = valueString.substring( 1, i );
								}
							}
							
							ae.setValue( search.lastAtom, valueString, p.getContext() );
						}
						else
						{
								//  we can convert a string to a lot of things, if
								//  we're clever, which we are ;)
							Class c = ae.getClassOf();
							
							if( c == Boolean.class || c == Boolean.TYPE )
							{
								if( valueString.equalsIgnoreCase( "true" ) )
									ae.setValue( search.lastAtom, Boolean.TRUE, p.getContext() );
								else if( valueString.equalsIgnoreCase( "false" ) )
									ae.setValue( search.lastAtom, Boolean.FALSE, p.getContext() );
								else
								{
									ic.sendFailure( "'" + valueString + "' can not be recognised as a boolean (use 'true' or 'false')" );
									return;
								}
							}
							else if( c == Integer.class || c == Integer.TYPE )
							{
								try
								{
									ae.setValue( search.lastAtom, new Integer( Integer.parseInt( valueString ) ), p.getContext() );
								}
								catch( NumberFormatException e )
								{
									ic.sendFailure( "'" + valueString + "' can not be recognised as an integer (use digits)" );
									return;
								}
							}
							else if( c == Gender.class )
							{
									// should probably be in gender
								if( valueString.equalsIgnoreCase( "male" ) )
									ae.setValue( search.lastAtom, Gender.MALE_GENDER, p.getContext() );
								else if( valueString.equalsIgnoreCase( "female" ) )
									ae.setValue( search.lastAtom, Gender.FEMALE_GENDER, p.getContext() );
								else if( valueString.equalsIgnoreCase( "neuter" ) )
									ae.setValue( search.lastAtom, Gender.NEUTER_GENDER, p.getContext() );
								else
								{
									ic.sendFailure( "'" + valueString + "' can not be recognised as a gender (use 'male', 'female' or 'neuter')" );
									return;
								}
							}
							else if( c == Duration.class )
							{
								try
								{
									ae.setValue( search.lastAtom, new Duration( Duration.parse( valueString ) ), p.getContext() );
								}
								catch( NumberFormatException e )
								{
									ic.sendFailure( "'" + valueString + "' can not be recognised as a duration: " + e.getMessage() );
									return;
								}
							}
							else if( c == DateTime.class )
							{
								if( valueString.equalsIgnoreCase( "now" ) )
									ae.setValue( search.lastAtom, new DateTime(), p.getContext() );
								else
									ic.sendFailure( "cannot coerce to datetime" );
							}
							else
							{
								ic.sendFailure( "cannot coerce to type " + Type.typeFor( c ).getName() );
								return;
							}
						}
					}
				}
			}
			else if( o != null )
			{
				ic.sendFailure( "That is not a property, it is: " + Type.typeOf( o ).getName() );
				return;
			}
			else
			{
				ic.sendFailure( "'" + propertyId + "' not found." );
				return;
			}
		}
		catch( IllegalAccessException e )
		{
			ic.sendError( "Illegal access: " + e.getMessage() );
		}
		
		sendOkay( ic );
	}

	protected void sendOkay( InteractiveConnection ic )
	{
		ic.sendFeedback( "Okay" );
		//Log.log( "level", p.getName() + ", from context '" + p.getContext().getId() + "': set " + propertyId + " " + valueString );
	}
}
