# misc help stuff
cd /online/help
# =============================================================================
# news - noble
#
load Screen news
cd news
set .title News
set .author Noble
edit .text

news
----

Forest news is a means of communicating with all citizens, by posting news items on the newsboard that are readable by everyone. However you need to be at the newsboard to post, but not read, news.

'news' is not a command in itself , it is a category of commands.
'news commands' lists all the available commands in the 'news' category.


Viewing news list
-----------------

Format: news view (abbreviation: nr)

Lists all the current news items that have been posted, displaying the post's message number, who posted it, and the subject of the post.

Reading news
------------

Format: news read <message number>  (abbreviation: nr number)

Displays the news item with the corresponding message number on the news list.

A displayed post will show the author, the date and time it was posted (in your own time if you have a timezone set), the subject, and the actual post.

Posting news
------------

Format: news post <subject>

This posts a news item with the set subject. You are put into the editor where your post can be up to 100 lines or 5000 characters long.

Format: news reply <message number>

Another means of posting an item is to reply to a previous post. This follows the same procedure as a normal post, except the post's subject will be the subject of the previous post prefixed by 're:' , and the original message will be included in your own.
.end
cd ..
# =============================================================================
# orient - noble
#
load Screen orient
cd orient
set .title orient
set .author Noble
edit .text
orient
------

Brings up information on the room and your status. Including:

  + Room name and id of the room one is currently in.
  + How many other people are currently online.
  + How many of those are in one's current room.
  + If one has new mail


See also: where
.end
cd ..
# =============================================================================
# eject - simon/gutter
load Screen eject
cd eject
set .title eject
set .author guTTer
edit .text
Eject
-----

Format: Eject <player>

Unwanted visitors into your rooms? Eject is the answer. If someone is currently in your room this command will remove them and put them into a public room.
.end
cd ..
# =============================================================================
# AFK - simon/gutter
#
load Screen afk
cd afk
set .title AFK
set .author guTTer
edit .text
AFK - Away from keyboard
---

Format: afk

This is a very usefull command if you are going away from your  computer, and you are worried about other people missusing your  character while your absent. When AFK is used the keyboard is  locked so that commands can not be typed until you unlock it by  typing in your password.

Behaviour is like that of the 'screenlock' command found on playground based talkers.
.end
cd ..
# =============================================================================
# comment - simon/gutter
#
load Screen comment
cd comment
set .title comment
set .author guTTer
edit .text
comment
-------

Format: comment (your comment)

For example: comment I love eclipse

When a session has been set this command allows you to  express you express your opinion on the topic. The comment is  viewble with everyone elses comments by using the comments command.

See also: comments, session
.end
cd ..
# =============================================================================
