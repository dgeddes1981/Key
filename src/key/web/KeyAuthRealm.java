package key.web;

import java.util.*;
import java.io.*;

public class KeyAuthRealm
{
    private String name = null;
    private key.Reference path = null;
	private String pathName = null;
    /**
	  *  Construct realm from property file
      * @param name The name of the realm
     */
    public KeyAuthRealm(String name,String filename)
	throws IOException
    {
		this.name=name;
		key.Container a = (key.Container) new key.Search( filename, key.Key.instance() ).result;
		pathName = a.getId();
		
		if( a != null )
			path = a.getThis();
		else
			path = key.Reference.EMPTY;
    }
    
    /**
	  *  Construct empty realm
      * @param name The name of the realm
     */
    public KeyAuthRealm(String name)
    {
	this.name=name;
    }    
    
    public String getName()
    {
		return name;
    }

	public key.Reference getPath()
	{
		return( path );
	}

	public String toString()
	{
		return( "(" + name + "): " +pathName );
	}
}
