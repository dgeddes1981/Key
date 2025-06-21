
/* ** Books section from people.jsp: ************

		else if ( action.equalsIgnoreCase( "books" ) )
		{
				Book spods = null;
			
				try {
					spods = (Book) new Search( "/var/books/spods", null ).result;
				}
				catch( InvalidSearchException ise ) {}
				
				if( spods==null )
				{
				%>
				<center>There is no Book of Spods.</center>
				<%
				}
				else
				{
					spods.read( null, null, new WebInteractiveConnection( out ), null, null, null);
					// horrible output
				}		
		}
		
********************************************** */



/* ** Entire contents of interactive register.jsp: ************

<%@ import="key.*" %>
<%@ import="key.web.*" %>
<%@ import="key.primitive.DateTime" %>
<%@ import="java.util.Enumeration" %>
<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>

<p>&nbsp;<p>
<center>

<%
try {
	if( session != null )
	{
		String errorstr = null;
	
		String supp = request.getParameter( "name" );
		String email = null;

		if ( supp != null )
		{
			Object a = Key.shortcuts().getExactElement( name );
		
			if( a != null )
			{
				supp = "";
				errorstr = "Sorry, but you can not use that name.";
			}			
			// check for existing player with that name
			// if there is , supp = "" and errorstr = "Already exists..."
			else
			{
			%>
				'<%=supp%>' not found in player database...
				<p>
			<%
			}
		}
		else
		{
			supp = "";
		}
		
		String event = request.getParameter( "register" );
		
		if ( register != null )
		{
			// attempt to create new player , errorstr
		}
		else
		{
			// gender = ""
			email = "";
		}

		if ( register == null || errostr == null )
		{
		
			// display form , using =supp =gender =email
			
			  This program requires that you specify your gender.  This is used for the purposes of correct grammar.  Below, enter either m or f to indicate whether you are male or female.
			  <p>
			  Male <input type=radio name=gender value=male> &nbsp;
			  Female <input type=radio name=gender value=female> &nbsp;
			  Neuter <input type=radio name=gender value=neuter>
			  <p>
			  In order to log into Forest it is necessary to enter your email address.  The way this works is that you type it in here, and we send an email to you with a 'verify code' in it.  When you get this email, telnet back here, type in your name and the code, and you're in.
			  <p>
			  Enter your email address: <input type=text size="40" name=title value="<%=email%>">
			  <p>
		}

	}
}
catch( Exception e )
{
	Log.debug( "register.jsp", e.toString() );
	%>
	<center>An error occured while generating this page.
	<p>
	Error: <font color=red><%=e.toString()%></font>
	<p>
	If the problem persists , please mail <a href="mailto:te0@realm.progsoc.uts.edu.au">te0@realm.progsoc.uts.edu.au</a> with a brief description of what went wrong.
	<p>
	Thanks , te0 management.
	<p>
	</center>
	<%
}
%>

</center>
<p>

********************************************** */