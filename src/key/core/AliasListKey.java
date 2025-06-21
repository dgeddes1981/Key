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
**  $Id: AliasListKey.java,v 1.1 1999/10/11 13:25:04 pdm Exp $
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
import java.util.Vector;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;
import java.util.NoSuchElementException;

public class AliasListKey implements java.io.Serializable
{
	private static final long serialVersionUID = -6663255759351326195L;
	protected String primaryKey;
	protected Vector secondarys;
	protected transient int[] secondaryHashcodes;
	protected transient int primaryHashcode;
	
	public AliasListKey()
	{
		primaryKey = "";
		secondarys = null;
		secondaryHashcodes = null;
	}
	
	public String getPrimary()
	{
		return( primaryKey );
	}
	
	public void setPrimary( String pk )
	{
		primaryKey = pk.toLowerCase();
		
		if( secondaryHashcodes != null )
			primaryHashcode = primaryKey.hashCode();
	}
	
	public void addSecondary( String sk )
	{
		ensureSecondary();
		secondarys.addElement( sk.toLowerCase() );
		secondaryHashcodes = null;
	}
	
	public void removeSecondary( String sk )
	{
		if( secondarys != null )
		{
			secondarys.removeElement( sk.toLowerCase() );
			secondaryHashcodes = null;
		}
	}
	
	public void copySecondaries( AliasListKey alk )
	{
		if( alk.secondarys != null )
			secondarys = (Vector) alk.secondarys.clone();
		else
			secondarys = null;
	}
	
	public Enumeration secondaryKeys()
	{
		ensureSecondary();
		return( secondarys.elements() );
	}
	
	public int countSecondaries()
	{
		if( secondarys == null )
			return( 0 );
		else
			return( secondarys.size() );
	}
	
	public boolean hasSecondaries()
	{
		return( !( secondarys == null || secondarys.size() == 0 ) );
	}
	
	private void ensureSecondary()
	{
		if( secondarys == null )
			secondarys = new Vector( 1, 3 );
	}
	
	public String toString()
	{
		return( primaryKey );
	}
	
	private void ensureHashcodes()
	{
		if( secondaryHashcodes == null )
		{
			primaryHashcode = primaryKey.hashCode();
			
			if( secondarys != null )
			{
				secondaryHashcodes = new int[ secondarys.size() ];
				
				for( int i = 0; i < secondarys.size(); i++ )
					secondaryHashcodes[i] = secondarys.elementAt( i ).hashCode();
			}
		}
	}
	
	/**
	  *  Returns the strength of this match (0 = no match, 1 = primary
	  *  match, 2 = first secondary, etc) based on the hashcode and 
	  *  string supplied.  hc = match.toLowercase().hashCode();
	 */
	public int getMatchStrength( int hc, String match )
	{
		ensureHashcodes();
		
		if( hc == primaryHashcode )
		{
			if( primaryKey.equalsIgnoreCase( match ) )
				return( 1 );
		}
		
		if( secondaryHashcodes != null )
		{
			for( int i = 0; i < secondaryHashcodes.length; i++ )
			{
				if( hc == secondaryHashcodes[i] )
				{
					if( ((String)secondarys.elementAt( i )).equalsIgnoreCase( match ) )
						return( i+2 );
				}
			}
		}
		
		return( 0 );
	}
}
