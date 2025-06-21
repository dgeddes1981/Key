# Forest Online Help v1.00 Chapter 3
cd /online/help
# (DISCOVERY) [3] (snapper 29/07/97 v1.00 )
load Screen discovery
cd discovery
set .title (DISCOVERY)
set .author snapper
edit .text

Discovery
---------

All about you in the digital bitstream is information, and within this realm, there is a plethora of data to access.  To make this easier in your sojourn here, the following discovery commands have been coded.

sub topics

  ^hcommands^-     (3.1)     gives a list of the commands available to you
  ^hexamine^-      (3.2)     examine a player
  ^hfinger^-       (3.3)     alternative command to examine
  ^hfootnote^-     (3.4)     the cool footnote command
  ^hhelp^-         (3.5)     this!
  ^hidle^-         (3.6)     shows how idle someone is (also does idlemode!)
  ^hlook^-         (3.7)     look at a room, exit or object
  ^hmotd^-         (3.8)     bring up the motd
  ^hstatus^-       (3.9)     current status of Forest
  ^hswho^-         (3.10)    list all players online
  ^htime^-         (3.11)    show the current time
  ^hwhere^-        (3.12)    find out where someone is
  ^hwho^-          (3.13)    list players online with information
  ^hwith^-         (3.14)    find out who someone is with
.end
cd ..
many discovery discover 3
# (DISCOVERY/commands) [3.1] (snapper 31/07/97 v1.00 )
load Screen commands
cd commands
set .title (DISCOVERY/commands) [3.1]
set .author snapper
edit .text

commands
--------

Gives a list of commands available to you, and the command set they belong to.
.end
cd ..
many commands 3.1
# (DISCOVERY/examine) [3.2] (snapper 31/07/97 v1.00 )
load Screen examine
cd examine
set .title (DISCOVERY/examine) [3.2]
set .author snapper
edit .text

examine
-------

Brings up information on a player, including their title, login time, and description.

A similar command is, '^hfinger^-'

SEE ALSO: describe finger title
.end
cd ..
many examine x 3.2
# (DISCOVERY/finger) [3.3] (snapper 31/07/97 v1.00 )
load Screen finger
cd finger
set .title (DISCOVERY/finger) [3.3]
set .author snapper
edit .text

finger
------

Brings up information on a player, including their title, last login, and plan.

A similar command is, '^hexamine^-'

SEE ALSO: examine
.end
cd ..
many finger 3.3
# (DISCOVERY/footnote) [3.4] (snapper 31/07/97 v1.00 )
load Screen footnote
cd footnote
set .title (DISCOVERY/footnote) [3.4]
set .author snapper
edit .text

footnote
--------

Brings up really cool message, based on a visable description that one of the Builders have inserted into their work.  Usually in a description.

Format: footnote <number>

Example: '^hfootnote 4^-'
You'll see: '^hFootnote 4:  The center of the old forest community.^-'
.end
cd ..
many footnote 3.4
# (DISCOVERY/help) [3.5] (snapper 31/07/97 v1.00 )
load Screen help
cd help
set .title (DISCOVERY/help) [3.5]
set .author snapper
edit .text

help
----

This!  If you're seeking help on how to use help, try '^hhelp nav^-' which will help you with navigating your way through the help system.
.end
cd ..
many help 3.5
# (DISCOVERY/idle) [3.6] (snapper 31/07/97 v1.00 )
load Screen idle
cd idle
set .title (DISCOVERY/idle) [3.6]
set .author snapper
edit .text

idle
----

This command can be used in two ways.  To check how idle a player is, and to toggle in and out of idle mode.

sub topics

  ^hidleplayer^-   (3.6.1)   to check the idle on a player
  ^hidlemode^-     (3.6.2)   to toggle in and out of idle mode
.end
cd ..
many idle 3.6
# (DISCOVERY/idle) [3.6.1] (snapper 31/07/97 v1.00 )
load Screen idleplayer 
cd idleplayer
set .title (DISCOVERY/idle) [3.6.1]
set .author snapper
edit .text

idling a player
---------------

To find out someone's idle time ( how long they've been without hitting the enter key ) use this command.

Format: idle <player>

Example: '^hidle snapper^-'
You'll see something like, '^hsnapper has been idle for 47 seconds^-'

SEE ALSO: idle idlemode
.end
cd ..
many idleplayer 3.6.1
# (DISCOVERY/idle) [3.6.2] (snapper 31/07/97 v1.00 )
load Screen idlemode
cd idlemode
set .title (DISCOVERY/idle) [3.6.2]
set .author snapper
edit .text

idle mode
---------

Idle Mode, is supposedly when you are running off to get a coke! or you plan to be idle for a certain period of time. ( so people know when they do a tell to you )...

To go idle type '^hidle^-'

