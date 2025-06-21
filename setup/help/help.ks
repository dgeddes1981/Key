# Forest Online Help v1.00
cd /online/help
# general (snapper 28/07/97 v1.00 )
load Screen general
cd general
set .title General Help
set .author snapper
edit .text
.wipe
Forest Online Help System
-------------------------

The online help system is a comprehensive documentation of all the commands
used on Forest and explanations about other miscellaneous topics.  To bring
up help on a topic, simply type: ^hhelp <topic>^-

Example: ^hhelp communication^-

Help is a series of files, organised into categories. These categories are:
  
1.  communication  ( eg: room, friends, clan )
2.  personal       ( eg: title, prefix )
3.  discovery      ( eg: inspect, look, finger )
4.  movement       ( eg: go, trans )
5.  permissions    ( eg: actions, allow, deny )
6.  building       ( eg: rooms, exits )
7.  correspondence ( eg: editor, mail, news )
8.  clans          ( eg: accept, revoke, secede )
9.  miscellaneous  ( eg: colours, page )
10. background     ( eg: clans, citizen, resident )
11. terminology    ( eg: spods, symbols, abbreviations )
12. Law            ( eg: profanity, harassment )

It is recommended you type: ^hhelp nav^- for instructions on how to use help.
.end
cd ..
# nav (snapper 28/07/97 v1.00 )
load Screen navigation
cd navigation
set .title Navigation through Help
set .author snapper
edit .text
.wipe
Navigation
----------

To navigate through help, either try: '^hhelp <topic>^-' or '^hhelp <number>^-' with
the number being a chapter or sub-chapter in the form x.x.x  For example,
To find out about the ^hsay^- command, you could type: '^hhelp say^-' or you could
navigate through the help system with '^hhelp 1^-' ( for communication ), then
'^hhelp 1.1^-' ( for room communication ), then ^hhelp 1.1.1^- for the say command.

To bring up an index on an individual chapter, use '^hhelp x.index^-' where ^hx^-
is the chapter number, eg '^hhelp 1.index^-'
.end
cd ..
many navigation nav

# Added
cd /online/help
load Screen clan_comm
edit clan_comm
These commands are used when you are enrolled in a
clan, if you are not in a clan there is no need to
read on.

c' - sends a clan say
(i.e. c' hi)
(CLAN): JeRRy says 'hi'

c; - sends a clan emote
(i.e. c; smiles)
(CLAN): JeRRy smiles

clan secede - quit from a clan
clan accept - enrol someone to a rank
clan revoke - remove someone from the clan

.end
set clan_comm.author JeRRy
load Screen quit
edit quit
quit - quits you from the program
.end
set quit.author JeRRy
load Screen comments
edit comments
comments - set a session comment
.end
set comments.author JeRRy
load Screen session
set session.author JeRRy
edit session
session - set a session
.end

