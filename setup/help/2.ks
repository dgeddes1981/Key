# Forest Online Help v1.00 Chapter 2
cd /online/help
# (PERSONAL) [2] (snapper 29/07/97 v1.00 )
load Screen personal
cd personal
set .title (COMMUNICATION) [2]
set .author snapper
edit .text

Personal
--------

Personal attributes are what make your charcter, well, have more character.

sub topics

  ^hage^-          (2.1)     the age command
  ^haka^-          (2.2)     the also known as command
  ^hblockMsg^-     (2.3)     the block message command
  ^hblockingMsg^-  (2.4)     the blocking message command
  ^hdescribe^-     (2.5)     the description command
  ^hemail^-        (2.6)     the email command
  ^hgender^-       (2.7)     the gender command
  ^hhide^-         (2.8)     the hide command
  ^hhomepage^-     (2.9)     the homepage command
  ^hidleMsg^-      (2.10)    the idle message command
  ^hliberate^-     (2.11)    the liberate command
  ^hmsgs^-         (2.12)    the msgs command
  ^nnoPager^-      (2.13)    the no pager command
  ^hpassword^-     (2.14)    the password command
  ^hplan^-         (2.15)    the plan command
  ^hprefix^-       (2.16)    the prefix command
  ^hprompt^-       (2.17)    the prompt command
  ^hrecap^-        (2.18)    the recap command
  ^hshow^-         (2.19)    the show command
  ^hterm^-         (2.20)    the term command
  ^htimezone^-     (2.21)    the timezone command
  ^htitle^-        (2.22)    the title command
.end
cd ..
many personal customisation custom me 2
# (PERSONAL/age) [2.1] (snapper 29/07/97 v1.00 )
load Screen age
cd age
set .title (PERSONAL/age) [2.1]
set .author snapper
edit .text

age
---

Sets your personal age to whatever you choose.  Valid ages are from 1 to 119.

Setting an age of 0 blanks the property so it is not displayed.

Format: age <age in years>

Example: age 19
.end
cd ..
many age 2.1
# (PERSONAL/aka) [2.2] (snapper 29/07/97 v1.00 )
load Screen aka
cd aka
set .title (PERSONAL/aka) [2.2]
set .author snapper
edit .text

aka
---

Also Known As is an alias, a nick name, and often your first name.

Typing aka by itself, will set the property so it is not displayed.

Format: aka <what you wish to be known as>

Examples: aka Jason
          aka the little green chicken
          aka sir!
          aka Jimmy the Hand
.end
cd ..
many aka 2.2
# (PERSONAL/blockMsg) [2.3] (snapper 29/07/97 v1.00 )
load Screen blockMsg
cd blockMsg
set .title (PERSONAL/blockMsg) [2.3]
set .author snapper
edit .text

blockMsg
--------

Sets what people see if they try to directly communicate with you, and if you are blocking them.

Format: blockMsg <message>

Example: '^hblockMsg I don't want to talk to you.^-'
people you are blocking who try to directly communicate with you will see:
'^hyourname is blocking you: I don't walk to talk to you.^-'

SEE ALSO: block blocking direct_comm msgs
.end
cd ..
many blockMsg 2.3
# (PERSONAL/blockingMsg) [2.4] (snapper 29/07/97 v1.00 )
load Screen blockingMsg
cd blockingMsg
set .title (PERSONAL/blockingMsg) [2.3]
set .author snapper
edit .text

blockingMsg
-----------

Sets what people see if they try to directly communicate with you, and if you are blocking tells in general via the '^hblock tells^-' command.

Format: blockingMsg <message>

Example: '^hblockingMsg I'm busy atm and don't wish to be disturbed ;)^-'
people you are blocking who try to directly communicate with you will see:
'^hyourname is blocking: I'm busy atm and don't wish to be disturbed ;)^-'

SEE ALSO: block blocking direct_comm msgs
.end
cd ..
many blockingMsg 2.4
# (PERSONAL/describe) [2.5] (snapper 29/07/97 v1.00 )
load Screen describe
cd describe
set .title (PERSONAL/describe) [2.5]
set .author snapper
edit .text

describe
--------

Edits the description of the current context (see ^hcontext^-).  The default context is the current player, so describe by itself, should edit your own personal description.

SEE ALSO: context
.end
cd ..
many describe 2.5
# (PERSONAL/email) [2.6] (snapper 30/07/97 v1.00 )
load Screen email
cd email
set .title (PERSONAL/email) [2.6]
set .author snapper
edit .text

email
-----

