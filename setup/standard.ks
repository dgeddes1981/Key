#include basic.ks
#include loadRooms.ks
#include createExits.ks

#include help/help.ks
#include help/1.ks
#include help/2.ks
#include help/3.ks
#include help/4.ks
#include help/6.ks
#include help/12.ks
# Syncing database
cd

# Admins, if you uncomment this, they'll have to enter their email address to log
# in.  You don't want that until you set up the email server, for instance. ;)
#edit /config/connection.newbieaskemail
#.wipe
#
#In order to log into Forest it is necessary to enter your email address.  The way this works is that you type it in here, and we send an email to you with a 'verify code' in it.  When you get this email, telnet back here, type in your name and the code, and you're in.
#
#Without further ado...
#
#.end

load Screen /online/logoutMsg
edit /online/logoutMsg.text
.wipe
Thanks for playing.
.end

sync
