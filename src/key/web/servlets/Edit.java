package key.web.servlets;

import com.mortbay.Servlets.*;
import com.mortbay.Base.*;
import com.mortbay.HTML.*;
import com.mortbay.HTTP.*;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import key.primitive.*;
import key.*;

public class Edit extends HttpServlet
{
    String pageType;
	
    public void init(ServletConfig config) throws ServletException
    {
		super.init(config);
		
		pageType = getInitParameter(Page.PageType);
		if( pageType ==null )
			pageType = Page.getDefaultPageType();
    }
	
    public void doPost( HttpServletRequest request, HttpServletResponse response ) 
		throws ServletException, IOException
    {
		doGet( request,response );
    }
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException
    {
		String id = request.getParameter( "id" );
		String valueString = request.getParameter( "val" );
		
		if( id == null || id.length() <= 0 )
		{
			return;
		}
		
		response.setContentType( "text/html" );
		OutputStream out = response.getOutputStream();
		PrintWriter pout = new PrintWriter(out);
		Atom context = Key.instance();
		
		pout.write( "<HR>\n" + request.getServletPath() + "<HR>" );
		
		String formStart = "<FORM METHOD='POST' ACTION='" + request.getServletPath() + "'><INPUT TYPE='hidden' NAME='id' VALUE='" + id + "'>\n";
		String formEnd = "<INPUT TYPE=Submit></FORM>\n";
		
		Search search = new Search( id, Key.instance() );
		
		Object o = search.matchedResult;
		
		try
		{
			if( o instanceof AtomicElement )
			{
				AtomicElement ae = (AtomicElement) o;
				
				if( ae.isReadOnly() )
				{
					pout.write( "That property is read-only." );
					pout.flush();
					return;
				}
				else if( ae.isReference() )
				{
					if( valueString != null )
					{
						ae.setValue( search.lastAtom, valueString, context );
					}
					else
					{
						String cv = ((Atom)ae.getValue( search.lastAtom )).getId();
						
						if( ae.isAtomic() )
							pout.write( formStart + "<INPUT type='text' name='val' value='" + cv + "'>" + formEnd );
						else
							pout.write( cv + "\n" );
					}
				}
				else if( ae.isAtomic() )
				{
					if( valueString == null )
					{
						String cv = ((Atom)ae.getValue( search.lastAtom )).toString();
						pout.write( cv + "\n" );
					}
					else if( valueString.equalsIgnoreCase( Key.nullString ) )
						ae.setValue( search.lastAtom, null, context );
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
								
								ae.setValue( search.lastAtom, r, context );
							}
							catch( ClassNotFoundException e )
							{
								pout.write( "Type not found: " + e.getMessage() );
								pout.flush();
								return;
							}
							catch( IllegalAccessException e )
							{
								pout.write( "Error accessing type: " + e.getMessage() );
								pout.flush();
								return;
							}
						}
						else
						{
							pout.write( "No value supplied for atomic set." );
							pout.flush();
							return;
						}
					}
				}
				else
				{
					if( valueString == null )
					{
						Object cva = ae.getValue( search.lastAtom );
						if( cva instanceof TextParagraph )
						{
							String cv = ((TextParagraph)cva).getText() + "\n";
							AtomicSpecial special = ae.getSpecial();
							if( special instanceof TextParagraphLengthWrapper )
							{
								int max_lines = ((TextParagraphLengthWrapper)special).getLineLimit();
								int max_row = ((TextParagraphLengthWrapper)special).getByteLimit() / max_lines;
								
								pout.write( formStart + "<TEXTAREA name='val' rows=" + max_lines + " cols=" + max_row + ">" + cv + "</TEXTAREA>" + formEnd );
							}
							else
								pout.write( formStart + "<TEXTAREA name='val'>" + cv + "</TEXTAREA>" + formEnd );
						}
						else
						{
								//  look up the max size
							AtomicSpecial special = ae.getSpecial();
							if( special instanceof StringLengthWrapper )
							{
								int bl = ((StringLengthWrapper)special).getByteLimit();
								pout.write( formStart + "<INPUT size=" + bl + " maxlength=" + bl + " type='text' name='val' value='" + cva.toString() + "'>" + formEnd );
							}
							else
								pout.write( formStart + "<INPUT type='text' name='val' value='" + cva.toString() + "'>" + formEnd );
						}
					}
					else if( valueString.equalsIgnoreCase( Key.nullString ) )
						ae.setValue( search.lastAtom, null, context );
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
							
							ae.setValue( search.lastAtom, valueString, context );
						}
						else
						{
								//  we can convert a string to a lot of things, if
								//  we're clever, which we are ;)
							Class c = ae.getClassOf();
							
							if( c == Boolean.class || c == Boolean.TYPE )
							{
								if( valueString.equalsIgnoreCase( "true" ) )
									ae.setValue( search.lastAtom, Boolean.TRUE, context );
								else if( valueString.equalsIgnoreCase( "false" ) )
									ae.setValue( search.lastAtom, Boolean.FALSE, context );
								else
								{
									pout.write( "'" + valueString + "' can not be recognised as a boolean (use 'true' or 'false')" );
									pout.flush();
									return;
								}
							}
							else if( c == Integer.class || c == Integer.TYPE )
							{
								try
								{
									ae.setValue( search.lastAtom, new Integer( Integer.parseInt( valueString ) ), context );
								}
								catch( NumberFormatException e )
								{
									pout.write( "'" + valueString + "' can not be recognised as an integer (use digits)" );
									pout.flush();
									return;
								}
							}
							else if( c == Gender.class )
							{
									// should probably be in gender
								if( valueString.equalsIgnoreCase( "male" ) )
									ae.setValue( search.lastAtom, Gender.MALE_GENDER, context );
								else if( valueString.equalsIgnoreCase( "female" ) )
									ae.setValue( search.lastAtom, Gender.FEMALE_GENDER, context );
								else if( valueString.equalsIgnoreCase( "neuter" ) )
									ae.setValue( search.lastAtom, Gender.NEUTER_GENDER, context );
								else
								{
									pout.write( "'" + valueString + "' can not be recognised as a gender (use 'male', 'female' or 'neuter')" );
									pout.flush();
									return;
								}
							}
							else if( c == Duration.class )
							{
								try
								{
									ae.setValue( search.lastAtom, new Duration( Duration.parse( valueString ) ), context );
								}
								catch( NumberFormatException e )
								{
									pout.write( "'" + valueString + "' can not be recognised as a duration: " + e.getMessage() );
									pout.flush();
									return;
								}
							}
							else if( c == DateTime.class )
							{
								if( valueString.equalsIgnoreCase( "now" ) )
									ae.setValue( search.lastAtom, new DateTime(), context );
								else
									pout.write( "cannot coerce to datetime" );
							}
							else if( c == TextParagraph.class )
							{
								ae.setValue( search.lastAtom, new TextParagraph( valueString ), context );
							}
							else
							{
								pout.write( "cannot coerce to type " + Type.typeFor( c ).getName() );
								pout.flush();
								return;
							}
						}
					}
				}
			}
			else if( o != null )
			{
				pout.write( "That is not a property, it is: " + Type.typeOf( o ).getName() );
				pout.flush();
				return;
			}
			else
			{
				pout.write( "'" + id + "' not found." );
				pout.flush();
				return;
			}
			
			pout.write( "Okay<BR>\n" );
		}
		catch( IllegalAccessException e )
		{
			pout.write( "Illegal access: " + e.getMessage() );
		}
		catch( Exception e )
		{
			pout.write( e.getClass().getName() + ": " + e.getMessage() );
		}
		
		pout.flush();
    }
}
