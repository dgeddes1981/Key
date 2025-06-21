# Key demo setup script
# $Id: demo.ks,v 1.1.1.1 1999/10/07 19:58:22 pdm Exp $
# setting up default commands...

cd /online
load Screen motd
edit motd
.wipe
                             Welcome to Key, trial edition

Key is a brand new style of talker code, and this is it's demonstration site.  As such, it has commands available to guests that would not be normally available.  Most of the information you enter into the program will be publically available to anyone who knows how to use the 'cd' and 'dump' commands.

Nevertheless, you're welcome to log in and play around.  All characters may resident themselves (just set your password "password", and type "save") and build rooms or just look around.

My name is subtle, if you see me online, feel free to ask me any questions you have.  The website for key is available at

http://realm.progsoc.uts.edu.au/~subtle/key

You can email me: subtle@realm.progsoc.uts.edu.au

.end

cd /commandsets

mv magecommands/cd base
mv magecommands/dump base
mv magecommands/set base
mv magecommands/out base
mv magecommands/yield base
mv magecommands/schedule base
mv magecommands/mv base

sort base
