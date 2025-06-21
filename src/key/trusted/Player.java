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
** $Id: Player.java,v 1.33 2000/06/23 18:51:46 subtle Exp $
**
** $Log: Player.java,v $
** Revision 1.33  2000/06/23 18:51:46  subtle
** Added cool timeout code
**
** Revision 1.32  2000/06/19 20:40:49  subtle
** scan timeouts now deletes players
** temporary atoms are now cleaned up regularly
** added 'rescan' command to refresh friends lists (bug workaround)
** added 'cancel' command to cancel a scheduled event
**
** Revision 1.31  2000/05/22 13:38:56  subtle
** Misc. bug fixes
**
** Revision 1.30  2000/03/03 21:44:35  subtle
** Daily checkin.  My apologies if it doesn't compile. ;)
**
** Revision 1.29  2000/03/02 22:23:37  subtle
** first checkin of personal objects - objects owned by someone aren't accessible to other people or outside that persons rooms.  there are probably security flaws, this needs testing.
**
** Revision 1.28  2000/02/25 22:27:04  subtle
** weekend checkin, cool stuff ;)
**
** Revision 1.27  2000/02/25 13:43:34  subtle
** default size for cookies hashtable is more memory efficient
**
** Revision 1.26  2000/02/22 17:16:20  subtle
** bugfixes, added blockautohistory so it only triggers once
**
** Revision 1.25  2000/02/18 14:28:03  subtle
** imp noble's createSupplemental fix
**
** Revision 1.24  2000/02/18 14:13:25  subtle
** imp noble's createSupplemental fix
**
** Revision 1.23  2000/02/15 15:56:27  subtle
** allowed getRank() to return "staff" if .staff=true
**
** Revision 1.22  2000/02/11 16:07:16  subtle
** changes for portFrom for bigg
**
** Revision 1.21  2000/01/07 16:07:31  noble
** tidied code (removed old mail check)
**
** Revision 1.20  2000/01/07 15:55:44  noble
** no message
**
** Revision 1.19  2000/01/07 15:36:05  noble
** Fixed newmail bug
**
** Revision 1.18  2000/01/05 12:14:05  noble
** More minor colour codes
**
** Revision 1.17  1999/12/28 16:10:20  subtle
** notimeout code
**
** Revision 1.16  1999/11/23 21:37:17  pdm
** killed /sites
**
** Revision 1.15  1999/11/23 15:10:38  noble
** fixed some ^@ ^$ outputting
**
** Revision 1.14  1999/11/11 16:15:15  pdm
** added fix for who problems.
** made connectionstats non-final in Player.  THIS IS A SECURITY RISK, but necessary
** since we're getting filesystem corruption on our linux box & we have no way of
** resetting the field once it's cleared, otherwise.  Ideally, I guess, a hack should
** be put in the 'load object' routine that substitutes a StoredImplicit style object
** for a null value - and a blank object is auto-instantiated when the parent atom is
** reloaded.  For the moment, however, I'm just going to clear final variables &
** use that.
**
** Revision 1.13  1999/11/10 16:12:28  pdm
** fixed whisper bug & eject bug
**
** Revision 1.12  1999/10/19 16:13:51  pdm
** fixed 'cd' permitted arbitrary command execution
**
** Revision 1.11  1999/10/14 18:58:01  pdm
** moved recalculateIdlePrompt
**
** Revision 1.10  1999/10/13 16:52:27  pdm
** added recalculateIdlePrompt() to connect screen - just after 'isIdle = false'
**
** Revision 1.9  1999/10/13 14:20:25  pdm
** modified lag-threshold settings
**
** Revision 1.8  1999/10/12 17:54:05  pdm
** fix for room 'with me'.  also some additional inspect behaviour
**
** Revision 1.7  1999/10/12 16:52:31  pdm
** changed to make logging exceptions more explicit (include playername/date) &
** fix a spawn bug in EW
**
** Revision 1.6  1999/10/11 14:48:32  pdm
** sync-code changes, comments
**
** Revision 1.5  1999/10/11 14:10:41  pdm
** added cvs log stuff
**
*/

package key;

import key.primitive.*;
import key.util.LinkedList;
import key.util.FilteredEnumeration;
import key.util.MultiEnumeration;
import key.util.RecursiveEnumeration;
import key.util.CircularBuffer;
import key.collections.StringKeyCollection;

import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Hashtable;
import java.util.Vector;
import key.effect.*;
import key.util.StringTokenizer;
import key.commands.Go;

/**
  *  The player class holds all the player specific information, such as
  *  eachs players title, password, and received email.
 */
