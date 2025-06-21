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
**  $Id: Speaker.java,v 1.1 1999/10/11 14:04:32 noble Exp $
**
**  Class History
**
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  28Sep99     Noble       created this object
**
*/

package key.talker.objects;

import key.*;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.*;
import key.effect.ObjectEffect;

/**
 */
public class Speaker extends Prop
{
	private static final long serialVersionUID = 3541394343978335420L;
	
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Speaker.class, String.class, "keywords",
			AtomicElement.PUBLIC_ACCESSORS,
			"what words the Speaker will react to" ),
		AtomicElement.construct( Speaker.class, String.class, "responses",
			AtomicElement.PUBLIC_ACCESSORS,
			"the responses the Speaker gives" ),	
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Prop.STRUCTURE, ELEMENTS );
	
	private String[] words = new String[0];
	private String[] resps = new String[0];	
	
	public Speaker()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public String getKeywords()
	{
		return( Grammar.commaSeperate( words ) );
	}	
	
	public void setKeywords( String s )
	{
		StringTokenizer st = new StringTokenizer( s, "," );
		Vector v = new Vector( 5, 20 );
		
		while( st.hasMoreTokens() )
		{
			v.addElement( st.nextToken() );
		}
		
		if( words.length != v.size() )
			words = new String[ v.size() ];
		v.copyInto( words );
		v.setSize( 0 );
	}
	
	public String[] keywords( Player p )
	{
		return( words );
	}

	public String getResponses()
	{
		return( Grammar.commaSeperate( resps ) );
	}
	
	public void setResponses( String s )
	{
		StringTokenizer st = new StringTokenizer( s, "," );
		Vector v = new Vector( 5, 20 );
		
		while( st.hasMoreTokens() )
		{
			v.addElement( st.nextToken() );
		}
		
		if( resps.length != v.size() )
			resps = new String[ v.size() ];
		v.copyInto( resps );
		v.setSize( 0 );
	}
	
	public String[] responses( Player p )
	{
		return( resps );
	}
				
	public void splash( Effect e, SuppressionList s )
	{
		if( e instanceof key.effect.Communication )
		{
			// possibility for && operations on multiple keywords ie. if these 2 keywords present ->			
		
			String line = e.getMessage( null );
		
			String[] kw = keywords( null );
			String[] rp = responses( null );
		
			if( line != null && line.length() > 0)
			{		
				for( int j = 0; j < kw.length; j++ )
				{
						if(line.toLowerCase().indexOf( kw[j].toLowerCase() ) >= 0 ) // line contains keyword
						{
							if(j < rp.length) {
								
								String sentence = rp[j].trim();
								String verbword = null;
								
								if( sentence.startsWith("%e") )
								{
									verbword = " ";
								}
								else if( sentence.startsWith("%t") )
								{
									verbword = " thinks . o O ( ";
									
									sentence = sentence + " )";
								}
								else if( sentence.startsWith("%s") )
								{
									if(sentence.endsWith("?")) verbword = " asks '";
									else if(sentence.endsWith("!")) verbword = " exclaims '";
									else verbword = " says '";
									
									sentence = sentence + "'";	
								}
								else return; //setup incorrect - missing % , report error?
								
								new key.effect.ObjectEffect( this, null, (getFullPortrait(null) + verbword + sentence.substring(2).trim()), null ).cause();
								
								return; //only gives 1 response per line
							}
						}
	
				}
			}			
		}
	}


}
