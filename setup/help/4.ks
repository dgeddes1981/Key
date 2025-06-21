# Forest Online Help v1.00 Chapter 4
cd /online/help
# (MOVEMENT) [4] (snapper 02/08/97 v1.00 )
load Screen movement
cd movement
set .title (MOVEMENT)
set .author snapper
edit .text

Movement
--------
Being able to look around your virtual environment is fine, but without being able to move inside it, it might as well be a virtual piece of paper.  So, here are some movement commands to help you navigate your way through this virtual environ!
sub topics

  ^hgo^-           (4.1)     go through an exit
  ^hexits^-        (4.2)     list the eixts in the current room you're in
  ^hhome^-         (4.3)     go to your home room
  ^hjoin^-         (4.4)     join another player
  ^hleave^-        (4.5)     leave the current room
  ^hsummon^-       (4.6)     summon someone else to where you are
  ^htrans^-        (4.7)     transport yourself to one of your own rooms
  ^hvisit^-        (4.8)     visit another players home room
.end
cd ..
many movement move 4
# (MOVEMENT/go) [4.1] (snapper 02/08/97 v1.00 )
load Screen go
cd go
set .title (DISCOVERY/go) [4.1]
set .author snapper
edit .text

go
--

This command makes you go through an exit into a new room, provided the exit exists of course.  To find out which exits there are in a room, type '^hexits^-'

Format: go <exit>

SEE ALSO: exit exits
.end
cd ..
many go 4.1
# (MOVEMENT/exits) [4.2] (snapper 02/08/97 v1.00 )
load Screen exits
cd exits
set .title (MOVEMENT/exits) [4.2]
set .author snapper
edit .text

exits
-----

Lists all the exits in the current room.  Use '^hgo <exit>^-' to go through one of them.

SEE ALSO: exit go
.end
cd ..
many exits 4.2
# (MOVEMENT/home) [4.3] (snapper 02/08/97 v1.00 )
load Screen home
cd home
set .title (MOVEMENT/home) [4.3]
set .author snapper
edit .text

home
----

This command takes you from where ever you are to your home room.

SEE ALSO: homeroom
.end
cd ..
many home 4.3
# (MOVEMENT/join) [4.4] (snapper 02/08/97 v1.00 )
load Screen join
cd join
set .title (MOVEMENT/join) [4.4]
set .author snapper
edit .text

join
----

Allows you to join another player in the room they are in regardless of where you or they are.  Provided of course, that you are allowed in that room.

Format: join <player>
SEE ALSO: room_act
.end
cd ..
many join 4.4
# (MOVEMENT/leave) [4.5] (snapper 02/08/97 v1.00 )
load Screen leave
cd leave
set .title (MOVEMENT/leave) [4.5]
set .author snapper 
edit .text

leave
-----

Takes you back to the last public room you were in.  That is, if you were in one of your own rooms, or another player's rooms, it takes you from there to the last public place you were at.
.end
cd ..
many leave 4.5
# (MOVEMENT/summon) [4.6] (snapper 02/08/97 v1.00 )
load Screen summon
cd summon
set .title (MOVEMENT/summon) [4.6]
set .author snapper
edit .text

summon
------

Summons a player to your current location, regardless of their location, provided you have permission to do so.

SEE ALSO: player_act
.end
cd ..
many summon grab 4.6
# (MOVEMENT/trans) [4.7] (snapper 02/08/97 v1.00 )
load Screen trans
cd trans
set .title (MOVEMENT/trans) [4.7]
set .author snapper
edit .text

trans
-----

Takes you to one of your own rooms, if they exist.  This command is the only way you can get your own rooms if they are not your home room (unless you make exits to them of course).

Format: trans <location> 

Example: if you had a room with the id den, you could type '^htrans den^-' to get there.
.end
cd ..
many trans 4.7
# (MOVEMENT/visit) [4.8] (snapper 02/08/97 v1.00 )
load Screen visit
cd visit
set .title (MOVEMENT/visit) [4.8]
set .author snapper
edit .text

visit
-----

This command will take you to the homeroom of the player you specify, if allowed.

Format: visit <player>

SEE ALSO: room_act
.end
cd ..
many visit 4.8
# (MOVEMENT/index) (snapper 03/08/97 v1.00 )
load Screen movement_index
cd movement_index
set .title (MOVEMENT/index)
set .author snapper
edit .text

Movement Index
--------------

  ^hgo^-           (4.1)     go through an exit
  ^hexits^-        (4.2)     list the eixts in the current room you're in
  ^hhome^-         (4.3)     go to your home room
  ^hjoin^-         (4.4)     join another player
  ^hleave^-        (4.5)     leave the current room
  ^hsummon^-       (4.6)     summon someone else to where you are
  ^htrans^-        (4.7)     transport yourself to one of your own rooms
  ^hvisit^-        (4.8)     visit another players home room
.end
cd ..
many movement_index 4.index