public final class Player
extends Container
implements Targetable,CommandContainer,Interactive,Splashable
{
	private static final long serialVersionUID = 8665452585164135888L;
	
	public static final int MAX_PLAN_LINES = 6;
	public static final int MAX_PLAN_BYTES = MAX_PLAN_LINES * 80;
	public static final int MAX_DESCRIPTION_LINES = 8;
	public static final int MAX_DESCRIPTION_BYTES = MAX_DESCRIPTION_LINES * 80;
	
	public static final int MAX_PROMPT_LENGTH = 20;
	public static final int MAX_TITLE_LENGTH = 60;
	public static final int MAX_PREFIX_LENGTH = 15;
	public static final int MAX_AKA_LENGTH = 20;
	public static final int MAX_WHEREFROM_LENGTH = 60;
	public static final int MAX_IDLEMSG_LENGTH = 70;
	public static final int MAX_BLOCKMSG_LENGTH = 70;
	public static final int MAX_BLOCKINGMSG_LENGTH = 70;
	public static final int MAX_LAST_CONNECT_FROM_SITE_LENGTH = 70;
	public static final int MAX_LOGIN_SCRIPT_LENGTH = 70;
	public static final int MAX_CONNECT_MSG_LENGTH = 40;
	public static final int MAX_OLD_LOGIN_LENGTH = 50;
	public static final int MAX_MODE_LENGTH = 50;
	public static final int MAX_COMMENT_LENGTH = 60;
	
	public static final int MAX_TERMINAL_NAME_LENGTH = 25;
	
	private static final AtomicElement[] ELEMENTS =
	{
		AtomicElement.construct( Player.class, String.class,
		    "password",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the players password" ),
		AtomicElement.construct( Player.class, String.class,
		    "prompt", "prompt",
			AtomicElement.PUBLIC_FIELD,
			"the players prompt",
			AtomicSpecial.StringLengthLimit( MAX_PROMPT_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"title", "title",
			AtomicElement.PUBLIC_FIELD,
			"a string that sometimes comes after the players name",
			AtomicSpecial.StringLengthLimit( MAX_TITLE_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"mode", "mode",
			AtomicElement.PUBLIC_FIELD,
			"a string that is prepended to every line typed",
			AtomicSpecial.StringLengthLimit( MAX_MODE_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"prefix", "prefix",
			AtomicElement.PUBLIC_ACCESSORS,
			"a string that sometimes comes before the players name",
			AtomicSpecial.StringLengthLimit( MAX_PREFIX_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"aka", "aka",
			AtomicElement.PUBLIC_FIELD,
			"'also known as', another name for the player",
			AtomicSpecial.StringLengthLimit( MAX_AKA_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"whereFrom", "whereFrom",
			AtomicElement.PUBLIC_FIELD,
			"physical location of the player",
			AtomicSpecial.StringLengthLimit( MAX_WHEREFROM_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"loginMsg", "loginMsg",
			AtomicElement.PUBLIC_FIELD,
			"Message appended to informs on login (prefix with ': ')",
			AtomicSpecial.StringLengthLimit( MAX_CONNECT_MSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"logoutMsg", "logoutMsg",
			AtomicElement.PUBLIC_FIELD,
			"Message appended to informs on logout (prefix with ': ')",
			AtomicSpecial.StringLengthLimit( MAX_CONNECT_MSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"enterMsg", "enterMsg",
			AtomicElement.PUBLIC_FIELD,
			"Message appended to enter room messages",
			AtomicSpecial.StringLengthLimit( MAX_CONNECT_MSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"exitMsg", "exitMsg",
			AtomicElement.PUBLIC_FIELD,
			"Message appended to exit room messages",
			AtomicSpecial.StringLengthLimit( MAX_CONNECT_MSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, TextParagraph.class,
			"description", "description",
			AtomicElement.PUBLIC_FIELD,
			"a description of the player",
				//  a players description is limited to 22 lines and 80*22 bytes
				//  also set in commands.Describe.java - redundant atm
			AtomicSpecial.TextParagraphLengthLimit( MAX_DESCRIPTION_BYTES,
			                                          MAX_DESCRIPTION_LINES ) ),
		AtomicElement.construct( Player.class, TextParagraph.class,
			"plan", "plan",
			AtomicElement.PUBLIC_FIELD,
			"the players plan",
			AtomicSpecial.TextParagraphLengthLimit( MAX_PLAN_BYTES,
			                                        MAX_PLAN_LINES ) ),
		AtomicElement.construct( Player.class, Gender.class,
			"gender",
			AtomicElement.PUBLIC_FIELD,
			"the players gender" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"quiet",
			AtomicElement.PUBLIC_FIELD,
			"if true, the player will never be 'beeped'" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
			"age",
			AtomicElement.PUBLIC_FIELD,
			"the players age" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
			"icq",
			AtomicElement.PUBLIC_FIELD,
			"the players icq#" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
			"florins",
			AtomicElement.PUBLIC_ACCESSORS,
			//AtomicElement.PUBLIC_FIELD,
			"the amount of money the player has" ),
		AtomicElement.construct( Player.class, Inventory.class,
			"inventory",
			AtomicElement.PUBLIC_FIELD,
			"the objects the player has" ),
		AtomicElement.construct( Player.class, NoKeyContainer.class,
			"objects",
			AtomicElement.PUBLIC_FIELD,
			"the individual objects the player has" ),
		AtomicElement.construct( Player.class, String.class,
			"forcedTerminal", "forcedTerminal",
			AtomicElement.PUBLIC_FIELD,
			"the terminal that the user has forced the use of",
			AtomicSpecial.StringLengthLimit( MAX_TERMINAL_NAME_LENGTH, false, false ) ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"willForceTerminal",
			AtomicElement.PUBLIC_FIELD,
			"true if the player wishes to override a detected terminal" ),
		AtomicElement.construct( Player.class, String.class,
			"idleMsg", "idleMsg", 
			AtomicElement.PUBLIC_FIELD,
			"the message output when the player is idle",
			AtomicSpecial.StringLengthLimit( MAX_IDLEMSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"sessionComment", "sessionComment", 
			AtomicElement.PUBLIC_ACCESSORS,
			"players comment to the current session",
			AtomicSpecial.StringLengthLimit( MAX_COMMENT_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, Integer.TYPE,
			"sessionTimestamp", 
			AtomicElement.PUBLIC_FIELD,
			"players timestamp for the current session" ),
		AtomicElement.construct( Player.class, String.class,
			"blockMsg", "blockMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message output when a message to this player is blocked",
			AtomicSpecial.StringLengthLimit( MAX_BLOCKMSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, String.class,
			"blockingMsg", "blockingMsg",
			AtomicElement.PUBLIC_FIELD,
			"the message output when this player is blocking all messages",
			AtomicSpecial.StringLengthLimit( MAX_BLOCKINGMSG_LENGTH, false, true ) ),
		AtomicElement.construct( Player.class, Duration.class,
			"timezone",
			AtomicElement.PUBLIC_FIELD,
			"the time-offset of this player compared to local time" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"isIdle",
			AtomicElement.PUBLIC_FIELD,
			"true if the player is currently idle" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"blockAutoHistory",
			AtomicElement.PUBLIC_FIELD,
			"true if the player doesn't want to see automatic history on connect to PublicRoom" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"hidden",
			AtomicElement.PUBLIC_FIELD,
			"true if the player is currently hidden" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"localecho",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.GENERATED,
			"true if we should echo to the player" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"hideRanks",
			AtomicElement.PUBLIC_FIELD,
			"true if the player doesn't want their ranks displayed" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"notimeout",
			AtomicElement.PUBLIC_FIELD,
			"true if the player won't timeout" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"liberated",
			AtomicElement.PUBLIC_FIELD,
			"true if the player can't be enrolled in a clan" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"brief",
			AtomicElement.PUBLIC_FIELD,
			"true if the player wants shorter output" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"privateEmail",
			AtomicElement.PUBLIC_FIELD,
			"true if the player wants an unlisted email address" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"privateFlorins",
			AtomicElement.PUBLIC_FIELD,
			"true if the player wants an unlisted florin count" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"citizen",
			AtomicElement.PUBLIC_FIELD,
			"true if the player is a citizen" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"expert",
			AtomicElement.PUBLIC_FIELD,
			"true if the player is in expert mode" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"hideTime",
			AtomicElement.PUBLIC_FIELD,
			"true if the player has hidden his logintime" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"newmail",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"true if the player has unread mail" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
			"titleTolerance",
			AtomicElement.PUBLIC_FIELD,
			"the largest number of people in the room with a verbose list" ),
		AtomicElement.construct( Player.class, DateTime.class,
			"banishedUntil",
			AtomicElement.PUBLIC_FIELD,
			"the date at which the player will be unbanished" ),
		AtomicElement.construct( Player.class, String.class,
			"banishType", "banishType",
			AtomicElement.PUBLIC_FIELD,
			"how the player is banished, 'S' for siteban too, 'B' otherwise",
			AtomicSpecial.StringLengthLimit( 1, false, false ) ),
		AtomicElement.construct( Player.class, String.class,
			"lastConnectFrom", "lastConnectFrom",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the last site the player connected from",
			AtomicSpecial.StringLengthLimit( MAX_LAST_CONNECT_FROM_SITE_LENGTH, false, false ) ),
		AtomicElement.construct( Player.class, CommandList.class,
			"commands",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the players personal command list" ),
		AtomicElement.construct( Player.class, CommandList.class,
			"specialCommands",
			AtomicElement.PUBLIC_FIELD,
			"the players custom command list" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"canSave",
			AtomicElement.PUBLIC_FIELD,
			"true iff the player has permission to write to disk" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"staff",
			AtomicElement.PUBLIC_FIELD,
			"true iff the player is a member of the talker staff" ),
		AtomicElement.construct( Player.class, MessageBox.class,
			"mailbox",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the players local mailbox" ),
		AtomicElement.construct( Player.class, TimeStatistics.class,
			"loginStats",
			AtomicElement.PUBLIC_FIELD,
			"the players login statistics" ),
		AtomicElement.construct( Player.class, ConnectionStatistics.class,
			"connectionStats",
			AtomicElement.PUBLIC_FIELD,
			"the players connection statistics" ),
		AtomicElement.construct( Player.class, Friends.class,
			"friends",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the players friend list" ),
		AtomicElement.construct( Player.class, Friends.class,
			"FriendRefTest",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the players friend list" ),
		AtomicElement.construct( Player.class, Group.class,
			"prefer",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"the players common mis-tell preferences" ),
		AtomicElement.construct( Player.class, InformList.class,
			"inform",
			AtomicElement.PUBLIC_FIELD | AtomicElement.ATOMIC,
			"this player wants to know when this list of people are online" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
			"informEveryone",
			AtomicElement.PUBLIC_FIELD,
			"this player wants to know when everyone connects" ),
		AtomicElement.construct( Player.class, Clan.class,
			"clan",
			AtomicElement.PUBLIC_FIELD,
			"the clan that the player is in" ),
		AtomicElement.construct( Player.class, Room.class,
			"home",
			AtomicElement.PUBLIC_FIELD,
			"the players home room" ),
		AtomicElement.construct( Player.class, Room.class,
			"location",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the players current location" ),
		AtomicElement.construct( Player.class, Realm.class,
			"realm",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the players current realm" ),
		AtomicElement.construct( Player.class, Room.class,
			"lastPublicRoomLocation",
			AtomicElement.PUBLIC_FIELD,
			"the players last location while in a public room" ),
		AtomicElement.construct( Player.class, Room.class,
			"loginRoom",
			AtomicElement.PUBLIC_FIELD,
			"the players requested login room" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
		    "saved",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"true if this player has been written to disk (at least once)" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
		    "beyond",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"true if this player has beyond access" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "scapeCount",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the number of scapes this player is in" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "rankCount",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the number of ranks this player is in" ),
		AtomicElement.construct( Player.class, InteractiveConnection.class,
		    "connection",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY,
			"the connection for this player (or null if not connected)" ),
		AtomicElement.construct( Player.class, String.class,
		    "titledName",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"the name and title of this player" ),
		AtomicElement.construct( Player.class, String.class,
		    "possessiveName",
			AtomicElement.PUBLIC_ACCESSORS | AtomicElement.READ_ONLY |
			AtomicElement.GENERATED,
			"the name of this player with 's appended" ),
		AtomicElement.construct( Player.class, EmailAddress.class,
		    "email",
			AtomicElement.PUBLIC_ACCESSORS,
			"the email address of this player" ),
		AtomicElement.construct( Player.class, Webpage.class,
		    "homepage",
			AtomicElement.PUBLIC_FIELD,
			"the homepage of this player" ),
		AtomicElement.construct( Player.class, Boolean.TYPE,
		    "showFlags",
			AtomicElement.PUBLIC_FIELD,
			"if this player is seeing tell codes " ),
		AtomicElement.construct( Player.class, String.class,
		    "oldLoginTime", "oldLoginTime",
			AtomicElement.PUBLIC_FIELD,
			"old EW forest login time",
			AtomicSpecial.StringLengthLimit( MAX_OLD_LOGIN_LENGTH, false, false ) ),
		AtomicElement.construct( Player.class, String.class,
		    "loginScript", "loginScript", 
			AtomicElement.PUBLIC_FIELD,
			"pipe seperated list of commands to be executed at login",
			AtomicSpecial.StringLengthLimit( MAX_LOGIN_SCRIPT_LENGTH, false, false ) ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "connecting",
			AtomicElement.PUBLIC_FIELD,
			"how many open reconnects?" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "examineHits",
			AtomicElement.PUBLIC_FIELD,
			"how many people have examined" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "fingerHits",
			AtomicElement.PUBLIC_FIELD,
			"how many people have fingered" ),
		AtomicElement.construct( Player.class, Integer.TYPE,
		    "portFrom",
			AtomicElement.PUBLIC_FIELD,
			"the port the player connected into, or -1" )
	};
	
		//  this class is immutable and final, therefore this is safe
	public static final AtomicStructure STRUCTURE = new AtomicStructure( Container.STRUCTURE, ELEMENTS );
	
	public static final int DEFAULT_MAX_OBJECTS = 4;
	public static final int DEFAULT_MAX_CUSTOMOBJECTS = 1;
	public static final int DEFAULT_MAX_MAIL = 20;
	public static final int CITIZEN_MAX_MAIL = 25;
	public static final int DEFAULT_MAX_FRIENDS = 50;
	public static final int CITIZEN_MAX_FRIENDS = 200;
	public static final int DEFAULT_MAX_PREFER = 10;
	public static final int DEFAULT_MAX_INFORM = 30;
	public static final int MAX_NAME = 16;
	public static final int MIN_NAME = 3;

	public static final int SHOWFLAG_DIRECTED = 0;
	public static final int SHOWFLAG_FRIENDS = 1;
	public static final int SHOWFLAG_ROOM = 2;
	public static final int SHOWFLAG_SHOUTS = 3;
	public static final int SHOWFLAG_ENTER = 4;
	public static final int SHOWFLAG_LEAVE = 5;
	public static final int SHOWFLAG_LOGIN = 6;
	public static final int SHOWFLAG_LOGOUT = 7;
	public static final int SHOWFLAG_BLOCKING = 8;  // name blocks the clan chan
	public static final int SHOWFLAG_OBJECTS = 9;
	public static final int SHOWFLAG_MUSIC = 10;
	
	public static final char[] SHOWCHARS =
		{ '>', '*', '-', '!', ')', '(', '}', '{', '#', '+', '/' };
	
	/** The players connection */
	transient InteractiveConnection ic;
	
	/** The players location */
	//  some confusion about this vs the property - I think the
	//  property might be the 'last public room' location.
	transient Room location;
	
	transient Reference realm;
	
	/** The current context for the player */
	transient Atom context;
	
	/** a reverseReference style linked list of the scapes this player is in */
	transient Vector reverseScapes;
	
	public static final int MAX_HISTORY_LINES = 10;
	
	/** a list of communication to/from this player */
	private transient CircularBuffer history;
	
	/**
	  *  This is not the definitive reference as to the ranks a player is in,
	  *  although it is reasonable to expect that it is up to date.  This
	  *  data structure gets updated by Rank.java as players are added and
	  *  removed to a rank.  It is used to load Ranks into memory as a player
	  *  comes online, allowing them to detect that the Player has come online,
	  *  and link them into the rank if they wish.
	 */
	Vector reverseRanks = new Vector( 5, 5 );
	
	/** a list of the people in the multi to reply to */
	//transient LWPlayerGroup replyList;
	
	/** a linkedList of qualifier ref's */
	QualifierList qualifierList;
	
	/** Whether the player is 'beyond' (account access) or not */
	private boolean beyond;
	private boolean canPage = true;
	
	boolean saved;

	public String getName()
	{
		return( super.getName() );
	}
	
	public Object createSupplemental()
	{
		return( new PlayerSupplemental( this, email ) );
	}
	
	boolean showFlags;
	
	boolean wordwrap = true;
	
	//--- system accessable properties ---//
	private Password password = new Password();
	String prompt = "-> ";
	String title = " the newbie, so please treat me nicely";
	String mode = null;
	String prefix = "";
	String aka = "";
	String whereFrom = "";
	TextParagraph description = new TextParagraph();
	TextParagraph plan = new TextParagraph();
	Gender gender = Gender.NEUTER_GENDER;
	boolean quiet = false;
	int age = 0;
	int icq = 0;
	int florins = 0;
	String forcedTerminal = null;
	boolean willForceTerminal = false;
	String idleMsg = "";
	String blockMsg = "";
	String blockingMsg = "";
	String loginMsg = "";
	String logoutMsg = "";
	String enterMsg = "appears from no-where.";
	String exitMsg = "slips sideways and disappears.";
	Duration timezone = new Duration();
	boolean isIdle = false;
	boolean hidden = false;
	boolean hideRanks = false;
	boolean notimeout = false;
	boolean liberated = false;
	boolean brief = false;
	boolean privateEmail = true;
	boolean privateFlorins = false;
	boolean citizen = false;
	boolean expert = false;
	boolean hideTime = false;
	boolean informEveryone = false;
	int titleTolerance = 20;
	DateTime banishedUntil = null;
	String banishType = "";
	String lastConnectFrom = "";
	Reference commands = Reference.EMPTY;
	Reference specialCommands = Reference.EMPTY;
	String oldLoginTime = "";
	
	transient String sessionComment;
	transient int sessionTimestamp;
	transient boolean blockAutoHistory = false;
	
	boolean canSave = true;
	boolean staff = false;
	int fingerHits = 0;
	int examineHits = 0;
	
	int revision = 0;
	
		//  it isn't a security violation to have these fields public,
		//  as they are final, and they look after their own immutability.
	public TimeStatistics loginStats = (TimeStatistics) Factory.makeAtom( TimeStatistics.class, "loginStats" );
	public ConnectionStatistics connectionStats = (ConnectionStatistics) Factory.makeAtom( ConnectionStatistics.class, "connectionStats" );
	
	public Reference FriendRefTest = Reference.EMPTY;
	
	public Friends friends = (Friends) Factory.makeAtom( Friends.class, "friends" );
	public Group prefer = (Group) Factory.makeAtom( Group.class, "prefer" );
	public InformList inform = (InformList) Factory.makeAtom( InformList.class, "inform" );
	public Inventory inventory = (Inventory) Factory.makeAtom( Inventory.class, "inventory" );
	public NoKeyContainer objects = (NoKeyContainer) Factory.makeAtom( NoKeyContainer.class, "objects" );
	public MessageBox mailbox = (MessageBox) Factory.makeAtom( MessageBox.class, "mailbox" );
	
	Reference clan = Reference.EMPTY;
	Reference room = Reference.EMPTY;
	Reference lastPublicRoomLocation = Reference.EMPTY;
	Reference loginRoom = Reference.EMPTY;
	Reference home = Reference.EMPTY;
	String loginScript = "orient|l";
	
	public boolean getSaved() { return( saved ); }
	public boolean getBeyond() { return( beyond ); }
	public int getScapeCount() { return( reverseScapes.size() ); }
	public int getRankCount() { return( reverseRanks.size() ); }
	
	private EmailAddress email = new EmailAddress();
	Webpage homepage = new Webpage();
	//--- end accessable properties ---//
	
	static char originatorCode = 'o';
	
	/**  Codes used for % replacements when this player does a command */
	transient String[] codes;

	/**  Used to store the information which allows the 'repeat' command to work */
	transient Repeated repeat;
	
		//  for detecting the player idling out in the latencycache
	private transient LatentlyCached latentCacheHook;
	
	/**  Constructs a player */
	Player()
	{
		setLimit( DEFAULT_MAX_OBJECTS );
		
		ic = null;
		location = null;
		realm = Key.instance().getDefaultRealm().getThis();
		beyond = false;
		saved = false;
		
			//  this now static
		//originatorCode = new Character( 'o' );

		init();
		qualifierList = new QualifierList();
		
		showFlags = true;
		
		getMailbox().setLimit( DEFAULT_MAX_MAIL );
		getFriends().setLimit( DEFAULT_MAX_FRIENDS );
		getPrefer().setLimit( DEFAULT_MAX_PREFER );
		getInform().setLimit( DEFAULT_MAX_INFORM );
		getObjects().setLimit( DEFAULT_MAX_CUSTOMOBJECTS );
		
			//  just a default so that a new player *has* some commands...
		commands = ((Atom) Key.instance().commandSets.getElement( "base" )).getThis();
		
			//  set up some default permissions
			//  generally, its okay to talk to people & friend them
		permissionList.allow( tellAction );
		permissionList.allow( friendAction );
		
			//  and its okay to send them email
		getMailbox().getPermissionList().allow( Container.addToAction );
		getInventory().getPermissionList().allow( Container.addToAction );
	}
	
	private void readObject( ObjectInputStream ois ) throws IOException
	{
		try
		{
			ois.defaultReadObject();
		}
		catch( ClassNotFoundException e )
		{
			throw new UnexpectedResult( e.toString() );
		}
		
		switch( revision )
		{
			case 0:
				getObjects().setLimit( DEFAULT_MAX_CUSTOMOBJECTS );
				break;
		}
		
			//  revision dependent read information
			//  MUST go before this line
		init();
		
			//  Playerfile revision code (example)
			//  We just added loginRoom, which is a Reference,
			//  and we want it to default to Reference.EMPTY.
		if( loginRoom == null )
			loginRoom = Reference.EMPTY;
		if( loginMsg == null )
			loginMsg = "";
		if( logoutMsg == null )
			logoutMsg = "";
		if( exitMsg == null )
			exitMsg = "slips sideways and disappears.";
		if( enterMsg == null )
			enterMsg = "appears from no-where.";
		if( whereFrom == null )
			whereFrom = "";
	}
	
	private void init()
	{
		reverseScapes = new Vector( 5, 5 );
		codes = new String[26];
		idleTicks = MAX_IDLE_TICKS;
		context = this;
		idlePrompt = "";
		repeat = new Repeated();
		spamThrottle = new long[ NUMBER_OF_THROTTLES ];
		spamViolations = new int[ NUMBER_OF_THROTTLES ];
		
		if( history == null )
			history = new CircularBuffer( MAX_HISTORY_LINES );
		
		if( FriendRefTest == null )
			FriendRefTest = Reference.EMPTY;
		
		if( email == null )
			email = new EmailAddress();
		
		if( homepage == null )
			homepage = new Webpage();

		totalPlayers++;
		commandLoopCount = 0;
		
			//  update the revision here
		revision = 1;
	}
	
	public Enumeration getTellHistory()
	{
		if( history != null )
		{
			if( getCurrent() != this )
				throw new AccessViolationException( this, "No-one may access another players tell history" );
			
			return( history.elements() );
		}
		
		return( key.util.EmptyEnumeration.EMPTY );
	}
	
	public AtomicStructure getDeclaredStructure()
	{
		return( STRUCTURE );
	}
	
	void stopBeingTemporary()
	{
		//  do nothing, we upgrade from temporary to distinct later
		//index = Registry.instance.allocateTemporaryIndex( this );
	}
	
	/**
	  *  Players always own themselves
	 */
	void assignInitialOwner()
	{
		owner = getThis();
	}
	
	public final void putCode( char c, String value )
	{
		try
		{
			codes[ c - 'a' ] = value;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			Log.error( "During Player::putCode()", e );
		}
	}
	
	public final String[] getCodes()
	{
		return( codes );
	}
	
	public boolean brief()
	{
		return( brief );
	}
	
	public void setBrief( boolean nb )
	{
		permissionList.check( modifyAction );
		brief = nb;
	}
	
	public boolean isBeyond()
	{
		return( beyond );
	}
	
	public boolean isExpert()
	{
		return( expert );
	}
	
	public void setWordwrap( boolean ww )
	{
		wordwrap = ww;
		
		if( connected() )
		{
			if( ic instanceof TelnetIC )
				((TelnetIC)ic).setWordwrap( ww );
			else
				throw new WrongTerminalTypeException();
		}
	}
	
	public boolean getWordwrap()
	{
		return( wordwrap );
	}

	public String getOldLoginTime()
	{
		if( hideTime )
			permissionList.check( seePrivateInfoAction );
		
		return( oldLoginTime );
	}
	
	//  required for the 'term' command.
	public synchronized void setTerminal( String name, boolean force, InteractiveConnection output_ic )
	{
		permissionList.check( modifyAction );
		
		if( connected() )
		{
			if( ic instanceof TelnetIC )
			{
				TelnetIC tic = ((TelnetIC)ic);

				if( name.equalsIgnoreCase( "reset" ) )
				{
					Terminal before = tic.getTerminal();
					tic.resetTerminal();
					Terminal after = tic.getTerminal();
					forcedTerminal = null;
					willForceTerminal = false;
					if( before != after )
						ic.sendFeedback( "Terminal reset to what was autodetected.  It is now '" + after.getName() + "'." );
					else
						ic.sendFeedback( "Your terminal is already set to '" + after.getName() + "'." );
				}
				else if( tic.hasDetectedTerminal() )
				{
					if( force )
					{
						boolean r = tic.forceTerminal( name );
						if( r )
						{
							forcedTerminal = name;
							willForceTerminal = true;
							ic.sendFeedback( "Terminal forced [autodetect will no longer be used] to '" + tic.getTerminal().getName() + "'.  This setting will persist when you connect later, so you may need to remember to turn it off, if appropriate." );
						}
						else
							ic.sendFailure( "Unknown terminal type '" + name + "'." );
					}
					else
					{
						ic.sendFailure( "Your terminal has been autodetected by Key to be '" + tic.getDetectedTerminal().getName() + "'.  If you wish to override this setting, use 'term " + name + " force'." );
					}
				}
				else
				{
					boolean r = tic.setLoginTerminal( name );
					if( r )
					{
						forcedTerminal = name;
						willForceTerminal = false;
						ic.sendFeedback( "Terminal set to '" + tic.getTerminal().getName() + "'.  If your terminal cannot be autodetected the next time you connect, this terminal will be used for you again." );
					}
					else
						ic.sendFailure( "Unknown terminal type '" + name + "'." );
				}
			}
			else
				throw new WrongTerminalTypeException();
		}
		else
			throw new PlayerNotConnectedException();
	}
	
	public Terminal getTerminal()
	{
		if( connected() )
			if( ic instanceof TelnetIC )
				return( ((TelnetIC)ic).getTerminal() );
			else
				throw new WrongTerminalTypeException();
		else
			throw new PlayerNotConnectedException();
	}

	public boolean getWillForceTerminal()
	{
		return( willForceTerminal );
	}
	
		//  this would be an invasion of privacy to export
	public Repeated getRepeated()
	{
			//  META: this modifyAction is temporary until we have some kind
			//  of action to support this
		permissionList.check( modifyAction );
		return( repeat );
	}
	
	public void addMail( Letter l ) throws NonUniqueKeyException,BadKeyException
	{
		getMailbox().add( l );
		
		if( connected() )
		{
			ic.sendSystem( "New mail from " + l.from + ", '" + l.description + "'.  Use 'read mail 1' to read this message." );
			ic.flush();
		}
	}
	
	public MessageBox getMailbox()
	{
		return( mailbox );
	}

	public Container getObjects()
	{
		return( objects );
	}
	
	public QualifierList.Immutable getImmutableQualifierList()
	{
			//  this is not a security risk because ImmutableQL isn't
			//  changeable
		return( qualifierList.getImmutable() );
	}
	
	public QualifierList getQualifierList()
	{
		permissionList.check( modifyAction );
		return( qualifierList );
	}
	
	public final Clan getClan()
	{
		try
		{
			return( (Clan) clan.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			clan = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".clan wrong (reset)", e );
			clan = Reference.EMPTY;
			return( null );
		}
	}

	public final Realm getRealm()
	{
		try
		{
			return( (Realm) realm.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			realm = Key.instance().getDefaultRealm().getThis();
			return( (Realm) realm.get() );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".realm wrong (reset)", e );
			realm = Reference.EMPTY;
			return( null );
		}
	}
	
	public void setCanPage( boolean v )
	{
		permissionList.check( modifyAction );
		canPage = v;
	}
	
	public String getIdleMsg()
	{
		return( idleMsg );
	}
	
		//  it is the inventories problem to prevent itself from being
		//  modified by unauthorised people
	public Inventory getInventory()
	{
		return( inventory );
	}
	
	public void setEmail( EmailAddress ea )
	{
		checkPermissionList( modifyAction );
		if( ea == null )
			email = new EmailAddress();
		else
			email = ea;
		
		updateSupplemental();
	}
	
	public EmailAddress getEmail()
	{
			//  this could be made better (immutable email address?)
		if( privateEmail )
			permissionList.check( seePrivateInfoAction );
		permissionList.check( modifyAction );
		return( email );
	}
	
	public String getSessionComment()
	{
		Realm r = getRealm();
		
		if( r.sessionTimestamp != sessionTimestamp )
			sessionComment = null;
		
		return( sessionComment );
	}
	
	public void setSessionComment( String sk )
	{
		Realm r = getRealm();
		
		sessionTimestamp = r.sessionTimestamp;
		sessionComment = sk;
	}
	
	/**
	  *  META:  this needs to be updated for the new permissionlist
	  *  that has a boolean check method (that doesn't throw exceptions)
	  *  for efficiency.
	 */
	public String getEmailAddress()
	{
		try
		{
			if( privateEmail )
				permissionList.check( seePrivateInfoAction );
			
			return( email.toString() );
		}
		catch( AccessViolationException e )
		{
			return( "<private>" );
		}
	}

	public Webpage getWebpage()
	{
		return( homepage );
	}
	
	public String getBlockMsg()
	{
		return( blockMsg );
	}
	
	public String getLoginMsg()
	{
		return( loginMsg );
	}
	
	public String getLogoutMsg()
	{
		return( logoutMsg );
	}
	
	public String getEnterMsg()
	{
		return( enterMsg );
	}
	
	public String getExitMsg()
	{
		return( exitMsg );
	}
	
	public String getAka()
	{
		return( aka );
	}

	public int getAge()
	{
		return( age );
	}
	
		//  this should be automatically determined
	public boolean isEmailPrivate()
	{
		return( privateEmail );
	}
	
	public String getPassword()
	{
		return( password.toString() );
	}
	
	public Password getActualPassword()
	{
		permissionList.check( modifyAction );
		return( password );
	}
	
	public boolean isPasswordSet()
	{
		return( password.isSet() );
	}
	
	public String getBlockingMsg()
	{
		return( blockingMsg );
	}
	
	public Paragraph getPlan()
	{
		return( plan );
	}

	public Paragraph getDescription()
	{
		return( description );
	}
	
	public final boolean isCitizen()
	{
		return( citizen );
	}

	public final boolean getExpert()
	{
		return( expert );
	}

	public final Duration getTimezone()
	{
		return( timezone );
	}
	
	public final void setTimezone( Duration nd )
	{
		permissionList.check( modifyAction );
		timezone = nd;
	}

	public boolean isHiding()
	{
		return( hidden );
	}
	
	public String getLastConnectFrom()
	{
		checkPermissionList( seePrivateInfoAction );
		return( lastConnectFrom );
	}
	
	public void setIdle( boolean tf )
	{
		permissionList.check( modifyAction );
		isIdle = tf;
		recalculateIdlePrompt();
	}
	
	public void setHiding( boolean tf )
	{
		permissionList.check( modifyAction );
		hidden = tf;
	}
	
		/**  if the player can't be put into a clan */
	public boolean isLiberated()
	{
		return( liberated );
	}

	public void setLiberated( boolean nl )
	{
		permissionList.check( modifyAction );
		liberated = nl;
	}

	public Room getLastPublicRoom()
	{
		try
		{
			return( (Room) lastPublicRoomLocation.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			lastPublicRoomLocation = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".lastpublicroom wrong (reset)", e );
			lastPublicRoomLocation = Reference.EMPTY;
			return( null );
		}
	}

	public Room getHome()
	{
		try
		{
			return( (Room) home.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			home = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".home wrong (reset)", e );
			home = Reference.EMPTY;
			return( null );
		}
	}

	public void setHome( Room r )
	{
		permissionList.check( modifyAction );
		
		try
		{
			home = r.getThis();
		}
		catch( TypeMismatchException e )
		{
			throw new UnexpectedResult( "property 'home' is not a Room in Player: " + e.toString() );
		}
	}

	public Friends getFriends()
	{
		return( friends );
	}

	public Group getPrefer()
	{
		return( prefer );
	}
	
	public String getPrefix()
	{
		return( prefix );
	}
	
	public void setPrefix( String s )
	{
		if( s == null || s.length() == 0 )
			prefix = "";
		else
			prefix = s + " ";
	}
	
	public String getTitledName()
	{
		return( getName() + "^@" + title + "^$" );
	}

	public InformList getInform()
	{
		return( inform );
	}
	
		//  should modify this routine to do
		//  authentication - don't allow anyone
		//  to set the password?
	public void setPassword( String newValue )
	{
		permissionList.check( modifyAction );
		password.set( newValue );
	}
	
	void linkToScape( Scape s )
	{
		if( !reverseScapes.contains( s ) )
			reverseScapes.addElement( s );
	}
	
	void unlinkFromScape( Scape s )
	{
		reverseScapes.removeElement( s );
	}
	
	void ejected( Room from, Effect enter, Effect leave )
	{
		Room wanted = Key.instance().getConnectRoom( this );
		
		if( leave != null )
			leave.cause();
		
		moveTo( wanted );
		enter.cause();
		roomLook();
	}
	
	void addReverseRank( Rank r )
	{
		if( !reverseRanks.contains( r.getThis() ) )
		{
			reverseRanks.addElement( r.getThis() );
			//System.out.println( "added revrank '" + r.getName() + "' to player " + getName() );
		}
	}
	
	void removeReverseRank( Rank r )
	{
		reverseRanks.removeElement( r.getThis() );
		//System.out.println( "removed revrank '" + r.getName() + "' to player " + getName() );
	}
	
	public Enumeration scapes()
	{
		return( reverseScapes.elements() );
	}
	
	public Enumeration ranks()
	{
		return( new FilteredEnumeration( 
				reverseRanks.elements(),
				new ReferenceEnumeratorFilter(
					new ReferenceEnumeratorFilter.EnumeratedThing()
					{
						public void noSideEffectRemove( Reference r )
							throws NonUniqueKeyException, java.util.NoSuchElementException,BadKeyException
						{
							Player.this.reverseRanks.removeElement( r );
						}
					}
				, true ) ) );
	}
	
	/**
  	  *  You can target a player if you can target
  	  *  any of the ranks that they're in.  That means
  	  *  that most staff members will _not_ be in the
  	  *  'resident' rank (so they can't hurt each other.
 	 */
	public boolean isOutRankedBy( Rank rank )
	{
			//  cache this value for speed
		Targets targets = rank.getTargets();
		
		if( targets != null )
		{
			for( Enumeration e = targets.elements(); e.hasMoreElements(); )
			{
				Object o = e.nextElement();
				if( o instanceof Rank )
				{
					Rank theRank = (Rank) o;
					if( theRank.containsAtAll( getThis() ) )
					{
						return true;
					}
				}
				else if( o == this )
					return true;
			}
		}
		
		return false;
	}

	/**  eg: "subtle's" */
	
	// META: better representation for name would be nice, these are called a lot
	public final String getPossessiveName()
	{
		return( getName() + "'s" );
	}
	
	/**  Gets the players location */
	public final Room getLocation()
	{
		if( location == null )
			return( null );
		
		if( isHiding() )
		{
			try
			{
				checkPermissionList( findAction );
			}
			catch( Exception e )
			{
					//  That was permission denied.  BUT,
					//  if the person is in a room we have
					//  permission to eject from, find them
				try
				{
					location.checkPermissionList( Room.ejectFromAction );
				}
				catch( Exception ex )
				{
					throw new PermissionDeniedException( this, "looking for " + getName() + ".  " + HeShe() + " is hiding, you don't have permission to find " + himHer() + ", and " + heShe() + " is not in any of your rooms." );
				}
			}
		}
		
		return( location );
	}
	
	/**
	  *  Asking for a players location violates their
	  *  'hide', but checking to see if they're in
	  *  a particular room (eg, the same room we're in)
	  *  does not.  If someone is hiding, you can still
	  *  find them by doing a room-by-room search, hence
	  *  the old game. ;p~
	 */
	public final boolean isLocation( Room r )
	{
		return( location == r );
	}
	
	//public static final int MAX_IDLE_TICKS = 1;
	//public static final int MAX_IDLE_TICKS = 5;
	public static final int MAX_IDLE_TICKS = 10;
	private transient int idleTicks;
	
	/**
	  *  Called by the interactive connection to reset the 
	  *  players idletime to 0
	 */
	final void unIdle()
	{
			//  don't you dare add anything to this routine to stop
			//  certain players idling out: it needs to be as small
			//  and fast as possible.  Put it in 'resetModify',
			//  if you must.
		connectionStats.unIdle();
		idleTicks = MAX_IDLE_TICKS;
	}
	
	public final Duration getIdle( DateTime now )
	{
		return( connectionStats.getIdleTime( now ) );
	}
	
	/**  A players aspect is its description when looked at */
	public Paragraph aspect()
	{
		return( description );
	}
	
	public boolean authenticate( String usedPwd )
	{
		updateSupplemental();
		return( password.check( usedPwd ) );
	}
	
	/**
	  *  Called when a password entry is required
	 */
	public boolean authenticate( InteractiveConnection ic ) throws IOException,PasswordEntryCancelled
	{
		updateSupplemental();
		if( password.isSet() )
		{
			int tries = 0;
			
			do
			{
				String in = password.checkGetPassword( getName(), ic );
				
				if( password.check( in ) )
				{
					ic.blankLine();
					return( true );
				}
				else
				{
					ic.send( "\n" );
					
					if( email.isAwaitingVerification() && email.verify( in, ic, this ) )
					{
						ic.send( "\n\n" );
						password.clear();
						command( "password", ic, false );
						ic.input( "\n\nPress return to continue: " );
						return( true );
					}
					else
					{
						ic.send( "Password incorrect." );
					}
				}
			} while( ++tries < 3 );
			
				//  offer to send the password via email:
				//    don't say what the address is, since then you can
				//    find out the email address of anyone!
			ic.sendFeedback( "\n\nYou have gotten your password wrong too many times.  If you wish, we can email you a new verification code that you can use to log in instead of your password.\n" );
			
			if( Grammar.getYesNo( "Do you want us to email a code to you? (y/N): ", false, ic ) )
			{
				email.send( ic, this );
				
				ic.sendFeedback( "\n\nEmail sent.  It should arrive in about 15 minutes.\n" );
			}
			else
				ic.sendFeedback( "\n\nEmail NOT sent.\n" );
			
			ic.input( "Press return to disconnect: " );
			ic.close();
		}
		else
		{
				//  no password, check if there's an email validation
				//  waiting:
			/*if( email.isValid() )
			{
				ic.sendFeedback( "\n\nThis character does not have a password, but does have an email address set.  If you've recieved a verification code by email, you can try this below.  If you have not received such a verification code, simply press return and we'll send you another one.\n\n" );
				String in = ic.input( "Enter your verification code (if you have one): " );
				if( in.length() == 0 )
				{
					email.send( ic, this );
					ic.sendFeedback( "\nNew email verification code sent.  All other verification codes you may have recieved are now invalid.\n" );
				}
				else if( email.verify( in, ic, this ) )
				{
					ic.send( "Verification code instead of password accepted.  ^hRemember to set your password so that you can save!^-" );
					ic.input( "\n\nPress return to continue: " );
					return( true );
				}
				else
				{
					ic.sendError( "Verification code incorrect." );
				}
				
				b = false;
				ic.input( "\n\nPress return to disconnect: " );
				ic.close();
				throw new PasswordEntryCancelled();
			}
			else */
			if( email.isAwaitingVerification() )
			{
				ic.send( "\n\nWelcome back, we've been waiting for you.\n\nIf you received the confirmation email we sent to '" + email.getInvalid() + "', you can type the verification code below.  Otherwise, you can type a different email address (or the same one, to send another code).\n\n", 0, 0, 0, 0, true );
				
				String in = ic.input( "Verify code or new Email address: " );
				
				if( in.length() == 0 )
					throw new PasswordEntryCancelled();
				
				if( in.indexOf( '@' ) != -1 )
				{
					email.set( in, ic, this );
					ic.send( "\n\nEmail address set to '" + in + "'.  A confirmation email with your password wll be sent to you shortly (within about 15 minutes).  When you receive it you can come back and log into " + Key.instance().getName() + ".", 0, 0, 0, 0, true );
					ic.input( "\n\nPress return to disconnect: " );
					ic.close();
				}
				else
				{
					ic.send( "\n" );
					email.verify( in, ic, this );
					
					if( email.isValid() )
					{
						ic.send( "\n\n" );
						command( "password", ic, false );
						ic.input( "\n\nPress return to continue: " );
						return( true );
					}
				}
			}
			else if( email.isValid() )
			{
				ic.sendFeedback( "\n\nThe password is not set on this character, and so it will not be possible for you to log in.  However, as you have an email address set (" + email.get() + "), we can send you a message with a special verification code.  When you receive that code, you'll be able to log in and change your password.\n\n" );
				
				if( Grammar.getYesNo( "Do you want us to email a code to you?", false, ic ) )
				{
					email.send( ic, this );
					
					ic.sendFeedback( "\n\nEmail sent.  It should arrive in about 15 minutes.\n\n" );
				}
				else
					ic.sendFeedback( "\nEmail NOT sent." );
			}
			else
				ic.sendFeedback( "\n\nThis character has neither a password or an email address set, so it isn't possible to log in.\n\n" );
			
			ic.input( "\n\nPress return to disconnect: " );
			ic.close();
		}
		
		return( false );
	}
	
	//META: private
	transient int connecting = 0;
	void beginConnect()
	{
		connecting++;
	}
	
	void endConnect()
	{
		if( connecting > 0 )
			connecting--;
	}
	
	public transient int portFrom = -1;
	public void setPortFrom( int p )
	{
		portFrom = p;
	}
	
	public void setContext( Atom r )
	{
		permissionList.check( modifyAction );
		if( r != null )
			context = r;
		else
			context = this;
	}

	/** Returns the current players context */
	public Atom getContext()
	{
		return( context );
	}

	protected transient int lagAmount = 0;
	protected transient boolean lagOnce = false;
	protected transient boolean discard = false;

	public void setLag( int amount, boolean continual )
	{
		permissionList.check( modifyAction );
		if( amount >= 0 )
		{
			lagAmount = amount * 1000;
			lagOnce = continual;
		}
	}
	
	/** The main thread of a player - simply starts the parser */
	public void run( InteractiveConnection ic )
	{
		//setPriority( Thread.NORM_PRIORITY-1 );
		if( ic != getConnection() )
		{
			ic.send( "Poor connection" );
			ic.close();
			return;
		}
		
		String s = "IC: " + getKey().toString();
		if( s.length() > 15 )
			s = s.substring( 0, 15 );
		ic.setKey( s );
		
			// 	exile: when they reconnect they are no longer idle
		isIdle = false;
		recalculateIdlePrompt();
		
		try
		{
			synchronized( this )
			{
				ic.blankLines( 3 );
				
				
					//  connect the player to a room, initially
				{
					Room startRoom = null; 

					try
					{
						startRoom = (Room) loginRoom.get();
					}
					catch( Exception e )
					{
						loginRoom = Reference.EMPTY;
						ic.sendFailure( "^hYour login room is no longer valid.^-" );
					}
					
					if( startRoom == null )
						startRoom = Key.instance().getConnectRoom( this );
					
					String message = startRoom.getLoginMessage();
					
					putCode( originatorCode, getName() );
					message = Grammar.substitute( message, getCodes() );
					
					moveTo( startRoom, null, new key.effect.Enter( this, startRoom, message ) );
				}
				
				if( location == null )
				{
					ic.sendError( "Could not connect you to a room, disconnecting..." );
					disconnect();
					return;
				}
				
					//  scan ranks
				int warnLostRank = 0;
				
				for( int i = 0; i < reverseRanks.size(); i++ )
				{
					Reference o = (Reference) reverseRanks.elementAt( i );
					
					try
					{
						Rank r = (Rank) o.get();
						
						if( !r.contains( this ) )
						{
							reverseRanks.removeElementAt( i-- );
							warnLostRank++;
						}
						else
							r.establish( this );
					}
					catch( OutOfDateReferenceException e )
					{
						reverseRanks.removeElementAt( i-- );
						warnLostRank++;
					}
					catch( ClassCastException e )
					{
						reverseRanks.removeElementAt( i-- );
						warnLostRank++;
					}
				}
				
					//  might as well let them know...
				if( warnLostRank > 0 )
				{
					if( warnLostRank == 1 )
						ic.send( "^hWhile you were offline, a rank that you were in was erased.  (Just letting you know)  You will not be notified of this again, and I have no way of determining which rank it was.^-" );
					else
						ic.send( "^hWhile you were offline, " + warnLostRank + " ranks that you were in were erased.  (Just letting you know)  You will not be notified of this again, and I have no way of determining which ranks they were.^-" );
				}
				
					//  this is the bit that links the player back
					//  into any groups they should be in. :)
				putCode( originatorCode, getName() );
				new key.effect.Login( this, "%o has connected^@" + loginMsg + "^$" ).cause();
				
					//  execute the players login script
				{
					StringTokenizer st = new StringTokenizer( loginScript, "|", false );
					
					commandLoopCount = 0;
					while( st.hasMoreTokens() )
					{
						command( st.nextToken(), ic, false );
						Thread.yield();  //  be nice and friendly
					}
				}
			}
			
			try
			{
				String entered;
				
				while( connected() )
				{
						//  just a quick check for the lag code
						//  *grin*
					if( lagAmount >= 0 )
					{
						try
						{
							Thread.sleep( lagAmount );
						}
						catch( InterruptedException e )
						{
						}
						
						if( lagOnce == true )
						{
							lagAmount = 0;
							lagOnce = false;
							
							if( discard )
							{
								try
								{
									ic.discard();
								}
								catch( IOException e )
								{
								}
								
								discard = false;
							}
						}
					}
					
					if( mode == null )
						entered = ic.input( getContextualPrompt() );
					else
					{
						entered = ic.input( getContextualPrompt() + mode );
						
						if( entered.length() == 0 )
							mode = null;
						else
							entered = mode + entered;
					}
					
					Thread.yield();  //  be nice and friendly
					
					if( entered.length() == 0 )
					{
						if( ic.isPaging() )
						{
							((TelnetIC)ic).drawNextPage();
						}
						
						continue;
					}
					
					try
					{
						commandLoopCount = 0;
						command( entered, ic, false );
					}
					catch( Exception e )
					{
						if( e instanceof UserOutputException )
							((UserOutputException)e).send( ic );
						else
							throw e;
					}
				}
			}
			catch( NetworkException e )
			{
				//System.err.println( e.toString() );
				//e.printStackTrace();
			}
		}
		catch( ThreadDeath td )
		{
			synchronized( this )
			{
				if( ic == getConnection() )
				{
					disconnect();
					ic = null;
				}
				else if( ic != null )
				{
					ic.close();
					ic = null;
				}
			}
			
			throw td;
		}
		catch( Exception e )
		{
			Log.error( "Player::run() [" + getName() + "]", e );
		}
		
		synchronized( this )
		{
				//  necessary for reconnections - we don't
				//  want to disconnect the newly connected
				//  player, but neither do we want to exit
				//  this thread with an unclosed connection.
			if( connected() )
			{
				if( ic == getConnection() )
					disconnect();
				else if( ic != null )
					ic.close();
			}
			else if( ic != null )
				ic.close();
		}
	}
	
	private transient String idlePrompt;
	
	private final void recalculateIdlePrompt()
	{
		if( isIdle )
			idlePrompt = "[IDLE] ";
		else
			idlePrompt = "";
	}
	
	public final boolean isIdle()
	{
		return( isIdle );
	}

	private transient boolean isafk = false;
	
	public final void afk( boolean v )
	{
		permissionList.check( modifyAction );
		isafk = v;
	}
	
	public final boolean isAfk()
	{
		return( isafk );
	}

	/**
	  *  Returns an ordered list of the commands that this
	  *  Player has access to.
	  *
	  *  I believe this isn't being used.
	Enumeration getCommands()
	{
			//  technically, this Observer might only be used
			//  by the Commands class - so we probably should just
			//  have a method that returns the above delayed enumeration
		return(
			new RecursiveEnumeration(
				getCommandListEnumerations(),
				false,  //  don't recurse deeply
				null  //  no observer required to just enumerate
			)
		);
	}
	 */
	
	/**
	  *  Returns an enumeration of enumerations of the
	  *  commands this player has access to.
	  *
	  *  I believe this isn't being used
	 */
	Enumeration getCommandListEnumerations()
	{
		return(
			new FilteredEnumeration(
				getCommandLists(),
				new FilteredEnumeration.Filter()
				{
					public boolean isValid( Object element, Enumeration enum )
						{ return( true ); }
					
					public Object replace( Object element, Enumeration enum )
					{
						if( element instanceof CommandList )
							return( ((CommandList)element).elements() );
						else
							return( null );
					}
				}
			)
		);
	}
	
	/**
	  *  Returns an enumeration of enumerations of the
	  *  commands this player has access to.
	 */
	public Enumeration getCommandLists()
	{
		final Enumeration clist = new Enumeration()
		{
			int state = 0;
			int substate = 0;
			int last_substate = 0;
			
			Object next = scanNextElement();
			
			public boolean hasMoreElements()
			{
				return( next != null );
			}
			
			public Object nextElement()
			{
				if( next == null )
					throw new NoSuchElementException();
				
				Object o = next;
				next = scanNextElement();
				return( o );
			}
			
			/**
			  *  This is done like this because, believe it or not,
			  *  it's cleaner and more efficient this way.  Really.
			  *  The old way sucked.
			  * <P>
			  *  Because of the way this is written, it can dynamically
			  *  scan command lists without having to pre-process them,
			  *  but also through the Enumeration interface.
			 */
			public Object scanNextElement()
			{
					//  temporary variable for result
				CommandList cl; 
				
				switch( state )
				{
					case 0:
						state++;  // state 1 is next
						
						if( context != null && context instanceof CommandContainer )
						{
							if( context instanceof Rank )
							{
								if( ((Rank)context).containsPlayer( Player.this ) )
								{
									cl = ((CommandContainer)context).getCommandList();
									
									if( cl != null && cl.count() > 0 )
										return( cl );
								}
							}
							else
							{
								cl = ((CommandContainer)context).getCommandList();
								
								if( cl != null && cl.count() > 0 )
									return( cl );
							}
						}
						//  proceed to next state now
					
					case 1:
							//  special commands for the player
							//  (overriddable only by context)
						state++;  // state 3 is next
						cl = Player.this.getSpecialCommandList();
						if( cl != null && cl != context && cl.count() > 0 )
							return( cl );
						
					case 2:
						state++;  // state 3 is next
						if( context != Player.this )
						{
							cl = Player.this.getCommandList();
							if( cl != null && cl.count() > 0 )
								return( cl );
						}
						//  proceed to next state now
						
					case 3:
						state++;  // state 4 is next
						last_substate = substate;  //  save the old substate
						substate = 0;  // initialise
						//  proceed to next state now
						
					case 4:
							//  return groups for each player based on
							//  the substate, which is the group we're
							//  currently upto
						int rss = reverseScapes.size() - 1;
						if( (substate) > rss )
						{
								//  this is an error, we should
								//  already be on the next state -
								//  just skip through
							state++;
						}
						else
						{
							if( substate == rss )
							{
									//  this is the last time we can do this
								state++;  //  next time we're in the next state
							}
							
							CommandContainer cc = ((CommandContainer)(reverseScapes.elementAt( substate++ )));
							
							if( cc != null && cc != context )
							{
								cl = cc.getCommandList();
								
								if( cl != null )
									return( cl );
								else
									return( scanNextElement() );
							}
							else
								return( scanNextElement() );
						}
					
					case 5:  // this is not a valid state.
					default:
						return( null );
				}
			}
			
			/**
			  *  Used to get some information about what we're
			  *  enumerating through for the command list.
			  * <P>
			  *  Returns a string of the form:
			  * <BR>
			  *  [context] name - x commands
			  * <P>
			  *  This method is called to get information about
			  *  the result of the <I>last</I> nextElement() call.
			  *
			  * @return formatted details string
			 */
			String getLastDetails()
			{
				switch( state - 1 )
				{
					case -1:
						throw new UnexpectedResult( "Player::getLastDetails() before nextElement()" );
					case 0:
							//  state 0 is the context scan
						return( "context " + context.getName() + " - " + "" );
						//break;
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
					default:
						throw new UnexpectedResult( "Player::getLastDetails(): invalid state" );
				}
				
				return null;
			}
		};
		
		return( clist );
	}
	
	private String getContextualPrompt()
	{
		if( ic == null )
			return( prompt );
		
		StringBuffer gprompt = new StringBuffer();
		
		gprompt.append( idlePrompt );
		
		if( context != this )
		{
			gprompt.append( '[' );
			gprompt.append( context.getName() );
			gprompt.append( "] " );
		}
		
		if( ic.isPaging() )
			gprompt.append( "[PAGER] " );
		
		gprompt.append( prompt );
		
		return( gprompt.toString() );
	}
	
	/**
	  *  Returns an enumeration of enumerations of the
	  *  modal commands this player has access to that
	  *  matches a certain prefix string.  The enumeration
	  *  contains Object[2] in each element.  The first
	  *  is the matched commandcontainer, the second is
	  *  the top level parent of that command container,
	  *  usually indicating the providing rank or scape.
	 */
	public Enumeration getCategoryCommandsMatching( java.util.StringTokenizer args )
	{
		//if( !args.hasMoreTokens() )
			//throw new UnexpectedResult( "No arguments to getCategoryCommandsMatching specified" );
		
		//String first = args.nextElement();
		
		Vector path = null;
		CommandList cl = null;
		Vector result = new Vector( 16, 16 );
		
		if( args != null && args.hasMoreTokens() )
		{
			path = new Vector( 6, 6 );
			
			do
			{
				path.addElement( args.nextToken() );
			} while( args.hasMoreTokens() );
		}
		
		//if( path.size() == 0 )
			//throw new UnexpectedResult( "No arguments to getCategoryCommandsMatching specified" );
		
scan:	for( Enumeration e = getCommandLists(); e.hasMoreElements(); )
		{
			cl = (CommandList) e.nextElement();
			
			if( cl == null )
				continue;
			
			CommandList top = cl;
			Object match = null;
			
			if( path != null )
			{
				for( Enumeration f = path.elements(); f.hasMoreElements(); )
				{
					match = cl.getExactElement( (String) f.nextElement() );
					//ic.send( "DEBUG TODO new2 match is " + match );
					
					if( match instanceof CommandContainer )
					{
						if( f.hasMoreElements() )
						{
								//  set up the loop to repeat
							cl = ((CommandContainer)match).getCommandList();
							
							if( cl == null )
								continue scan;  //  no more from this modal
						}
						else
							break;  //  just an optimisation, not reqd.
					}
					else
						continue scan;  //  not enough in this modal
				}
			}
			
			Object[] o = new Object[2];
			o[0] = match;
			o[1] = top;
			
				//  if we get here, we have a matching CommandContainer
			result.addElement( o );
		}
		
		if( path != null )
			path.setSize( 0 );
		
		return( result.elements() );
	}
	
	private transient int commandLoopCount = 0;
	private transient String lastCommand = null;
	
	public String getLastCommandExecuted()
	{
		checkPermissionList( Atom.modifyAction );
		return( lastCommand );
	}
	
	/**
	  *  This routine kind of doubles up with CategoryCommand,
	  *  but searches many more places than CategoryCommand
	  *  does.  Searches the current context + others
	 */
	public void command( String fullLine, InteractiveConnection ic, boolean query )
	{
		if( getCurrent() == this )
		{
			if( commandLoopCount > 5 )
			{
				ic.sendError( "Too many command loops." );
				return;
			}
		
			commandLoopCount++;
		}
		
			//  stored for debugging.
		lastCommand = fullLine;
		
		StringTokenizer args = new StringTokenizer( fullLine.trim() );
		Commandable match = null;
		String command;
		Commandable last = null;
		Commandable lastUnique = null;
		Command.Match lastcm = null;
		StringTokenizer argsFor = null;
		
		int highest_loop_count = -1;
		
		if( !args.hasMoreTokens() )
			return;
		
		command = args.nextToken();	//  the first word
		
			//  attempt to find a matching command:
		
scan:	for( Enumeration e = getCommandLists(); e.hasMoreElements(); )
		{
			try
			{
				last = match;
				match = (Commandable) ((CommandList) e.nextElement()).getExactElement( command );
				if( last != match )
					lastUnique = last;
				//ic.send( "set last to " + last + ",   new match is " + match );
			}
			catch( ClassCastException t )
			{
				Log.error( "during command matching", t );
				match = null;
			}
			
			if( match != null )
			{
				StringTokenizer args_backup = (StringTokenizer) args.clone();
				
				if( match.recloneArgs() )
				{
						//  take a copy of the arguments
						//  now in case we need to repass
						//  them to the command if it doesn't
						//  match later.
					argsFor = (StringTokenizer) args.clone();
				}
				else
					argsFor = null;
				
				int loop_count = 0;
				
				do
				{
					if( highest_loop_count < loop_count )
					{
							//  this is the furthest we've matched
							//  this command - get an error string
							//  in case we don't find a good match
						Command.Match cm = match.getFinalMatch( this, args );
						
						lastcm = cm;
						
						if( cm.match != match )
							lastUnique = match;
						
						last = match;
						match = cm.match;
						//ic.send( "set 2 last to " + lastUnique + ",  new match is " + match );
						
						highest_loop_count = loop_count;
					}
					else
					{
						last = match;
						match = match.getMatch( this, args );
						if( last != match )
							lastUnique = last;
						//ic.send( "set 3 last to " + last + ",  new match is " + match );
					}
					
					if( match == last )
					{
						if( argsFor != null )
							args = argsFor;
						
						break scan;
					}
					else if( match == null )
					{
						args = args_backup;
						continue scan;
					}
					else
					{
						if( match.recloneArgs() )
						{
								//  take a copy of the arguments
								//  now in case we need to repass
								//  them to the command if it doesn't
								//  match later.
							argsFor = (StringTokenizer) args.clone();
						}
						else
							argsFor = null;
					}
				} while( loop_count++ < 5 );
				
					//  I don't do infinite loops on general
					//  principle in code like this.  This
					//  will limit the damage of a circular
					//  routine.
				throw new LimitExceededException( "too many modal commands" );
			}
		}
		
				//  check the exits in the players current room
		if( match == null && location != null && lastcm == null )
		{
			Object o = location.getElement( command );
			
			if( o instanceof Exit )
			{
				Go.useExit( this, (Exit) o, ic, args, null );
				return;
			}
		}
		
		if( match == null && lastcm != null && lastcm.lastchance != null )
		{
			if( lastcm.lastchance.recloneArgs() )
			{
				last = match;
				match = lastcm.lastchance;
				if( last != match )
					lastUnique = last;
				//ic.send( "set 4 last to " + last + ",   new match is " + match );
				if( argsFor != null )
					args = argsFor;
			}
		}
		
		if( match != null )
		{
			CategoryCommand caller;
			
			try
			{
				caller = (CategoryCommand) lastUnique;
			}
			catch( ClassCastException t )
			{
				caller = null;
			}
			
			if( match.isDisabled() )
			{
				ic.sendFailure( "This command has been temporarily deactivated" );
				return;
			}
			
			if( query )
			{
				if( caller != null )
					ic.sendFeedback( caller.getWhichId() + "  ->  " + match.getWhichId() );
				else
					ic.sendFeedback( match.getWhichId() );
				
				return;
			}
			
			try
			{
				if( caller != null )
					caller.runCommand( match, this, args, fullLine, ic, null );
				else
					match.run( this, args, fullLine, caller, ic, null );
			}
			catch( NotEnoughParametersException t )
			{
					//  do nothing other than signal an error
					//  state (ie, return code != 0, if we
					//  were in unix.  we're not, so nothing)
			}
			catch( Exception t )
			{
				if( connected() )
				{
					if( t instanceof UserOutputException )
						((UserOutputException)t).send( ic );
					else
					{
						ic.sendError( "Error: " + t.toString() );
						
						if( expert )
							ic.printStackTrace( t );
						else
							t.printStackTrace();
					}
				}
			}
			catch( Error t )
			{
				ic.sendError( t.toString() );
				
				if( expert )
					ic.printStackTrace( t );
				
				Log.error( t );
				throw t;
			}
		}
		else
		{
			if( lastcm != null )
				ic.sendError( lastcm.getErrorString() );
			else
				ic.sendError( "Cannot find command '" + command + "'" );
		}
	}
	
	/**  true if the player is connected */
	public boolean connected()
	{
		return( ic != null );
	}

	public void setLocalecho( boolean b ) throws IOException
	{
		if( connected() )
		{
			if( ic instanceof TelnetIC )
				((TelnetIC)ic).setLocalEcho( b );
			else
				sendFailure( "Wrong terminal type for toggling local echo" );
		}
		else
			throw new PermissionDeniedException( this, "Player not connected" );
	}
	
	public boolean getLocalecho() throws IOException
	{
		if( connected() && ic instanceof TelnetIC )
			return( ((TelnetIC)ic).getLocalEcho() );
		
		return( false );
	}
	
	public InteractiveConnection getConnection()
	{
		if( connected() )
			return( ic );
		else
			throw new PlayerNotConnectedException();
	}

	public InteractiveConnection getNullConnection()
	{
		return( ic );
	}
	
	/**  true if the player has a location */
	public boolean located()
	{
		return( location != null );
	}
	
	public void verifyAccount()
	{
		if( loginStats == null )
			loginStats = (TimeStatistics) Factory.makeAtom( TimeStatistics.class, "loginStats" );
		if( connectionStats == null )
			connectionStats = (ConnectionStatistics) Factory.makeAtom( ConnectionStatistics.class, "connectionStats" );
		
		if( friends == null )
			friends = (Friends) Factory.makeAtom( Friends.class, "friends" );

		if( prefer == null )
			prefer = (Group) Factory.makeAtom( Group.class, "prefer" );

		if( inform == null )
			inform = (InformList) Factory.makeAtom( InformList.class, "inform" );

		if( inventory == null )
			inventory = (Inventory) Factory.makeAtom( Inventory.class, "inventory" );

		if( objects == null )
			objects = (NoKeyContainer) Factory.makeAtom( NoKeyContainer.class, "objects" );
		if( mailbox == null )
			mailbox = (MessageBox) Factory.makeAtom( MessageBox.class, "mailbox" );
	
	}
	
	/**
	  *  Called to connect a player to an interactive connection.
	  *
	  *  <ul>
	  *  <li>splashes everyone on the program, letting them know this person
	  *      has connected.
	  *  <li>tells the Key object to link this player into the online players
	  *  <li>locks this players soul into memory
	  *  <li>updates the players statistics (tells the loginStats to begin
	  *  <li>tells the interactive connection which player to unidle (this) when
	  *      a command is entered.
	  *  </ul>
	 */
	public synchronized void connectTo( InteractiveConnection connection ) throws NonUniqueKeyException,BadKeyException
	{
			//  safety.  should almost certainly have the authenticate here
		permissionList.check( modifyAction );
		
		verifyAccount();
		
		if( connected() )
		{
			throw new UnexpectedResult( "already connected" );
		}
		
		Key.instance().linkPlayer( this );  //  do this first - this is the one
		                                    //  that will throw the exceptions
		ic = connection;
		
			//  this is the bit that restored their
			//  terminal parameters to the overridden ones
		if( ic instanceof TelnetIC )
		{
			TelnetIC tic = (TelnetIC) ic;
			if( forcedTerminal != null )
			{
				if( willForceTerminal )
					tic.forceTerminal( forcedTerminal );
				else
					tic.setLoginTerminal( forcedTerminal );
			}
			
			tic.setCanPage( canPage );
			tic.setWordwrap( wordwrap );
		}
		
		{
				//  only if they've connected before
			if( lastConnectFrom.length() > 0 && loginStats.lastConnection != null )
			{
				ic.send( "\n\nYou last connected at " + loginStats.lastConnection.toString( this ) + ",\nfrom: " + lastConnectFrom );
			}
		}
		
			//  update the onSince time to now
		lastConnectFrom = ic.getPrivateSiteName();
		loginStats.startConnection();
		
			//  begin un-idling the player
		ic.startUnIdling( this );
		
		if( latentCacheHook == null )
		{
			latentCacheHook = new LatentlyCached()
			{
					//  don't you dare add anything to this routine to stop
					//  certain players idling out: it needs to be as small
					//  and fast as possible.  Put it in 'resetModify'
					//  if you must.
				public final boolean modified()
				{
					return( idleTicks > 0 );
				}
				
				public void resetModify()
				{
					idleTicks--;
					
						//  we used to have an idle warning here, but that
						//  isn't a good idea (people use trigger macros on
						//  it.  Increased possible 'idle' time to one hour
						//  to compensate for the lack.
					//if( idleTicks == 1 && connected() )
					//	ic.sendError( "\n\n" );
					//
					//if( I don't want to idle out as often )
					//  make idleTicks bigger, and remember not to do it
					//  every single time (idle everyone out, eventually)
				}
				
				public synchronized void deallocate()
				{
					if( connected() )
					{
						ic.sendFeedback( "\n\n\nYou have been idle for too long, disconnecting." );
						disconnect();
					}
				}
			};
		}
		
		Key.getLatencyCache().addToCache( latentCacheHook );
		
		Thread.yield();
		
	}
	
	public final void setAge( int na )
	{
		permissionList.check( modifyAction );
		age = na;
	}
	
	public final void setAka( String na )
	{
		permissionList.check( modifyAction );
		aka = na;
	}
	
	public final void setBlockMsg( String na )
	{
		permissionList.check( modifyAction );
		blockMsg = na;
	}
	
	public final void setCitizen( boolean ba )
	{
		permissionList.check( modifyAction );
		citizen = ba;
		
		if( citizen == true )
		{
			getFriends().setLimit( CITIZEN_MAX_FRIENDS );
			getMailbox().setLimit( CITIZEN_MAX_MAIL );
		}
		else
		{
			getMailbox().setLimit( DEFAULT_MAX_MAIL );
			getFriends().setLimit( DEFAULT_MAX_FRIENDS );
			getPrefer().setLimit( DEFAULT_MAX_PREFER );
			getInform().setLimit( DEFAULT_MAX_INFORM );
		}
	}
	
	public final void setBlockingMsg( String na )
	{
		permissionList.check( modifyAction );
		blockingMsg = na;
	}
	
	private transient boolean disconnecting = false;
	
	/**
	  *  Called to terminate a players connection.
	  *
	  *  <ul>
	  *  <li>stops time recording
	  *  <li>takes the player out of the room that they're in
	  *  <li>removes the player from the online list of players in Key
	  *  <li>splashes everyone that the player is disconnecting
	  *  <li>closese the interactive connection
	  *  <li>stops the thread
	  *  </ul>
	 */
	public synchronized void disconnect()
	{
		permissionList.check( modifyAction );
		
		if( disconnecting )
			return;
		
		if( connected() )
		{
			try
			{
				ic.flush();
			}
			catch( Exception e )
			{
			}
			
			try
			{
				disconnecting = true;
				InteractiveConnection dcd = ic;
				
				Key.getLatencyCache().removeFromCache( latentCacheHook );
				
				florins += (int) loginStats.getTimeSinceConnection().getHours();
				
				loginStats.endConnection();
				
				if( Key.isRunning() )
				{
					if( location != null )
					{
						String message = location.getLogoutMessage();
						
						putCode( originatorCode, getName() );
						message = Grammar.substitute( message, getCodes() );
						
						(new key.effect.Leave( this, location, message )).cause();
					}
					
					(new key.effect.Logout( this, getName() + " has disconnected^@" + logoutMsg + "^$" )).cause();
				}
				
				int rss = reverseScapes.size();
				while( rss > 0 )
				{
					Scape s = (Scape) reverseScapes.elementAt( 0 );
					
					try
					{
						s.unlinkPlayer( this );
					}
					catch( NonUniqueKeyException t )
					{
						Log.error( "while removing ourselves from scapes", t );
						reverseScapes.removeElement( s );
					}
					catch( BadKeyException t )
					{
						Log.error( "while removing ourselves from scapes", t );
						reverseScapes.removeElement( s );
					}
					
						//  over engineering to prevent an infinite loop here
					int nrs = reverseScapes.size();
					if( nrs == rss )
					{
						Log.error( "did not seem to remove scape as intended: please analyse algorithm" );
						reverseScapes.removeElement( s );
						nrs = reverseScapes.size();
						if( nrs == rss )
						{
							Log.error( "as above, but even more curious" );
							break;
						}
					}
					
					rss = nrs;
				}
				
				try
				{
						//  sync it only if we're a temporary atom atm.
					if( Registry.instance.getStorageTypeIndex( index, timestamp ) == Registry.STORAGE_TEMPORARY )
						sync();
					
					try
					{
						Object o = new Search( "/online/logoutMsg", null ).result;
						if( o instanceof Screen )
						{
							dcd.send( ((Screen)o) );
							dcd.flush();
						}
						else if( o != null )
						{
							Log.debug( this, "/online/logoutMsg is not key.Screen, it is " + o.getClass().getName() );
						}
					}
					catch( InvalidSearchException e )
					{
					}
				}
				catch( IOException e )
				{
					dcd.send( e.toString() + " occured while trying to save your character" );
					Log.error( "while saving player " + getName(), e );
				}
				finally
				{
					dcd.close();
				}
			}
			catch( NetworkException e )
			{
			}
			catch( Exception t )
			{
				Log.error( "during Player::disconnect", t );
			}
			finally
			{
				ic.stopUnIdling();
				
				ic.close();
				ic = null;
				location = null;
				idleTicks = 0;
				
				if( !willSync() )
					dispose();
				
				disconnecting = false;
				updateSupplemental();
			}
		}
		else
			throw new UnexpectedResult( "disconnecting a non-connected player" );
	}
	
	public void updateSupplemental()
	{
		try
		{
					//  Update our email in the supplemental
			PlayerSupplemental s = (PlayerSupplemental) Registry.instance.getSupplemental( index, timestamp );
			if( s != null )
				s.update( this, email );
			else
				Registry.instance.updateSupplemental( index );
		}
		catch( Exception e )
		{
		}
	}
	
	void setSaved()
	{
		saved = true;
	}
	
	public boolean hasPassword()
	{
		return( password.isSet() );
	}

	/**
	  * @return true iff the email has been set (it does not have to be valid)
	 */
	public boolean hasEmail()
	{
		return( email.isSet() );
	}
	
	public boolean getCanSave()
	{
		return( canSave );
	}
	
	public boolean isStaff()
	{
		return( staff );
	}

	public String getRank()
	{
		if( willSync() )
		{
			//if( beyond )
				//return( "director" );
			if( isStaff() )
				return( "staff" );
			else if( isCitizen() )
				return( "citizen" );
			else
				return( "resident" );
		}
		else
			return( "visitor" );
	}
	
	void clearTransient()
	{
		super.clearTransient();
		
		if( connected() )
			disconnect();
	}
	
	public boolean willSync()
	{
		return( (hasPassword() || hasEmail()) && canSave );
	}
	
	public void sync() throws IOException
	{
		if( canSave )
		{
			if( hasPassword() || hasEmail() )
			{
				setSaved();
				super.sync();
				if( connected() )
					ic.send( "Character saved." );
			}
			else
			{
				if( connected() )
					ic.send( "You have no password, so your character has not been saved." );
			}
		}
		else
		{
			if( connected() )
				ic.send( "You have not been given permission to save" );
		}
	}
	
	public void moveTo( Room newLocation, Effect leave, Effect enter )
	{
		if( !connected() )
			throw new PlayerNotConnectedException();
		
		permissionList.check( summonAction );
		newLocation.checkPermissionList( Room.enterAction );
		
		if( leave != null )
			leave.cause();
		
		moveTo( newLocation );
		enter.cause();
	}
	
	public void roomLook()
	{
		if( ic == null )
			throw new PlayerNotConnectedException();
		
		//permissionList.check( modifyAction );
		
		Room l = getLocation();
		
		if( brief() )
		{
			ic.send( new TextParagraph( (String) getLocation().called ), false );
			ic.send( l.who( this ), false );
		}	
		else
			ic.send( l.aspect( this ), false );
		
		ic.blankLine();
		ic.flush();
	}
	
	void moveTo( Room newLocation )
	{
		newLocation.checkPermissionList( Room.enterAction );
		
		try
		{
			if( location != null )
				location.unlinkPlayer( this );
			
			location = newLocation;
			realm = newLocation.getRealm().getThis();
			
			if( location != null )
			{
				location.linkPlayer( this );
				
					//  if they're moving into a public
					//  room, store that they're there,
					//  so we can return them to it when
					//  they type 'leave', or when they
					//  reconnect.
				if( !(location.getParent() instanceof Player) )
					lastPublicRoomLocation = location.getThis();
			}
		}
		catch( BadKeyException e )
		{
			Log.error( e );
		}
		catch( NonUniqueKeyException e )
		{
			Log.error( e );
		}
	}

	public boolean getShowFlags()
	{
		return( showFlags );
	}

	public final String himHer()
	{
		return( gender.himHer() );
	}
	
	public final String HisHer()
	{
		return( gender.HisHer() );
	}
	
	public final String hisHer()
	{
		return( gender.hisHer() );
	}
	
	public final String HeShe()
	{
		return( gender.HeShe() );
	}
	
	public final String heShe()
	{
		return( gender.heShe() );
	}
	
	public final String maleFemale()
	{
		return( gender.maleFemale() );
	}

	public final void setGender( Gender ng )
	{
		gender = ng;
	}
	
	public final Gender getGender()
	{
		return( gender );
	}
	
	public synchronized void enrolIntoClan( Clan newClan )
	{
		Clan old = getClan();
		
			//   this will be a problem if we ever do
			//   untrusted coding - they could simply
			//   enrolIntoClan( null ) & enrolIntoClan( newC )
			//   to get around the whole thing.
		if( old != null && newClan != null )
			throw new PermissionDeniedException( this, getName() + " is already in clan " + old.getName() + ", and cannot be enrolled into a new clan until they leave." );
		else
		{
			if( newClan == null )
			{
				clan = Reference.EMPTY;
			}
			else
				clan = newClan.getThis();
		}
		
			//  if they're blocking a clan, make them stop
		Type c = Type.CLAN;
		
		if( qualifierList.check( c ) == Qualifiers.SUPPRESSION_CODE )
		{
			qualifierList.set( c, Qualifiers.UNSUPPRESSION_CODE );
			
			if( connected() )
				ic.sendSystem( "You have been enrolled into a new clan, and so you no longer have 'block clan' set" );
		}
		
		if( old != null )
		{
				//  check to ensure the old clan isn't still
				//  on the inform list
			try
			{
				getInform().removeClanInform( old );
			}
			catch( BadKeyException e )
			{
			}
			catch( NonUniqueKeyException e )
			{
			}
		}
	}
	
	/**
	  *  This is a special accessor that allows limited access
	  *  to the supplied players florin count: only to increase
	  *  it.
	  *
	  * @to the player to give the florins to
	  * @amount the amount of florins to transfer
	 */
	public void transferFlorins( Player to, int amount )
	{
		if( amount > florins )
			throw new InvalidArgumentException( getName() + " doesn't have that many florins" );
		if( amount < 1 )
			throw new InvalidArgumentException( "Unable to transfer negative amounts of florins" );
		
		permissionList.check( modifyAction );
		to.florins += amount;
		florins -= amount;
		
			//  if they are connected tell them about the good deed.
		if( to.connected() )
		{
			if( amount != 1 )
				to.send( getName() + " gives you " + amount + " silver florins." );
			else
				to.send( getName() + " gives you 1 silver florin." );
		}
	}
	
	public void transferFlorins( key.talker.objects.Prop from )
	{
		florins += from.value;
		
		if( connected() )
		{
			if( from.value != 1 )
				send( "You get " + from.value + " silver florins." );
			else
				send( "You get an extra silver florin." );
		}
	}
	
	public int getFlorins()
	{
		if( privateFlorins )
			checkPermissionList( seePrivateInfoAction );
		return( florins );
	}
	
	public void subtractFlorins( int subflorins )
	{
		permissionList.check( modifyAction );
		
		if( florins >= subflorins )
			florins -= subflorins;
		else
			throw new NotEnoughMoney( subflorins, florins );
	}
	
	public void setFlorins( int newflorins )
	{
		Player c = getCurrent();
		if( c != null )
		{
			if( c.isBeyond() )
			{
				florins = newflorins;
				return;
			}
		}
		
		throw new PermissionDeniedException( this, "You may not create florins in this manner." );
	}
	
	public boolean canMakeFriend( Player p )
	{
			//  this one just does a straight check (doesn't check groups, ranks,
			//  or whatever).
		//return( permissionList.check( p.getThis(), friendAction ) );
		return( permissionList.permissionCheck( friendAction, false, false ) );
	}
	
	public String getFullName()
	{
		return( "^@" + prefix + "^$" +  getName() );
	}
	
	/**
	  *  I hereby declare each player to be
	  *  a soverign state, owned by no
	  *  other. ;p~
	  *
	  *  taken out only for the reason that it isn't
	  *  strictly necessary and I wanted to make isOwner final
	  *  subtle, 05Nov98
	  *
	public boolean isOwner( Atom potential )
	{
		if( potential == this )
			return( true );
		else
			return( false );
	}
	 */
	
	/*
	public boolean isTellBlockingPlayer( Reference p )
	{
		return( !permissionList.check( originator.getThis(), tellAction ) );
	}
	*/
	
	public void splash( Effect e, SuppressionList s )
	{
		super.splash( e, s );

		if( !connected() )
			throw new PlayerNotConnectedException();
		
		QualifierList.Entry q = null;
		boolean bounce = false;
		char preChar = ' ';
		String message = e.getMessage( this );
		Player originator = e.getOriginator();
		
		if( e instanceof ObjectEffect )
		{
			if( originator != null )
			{  
				if( !permissionList.check( originator.getThis(), tellAction ) )
				{
					if( originator.isBeyond() )
					{
						if( s != null )
							s.add( this, SuppressionList.SPECIFIC, "(beyond used to send anyway)" );
					}
					else
					{
						if( s != null )
							s.add( this, SuppressionList.SPECIFIC, blockingMsg );
						
						return;
					}
				}
			}
			
			if( showFlags )
			{
				preChar = SHOWCHARS[ SHOWFLAG_OBJECTS ];
			}
			
			q = qualifierList.getEntryFor( Type.OBJECTS );
		}
		else if( e instanceof Communication )
		{
			if( originator != null )
			{
				if( originator != this || e instanceof Broadcast )
				{
						//  if not allowed, splash a message back and finish
					if( !permissionList.check( originator.getThis(), tellAction ) )
					{
						if( originator.isBeyond() )
						{
							if( s != null )
								s.add( this, SuppressionList.SPECIFIC, "(beyond used to send anyway)" );
						}
						else
						{
								//  this is a bit sneaky -
								//  it makes it appear as if you're blocking
								//  friends when you're really not.
							if( s != null )
								s.add( this, SuppressionList.SPECIFIC, blockingMsg );
								//s.add( this, SuppressionList.GENERAL, blockingMsg ); Noble
							
							return;
						}
					}
					
					Object espl = e.getSplasher();

					if( espl instanceof Friends ) 
					{
						// this case seems solely for a beyonder with a friend who
						// is blocking them???  Friends dont even have a
						// supressionlist so all it does is prevent a beyond friend
						// broadcast from getting through (ie no mesage either)
						// Recommendation: Deletion.  Noble09Oct
						// 
						// [paul 11Oct99]: Lets leave it for now, following online
						// conversation, we'll take it out when/if we code beyond
						// so that it can be turned on and off.
						// 
						//  special permission list check
						if( !permissionList.check( originator.getThis(), tellAction ) )
						{ 
							if( s != null )
								s.add( this, SuppressionList.SPECIFIC, blockMsg );
							return; 
						}
					}
					
					if( originator != this || !(espl instanceof Room || espl instanceof Player) )
					{
						q = qualifierList.getEntryFor( Type.typeOf( e.getSplasher() ) );
						if( q == null )
							q = qualifierList.getEntryFor( Type.typeOf( e ) );
					}
					
					if( originator != this )
					{
						if( showFlags )
						{
							if( e instanceof Directed )
								preChar = SHOWCHARS[ SHOWFLAG_DIRECTED ];
							else if( e instanceof Broadcast )
							{
								Atom rs = e.getSplasher();
								
								if( rs instanceof Friends )
									preChar = SHOWCHARS[ SHOWFLAG_FRIENDS ];
								else if( rs instanceof Room )
									preChar = SHOWCHARS[ SHOWFLAG_ROOM ];
								else if( e instanceof Shout )
									preChar = SHOWCHARS[ SHOWFLAG_SHOUTS ];
								else if( e instanceof key.effect.forest.Music )
									preChar = SHOWCHARS[ SHOWFLAG_MUSIC ];
							}
						}
					}
					else if( q == null )
					{
							//  if it isn't coloured any other way
							//  and we're the originator, feedback it
						q = qualifierList.getEntryFor( Type.FEEDBACK );
					}
					
					bounce = true;  //  tell them if a communication message fails
				}
				else if( q == null )
				{
						//  if it isn't coloured any other way
						//  and we're the originator, feedback it
					q = qualifierList.getEntryFor( Type.FEEDBACK );
				}
			}
		}
		else if( e instanceof Movement )
		{
			if( showFlags && originator != this )
			{
				if( e instanceof Enter )
					preChar = SHOWCHARS[ SHOWFLAG_ENTER ];
				else if( e instanceof Leave )
					preChar = SHOWCHARS[ SHOWFLAG_LEAVE ];
			}
			
			//q = qualifierList.getEntryFor( Type.typeOf( e ) );
			q = qualifierList.getEntryFor( Type.MOVEMENT );
		}
		else if( e instanceof key.effect.Connection )
		{
			StringBuffer foundBy = new StringBuffer( message );
			
			Reference pRef = originator.getThis();
			Reference pClan;
			
			{
				Atom a = originator.getClan();
				if( a != null )
				{
					pClan = a.getThis();
					
					if( !pClan.equals( clan ) )
						pClan = Reference.EMPTY;
				}
				else
					pClan = Reference.EMPTY;
			}
			
			boolean triggered = false;
			
			for( Enumeration en = getInform().referenceElements(); en.hasMoreElements(); )
			{
				Reference a = (Reference) en.nextElement();
				
					//  this is an optimisation - (this code gets called a lot)
					//  if the Reference isn't loaded, there
					//  isn't much chance that this list entry
					//  counts.  if you take this out, be sure
					//  to put it back in on the final 'else'
					//  clause, since it is required there.
				if( a.isLoaded() )
				{
					if( pRef.equals( a ) )
					{
						foundBy.append( " [inform]" );
						triggered = true;
					}
					else if( pClan.equals( a ) )
					{
						foundBy.append( " [clan]" );
						triggered = true;
					}
					else  //  if( a.isLoaded() ) <taken out, see above>
					{
						Atom j = a.get();
						
						//if( j instanceof Scape && ((Scape)j).containsPlayer( originator ) )
						if( j instanceof Scape )
						{
							if( ((Scape)j).containsPlayer( originator ) )
							{
								foundBy.append( " [" );
								foundBy.append( j.getName() );
								foundBy.append( "]" );
								
								triggered = true;
							}
						}
					}
				}
			}
			
			if( !triggered )
				return;
			
			message = foundBy.toString();
			
			if( showFlags && originator != this )
			{
				if( e instanceof Login )
					preChar = SHOWCHARS[ SHOWFLAG_LOGIN ];
				else if( e instanceof Logout )
					preChar = SHOWCHARS[ SHOWFLAG_LOGOUT ];
			}
			
			q = qualifierList.getEntryFor( Type.CONNECTION );
		}
		else if( e instanceof Blocking )
		{
			q = qualifierList.getEntryFor( Type.BLOCKING );
			
			if( showFlags && originator != this )
				preChar = SHOWCHARS[ SHOWFLAG_BLOCKING ];
			
			if( q == null && e instanceof Block )
			{
				Scape sc = ((Block)e).blockOf();
				
				q = qualifierList.getEntryFor( Type.typeOf( sc ) );
			}
		}
		else if( e instanceof Global )
		{
			ic.send( "\n\n" );
			ic.send( new HeadingParagraph( message ) );
			ic.send( "\n\n" );
			ic.flush();
			return;
		}
		else
			ic.send( " --[ Unknown splash type recvd ]--" );
		
		if( q != null )
		{
			char c = q.get();
			
			if( c == Qualifiers.UNKNOWN_CODE )
				sendSplash( preChar, message, s, e );
			else if( c == Qualifiers.SUPPRESSION_CODE )
			{
				//Log.debug( this, "suppressing message, bounce is " + bounce + ", sl is " + s.toString() );
				
					//  beyond goes through for directed events
				if( !(e.getOriginator().isBeyond() && e instanceof Directed ) ) // not fixed this line. Noble
				{
						//  this has been suppressed - splash a message back
					if( bounce && s != null )
						s.add( this, SuppressionList.GENERAL, "(beyond used to deliver message anyway)" );
					return;
				}
				else
				{
						//  this has been suppressed - splash a message back
					if( bounce && s != null )
						s.add( this, SuppressionList.GENERAL, getBlockingMsg() );
					
					c = q.getMark();
					if( c != Qualifiers.UNKNOWN_CODE )
						sendSplash( preChar, "^" + q.getMark() + message, s, e );
					else
						sendSplash( preChar, message, s, e );
				}
			}
			else
				sendSplash( preChar, "^" + c + message, s, e );
		}
		else
			sendSplash( preChar, message, s, e );
		
		ic.flushIfWaiting();
	}
	
	public void addHistoryLine( String s )
	{
		history.addElement( s );
	}
	
	public void beep()
	{
		if( connected() && !quiet )
			ic.beep();
	}
	
	private final void sendSplash( char preChar, String message, SuppressionList sl, Effect e )
	{
		e.sending( message, this );
		
		ic.sendEffect( preChar, message, e );
		
		if( sl != null )
		{
			if( isIdle() || isAfk() )
				sl.add( this, SuppressionList.IDLING, idleMsg );
		}
	}
	
	public void sendSystem( String message )
	{
		ic.send( message );
		ic.flush();
	}
	
	/**
	  * This function is used to output direct,
	  * *successful* feedback of a players command.
	  * It is important that this feedback is normal
	  * and expected by the player, since it generally
	  * won't be hilited in any way.  Please try
	  * to restrict this output to a single line.
	  * <p>
	  * this command does start a new line
	  * <p>
	  * Examples:
	  * <p>
	  * You tell name 'hi' <br>
	  *
	  * @param message the text to be displayed
	 */
	public void sendFeedback( String message )
	{
		ic.send( message );
		ic.flush();
	}
	
	/**
	  * This function is used when the user makes some
	  * sort of error in typing - *not* if a command
	  * can't be executed because of a failure.  So,
	  * Command not found would go here, but 'Not enough
	  * privs to nuke snapper' wouldn't.
	  * <p>
	  * this command does start a new line
	 */
	public void sendError( String message )
	{
		ic.send( message );
		ic.flush();
	}

	public void sendFailure( String message )
	{
		ic.sendFailure( message );
		ic.flush();
	}
	
	/**
	  *  Meant to be used to send part of what would
	  *  normally be a paragraph.
	 */
	public void send( String message )
	{
		ic.send( message );
		ic.flush();
	}

	final void maybeClearBeyond()
	{
		if( !Key.instance().isRunning() )
			beyond = false;
		else
			throw new UnexpectedResult( "trying to clear beyond while running" );
	}
	
	final void maybeSetBeyond()
	{
		if( !Key.instance().isRunning() )
		{
			beyond = true;
			expert = true;
		}
		else
			throw new UnexpectedResult( "trying to set beyond while running" );
	}

	/**
	  *  Returns the number of rooms the player has
	 */
	public final int countRooms()
	{
		int i = 0;
		
		for( Enumeration e = elements(); e.hasMoreElements(); )
		{
			if( e.nextElement() instanceof Room )
				i++;
		}

		return( i );
	}
	
	//---  commands  ---//
	/**
	  *  A shortcut, since matching it from the
	  *  string could be prohibitively slow
	 */
	public final CommandList getCommandList()
	{
		try
		{
			return( (CommandList) commands.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			commands = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".commands wrong (reset)", e );
			commands = Reference.EMPTY;
			return( null );
		}
	}
	
	public final CommandList getSpecialCommandList()
	{
		try
		{
			return( (CommandList) specialCommands.get() );
		}
		catch( OutOfDateReferenceException e )
		{
			specialCommands = Reference.EMPTY;
			return( null );
		}
		catch( NullPointerException e )
		{
			Log.error( "during getSpecialCommandList: this is once-only code for converting pfiles", e );
			specialCommands = Reference.EMPTY;
			return( null );
		}
		catch( ClassCastException e )
		{
			Log.error( "somebody set " + getId() + ".specialcommands wrong (reset)", e );
			commands = Reference.EMPTY;
			return( null );
		}
	}
	
	//---  actions  ---//
	
	protected static StringKeyCollection staticActions;
	public static Action tellAction;
	public static Action findAction;
	public static Action friendAction;
	public static Action summonAction;
	public static Action seePrivateInfoAction;
	
	static
	{
		staticActions = new StringKeyCollection();
		findAction = newAction( Player.class, staticActions, "find", false, false );
		friendAction = newAction( Player.class, staticActions, "friend", false, false );
		seePrivateInfoAction = newAction( Player.class, staticActions, "seePrivateInfo", false, false );
		summonAction = newAction( Player.class, staticActions, "summon", false, false );
		tellAction = newAction( Player.class, staticActions, "tell", false, false );
	}
	
	public Enumeration getActions()
	{
		return( new MultiEnumeration( staticActions.elements(), super.getActions() ) );
	}
	
	public boolean containsAction( Action a )
	{
		return( staticActions.contains( a ) || super.containsAction( a ) );
	}
	
	
	
	/**
	  *  Returns the action corresponding to the
	  *  supplied name.  This routine will need to
	  *  be overriden by sub-classes using actions.
	 */
	public Action getAction( String name )
	{
		Action a = (Action) staticActions.get( name );

		if( a == null )
			return( super.getAction( name ) );
		else
			return( a );
	}
	
	private static int totalPlayers;
	
	public static int getTotalPlayers()
	{
		return( totalPlayers );
	}
	
	protected void finalize() throws Throwable
	{
		super.finalize();
		
		totalPlayers--;
	}
	
	boolean canSwap()
	{
		return( !(connected() || (connecting > 0)) );
	}
	
		// property routines
	public boolean isBanished()
	{
		DateTime now = new DateTime();

		if( banishedUntil == null )
			return( false );

		if( now.getTime() > banishedUntil.getTime() )
		{
			banishedUntil = null;
			banishType = "";
			return( false );
		}
		return( true );
	}

	public boolean getNewmail()
	{
		return( checkNewMail() );
	}
	
	public boolean checkNewMail()
	{
		return( mailbox.unread > 0 );
	}
	
	public void sendLookScreen( Player from, InteractiveConnection ic )
	{
		if( description != null && description.length() > 0 )
		{
			ic.send( description );
			ic.blankLine();
		}
		
		Paragraph p = getInventory().look( from );
		
		if( p == null )
		{
			if( description == null || description.length() == 0 )
				ic.send( getName() + " has no description and is not wearing anything." );
		}
		else
		{
			ic.sendFeedback( getName() + " is wearing:" );
			ic.send( p );
		}
	}
	
	public void sendExamineScreen( InteractiveConnection ic )
	{
		boolean seeThrough = permissionList.permissionCheck( seePrivateInfoAction, false, true );
		Player p = getCurrent();
		
		if( p != this )
			examineHits++;
		
		ic.sendLine();
		ic.send( getTitledName() );
		
			// show the description
		{
			if( description != null )
			{
				ic.sendLine();
				ic.send( Grammar.substitute( description ) );
			}
		}
		
		ic.sendLine();
		
		if( connected() )
		{
			ic.send( getFullName() + " has been logged in for " + loginStats.getTimeSinceConnection() );
		}
		else
		{
			DateTime dateTime = (DateTime) loginStats.getLastConnection();
			
			if( dateTime == null )
				ic.send( getFullName() + " has never logged in." );
			else
				ic.send( getFullName() + " was last seen at " + dateTime.toString( p ) );
		}
		
		{
			StringBuffer sb = new StringBuffer();

			{
				if( aka != null && aka.length() > 0 )
				{
					sb.append( "Also known as ^@" );
					sb.append( aka );
					sb.append( "^$; " );
				}
			}
			
			sb.append( getName() );
			sb.append( " is " );
			
			if( age != 0 )
			{
				sb.append( Grammar.aAn( age ) );
				sb.append( " year old " );
			}
			
			sb.append( maleFemale() );
			sb.append( ".\n" );
		
			//  clan
			Clan c = getClan();
			
			if( c != null )
			{
				sb.append( HeShe() );
				sb.append( " is in clan " );
				sb.append( c.getName() );
				sb.append( " [" );
				
				boolean first = true;
				
				for( Enumeration e = c.ranks.elements(); e.hasMoreElements(); )
				{
					Rank r = (Rank) e.nextElement();
					
					if( r.contains( this ) )
					{
						if( first )
						{
							sb.append( ' ' );
							first = false;
						}
						
						sb.append( r.getName() );
						sb.append( ' ' );
					}
				}
				
				sb.append( "]\n" );
			}
			else if( isLiberated() )
			{
				sb.append( HeShe() );
				sb.append( " doesn't want to be in a clan.\n" );
			}
			
			sb.append( HeShe() );
			sb.append( " has been idle for " );
			sb.append( getIdle( new DateTime() ).toString() );
			sb.append( '.' );
			
			if( !privateFlorins || seeThrough )
			{
				sb.append( '\n' );
				sb.append( HeShe() );
				sb.append( " has " );
				if( florins != 1 )
				{
					if( florins > 10000 )
						sb.append( "way more silver florins than " + heShe() + " can realistically carry." );
					else
					{
						sb.append( florins );
						sb.append( " silver florins." );
					}
				}
				else
					sb.append( "1 silver florin." );
				
				if( privateFlorins )
					sb.append( " <hidden>" );
			}
			
			ic.send( sb.toString() );
		}
		
		ic.send( new HeadingParagraph( Integer.toString( examineHits ) + " hits", HeadingParagraph.RIGHT ) );
		
			//  output ranks.
		if( !hideRanks )
		{
			String rs = Grammar.enumerate( new FilteredEnumeration( ranks(),
					new key.util.FilteredEnumeration.Filter()
					{
						public boolean isValid( Object element, Enumeration enum )
						{
							return( element instanceof Rank);
						}
						
						public Object replace( Object element, Enumeration enum )
						{
							return( ((Rank)element).getContainedId() );
						}
					}) );
			
			if( rs.length() != 0 )
			{
				ic.sendFeedback( rs );
				ic.sendLine();
			}
		}
	}
	
	public static final Paragraph planHeading = new HeadingParagraph( "plan", HeadingParagraph.RIGHT );
	
	public void sendFingerScreen( InteractiveConnection ic )
	{
		boolean seeThrough;
		Player p = getCurrent();
		
		if( p != this )
			fingerHits++;
		
		if( p == null )
			seeThrough = false;
		else
			seeThrough = permissionList.permissionCheck( seePrivateInfoAction, false, true );
		
		String ea = null;
		
		if( !privateEmail || seeThrough )
			ea = getEmailAddress();
		
		ic.send( new HeadingParagraph( getFullName() ) );
		
		if( connected() )
		{
			ic.send( getFullName() + " has been logged in for " + loginStats.getTimeSinceConnection() );
			
			if( p != null )
			{
				InteractiveConnection pic = getConnection();
				
				if( pic instanceof SocketIC )
				{
					if( p == this || p.isBeyond() )
						ic.send( getName() + " is connected from: " + ((SocketIC)pic).getPrivateSiteName() );
					else
						ic.send( getName() + " is connected from: " + ((SocketIC)pic).getFullSiteName() );
				}
			}
		}
		else
		{
			DateTime now = new DateTime();
			DateTime dateTime = loginStats.getLastDisconnection();
			if( dateTime == null )
				ic.send( getFullName() + " has never logged in." );
			else
			{
				if( seeThrough && notimeout )
					ic.send( getFullName() + " was last seen at " + dateTime.toString( p ) + " [notimeout]" );
				else
					ic.send( getFullName() + " was last seen at " + dateTime.toString( p ) );
			}
			DateTime lc = loginStats.getLastConnection();
			if( lc != null )
			{
				ic.send( "That is, " + dateTime.difference( now ).toStdString( 1 ) + " ago " + heShe() + " was on for " + lc.difference( dateTime ).toStdString( 2 ) );
			}
		}
		
		if( whereFrom != null && whereFrom.length() > 0 )
		{
			Date date = new Date();
			
			DateTime d = new DateTime( date.getTime(), timezone );
			ic.send( "The time in " + "^@" + whereFrom + "^$" + " is " + d.toString() );
		}
		
		if( !hideTime || seeThrough )
		{
			if( hideTime )
				ic.send( gender.HisHer() + " total login time is " + loginStats.getTotalConnectionTime().toLimitedString(4)  + " <hidden>" );
			else
				ic.send( gender.HisHer() + " total login time is " + loginStats.getTotalConnectionTime().toLimitedString(4) );
			
			if( loginStats.firstConnection != null )
			{
				ic.send( "Participation: [" + ("" + loginStats.getParticipation()).substring( 0,4 ) + "%] since " + ((oldLoginTime != null && oldLoginTime.length() > 0) ? "the Eclipse (Apr 19, 1999)" : (loginStats.firstConnection.toShortString())) );
			}
			
			if( oldLoginTime != null && oldLoginTime.length() > 0 )
			{
				if( hideTime )
					ic.send( gender.HisHer() + " pre-eclipse login time was " + oldLoginTime + " <hidden>" );
				else
					ic.send( gender.HisHer() + " pre-eclipse login time was " + oldLoginTime );
			}
		}
		
			//  email addresses
		{
			if( ea != null )
			{
				if( privateEmail )
					ic.sendFeedback( HisHer() + " email address is: " + ea + " <private>" );
				else
					ic.sendFeedback( HisHer() + " email address is: " + ea );
			}
		}
		
		{
			String wp = homepage.get();
			
			if( wp != null )
				ic.sendFeedback( HisHer() + " web page is: ^<" + wp + "^>" );
		}
		
		if( icq > 0 )
		{
			ic.sendFeedback( HisHer() + " ICQ # is: " + icq );
		}
		
		if( checkNewMail() )
		{
			ic.sendFeedback( HeShe() + " has new mail." );
		}		
		
		Paragraph plan = getPlan();
		
			//  show the plan (exile 28jul97)
		if( plan != null && !plan.isEmpty() )
		{
			ic.send( planHeading );
			ic.send( Grammar.substitute( plan ) );
		}
		
		ic.send( new HeadingParagraph( Integer.toString( fingerHits ) + " hits", HeadingParagraph.RIGHT ) );
	}
	
	// --  cookies aren't saved, but are kind of cool
	// --  for whatever you want to use them for.  Generally
	// --  saving state during seperate commands is a good
	// --  thing.

	private transient Hashtable cookies;
	public void setCookie( Object key, Object value )
	{
		if( cookies == null )
			cookies = new Hashtable( 9 );
		
		cookies.put( key, value );
	}
	
	public void removeCookie( Object key )
	{
		if( cookies != null )
			cookies.remove( key );
	}
	
	public Object getCookie( Object key )
	{
		if( cookies == null )
			return( null );
		else
			return( cookies.get( key ) );
	}
	
	public boolean isHideTime()
	{
		return( hideTime );
	}
	
	private transient long[] spamThrottle;
	private transient int[] spamViolations;
	private static final int[] THROTTLE_THRESHOLD = { 0, 3 };
	private static final int[] THROTTLE_PENALTY = { 1000, 2000 };
	public static final int NUMBER_OF_THROTTLES = 2;
	
	/**
	  *  Called just before a broadcast event.  It prevents
	  *  spamming broadcast channels.  The type is the
	  *  'type' of the channel.  Each channel type has different
	  *  throttle rates (the more public, generally, the less
	  *  tolerant we are of 'spam' type communication.
	 */
	public final void aboutToBroadcast( int type )
	{
		long a = System.currentTimeMillis() / 1000;
		
		if( (a - THROTTLE_THRESHOLD[type]) <= spamThrottle[type] )
		{
			try
			{
				spamViolations[type]++;
				
				if( spamViolations[type] > 9 )
				{
					for( int i = 0; i < NUMBER_OF_THROTTLES; i++ )
						spamViolations[type] = 0;
					
					ic.send( "^hYou have been broadcasting too quickly.^-" );
					ic.send( "^hPlease talk a little slower and be more lag.friendly in the future.^-" );
					disconnect();
				}
				else
				{
					if( spamViolations[type] > 2 )
					{
						Thread.sleep( THROTTLE_PENALTY[type] );
						spamThrottle[type]++;
					}
					else if( spamViolations[type] > 7 )
					{
						throw new LimitExceededException( "You are sending communication too quickly.  Please be lag.friendly and wait a few seconds between says, shouts and tells." );
					}
				}
			}
			catch( InterruptedException e )
			{
			}
		}
		else
		{
			if( a - spamThrottle[type] > 10 )
				spamViolations[type] = 0;
			else
			{
				if( spamViolations[type] > 0 )
					spamViolations[type]--;
			}
			
			spamThrottle[type] = a;
		}
	}
	
	private static Class httpThreadClass;
	
	static
	{
		try
		{
			httpThreadClass = Class.forName( "com.mortbay.Util.ThreadPool$PoolThread" );
			System.out.println( "Web Authentication Extensions enabled" );
		}
		catch( ClassNotFoundException e )
		{
			System.out.println( "Web Authentication Extensions disabled (Jetty not installed)" );
		}
	}
	
	public static Player getCurrent()
	{
		Thread t = Thread.currentThread();
		Player p = null;
		
		//System.out.println( "(Thread class is " + t.getClass().getName() + ")");
		
		if( t instanceof Animated )
		{
			Animate a = ((Animated)t).is();
			
			//System.out.println( "  animate is " + a.getClass().getName() );
			
			if( a instanceof InteractiveConnection )
			{
				p = ((InteractiveConnection)a).getPlayer();
				
				if( p == null )
				{
					Interactive i = ((InteractiveConnection)a).getInteracting();
					//System.out.println( "  interactive is " + i.getClass().getName() );
					if( i == null )
						return( null );
					else if( i instanceof ConnectingPlayer )
						return( null );  //  this class is permitted to act with beyond
					else if( i instanceof ConnectingTwig )
						return( null );  //  this class is permitted to act with beyond
				}
				//else
				//{
					//System.out.println( "  IC player is " + p.getName() );
				//}
			}
			else if( a instanceof TelnetConnectPort )
				return( null );
			else if( a instanceof TwigConnectPort )
				return( null );
			else if( a instanceof LatentCache )
				return( null );
			else if( a instanceof Scheduler )
				return( null );
		}
		else if( t instanceof key.web.PoolThread )
		{
			p = ((key.web.PoolThread)t).getPlayer();
		}
		else
		{
				//  sometimes, from a system script, currentThread won't be
				//  animated.  In this case, however, we're always going
				//  to be the only player online, and Key isn't going to
				//  be running.  In that case:
			Key key = Key.instance();
			if( key != null && !key.isRunning() )
			{
				if( key.numberPlayers() == 1 )
				{
					for( Enumeration e = key.players(); e.hasMoreElements(); )
					{
						return( (Player) e.nextElement() );
					}
				}
			}
			
			return( null );
		}
		
		if( p == null )
		{
			p = Key.instance().getAnonymous();
			
			if( p == null )
				throw new AccessViolationException( t, "Anonymous players not allowed" );
		}
		
		return( p );
	}
	
	public Object retrieveField( java.lang.reflect.Field f )
		throws IllegalAccessException
	{
		if( f.getDeclaringClass() == Player.class )
			return( f.get( this ) );
		else
			return( super.retrieveField( f ) );
	}
}

