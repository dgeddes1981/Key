package key.web;

import com.mortbay.Base.*;
import com.mortbay.HTTP.*;
import com.mortbay.HTTP.Handler.*;
import com.mortbay.Util.ThreadPool;
import com.mortbay.Util.PropertyTree;
import java.util.*;
import java.io.*;

public class KeyAuthHandler extends NullHandler 
{
    /* ----------------------------------------------------------------- */
    private PathMap realms;
    
	public KeyAuthHandler()
	{
	}
	
	public KeyAuthHandler( Properties properties ) throws IOException
	{
		setProperties( properties );
	}
	
    public KeyAuthHandler(PathMap realms)
    {
		this.realms = realms;
    }
	
    /**
	  *  Configure from Properties.
      * Properties are assumed to be in the format of a PropertyTree
      * like:<PRE>
      * name.LABEL : The realm label
      * name.PATHS : /pathMap/entry;/list
      * name.PROPERTIES : fileNameOfLoginPasswordMapping
      * name.PROPERTY.login : password
      *</PRE>
      * @param properties Configuration.
     */
    public void setProperties(Properties properties)
	throws IOException
    {
		PropertyTree tree=null;
		if (properties instanceof PropertyTree)
			tree = (PropertyTree)properties;
		else
			tree = new PropertyTree(properties);
		Code.debug(tree);
		
		realms = new PathMap();
		
		Enumeration names = tree.getNodes();
		while (names.hasMoreElements())
		{
			String realmName = names.nextElement().toString();
			Code.debug("Configuring realm "+realmName);
			PropertyTree realmTree = tree.getTree(realmName);
			String rank = realmTree.getProperty( "ID" );
			String label = realmTree.getProperty( "LABEL" );
			
			if( rank != null && label != null )
			{
				KeyAuthRealm realm = new KeyAuthRealm(label, rank);
				Vector paths = realmTree.getVector("PATHS",",;");
				for( int r = paths.size(); r-- > 0; )
					realms.put( paths.elementAt(r), realm );
			}
			else
			{
				System.err.println( "Cannot configure " + realmName );
			}
		}

		Code.debug(realms);
    }
    
    /* ----------------------------------------------------------------- */
    public void handle(HttpRequest request,
		       HttpResponse response)
	 throws Exception
    {
		String address = request.getPathInfo();
		
		String path=realms.longestMatch(address);
		//t.setName( "" );
		
		if( path != null )
		{
			KeyAuthRealm realm = (KeyAuthRealm) realms.get( path );
			Code.debug( "Authenticate in Realm " + realm.getName() );
			
			String credentials =
			request.getHeader( HttpHeader.Authorization );
			
			if( credentials != null )
			{
				Code.debug( "Credentials: " + credentials );
				credentials =
					credentials.substring( credentials.indexOf( ' ' )+1 );
				credentials = B64Code.decode( credentials );
				int i = credentials.indexOf( ':' );
				String user = credentials.substring( 0,i );
				String password = credentials.substring( i+1 );
				request.setRemoteUser( "Basic", user );
				
				key.Reference cref = realm.getPath();
				
				key.Container c = (key.Container) cref.get();
				
				if( c != null )
				{
					key.Atom a = (key.Atom) c.getElement( user );
					
					if( password != null && a instanceof key.Player )
					{
						if( ((key.Player)a).authenticate( password ) )
						{
							//t.setName( ((key.Player)a).getName() );
							
							return;
						}
					}
				}
			}
			
			Code.debug("Unauthorized in "+realm.getName());
			
			response.setStatus(HttpResponse.SC_UNAUTHORIZED);
			response.setHeader(HttpHeader.WwwAuthenticate,
					   "basic realm=\""+realm.getName()+'"');
			response.writeHeaders();	    
		}
    }
}