This command toggles, so to return from being idle, simply type '^hidle^-' again.
.end
cd ..
many idlemode 3.6.2
# (DISCOVERY/look) [3.7] (snapper 01/08/97 v1.00 )
load Screen look
cd look
set .title (DISCOVERY/look) [3.7]
set .author snapper
edit .text

look
----

Look at the current room, an exit or an object that exists in the room.  To see the room by itself simply type '^hlook^-' otherwise, '^hlook <object|exit>^-' is used.

To see who is in the room without the description use '^hlook -^-'

Shortcut: l

SEE ALSO: exits 
.end
cd ..
many look l 3.7
# (DISCOVERY/motd) [3.8] (snapper 02/08/97 v1.00 )
load Screen motd
cd motd
set .title (DISCOVERY/motd) [3.8]
set .author snapper
edit .text

message of the day
------------------

The message of the day is usually something important the Directors want you to know about.  It's also used to display things of note, changes etc.

Format: motd
.end
cd ..
many motd 3.8
# (DISCOVERY/status) [3.9] (snapper 03/01/99 v1.01 )
load Screen status
cd status
set .title (DISCOVERY/status) [3.9]
set .author snapper
edit .text

status
------

Brings up some current information about forest.  This includes the current time, how long the program has been up for, some memory information, number of people online, and some tallys of atoms, containers, scapes, players, and other objects. 
.end
cd ..
many status 3.9
# (DISCOVERY/swho) [3.10] (snapper 03/01/99 v1.01 )
load Screen swho
cd swho
set .title (DISCOVERY/swho) [3.10]
set .author snapper
edit .text

swho
----

Brings up a short named listing of all people currently online.
.end
cd ..
many swho 3.10
# (DISCOVERY/time) [3.11] (snapper 03/01/99 v1.01 )
load Screen time
cd time
set .title (DISCOVERY/time) [3.11]
set .author snapper
edit .text

time
----

Brings up the current time ( including your own time if you have a timezone set ), how long the program has been up for, the number of people online, and the name of the top spod ( the person who has been online for the longest ).
.end
cd ..
many time 3.11
# (DISCOVERY/where) [3.12] (snapper 03/01/99 v1.01 )
load Screen where
cd where
set .title (DISCOVERY/where) [3.12]
set .author snapper
edit .text

where
-----

Brings up a short description on where a person is, you might be able to recognise the place. ( It is based on the room's portrait that the person is in )

Format: where <name>
Example: '^hwhere snapper^-'
'^hsnapper is in somewhere^-'
.end
cd ..
many where 3.12
# (DISCOVERY/who) [3.13] (snapper 03/01/99 v1.01 )
load Screen who
cd who
set .title (DISCOVERY/who) [3.13]
set .author snapper
edit .text

who
---

This command gives a paged listing of who is online, a short word on their location, what clan they belong to ( if any ), how idle they are, and their rank.

If you supply a letter or letters, it will match any players that meet the
criteria.

Example: '^hw sna^-' will match all players who's name begins with sna.

NOTE: If only one player is matched, it will bring up a semi-detailed description of them, much akin to examine and finger.

Shortcut: w

SEE ALSO: examine finger
.end
cd ..
many who w 3.13
# (DISCOVERY/with) [3.14] (snapper 03/01/99 v1.01 )
load Screen with
cd with
set .title (DISCOVERY/with) [3.14]
set .author snapper
edit .text

with
----

Finds who someone is with. ( with being in the same room as ), provided the person is not hiding.  Any person who is hiding, will not show up.  

Example: '^hwith snapper^-' would list all people snapper is with.
.end
cd ..
many with 3.14
# (DISCOVERY/index) (snapper 02/08/97 v1.00 )
load Screen discovery_index
cd discovery_index
set .title (DISCOVERY/index)
set .author snapper
edit .text

Discovery Index
---------------

  ^hcommands^-     (3.1)     gives a list of the commands available to you
  ^hexamine^-      (3.2)     examine a player
  ^hfinger^-       (3.3)     alternative command to examine
  ^hfootnote^-     (3.4)     the cool footnote command
  ^hhelp^-         (3.5)     this!

  ^hidle^-         (3.6)     shows how idle someone is (also does idlemode!)

  ^hidleplayer^-   (3.6.1)   to check the idle on a player
  ^hidlemode^-     (3.6.2)   to toggle in and out of idle mode

  ^hlook^-         (3.7)     look at a room, exit or object
  ^hmotd^-         (3.8)     bring up the motd
  ^hstatus^-       (3.9)     current status of Forest
  ^hswho^-         (3.10)    list all players online
  ^htime^-         (3.11)    show the current time
  ^hwhere^-        (3.12)    find out where someone is
  ^hwho^-          (3.13)    list players online with information
  ^hwith^-         (3.14)    find out who someone is with
.end
cd ..
many discovery_index 3.index



