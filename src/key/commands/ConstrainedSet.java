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

package key.commands;

import key.*;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  This is a wonderful command that can be instantiated and used
  *  for all sorts of 'constraints' when allowing users to set
  *  things.
  *
  *  Basically, if one of the required properties is null, the
  *  user is prompted for it.  Here are the properties (in the
  *  order the user needs to type them, for your usage displays)
  *
  *  <UL>
  *   <LI> on - if not set, the user must put the name of the
  *             atom to be used here.
  *   <LI> set - the property to be set on that atom - must be
  *              put here, if not set in the command.
  *  </UL>
  *
  *  Followed by the string value to set, if the maximum length
  *  (set on the command) isn't 0.  If the length is zero, the
  *  property will simply be blanked.  (set to "")
 */
public class ConstrainedSet extends Set
{
	private static final long serialVersionUID = 3128665239895446113L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( ConstrainedSet.class, String.class, "blankFeedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent when the command is executed without arguments" ),
		AtomicElement.construct( ConstrainedSet.class, String.class, "filledFeedback",
			AtomicElement.PUBLIC_FIELD,
			"the message sent after the command is executed with arguments" ),
		AtomicElement.construct( ConstrainedSet.class, String.class, "setProperty",
			AtomicElement.PUBLIC_FIELD,
			"the constraint; the property to be set" ),
		AtomicElement.construct( ConstrainedSet.class, String.class, "displayProperty",
			AtomicElement.PUBLIC_FIELD,
			"the property used to display the value, if it is different to the setProperty" ),
		AtomicElement.construct( ConstrainedSet.class, Integer.TYPE, "maxLength",
			AtomicElement.PUBLIC_FIELD,
			"another constraint; the maximum length of the property" ),
		AtomicElement.construct( ConstrainedSet.class, Atom.class, "in",
			AtomicElement.PUBLIC_FIELD,
			"another constraint; the container the atom is in" ),
		AtomicElement.construct( ConstrainedSet.class, String.class, "on",
			AtomicElement.PUBLIC_FIELD,
			"another constraint; the atom to set the property on" ),
		AtomicElement.construct( ConstrainedSet.class, String.class, "mustContain",
			AtomicElement.PUBLIC_FIELD,
			"the set value must contain this string" ),
		AtomicElement.construct( ConstrainedSet.class, Boolean.TYPE, "subVerify",
			AtomicElement.PUBLIC_FIELD,
			"if true, prevents carats from appearing before the percent substitute code" ),
		AtomicElement.construct( ConstrainedSet.class, Boolean.TYPE, "canBeBlank",
			AtomicElement.PUBLIC_FIELD,
			"if true, will set the field to blank if unspecified" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Set.STRUCTURE, ELEMENTS );
	
	public static final char valueCode = 'v';
	public static final char targetCode = 't';
	
	public static final int DEFAULT_MAX_LENGTH = 60;
	
	public String blankFeedback = "The current value for this room is '%v'";
	public String filledFeedback = "The value now reads: %v";
	public String setProperty = "portrait";
	public String mustContain = "";
	public String displayProperty = "fullPortrait";
	public int maxLength = DEFAULT_MAX_LENGTH;
	public boolean subVerify = false;
	public boolean canBeBlank = false;
	public Reference in = Key.shortcuts().getThis();
	
	/**
	  *  use a string here because we can't have a relative
	  *  reference to a transient atom - which is precisely what
	  *  we want to achieve.
	 */
	public String on = "here";
	
	public ConstrainedSet()
	{
		setKey( "consset" );	//  a default
		usage = "<new message>";
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer args, String fullLine, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Container cin = null;
		
		try
		{
			cin = (Container) in.get();
		}
		catch( OutOfDateReferenceException e )
		{
			in = Reference.EMPTY;
			Log.error( "somebody set " + getId() + ".in wrong (disabled)", e );
			disable();
		}
		catch( ClassCastException e )
		{
			in = Reference.EMPTY;
			Log.error( "somebody set " + getId() + ".in wrong (disabled)", e );
			disable();
		}
		
		Atom r;
		
		if( cin == null )
			cin = Key.instance();
		
		String on = this.on;
		
			//  read the symbol from the arguments
		if( on == null )
		{
			if( args.hasMoreTokens() )
				on = args.nextToken();
			else
			{
					//  assuming they've set up the usage properly ;)
				usage( ic );
				return;
			}
		}
		
		r = (Atom) new Search( on, cin ).result;
		
		if( r == null )
		{
			ic.sendFailure( "Could not find '" + on + "' in " + cin.getId() );
			return;
		}
		
		p.putCode( targetCode, r.getName() );
		
		String pToSet = setProperty;
		
		if( pToSet == null )
		{
			if( args.hasMoreTokens() )
			{
				pToSet = args.nextToken();
			}
			else
			{
				usage( ic );
				return;
			}
		}
		
		if( maxLength > 0 )
		{
			String np;
			
			if( !args.hasMoreTokens() )
			{
				if( !canBeBlank )
				{
					usage( ic );
					
					{
						Object o = r.getProperty( displayProperty );
						if( o == null )
							o = "";
						
						p.putCode( valueCode, o.toString() );
					}
					
					ic.sendFeedback( Grammar.substitute( blankFeedback, p.getCodes() ) );
					return;
				}
				else
					np = "\"\"";
			}
			else
				np = args.nextToken( "" );
			
			if( maxLength < np.length() )
			{
				ic.sendError( "That is too long.  Please use at most " + maxLength + " character" + ((maxLength!=1) ? "s." : "." ) );
				return;
			}
			
			if( mustContain != null && mustContain.length() > 0 )
			{
				if( np.indexOf( mustContain ) == -1 )
				{
					ic.sendError( "The string must contain, at least '" + mustContain + "'." );
					return;
				}
			}
			
			if( subVerify )
			{
				if( np.indexOf( "^%" ) != -1 )
				{
					ic.sendError( "You may not place a carat (^^) before the substitute code (%)" );
					return;
				}
			}
			
			String fl = "." + pToSet + " " + np;
			
			Atom o = p.getContext();
			p.setContext( r );
			super.run( p, new StringTokenizer( fl ), fl, caller, ic, flags );
			p.setContext( o );
			
			{
				Object ob = r.getProperty( displayProperty );
				if( ob == null )
					ob = "";
				
				p.putCode( valueCode, ob.toString() );
			}
			
			ic.send( Grammar.substitute( filledFeedback, p.getCodes() ) );
		}
		else
		{
				//  as the length is 0 (or less, if they're stupid,
				//  we're just a blank command, not a set at all.
			r.setProperty( pToSet, "" );
			
			ic.send( Grammar.substitute( filledFeedback, p.getCodes() ) );
		}
	}
	
	protected void sendOkay( InteractiveConnection ic )
	{
	}
}
