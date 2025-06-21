# Forest Online Help v1.00 chapter 1
cd /online/help
# (COMMUNICATION) [1] (snapper 28/07/97 v1.00)
load Screen communication
cd communication
set .title (COMMUNICATION) [1]
set .author snapper
edit .text

Communication
-------------

There are many ways to communicate, and many modes of communication.  There is room communication, by which only people in the same room as you, can hear your comments and actions, and you theirs; Direct communication, one-to-one style "private" communication; and channels.

sub topics

  ^hroom_comm^-    (1.1)     room communication commands 
  ^hdirect_comm^-  (1.2)     direct communication commands
  ^hchan_comm^-    (1.3)     channel communication commands
  ^hblock^-        (1.4)     the block command
  ^hblocking^-     (1.5)     the blocking command
.end
cd ..
many communication comm comms 1
# (COMMUNICATION/room) [1.1] (snapper 28/07/97) v1.00 )
load Screen room_comm
cd room_comm
set .title (COMMUNICATION/room) [1.1]
set .author snapper
edit .text

Room Communication Commands
---------------------------

Any communication command issued goes only to the room, but it is still considered public communication as anyone in the room can see it. ( It's like talking in a room at with a group of people )

sub topics

  ^hsay^-          (1.1.1)   the humble say command
  ^hemote^-        (1.1.2)   the emote command
  ^hthink^-        (1.1.3)   the think command
  ^hwhisper^-      (1.1.4)   the whisper command
.end
cd ..
many room_comm 1.1
# (COMMUNICATION/room) [1.1.1] (snapper 28/07/97) v1.00 )
load Screen say
cd say
set .title (COMMUNICATION/room) [1.1.1]
set .author snapper
edit .text

say
---

Sends a message to everyone in the same room as you.

Format: say <message>

Shortcuts: '  "

Example: ^h'say hi'^-
  people in the same room see '^hyourname says 'hi'^-'

SEE ALSO: emote think
.end
cd ..
many say ' " 1.1.1
# (COMMUNICATION/room) [1.1.2] (snapper 28/07/97) v1.00 )
load Screen emote
cd emote
set .title (COMMUNICATION/room) [1.1.2]
set .author snapper
edit .text

emote
-----

Sends an action to everyone in the same room as you.

Format: emote <message>

Shortcuts: ;  :

Example: '^hemote grins a wicked grin.^-'
  people in the room see '^hyourname grins a wicked grin.^-'

SEE ALSO: say think
.end
cd ..
many emote ; : 1.1.2
# (COMMUNICATION/room) [1.1.3] (snapper 28/07/97) v1.00 )
load Screen think
cd think
set .title (COMMUNICATION/room) [1.1.3]
set .author snapper
edit .text

think
-----

Sends a thought bubble to everyone in the same room as you.

Format: think <message>

Shortcut: ~  

Example: '^hthink mmm, chicken^-'
  people in the room see '^hyourname thinks . o O ( mmm, chicken )^-'

SEE ALSO: say emote
.end
cd ..
many think ~ 1.1.3
# (COMMUNICATION/room) [1.1.4] (snapper 29/07/97) v1.00 )
load Screen whisper 
cd whisper
set .title (COMMUNICATION/room) [1.1.4]
set .author snapper
edit .text

whisper
-------

Sends a semi-private message to someone in the same room as you.

Format: whisper <name> <message>

Shortcut: =

Example: '^hwhisper snapper hey man^-'   (provided snapper is in the same room) 
  people in the room see '^hyourname whispers something to snapper^-'
  snapper sees '^hyourname whispers 'hey man' to you^-'
.end
cd ..
many whisper = 1.1.4
# (COMMUNICATION/direct) [1.2] (snapper 28/07/97) v1.00 )
load Screen direct_comm
cd direct_comm
set .title (COMMUNICATION/direct) [1.2]
set .author snapper
edit .text

Direct Communication Commands
-----------------------------

These commands are private communication commands in that the only people to see, is the name(s) you specify when you execute the command.

Multiple names can be encorporated into the command by using a comma seperated list with no spaces.

Example: '^htell snap,subt,exile y0^-'
  sends a tell to snapper, subtle and exile ( if they're on ).  exile will see:
    '^hyourname tells you, subtle and snapper 'y0!'^-'
  the others will see similar messages.
  
sub topics

  ^htell^-         (1.2.1)   the tell command
  ^hremote^-       (1.2.2)   the remote command
  ^hrthink^-       (1.2.3)   the remote think command
.end
cd ..
many direct_comm direct_communication 1.2
# (COMMUNICATION/direct) [1.2.1] (snapper 28/07/97) v1.00 )
load Screen tell
cd tell
set .title (COMMUNICATION/direct) [1.2.1]
set .author snapper
edit .text

tell
----

Send a direct message to a person or persons.

Format: tell <name> <message>

Shortcuts: .  >

Example: '^htell subtle I hate writing help files^-'
  subtle will see '^hyourname tells you 'I hate writing help files'^-'

SEE ALSO: remote rthink
.end
cd ..
many tell > . 1.2.1
# (COMMUNICATION/direct) [1.2.2] (snapper 28/07/97) v1.00 )
load Screen remote
cd remote
set .title (COMMUNICATION/direct) [1.2.2]
set .author snapper
edit .text

remote
------

Send a remote emote to a person or persons.

Format: remote <name> <message>

Shortcuts: ,  <

Example: '^hremote subtle hates writing help files^-'
  subtle will see '^hyourname hates writing help files'^-'

SEE ALSO: tell rthink
.end
cd ..
many remote < , 1.2.2
# (COMMUNICATION/direct) [1.2.3] (snapper 28/07/97) v1.00 )
load Screen rthink
cd rthink
set .title (COMMUNICATION/direct) [1.2.3]
set .author snapper
edit .text

rthink
------

Send a remote thought bubble to a person or persons.

Format: rthink <name> <message>

Shortcut: rt

Example: '^hrthink subtle worked it out yet?^-'
  subtle will see '^hyourname thinks . o O ( worked it out yet? )'^-'

SEE ALSO: tell remote
.end
cd ..
many rthink rt 1.2.3
# (COMMUNICATION/channels) [1.3] (snapper 02/08/97) v1.01 )
load Screen chan_comm
cd chan_comm
set .title (COMMUNICATION/channels) [1.3]
set .author snapper
edit .text

Channel Communication Commands
------------------------------

There are three channels currently on Forest that residents can have access to. Those being: friends channel, clan channel and the shout channel.

sub topics

  ^hfriends_chan^- (1.3.1)   the friends channel commands
  ^hclan_chan^-    (1.3.2)   the clan channel commands
  ^hshout_chan^-   (1.3.3)   the shout channel commands

SEE ALSO: clans friends
.end
cd ..
many chan_comm channel_communication chan_communication channel_comm 1.3
# (COMMUNICATION/channels) [1.3.1] (snapper 29/07/97) v1.00 )
load Screen friends_chan
cd friends_chan
set .title (COMMUNICATION/channels) [1.3.1]
set .author snapper
edit .text

The friends channel commands
----------------------------

Format: tf <message>   sends '^hyourname tells its friends '<message>'^-'
        rf <message>   sends '^hyourname <message>^-'
        ft <message>   sends '^hyourname thinks . o O ( <message> )^-'

                     - to your friends ( providing you're not blocking them )

To block the friends channel use '^hblock friends^-'

Shortcuts: tf   f'
           rf   f;
           ft   f~

SEE ALSO: block friend friends 
.end
cd ..
many friends_chan friends_channel tf rf ft f f' f; f~ 1.3.1
# (COMMUNICATION/channels) [1.3.2] (snapper 29/07/97) v1.00 )
load Screen clan_chan
cd clan_chan
set .title (COMMUNICATION/channels) [1.3.2]
set .author snapper
edit .text

The Clan channel commands
-------------------------

Format: clan say <message>     sends '^h(CLAN): yourname says '<message>'^-'
        clan emote <message>   sends '^h(CLAN): yourname <message>^-'
        clan think <message>   sends '^h(CLAN): yourname thinks . o O ( <message> )^-'
                     
                       - to your clan ( providing you're not blocking it )

To block the clan channel use ^h'block clan^-'

Shortcuts: clan say     c'
           clan emote   c;
           clan think   c~

SEE ALSO: block clan clans
.end
cd ..
many clan_chan clan_channel c' c; c~ 1.3.2
# (COMMUNICATION/channels) [1.3.3] (snapper 02/08/97 v1.00 )
load Screen shout_chan
cd shout_chan
set .title (COMMUNICATION/channels) [1.3.3]
set .author snapper
edit .text

the shout channel commands
--------------------------

The shout channel is a global channel that by default everyone in the realm is listening to.  Note, most people get sick of it, and block it real quick.  The commands for which are:

Format: shout <message>    sends '^hyourname shouts '<message>'^-'
        eshout <message>   sends '^hyourname <message>^-'


To block shouts, use '^hblock shouts^-'

Shorcuts: shout    ! e'
          eshout   e;

SEE ALSO: block
.end
cd ..
many shout_chan shout shouts eshout ! e' e; 1.3.3
# (COMMUNICATION/block) [1.4] (snapper 29/07/97 v1.00 )
load Screen block
cd block
set .title (COMMUNICATION/block) [1.4]
set .author snapper
edit .text

block
-----

Toggles or stops/allows communication from either a player or scape. ^hon^- or ^hoff^- merely enforce the toggle to one direction, on or off =)

To list what you are blocking, use '^hblocking^-'
Format: block <"clan" | "friends" | "movement" | "tells" | player> [on | off]

clan     - the clan channel
friends  - the friends channel
movement - any movement to and from the room from other players
tells    - all tells from any player 
player   - all tells from one specific player

SEE ALSO: clan friends movement tell
.end
cd ..
many block 1.4 
# (COMMUNICATION/blocking) (snapper 29/07/97 v1.00 )
load Screen blocking
cd blocking
set .title (COMMUNICATION/blocking) [1.5]
set .author snapper
edit .text

blocking
--------

Shows what you are blocking.

SEE ALSO: block
.end
cd ..
many blocking 1.5
# (COMMUNICATION/index) (snapper 29/07/97 v1.00 )
load Screen comm_index
cd comm_index
set .title (COMMUNICATION/index)
set .author snapper
edit .text

Communication Index
-------------------

  ^hroom_comm^-    (1.1)     room communication commands

  ^hsay^-          (1.1.1)   the humble say command
  ^hemote^-        (1.1.2)   the emote command
  ^hthink^-        (1.1.3)   the think command
  ^hwhisper^-      (1.1.4)   the whisper command
  
  ^hdirect_comm^-  (1.2)     direct communication commands
  
  ^htell^-         (1.2.1)   the tell command
  ^hremote^-       (1.2.2)   the remote command
  ^hrthink^-       (1.2.3)   the remote think command
  
  ^hchan_comm^-    (1.3)     channel communication commands
  
  ^hfriends_chan^- (1.3.1)   the friends channel commands
  ^hclan_chan^-    (1.3.2)   the clan channel commands

  ^hblock^-        (1.4)     the block command
  ^hblocking^-     (1.5)     the blocking command

.end
cd ..
many comm_index 1.index