Sets your email address to public ( so anyone can see ) or private ( so you, staff and those you've given seePrivateInfo can see ).  The command: '^hemail^-' by itself will set your email address.

NOTE:  An email is sent to the address you specify with a verification code you will need to verify your character with.

Format: email [public|private]

SEE ALSO: seePrivateInfo
.end
cd ..
many email public 2.6
# (PERSONAL/gender) [2.7] (snapper 29/07/97 v1.00 )
load Screen gender
cd gender
set .title (PERSONAL/gender) [2.7]
set .author snapper
edit .text

gender
------

Changes your gender ( ooer ) to whatever you please within the valid selection. Valid sex's include: male, female, and neuter.

Format: gender <m/f/n>

Example: '^hgender f^-'
You see: '^hYou set your gender to female.^-'
.end
cd ..
many gender 2.7
# (PERSONAL/hide) [2.8] (snapper 31/07/97 v1.00 )
load Screen hide
cd hide
set .title (PERSONAL/hide) [2.8]
set .author snapper
edit .text

hide
----

Hide is used to conceal your location to those using the '^hwho^-', '^hwhere^-' and '^hwith^-' commands.  It makes you a little more elusive.

NOTE: The command toggles, that is use it once to hide, and twice to be found!

SEE ALSO: where who with
.end
cd ..
many hide 2.8
# (PERSONAL/homepage) [2.9] (snapper 29/07/97 v1.00 )
load Screen homepage
cd homepage
set .title (PERSONAL/homepage) [2.9]
set .author snapper
edit .text

homepage
--------

If you have one, use this command to set your homepage, and remember to leave out colour codes.  This command actually validates to make sure what you enter exists.

Format: homepage [<WWW homepage address>]

Example: homepage http://realm.progsoc.uts.edu.au/forest
.end
cd ..
many homepage 2.9
# (PERSONAL/idleMsg) [2.10] (snapper 31/07/97 v1.00 )
load Screen idleMsg
cd idleMsg
set .title (PERSONAL/idleMsg) [2.10]
set .author snapper
edit .text

idleMsg
-------

This command

Sets what people see if they try to directly communicate with you, and if you are in idle mode.

Format: idleMsg <message>

Example: '^hidleMsg I'm rubbing a chicken.^-'
         '^hidle^-'
people who try to directly communicate with you will see:
'^hyourname is idle: I'm rubbing a chicken.^-'

SEE ALSO: idle msgs
.end
cd ..
many idleMsg idlemsg 2.10
# (PERSONAL/liberate) [2.11] (snapper 31/07/97 v1.00 )
load Screen liberate
cd liberate
set .title (PERSONAL/liberate) [2.11]
set .author snapper
edit .text

liberate
--------

This command stop people from accepting you into clans.

NOTE: The command toggles, that is use it once to liberate yourself, and twice to embrace the rule of clans!

SEE ALSO: clan clans
.end
cd ..
many liberate 2.11
# (PERSONAL/msgs) [2.12] (snapper 29/07/97 v1.00 )
load Screen msgs
cd msgs
set .title (PERSONAL/msgs) [2.12]
set .author snapper
edit .text

msgs
----

Lists your idle, block, and blocking messages.

SEE ALSO: idleMsg blockMsg blockingMsg
.end
cd ..
many msgs 2.12
# (PERSONAL/noPager) [2.13] (snapper 31/07/97 v1.00 )
load Screen noPager
cd noPager
set .title (PERSONAL/noPager) [2.13]
set .author snapper
edit .text

noPager
-------

Turns getting paged output, or or off. (ie. turn the pager off or on ).  The pager is on by default, so this command is used to turn it off.

NOTE: This command toggles so to turn the pager back on, use it twice.

SEE ALSO: pager
.end
cd ..
many noPager 2.13
# (PERSONAL/password) [2.14] (snapper 31/07/97 v1.00 )
load Screen password
cd password
set .title (PERSONAL/password) [2.14]
set .author snapper
edit .text

password
--------

A password is used to make sure only you have access to your character.  To set a password for this first time, simply type '^hpassword^-'.  It will prompt you for a password, and then again to verify it ( to make sure you didn't make a mistake the first time )

If you wish to change your password any time thereafter, use '^hpassword^-' again to change it.  You will be prompted for you current password, before being asked for a new one, and the verify check.
.end
cd ..
many password 2.14
# (PERSONAL/plan) [2.15] (snapper 29/07/97 v1.00 )
load Screen plan
cd plan
set .title (PERSONAL/plan) [2.15]
set .author snapper
edit .text

plan
----

A property that usually describes your goal in life, though it often used for quotes, or music lyrics relevant to the person who set it.

This command puts you in the editor and you have 6 lines or 500 bytes to play with.
.end
cd ..
many plan 2.15
# (PERSONAL/prefix) [2.16] (snapper 29/07/97 v1.00 )
load Screen prefix
cd prefix
set .title (PERSONAL/prefix) [2.16]
set .author snapper
edit .text

prefix
------

Sets what people see before your name in some communication commands.  

Format: prefix <prefix>

Example: '^hprefix legend^-'
         '^htell snapper hey^-'
snapper will see '^hlegend yourname tells you 'hey'^-'

NOTE:  Some prefix's will not be allowed due to their spoof nature.

SEE ALSO: spoof
.end
cd ..
many prefix 2.16
# (PERSONAL/prompt) [2.17] (snapper 29/07/97 v1.00 )
load Screen prompt
cd prompt
set .title (PERSONAL/prompt) [2.17]
set .author snapper
edit .text

prompt
------

Sets what you see before everything you type, defaults to '^h->^-' if no prompt is supplied.

Format: prompt <prompt>
.end
cd ..
many prompt 2.17
# (PERSONAL/recap) [2.18] (snapper 29/07/97 v1.00 )
load Screen recap
cd recap
set .title (PERSONAL/recap) [2.18]
set .author snapper
edit .text

recap
-----

Recapitalise your name.  So It LoOkS fUnKy!

Format: recap <capitalisation>

Example: if your name was chicken, '^hrecap ChicKeN^-'
.end
cd ..
many recap 2.18
# (PERSONAL/show) [2.19] (snapper 29/07/97 v1.00 )
load Screen show
cd show
set .title (PERSONAL/show) [2.19]
set .author snapper
edit .text

show
----

Toggles what qualifying effects you have set. Qualifying effects are characters that prepend all text, relevant to the effect.  The effects are:

>  tells
*  friends
-  room
!  shouts
)  enter
(  leave
}  login
{  logout

Format: show <qualifier>
Example: '^hshow friends^-'
.end
cd ..
many show qualify effect 2.19
# (PERSONAL/term) [2.20] (snapper 31/07/97 v1.00 )
load Screen term
cd term
set .title (PERSONAL/term) [2.20]
set .author snapper
edit .text

term
----

Sets your term, so you can view Forest output the best way possible! ( This means highlighted tells, and colours for those that can view them!! )

This command actually forces a terminal, as Foresty autodetects what terminal you are using.  To set it so autodetect is on, use '^term none^-' and Forest will autodetect every time you log on.

NOTE: programs such as tinyfugue foil Forest's attempt to autodetect, so it's better if you force the terminal with this command.

Term by itself, will return what you term is currently set to.

Supported terms: vt100, ansi, sun, dumb

Format: term <term type>
.end
cd ..
many term hitells 2.20
# (PERSONAL/timezone) [2.21] (snapper 29/07/97 v1.00 )
load Screen timezone
cd timezone
set .title (PERSONAL/timezone) [2.21]
set .author snapper
edit .text

timezone
--------

Timezone sets the time difference between your geographical location and key time (Australian EST).

Suppose, when you look at your watch, the time it said was 3 hours ahead of the time you see when you type 'time' on the program.  In order to set up your character so that it knows which timezone you are in, you would type '^htimezone 3h^-', to mean 'my time is 3 hours ahead'.  To do negative timezones say if you were half an hour behind, you could use '^htimezone -30m^-', for 'my time is 30 minutes behind'.

Note: '^htimezone 0^-' resets the difference.

Format: timezone <time difference>
.end
cd ..
many timezone jetlag 2.21
# (PERSONAL/title) [2.22] (snapper 29/07/97 v1.00 )
load Screen title
cd title
set .title (PERSONAL/title) [2.22]
set .author raiders
edit .text

title
-----

Title is the line which follows your player name on your player file.

Format: title <your title>

Example: '^-title the wolf^-'

You set the title of yourname so that it now reads:
yourname the wolf
.end
cd ..
many title 2.22
# (PERSONAL/index) (snapper 29/07/97 v1.00 )
load Screen personal_index
cd personal_index
set .title (PERSONAL/index)
set .author snapper
edit .text

Personal Index
--------------

  ^hage^-          (2.1)     the age command
  ^haka^-          (2.2)     the also known as command
  ^hblockMsg^-     (2.3)     the block message command
  ^hblockingMsg^-  (2.4)     the blocking message command
  ^hdescribe^-     (2.5)     the description command
  ^hemail^-        (2.6)     the email command
  ^hgender^-       (2.7)     the gender command
  ^hhide^-         (2.8)     the hide command
  ^hhomepage^-     (2.9)     the homepage command
  ^hidleMsg^-      (2.10)    the idle message command
  ^hliberate^-     (2.11)    the liberate command
  ^hmsgs^-         (2.12)    the msgs command
  ^nnoPager^-      (2.13)    the no pager command
  ^hpassword^-     (2.14)    the password command
  ^hplan^-         (2.15)    the plan command
  ^hprefix^-       (2.16)    the prefix command
  ^hprompt^-       (2.17)    the prompt command
  ^hrecap^-        (2.18)    the recap command
  ^hshow^-         (2.19)    the show command
  ^hterm^-         (2.20)    the term command
  ^htimezone^-     (2.21)    the timezone command
  ^htitle^-        (2.22)    the title command

.end
cd ..
many personal_index 2.index
