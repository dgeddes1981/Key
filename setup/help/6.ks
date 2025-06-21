# Forest Online Help v1.00 chapter 6
cd /online/help
# (BUILDING) [6] (exile 02/08/97 v1.00)
load Screen building
cd building
set .title (BUILDING) [6]
set .author exile
edit .text

Building
--------

An important part of creating and customising your own Forest environment is by building rooms. Any rooms that you create are yours to describe and change as you like (within the rules of the realm that is).

sub topics
  
  ^hcreate_room^-   (6.1)     create a room.
  ^hchange_room^-   (6.2)     modify how your room looks.
  ^hcreate_exit^-   (6.3)     create an exit.
  ^hrooms^-         (6.4)     getting information on rooms.
  ^hroom_tutorial^- (6.5)     a simple room tutorial.
  ^hroom_overview^- (6.6)     another room tutorial.

  [todo]
  homes (sethome, 'home' command)


.end
cd ..
many building build 6

# (BUILDING/create_room)
load Screen create_room
cd create_room
set .title (BUILDING/create_room) [6.1]
set .author eXile
edit .text

Creating Rooms

You create rooms by using the ^hconstruct^- command. By default an individual person can have up to and including four rooms.

Format: construct room <id> [<name>]

Example: '^hconstruct room treehouse The Tree House^-'

The first room you create will be set as your 'home'.

See Also: 6.5 room_tutorial (help 6.5)

.end
cd ..
many create_room createroom constructroom 6.1


# (BUILDING/create_exit)
load Screen create_exit
cd create_exit
set .title (BUILDING/create_exit) [6.3]
set .author eXile
edit .text

Creating Exits
--------------

Exits are links between rooms. Actions cannot be carried out in an exit, but a description can be displayed as one travels through an exit. In order for an exit to work correctly both ends must be anchored to a room. 

Format: construct exit <name> <destination>

Example: '^hconstruct exit rope backyard^-'

This example will create an exit called 'rope' from the existing room to the room of id 'backyard'. The room backyard must exist and you must have permission to create an exit to the room for this to work properly.

The exit created is only one way. You must create an exit back to the room to have an exit that goes both ways.

See Also: 6.5 room_tutorial (help 6.5)

.end
cd ..
many create_exit createexit constructexit 6.3


# (COMMANDS/construct)
load Screen construct
cd construct
set .title (COMMANDS/construct) []
set .author eXile
edit .text

The Construct Command
---------------------

The construct command is used to build rooms and exits.

Format: construct [ room <id> [<name>] | exit <name> <destination> ]

Example: '^hconstruct room backyard eXile's Backyard^-'

Only one room or exit can be constructed in one construct statement.

.end
cd ..
many construct  


# (BUILDING/rooms)
load Screen rooms
cd rooms
set .title (BUILDING/rooms) [6.4]
set .author eXile
edit .text

Getting Information on Rooms
----------------------------

The ^hrooms^- command displays room information on the rooms that you control.

Format: rooms

Example: ^h
-> rooms
  name               portrait                                pl# ob#
|------------------|----------------------------------------|---|---|
 treehouse          Someone is in somewhere                  0   1   
 backyard           Someone is in somewhere                  1   1   
|------------------------------------------------------- 2 rooms ---|
^-

Definitions
^hname^-     : the room id.
^hportrait^- : what is shown when a ^hwhere^- is used to locate player 'Someone'.
^hpl#^-      : the number of people currently in the room.
^hob#^-      : the number of objects currently in the room. Each exit counts as an object.

.end
cd ..
many rooms roominfo roomsinfo 6.4


# (BUILDING/change_room)
load Screen change_room
cd change_room
set .title (BUILDING/change_room) [6.2]
set .author eXile
edit .text

Changing the Appearance of Rooms and Exits
------------------------------------------

Rooms and exits can have their description changed in several ways.

To change a room's description you can enter room mode by typing 'room' then 'describe'. Use the standard Key editor to change your description. Once the description has been changed and saved you can exit room mode by typing 'end'

A faster method is to use the 'edit' command.

Syntax: edit <property>

Example: '^hedit treehouse.description^-'

Where treehouse is the id of the room.

To edit an exits description go to the room that holds the exit and type 'room' to enter room mode. Type '^hedit rope.description^-' and use the editor as usual.

To modify what is seen when one travels through an exit, use '^hedit rope.through^-'

Where 'rope' is the id of the exit.

Advanced Method.
----------------

Syntax: edit <room>.description
        edit <room>/<exit>.description 
.end
cd ..
many change_room describe_exit describe_room 6.2


# (COMMANDS/portrait)
load Screen portrait
cd portrait
set .title (COMMANDS/portrait) []
set .author eXile
edit .text

Portrait
--------

The portrait is what is seen in the '^hwhere^-' output.

Syntax: portrait <portrait text>

Example: '^hportrait the treehouse playing.^-'

Produces: [exile is in] the treehouse playing.

The output syntax is ^h<playername> is in <portrait text>^-

.end
cd ..
many portrait  

# (BUILDING/room_tutorial)
load Screen room_tutorial
cd portrait
set .title (COMMANDS/portrait) [6.5]
set .author eXile
edit .text

A Simple Room Tutorial.
-----------------------

Below is a script that sets up two rooms and an exit between them.

-> construct room treehouse The Tree House
Constructed the room 'treehouse' (in eXile)
This room has also been set to your home, since you don't have any others.
-> construct room backyard The Backyard
Constructed the room 'backyard' (in eXile)
-> home
                              The Tree House
