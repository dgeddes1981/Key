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
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  18Jul97     subtle       created this class
**
*/

package key;

import java.net.*;
import java.io.*;
import java.util.*;

/**
  *  This classes isn't strictly _necessary_.  We use it to add
  *  some very forest specific behaviour to Key.  (Our special
  *  rooms, the concept of realms, which we never did finish,
  *  and our ranks.)  You could modify this fairly easily to do
  *  this for your own talker.  It isn't, of course, necessary
  *  to spell out your categories of rooms in seperate landscapes
  *  files here - indeed, most of this class only exists because we
  *  wanted to seperate the data into different files, as opposed
  *  to leaving it all in the main one (probably online, but who
  *  knows.)
 */
public class Forest extends Key
{
	private static final long serialVersionUID = 4168035326113564429L;
	
	public Forest()
	{
	}
	
	public String getCopyright()
	{
		return( "\n\n^gForest:  \"no harm intended\"; Key edition.^-\nForest was developed by the original Forest Board of Directors:\n\tsnapper, eXile, Merlin, subtle, druss, milamber, shimone." );
	}
}
