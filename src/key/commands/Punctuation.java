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
**  $Id: Punctuation.java,v 1.1.1.1 1999/10/07 19:58:29 pdm Exp $
**
**  Class History
**  
**  Date        Name         Description
**  ---------|------------|-----------------------------------------------
**  21Jul97    subtle       added default command
**
*/

package key.commands;

import key.*;
import key.util.Trie;
import java.io.*;
import java.util.StringTokenizer;

/**
  *  This class is designed to be installed as having a name of "".
  *  This means that it will catch anything that is passed to a Trie
  *  that starts with something unmatchable ( ie, something not a-z ).
  *  This is just perfect for command shortcuts.  You'd almost think
  *  it was designed this way ;p~ - subtle
 */
public class Punctuation extends Command
{
	private static final long serialVersionUID = 1155529204820859205L;
	public static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Punctuation.class, Command.class, "tell",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the tell command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "remote",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the remote command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "say",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the say command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "emote",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the emote command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "think",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the think command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "whisper",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the whisper command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "shout",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the shout command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "help",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the help command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "reply",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the reply command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "ereply",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the ereply command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "echo",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the echo command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "recho",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the recho command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "distant",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the distant command" ),
		AtomicElement.construct( Punctuation.class, Command.class, "otherwise",
			AtomicElement.PUBLIC_FIELD,
			"a reference to the default command" )
	};
	
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Command.STRUCTURE, ELEMENTS );
	
	public Reference tell = Reference.EMPTY;
	public Reference remote = Reference.EMPTY;
	public Reference say = Reference.EMPTY;
	public Reference emote = Reference.EMPTY;
	public Reference think = Reference.EMPTY;
	public Reference whisper = Reference.EMPTY;
	public Reference shout = Reference.EMPTY;
	public Reference help = Reference.EMPTY;
	public Reference reply = Reference.EMPTY;
	public Reference ereply = Reference.EMPTY;
	public Reference echo = Reference.EMPTY;
	public Reference recho = Reference.EMPTY;
	public Reference distant = Reference.EMPTY;
	public Reference otherwise = Reference.EMPTY;
	
	public Punctuation()
	{
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	public void run( Player p, StringTokenizer st, String args, CategoryCommand caller, InteractiveConnection ic, Flags flags ) throws IOException
	{
		Command command = null;
		
		int i = 0;
		int l = args.length()-1;
		while( (i < l) && (Trie.negIndex( args.charAt( i ) ) != -1) )
			i++;
		
		switch( args.charAt( i ) )
		{
			case '.':  //  tell
			case '>':
				command = (Command) tell.get();
				break;

			case '<':  //  remote
			case ',':
				command = (Command) remote.get();
				break;
			
			case '\'':  //  say
			case '\"':
				command = (Command) say.get();
				break;

			case ':':  //  emote
			case ';':
				command = (Command) emote.get();
				break;

			case '~':  //  think
			case '`':
				command = (Command) think.get();
				break;
			
			case '=':  //  whisper
				command = (Command) whisper.get();
				break;
			
			case '!':  //  shout
				command = (Command) shout.get();
				break;
			
			case '?':  //  help
				command = (Command) help.get();
				break;
			
			case ']':  //  reply
				command = (Command) reply.get();
				break;
			
			case '[':  //  ereply
				command = (Command) ereply.get();
				break;
			
			case '+':  //  echo
				command = (Command) echo.get();
				break;
			
			case '-':  //  recho
				command = (Command) recho.get();
				break;
			
			case '@':  //  remote room command
				command = (Command) distant.get();
				break;
			
			default:
				command = (Command) otherwise.get();
				break;
		}
		
		if( command != null )
		{
			StringTokenizer new_st = new StringTokenizer( args.substring( i+1 ) );
			command.run( p, new_st, args, caller, ic, flags );
		}
		else
			ic.sendError( "No shortcut for '" + args.charAt( i ) + "'" );
	}
}