Flat, gray and featureless.  Terribilis est locus iste.


There is nobody here but you.

-> edit treehouse.description
------------------------------------------------------------------------------
Entering the forest editor <footnote 10>:
  If you're not sure how to use the editor, enter '.quit' immediately (without
  the quotes), and read 'help editor'

Limitations:  200 lines, 10000 characters.
------------------------------------------------------------------------------
Flat, gray and featureless.  Terribilis est locus iste.

------------------------------------------------------------------------------
+ .wipe
Editing Buffer Cleared.
+ This is a tree house.
This is a tree house.
+ .end
\Saving editor buffer...
-> edit backyard.description
------------------------------------------------------------------------------
Entering the forest editor <footnote 10>:
  If you're not sure how to use the editor, enter '.quit' immediately (without
  the quotes), and read 'help editor'

Limitations:  200 lines, 10000 characters.
------------------------------------------------------------------------------
Flat, gray and featureless.  Terribilis est locus iste.

------------------------------------------------------------------------------
+ .wipe
Editing Buffer Cleared.
+ This is the Backyard of eXile's place.
This is the Backyard of eXile's place.
+ .end
Saving editor buffer...
-> construct exit rope backyard
Constructed the exit rope in treehouse, which leads to somewhere (backyard)
-> go rope
You walk through the exit
                               The Backyard
This is the Backyard of eXile's place.


There is nobody here but you.

-> construct exit rope treehouse
Constructed the exit rope in backyard, which leads to somewhere (treehouse)


---
Now using the edit command we can change the exit's description and the 'through'. Remember you have to edit both instances of the 'rope' exit.

Happy construction!

.end
cd ..
many room_tutorial room_howto howto_room room_tut 6.5  

# (BUILDING/room_overview)
load Screen room_overview
cd room_overview
set .title (BUILDING/room_overview) [6.6]
set .author subtle
edit .text

Creating a new room
-------------------

You create rooms and exits using the ^hconstruct^- command.  An individual may usually have up to four rooms.  For example, if I wished to create a treehouse, I might type:

construct room treehouse Paul's Treehouse

The first word to the construct command is the ^hid^- of the room, and the rest of the line is 'called' property.  The called room property is put at the top of the room description, in the centre of the screen, when someone in the room uses the ^hlook^- command.

The easiest way to go to your new room is the ^htrans^- command, which moves you to one of your own named rooms.  For instance, you would type 'trans treehouse' to go to your new treehouse.  (If you had built one).

When you enter, you'll notice that it looks kind of empty.

Setting up a room
-----------------

One of the more useful commands when setting up a room is the ^hinspect^- command.  This command shows some information about how the room is set up at the moment.  Our new treehouse might look like this:

-----------------------------------------------------
/players/subtle/treehouse is a room owned by subtle.
-----------------------------------------------------
There is nobody here but you.
There aren't any objects in this room.

The portrait reads:  Someone is [in] [somewhere].
-----------------------------------------------------

This screen shows the rooms id (/players/subtle/treehouse), who it is owned by (subtle), as well as the rooms portrait.  As you can see, a rooms portrait consists of two parts, a 'relation', and the actual portrait.  The relation is the 'in' part, and the portrait is the 'somewhere' part.  The rooms description and called property may be viewed normally with the ^hlook^- command.

The portrait itself shows what appears when someone does a ^hwhere^- command on someone in this room.  For instance, 'where subtle' would return 'subtle is in somewhere' at the moment.  For a treehouse, we're simply going to change the 'somewhere' to be 'the treehouse':

-> portrait the treehouse
The portrait now reads: [subtle is in] the treehouse
->

To change the relation property (which you might want to do when creating a hill - no-one is ever 'in' a hill), you use the ^hrelation^- command:

-> relation
The portrait now reads: [subtle is on] the treehouse
->

(We might use this trick later to create a room that is on top of the treehouse)

Using the ^hlook^- command, we can see what the room looks like at the moment:

-> l
                  ^hPaul's Treehouse^-

There is nobody here but you.
->

To add a describe, we use the ^hroom describe^- command, and the editor.  The editor has some more advanced features, allowing you to fix mistakes and delete lines, but for now, there are only 3 things you need to know about it.

When you're in the editor, you have a + prompt, and whatever you type will appear in your description, unless it starts with a period '.'.  There are many period commands, but the best ones are ".end" (to finish editting), and ".wipe" (to wipe everything and start over).

We start the editor:

----------------------------------------------------------------------
Entering the forest editor <footnote 10>:
  If you're not sure how to use the editor, enter '.quit' immediately
  (without the quotes), and read 'help editor'

Limitations:  6 lines, 480 characters.
----------------------------------------------------------------------
----------------------------------------------------------------------
+

We can now type in a simple description:

+ The treehouse is of the normal wooden variety, albeit rather rickety.  Several comics are lying scattered around.
The treehouse is of the normal wooden variety, albeit rather rickety. 
 Several comics are lying scattered around.
+ .end
Saving editor buffer...
You change the description of 'treehouse'

When you type in descriptions, type whole paragraphs on one line if you can.  This will make the program automatically word-wrap them to the width of whoever is looking at them.

Using the ^hlook^- command now shows us our complete room!  The next section shows you how to create exits between rooms (for the go command), and mentions a little more about room management.


SEE ALSO: construct inspect portrait relation look where describe room
.end
cd ..
many room_overview roomoverview 6.1
