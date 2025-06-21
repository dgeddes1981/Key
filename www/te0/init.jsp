<%@ import="javax.servlet.*" %>
<%@ import="javax.servlet.http.*" %>
<%@ import="key.*" %>
<%@ import="key.te0.*" %>
<%
	// declare & initialise any variables that should be present through all JSPages.

	HttpSession session = request.getSession( true );
	Key key = Key.instance();
	te0 te0 = (te0) key.getElement( "te0" );
	Realm realm = te0.getTe0Realm();
%>