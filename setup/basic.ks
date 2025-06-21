#: Key setup script
#: $Id: basic.ks,v 1.7 2000/03/07 14:30:05 subtle Exp $
#: $Log: basic.ks,v $
#: Revision 1.7  2000/03/07 14:30:05  subtle
#: added log
#:

#: setting up default commands...

# Ref (subtle 04Sep96)
load commands.Cd .commands/cd
load commands.Out .commands/out
load commands.Move .commands/mv

# Set (subtle 1Sep96)
load commands.Set .commands/set

# Forest staff setup
cd /ranks
load Rank mage
load Rank advisor

	#  'sleepers' are non-mages ;)
load ReverseRank sleepers
set sleepers.reversedRank mage

	#  mage may be used to target anyone except mages
ln sleepers mage.targets

# EW Compatibility commandset - it holds temporary commands
# that are used for some of the aliases used to do things in
# the EW style.
#
cd /commandsets
load CommandList ew
cd ew
load commands.Echo notImplemented
set notImplemented.echo "This command has not been implemented"


# The main & default commandset
cd /commandsets/base

# load all commands into the base command set, then shift commands

load commands.Conceal conceal
load commands.Reveal reveal

# Whisper (subtle 29Aug96)
# (the ^@ and ^$ are markers to prevent the users colour
# changes from affecting anything else ... they kind of
#  store the current colour, and the restore it at the end)
load commands.Whisper whisper
set whisper.broadcast "%o whispers something to %t"
set whisper.feedback "You whisper '^@%m^$' to %t"
set whisper.feedbackQuestion "You whisper, asking %t '^@%m^$'"
set whisper.feedbackExclaim "You exclaim in a whisper '^@%m^$' to %t"
set whisper.direct "%o whispers '^@%m^$' to %t"
set whisper.directQuestion "%o whispers, asking %t '^@%m^$'"
set whisper.directExclaim "%o exclaims in a whisper '^@%m^$' to %t"

# Say (subtle 29Aug96)
load commands.Broadcast say
set say.broadcast "%p says '^@%m^$'"
set say.feedback "You say '^@%m^$'"
set say.broadcastQuestion "%p asks '^@%m^$'"
set say.feedbackQuestion "You ask '^@%m^$'"
set say.broadcastExclaim "%p exclaims '^@%m^$'"
set say.feedbackExclaim "You exclaim '^@%m^$'"

# Echo (subtle 14Dec98)
load commands.Broadcast echo
set echo.broadcast "^@%m^$ [%o]"
set echo.feedback "You echo: ^@%m^$"

load commands.Directed recho
set recho.direct "^@%m^$ [%o]"
set recho.feedback "You echo to %t: ^@%m^$""

# Emote (subtle 29Aug96)
load commands.Broadcast emote
set emote.broadcast "%o%s^@%m^$"
set emote.feedback "You emote: %o%s^@%m^$"

# Think (subtle 29Aug96)
load commands.Broadcast think
set think.broadcast "%o thinks . o O ( ^@%m^$ )"
set think.feedback "You think . o O ( ^@%m^$ )"

# Tell (subtle 29Aug96)
load commands.Directed tell
set tell.direct "%p tells %t '^@%m^$'"
set tell.feedback "You tell %t '^@%m^$'"
set tell.directQuestion "%p asks %t '^@%m^$'"
set tell.feedbackQuestion "You ask %t '^@%m^$'"
set tell.directExclaim "%p exclaims to %t '^@%m^$'"
set tell.feedbackExclaim "You exclaim to %t '^@%m^$'"

load commands.Also tel
set tel.command tell
conceal tel

load commands.Also t
set t.command tell
conceal t

# Remote (subtle 26Sep96)
load commands.Directed remote
set remote.direct "%o%s^@%m^$"
set remote.feedback "You emote '%o%s^@%m^$' to %t"

# Rthink (subtle 26Sep96)
load commands.Directed rthink
set rthink.direct "%o thinks . o O ( ^@%m^$ )"
set rthink.feedback "You think . o O ( ^@%m^$ ) to %t"

load commands.Also rt
set rt.command rthink
conceal rt

# Shout (subtle 29Jul97)
load commands.ShoutBroadcast shout
set shout.broadcast "%p shouts '^@%m^$'"
set shout.feedback "You shout '^@%m^$'"
set shout.scapeFor /
set shout.throttleType 1

load commands.ShoutBroadcast eshout
set eshout.broadcast "%o%s^@%m^$"
set eshout.feedback "You emote to everyone: %o%s^@%m^$"
set eshout.scapeFor /
set eshout.throttleType 1

# Leave (subtle 20Jul97)
load commands.Leave leave
set leave.failure "But you aren't in anyone's rooms."
set leave.referenceIn ~me
set leave.roomProperty lastPublicRoomLocation
set leave.enter "%o slowly fades into view..."

# Home (subtle 20Jul97)
load commands.ChangeRoom home
set home.failure "But you're already in your home room."
set home.referenceIn ~me
set home.roomProperty home
set home.enter "%o slowly fades into view..."
set home.leave "%o slowly fades from view..."
set home.noFind "You don't seem to have a home"

load commands.Contents informs
set informs.containerFor ~informs
set informs.singular You have only one entry on your inform list
set informs.footer You have %n entries on your inform list
set informs.empty You have no-one on your inform list

load commands.Contents prefers
set prefers.containerFor ~me.prefer
set prefers.singular You have only one entry on your prefer list
set prefers.footer You have %n entries on your prefer list
set prefers.empty You have no-one on your prefer list

# notsnapper 21/7 - friends stuff
load commands.ScapeWho fwho
set fwho.scapeFor ~friends
set fwho.singular You have one friend online
set fwho.footer You have %n friends online
set fwho.empty No friends on at the moment

load commands.Also qwho
set qwho.command fwho
conceal qwho

load commands.Contents friends
set friends.containerFor ~friends
set friends.singular You have only one friend
set friends.footer You have %n friends
set friends.empty You have no friends!

load commands.FriendsBroadcast tf
set tf.scapeFor ~friends
load commands.FriendsBroadcast rf
set rf.scapeFor ~friends
load commands.FriendsBroadcast ft
set ft.scapeFor ~friends

set tf.broadcast "%p tells %h friends '^@%m^$'"
set tf.broadcastExclaim "%p exclaims to %h friends '^@%m^$'"
set tf.broadcastQuestion "%p asks of %h friends '^@%m^$'"
set tf.feedback "You tell your friends '^@%m^$'"
set tf.feedbackExclaim "You exclaim to your friends '^@%m^$'"
set tf.feedbackQuestion "You ask of your friends '^@%m^$'"
set tf.noone "None of your friends are online at the moment."

set rf.broadcast "%o%s^@%m^$"
set rf.feedback "You emote '%o%s^@%m^$' to your friends."
set rf.noone "None of your friends are online at the moment."

set ft.broadcast "%o thinks . o O ( ^@%m^$ )"
set ft.feedback "You emote '%o thinks . o O ( ^@%m^$ )' to your friends."
set ft.noone "None of your friends are online at the moment."

load commands.Punctuation f
set f.say /commandsets/base/tf
set f.tell /commandsets/base/tf
set f.emote /commandsets/base/rf
set f.remote /commandsets/base/rf
set f.think /commandsets/base/ft
conceal tf
conceal rf
conceal ft

load commands.LoginMsg loginMsg
load commands.LogoutMsg logoutMsg

load commands.Echo entermsg
set entermsg.echo "This command is superceded in Key.  Messages received when a player enters or leaves a room depend upon the exit they use.  If they do not use an exit (travel, join), the command itself chooses the message."
conceal entermsg

load commands.Actions actions
load commands.Age age
load commands.As as
load commands.Aka aka
load commands.Echo rlname
set rlname.echo "Please use the '^haka^-' command to set your real-life name instead."
conceal rlname

load commands.Allow allow
load commands.BlockMsg blockMsg

load commands.Echo ignore
set ignore.echo "Use the 'block' command to ignore someone."

load commands.BlockingMsg blockingMsg
load commands.Blocking blocking
load commands.Cls cls

load commands.Also clear
set clear.command cls

load commands.Commands commands
load commands.ConnectRoom connectRoom

load commands.Alias ghome
set ghome.command "connectRoom ~me.home"

load commands.Colours colours
load commands.Also colors
set colors.command colours
conceal colors

load commands.Default default
load commands.Delete delete
load commands.Deny deny
load commands.Describe describe

load commands.Alias description
set description.command describe
conceal description

load commands.Disconnect dc
load commands.Edit edit
load commands.Eject eject

load commands.Also boot
set boot.command eject

load commands.Summon summon
load commands.Visit visit

# merlin 12/7 - end command
load commands.End end

load commands.Examine x
conceal x
load commands.Also examine
set examine.command x

load commands.Exits exits
load commands.Footnote footnote
load commands.Friend friend
load commands.Finger finger
set f.otherwise /commandsets/base/finger

load commands.Echo find
set find.echo "Please use 'allow <name> find' instead."
conceal find

load commands.Go go

load commands.Alias nr
set nr.command "read news %m"
conceal nr

load commands.Summon grab
set grab.leave "A hand reaches down from the sky and grabs %t."
set grab.enter "%t spins rapidly into being."
set grab.feedback "You successfully grab %t."
set grab.failure "You can't grab them.";

load commands.Gc yield
load commands.Give give
load commands.Help help

load commands.Toggle hide
set hide.togglefield "~me.hidden"
set hide.onFeedback "You hide.  Everybody counts to 10."
set hide.offFeedback "You stop hiding."

load commands.Toggle localecho
set localecho.toggleField "~me.localecho"
set localecho.onFeedback "(if you have trouble toggling localecho, remember 'localecho on' and 'localecho off' will force it to resend.  Use 'term' to display the current settings.)"
set localecho.offFeedback "(if you have trouble toggling localecho, remember 'localecho on' and 'localecho off' will force it to resend.  Use 'term' to display the current settings.)"

load commands.Toggle hidetime
set hidetime.togglefield "~me.hidetime"
set hidetime.onFeedback "You are now a registered spod in denial. (logintime hidden)"
set hidetime.offFeedback "Your logintime is now visible."

load commands.Toggle quiet
set quiet.togglefield "~me.quiet"
set quiet.onFeedback "You will not be beeped anymore (informs, wake/page, etc)."
set quiet.offFeedback "You will get beeps again."

load commands.History history
load commands.Idle idle
load commands.IdleMsg idleMsg
load commands.Inform inform

load commands.IsColour iscolour
load commands.Also iscolor
conceal iscolor
set iscolor.command iscolour

load commands.Join join
load commands.Lag lag
load commands.LimitActions limitActions
load commands.Liberated liberate
load commands.List ls

load commands.Look l
conceal l
load commands.Also look
set look.command l

load commands.Many many
load commands.Motd motd
load commands.Msgs msgs
load commands.NoPager noPager
load commands.Newbies newbies
load commands.PagerNext n
conceal n
load commands.PagerQuit q
conceal q
load commands.Page page
set page.usage "<name> <message>  (use 'wake' if you want to beep someone without a message)"

load commands.Page wake
set wake.enforceMessage false
set wake.send "A pageboy taps you on the shoulder and says '%o wants to talk to you.'"
set wake.feedback "You send a pageboy to wake %t."
set wake.usage "<name>"

load commands.Password password
load commands.Plan plan
load commands.Post post
load commands.Prefix prefix
load commands.Prefer prefer
load commands.Prompt prompt
load commands.Print print
conceal print
load commands.Pwd pwd
load commands.Register register

load commands.Qualify colour
load commands.Also color
conceal color
set color.command colour

load commands.Recap recap
load commands.Repeat re
conceal re
load commands.Also repeat
set repeat.command re

load commands.Remove remove

load commands.RefRoom room
cd room.commands
	
	set .title room
	
	load commands.Alias commands
	set commands.command "commands room"
	
	load commands.Name name
	
	load commands.Relation relation
	load commands.Portrait portrait
	
	load commands.Also actions
	set actions.command ../../actions
	
	load commands.Also allow
	set allow.command ../../allow
	load commands.Also deny
	set deny.command ../../deny
	load commands.Also default
	set default.command ../../default
	
	load commands.Describe describe
	load commands.Also edit
	set edit.command describe
	conceal edit
	
	load commands.Also look
	set look.command ../../look
	
	load commands.SetHome sethome
	
	load commands.RoomHistory history
	
	sort .

cd /commandsets/base

load commands.ConstrainedSet titleTolerance
set titleTolerance.setProperty titleTolerance
set titleTolerance.displayProperty titleTolerance
set titleTolerance.maxLength 2
set titleTolerance.blankFeedback "The current number of lines available for player listings in the 'look' screen is %v."
set titleTolerance.filledFeedback "Your new title tolerance is %v."
set titleTolerance.usage "<number of lines available to player listings>"
set titleTolerance.on ~me
set titleTolerance.in $
set titleTolerance.mustContain $
set titleTolerance.subVerify false

load commands.ConstrainedSet icq
set icq.setProperty icq
set icq.displayProperty icq
set icq.maxLength 9
set icq.blankFeedback "Your ICQ # is set to %v."
set icq.filledFeedback "Your ICQ # is now %v."
set icq.usage "<ICQ #>"
set icq.on ~me
set icq.in $
set icq.mustContain $
set icq.subVerify false

load commands.ConstrainedSet wherefrom
cd wherefrom
set .setproperty wherefrom
set .usage "<your physical location, eg "Sydney, Australia">"
set .maxlength 60
set .on ~me
set .in $
set .mustcontain $
set .subverify false
set .displayproperty wherefrom
set .blankfeedback "Your irl location is set to '%v'."
set .filledfeedback "Your irl location has been set to '%v'."
cd ..

load commands.RefExit exit
cd exit.commands
	
	set .title exit
	
	load commands.Alias commands
	set commands.command "commands exit"
	
	load commands.Also describe
	set describe.command ../../describe
	
	load commands.Alias through
	set through.command "edit ~context.through"

	load commands.Toggle autolook
	set autolook.togglefield ~context.autolook
	set autolook.onfeedback "Autolook for this exit turned on"
	set autolook.offfeedback "Autolook for this exit turned off"
	
	load commands.ConstrainedSet arriveRoom
	set arriveRoom.setProperty arriveRoom
	set arriveRoom.displayProperty arriveRoom
	set arriveRoom.maxLength 60
	set arriveRoom.usage "<message that the room you are arriving at sees>"
	set arriveRoom.on ~context
	set arriveRoom.in $
	set arriveRoom.mustContain "%o"
	set arriveRoom.subVerify true
	
	load commands.ConstrainedSet departRoom
	set departRoom.setProperty departRoom
	set departRoom.displayProperty departRoom
	set departRoom.maxLength 60
	set departRoom.usage "<message that the room you are departing sees>"
	set departRoom.on ~context
	set departRoom.in $
	set departRoom.mustContain "%o"
	set departRoom.subVerify true
	
cd /commandsets/base


load commands.Rooms rooms
load commands.Save save
load commands.Sort sort
# show not implemented
#load commands.Show show
load commands.Schedule schedule
load commands.Alias shutdown
set shutdown.command "schedule /online/shutdown 0s"
load commands.SetGender gender
load commands.ScapeWho swho
set swho.scapeFor /
set swho.singular You are all alone, *schniff*
set swho.footer There are %n players online
set swho.empty There is nobody online
load commands.Term term
#load commands.Trans trans
load commands.Travel trans
load commands.Time time

load commands.Alias date
set date.command "time -"
conceal date

# merlin 18/7 - add timezone command (jetlag from ew );
load commands.Timezone timezone
load commands.Title title
load commands.Threads threads
load commands.Url webpage

load commands.Toggle privateFlorins
set privateFlorins.toggleField "~me.privateFlorins"
set privateFlorins.onFeedback "The amount of florins you have is now hidden."
set privateFlorins.offFeedback "The amount of florins you have is now visible."


load commands.Echo jetlag
set jetlag.echo "Please use the 'timezone' command to change your timezone."

load commands.Echo url
set url.echo "Please use the '^hwebpage^-' command to set your homepage."
conceal url

load commands.Also kwit
set kwit.command quit
conceal kwit

load commands.Echo pemote
set pemote.echo "You can do possessive emotes with the emote command.  For example: ^hemote 's eyes roll skyward^-"
conceal pemote

load commands.Echo premote
set premote.echo "You can do possessive remotes with the remote command.  For example: ^hremote subtle 's stomach grumbles^-"
conceal premote

load commands.Verify verify
load commands.Where where
load commands.Wordwrap wordwrap
load commands.Who who
load commands.Which which
load commands.Version version
load commands.Also w
conceal w
set w.command who

load commands.Echo suicide
set suicide.echo "If you *really* want to delete your own character, you can type '^hdelete /players/name^-'.  You won't get a warning, and there is no way to stop it once you hit return."
conceal suicide

load commands.Echo seeecho
set seeecho.echo "See-echos are on by default in Key.  You can't turn them off."
conceal seeecho

load commands.Transfer transfer
load commands.Trace trace

# Object commands, subtle 19Oct98
# the standard verbs
load commands.Verb use
set use.verb use
set use.checkInventory true
set use.checkRoom true

load commands.Verb wear
set wear.verb wear
set wear.checkInventory true

load commands.Verb wield
set wield.verb wield
set wield.checkInventory true

load commands.Verb get
set get.verb get
set get.checkRoom true

load commands.Verb drop
set drop.verb drop
set drop.checkInventory true

load commands.Verb sit
set sit.verb sit
set sit.checkRoom true

# a couple of special verbs
load commands.Stand stand

load commands.Read read
set read.checkInventory true
set read.checkRoom true

load commands.Inspect inspect
set inspect.checkInventory true
set inspect.checkRoom true

# utility commands
load commands.ListInventory i
conceal i
load commands.Also inventory
set inventory.command i

load commands.Orient orient

# Wizards First Rule:  People are stupid
# out
# load commands.Echo commandsets/base/save
# set commandsets/base/save.echo "Character saved. <footnote 8>"

# 'check' category
load CommandCategoryContainer check
cd check.commands

	set .title check

	load commands.Alias commands
	set commands.command "commands check"

	load commands.ScapeWho home
	set home.scapeFor ~me.home
	set home.empty "There isn't anyone in your home."
	set home.doesntExist "You don't seem to have a home"

	load commands.Alias mail
	set mail.command "read mail"

	load commands.Alias news
	set news.command "read news"

	#'Also' is faster than 'alias' if no special args are required
	load commands.Also exits
	set exits.command ..../exits

	load commands.Also entry
	set entry.command /commandsets/ew/notImplemented
	load commands.Also sent
	set sent.command /commandsets/ew/notImplemented
	load commands.Also list
	set list.command /commandsets/ew/notImplemented

		# the primary difference here is that if your context is
		# different (ie, clan, or if you cd'd around), "check rooms"
		# will still check -your- rooms, while 'rooms' will not)
	load commands.Also rooms
	set rooms.command ..../rooms
	set rooms.context ~me

	load commands.Also room
	set room.command ..../inspect
	set room.context ~here
	set room.passArgs false

	load commands.CheckEmail email

	load commands.Also wrap
	set wrap.command ..../wordwrap
	set wrap.passArgs false
	
	sort .

cd ....

load CommandCategoryContainer blank
cd blank.commands

	set .title blank

	load commands.Alias commands
	set commands.command "commands blank"

	load commands.Blank$actions actions
	load commands.Blank$friends friends
	load commands.Blank$informs informs
	load commands.Blank$prefers prefers
	sort .

cd ....

# news * EW style commands
load CommandCategoryContainer news
cd news.commands

	set .title news

	load commands.Alias commands
	set commands.command "commands news"

	load commands.Alias check
	set check.command "read news"

	load commands.Alias view
	set view.command "read news"
	conceal view

	load commands.Alias read
	set read.command "read news %m"

	load commands.Alias reply
	set reply.command "reply news %m"

	load commands.Alias followup
	set followup.command "reply news %m"
	conceal followup

	load commands.Alias post
	set post.command "post news %m"

	sort .

cd ....

# mail * EW style commands
load CommandCategoryContainer mail
cd mail.commands
	set .title mail

	load commands.Alias commands
	set commands.command "commands mail"

	load commands.Alias check
	set check.command "read mail"

	load commands.Alias view
	set view.command "read mail"
	conceal view

	load commands.Alias read
	set read.command "read mail %m"

	load commands.Alias reply
	set reply.command "reply mail %m"

	load commands.Alias post
	set post.command "post %m"

	# this may be broken in the future...
	load commands.Alias remove
	set remove.command "remove mail %m"

	load commands.Alias delete
	set delete.command "remove mail %m"
	conceal delete

	sort .

cd ....

load commands.Block block
cd block.commands

	load commands.Alias commands
	set commands.command "commands block"

	load commands.Block$clan clan

	load commands.Block$type friends
	set friends.type friends
	set friends.blockMsg "You block friend tells."
	set friends.unblockMsg "You stop blocking friend tells."

	load commands.Block$type movement
	set movement.type movement
	set movement.blockMsg "You will now not see people entering or leaving."
	set movement.unblockMsg "You will now see people entering and leaving."

	load commands.Block$type tells
	set tells.type player
	set tells.blockMsg "You block all tells."
	set tells.unblockMsg "You stop blocking tells."

	load commands.Block$type shouts
	set shouts.type shouts
	set shouts.blockMsg "You block shouts."
	set shouts.unblockMsg "You stop blocking shouts."

	load commands.Toggle roomAutoHistory
	set roomAutoHistory.toggleField ~me.blockautohistory
	set roomAutoHistory.onFeedback "You won't see the last 10 messages when you enter a room"
	set roomAutoHistory.offFeedback "You will see the last 10 messages when you enter a room"
	
	sort .

cd ....

load commands.Also blocktells
set blocktells.command block.commands/tells
conceal blocktells

load commands.Also earmuffs
set earmuffs.command block.commands/shouts

load Container /online/messages
load Screen /online/messages/listExplanation
edit /online/messages/listExplanation
.wipe
If you're looking for a list of the people you're blocking or have other flags set on, try '^hactions^-'.
Other listable things are:
'^hfriends^-' - To list your friends
'^hinforms^-' - To list the people on your inform list
'^hprefers^-' - To list the people on your prefer list
.end
load commands.Alias list
set list.command print (/online/messages/listExplanation)
conceal list

load commands.With with

load commands.KBLock afk

load commands.Reply reply
load commands.Brief brief
# Punctuation (subtle 04Sep96)
load commands.Punctuation $
conceal $
set $.tell /commandsets/base/tell
set $.remote /commandsets/base/remote
set $.say /commandsets/base/say
set $.emote /commandsets/base/emote
set $.think /commandsets/base/think
set $.whisper /commandsets/base/whisper
set $.shout /commandsets/base/shout
set $.echo /commandsets/base/echo
set $.recho /commandsets/base/recho
set $.help /commandsets/base/help

load commands.Punctuation e
set e.say /commandsets/base/shout
set e.tell /commandsets/base/shout
set e.emote /commandsets/base/eshout
set e.remote /commandsets/base/eshout
set e.shout /commandsets/base/shout
conceal e

# Gate/Yard, etc commands

#cd /commandsets/ew
#load commands.Alias gate
#set gate.command "trans gate"
#
#cd /commandsets/base
#load commands.Also gate
#set gate.context /realm/ruins
#set gate.command /commandsets/ew/gate
#
#cd /commandsets/ew
#load commands.Alias yard
#set yard.command "trans yard"
#
#cd /commandsets/base
#load commands.Also yard
#set yard.context /realm/ruins
#set yard.command /commandsets/ew/yard

cd /commandsets/base
load commands.Alias gate
set gate.command "trans ruins/gate"
conceal gate

load commands.Alias yard
set yard.command "trans ruins/yard"
conceal yard

load commands.Alias square
set square.command "trans city/square"

load commands.Alias garden
set garden.command "trans city/garden"

load commands.Alias tavern
set tavern.command "trans city/tavern"

load commands.Echo public
set public.echo "To set your email address to be publically visible, use '^hemail public^-'.  You can use '^hemail private^-' to make your email address private."
conceal public

# defect (subtle 20Jul97)
load commands.AddLogEntry defect
set defect.logName "defect"
edit defect.feedback
.wipe
Program defect noted, thanks!

.end

load commands.Echo bug
set bug.echo "Please use the 'defect' command to log a program error."
conceal bug

# Idea (subtle 20Jul97)
load commands.AddLogEntry idea
set idea.logName "idea"
edit idea.feedback
.wipe
Idea logged and flagged for a Director's attention

Thanks for taking an interest in Forest.
.end

load commands.Email email
edit email.warning
.wipe
Forest's new code sends an email verification message to your email account when you set it.  This message will contain a special code that can be entered into
Forest in order to validate your email address.

This system is used to prove that you are who you say you are, and ensure that
all the email addresses on the system are valid.

Everyone on Forest is permitted a single character ONLY.  The registration of more than one character will bring about severe consequences.

If you wish to not enter an email address below, simply hit return.

.end

load commands.RefSite site
load commands.BanType newbieban
set newbieban.banType "N"
load commands.BanType siteban
set siteban.banType "C"
load commands.SiteUnban unban
load commands.SitesBanned sitesbanned
load commands.SiteDisplay display

# banish and splat - 24/7
load commands.TimedBanish splat
set splat.banTime 30m
set splat.commandType "S"
load commands.TimedBanish banish
set banish.banTime 3d
set banish.commandType "B"
load commands.Unbanish unsplat
set unsplat.commandType "S"
load commands.Unbanish unbanish
set unbanish.commandType "B"

# online stuff
out
cd online
load events.Shutdown shutdown

load Container footnotes memo

load Memo footnotes/1
set footnotes/1.value "Footnote 1:  The traders became very disgruntled after the cabbage price crash of 97 and pleaded with the city rulers to intervene to keep prices stable.  The rulers were very tired of eating cabbages at the time and opted to support the turnip industry after the move to the new city."

load Memo footnotes/2
set footnotes/2.value "Footnote 2:  I don't think I can sufficently convey to you how much the directors made fun of the author of this sentence. He seems to have put himself in a state of exile over it."

load Memo footnotes/3
set footnotes/3.value "Footnote 3:  Even after the move to the new city we still couldn't find the keys... but we think we know who took them now."

load Memo footnotes/4
set footnotes/4.value "Footnote 4:  The center of the old forest community."

load Memo footnotes/5
set footnotes/5.value "Footnote 5:  Of course they said that about the Titanic as well *shrug*"

load Memo footnotes/6
set footnotes/6.value "Footnote 6:  Footnotes are a lot of fun.  You can see them scattered around the realm, so if you ever see something like <footnote 9>, you know to look at it."

load Memo footnotes/7
set footnotes/7.value "Footnote 7:  The heroic nature of the `spod' is currently a topic of heated discussion in the behavioural psychology classes of a nearby university.  Some even claim that aspiring to `spodness' (or addiction to mindless banter)  is, as one lecturer puts it, `bloody stupid'."

load Memo footnotes/8
set footnotes/8.value "Footnote 8:  Wizard^G'^-s First Rule:  People are stupid."

load Memo footnotes/9
set footnotes/9.value "Footnote 9:  This is the famous recursive footnote, footnote 13.  <footnote 9>"

load Memo footnotes/10
set footnotes/10.value "Footnote 10:  It's not much, but its all we've got."

load Memo footnotes/11
set footnotes/11.value "Footnote 11:  There is no footnote 11"

load Memo footnotes/12
set footnotes/12.value "Footnote 12:  This is a placeholder footnote so that we could have a footnote 13"

load Memo footnotes/13
set footnotes/13.value "Footnote 13:  The famous recursive footnote, footnote 13, has moved.  No junk mail, please."

load Memo footnotes/14
set footnotes/14.value "Footnote 14:  Isn't it fun looking through all the footnotes?"

load Memo footnotes/15
set footnotes/15.value "Footnote 15:  It is slightly interesting to note that 'block tells' is an entirely different beast"

load Screen disclaimer
edit disclaimer
.wipe
      --==>   Disclaimer of Liability and Indemnification   <==--

The Forest is not liable to the users of this service or to its listed
participants for the content, quality, performance or any other aspect of
any information provided by the listed participants and transmitted by
this service or for any errors in the transmission of said information.
Nor is Forest responsible to any person for any damages arising in any
manner out of the use of this service. The users and participants
acknowledge that they assume all risk and/or responsibility for any loss
or damage arising from the uses to which this service is put.

The user and/or the listed participant shall indemnify and hold harmless
Forest, its officers and employees, from and against any claims,
liabilities, losses, costs, damages or expenses (including attorney's
fees) arising from the user's use of or participation in this service or
the information contained thereon.

           By using this program,  you are stating that
           you  understand and  agree to the statements
           above: now and whenever you use this program

.centrealign
.end

load Screen newbieban
edit newbieban
.wipe

        New players are not permitted to directly log in to Forest
		any longer.  To have an account created for you, please
		email forest@realm.progsoc.uts.edu.au, including your
		name and a password.

.centrealign
.end

load Screen siteban
edit siteban
.wipe
          --==>    This site has been completely banned    <==--

    Why?  Well...

        If people are constantly logging on from your site and abusing
        people, continually, and generally preventing everyone from having
        fun (which is the main purpose of this talker), then yes, we're
        going to ban your site.


        You're probably not one of those people.  (Since you *are* reading
        this - which says something).  We're sorry.

        Please note that you may be able to get this 'siteban' removed by
        writing some email to:  forest@realm.progsoc.uts.edu.au

.centrealign
.end

load Screen playerBanished
edit playerBanished

              --==>    This name has been banished    <==--

    What?

        The name you have attempted to use has been barred from entering
        this program.  It probably isn't even directed at you personally,
        you may just have picked a name that someone else (who obviously
        wasn't very popular) has used in the past.  Regardless... you're
        going to have to pick a new name.  [bzzzzt.  try again]

    Why?  Well...

        There are two major reasons that a name could be 'banished' from
        Forest.  Some names are just plain offensive - and we really don't
        like offensive things in our beautiful Forest.  I'd start giving
        examples here, but its not really in good taste, is it?

        Alternatively, some admin may have taken a particular disliking to
        you.  In this case, its probably so that some discussion of sorts
        can be carried out, before deciding upon what action to take (to
        allow you back in, or not).  In this case, it may be worth putting
        in your two cents worth by mailing: forest@realm.progsoc.uts.edu.au

.centrealign
.end

cd /online

#---  the council  -----------------------------------------------------

load Container channels scape
cd /online/channels

	load talker.forest.Council council
	load CommandList /commandSets/council
	set council.commands /commandSets/council

	cd council.commands
		load commands.Broadcast cosay
		set cosay.broadcast "/%o\ ^@%m^$"
		set cosay.feedback "/%o\ ^@%m^$"
		set cosay.scapeFor /online/channels/council
		conceal cosay
		
		load commands.Broadcast coemote
		set coemote.broadcast "/%o%s^@%m^$\"
		set coemote.feedback "/%o%s^@%m^$\"
		set coemote.scapeFor /online/channels/council
		conceal coemote
		
		load commands.Broadcast cothink
		set cothink.broadcast "/%o thinks . o O ( ^@%m^$ )\"
		set cothink.feedback "/%o thinks . o O ( ^@%m^$ )\"
		set cothink.scapeFor /online/channels/council
		conceal cothink
		
		load commands.Punctuation h
		set h.say /commandsets/council/cosay
		set h.emote /commandsets/council/coemote
		set h.think /commandsets/council/cothink
		
		load CommandCategoryContainer block
		cd block.commands

			set .title block
		
			load commands.Block$scape council
			set council.blockMsg "You block the council"
			set council.unblockMsg "You stop blocking the council"
			set council.notinBlockMsg "If you ever become a founder, the council will be blocked"
			set council.notinUnblockMsg "If you ever become a founder, you won't be blocking the council"
			set council.blockScapeMsg "<> %o blocks the council"
			set council.unblockScapeMsg "<> %o stops blocking the council"
			set council.scapeFor /online/channels/council
			set council.type council
			
		cd ....
	
#  Council related resident commands:
	
	cd /commandsets/base
	
	load commands.ScapeWho council
	set council.scapeFor /online/channels/council
	set council.singular There is one member of the council online
	set council.footer There are %n council members on
	set council.empty No council members on at the moment.
	
#---  the advisors  ----------------------------------------------------

cd /online/channels
	
	#  This is the replacement SU channel for the old
	#  fobies.  It should keep 'em happy.
	#
	#  keep the old SU channel feel (s*)
	
	load talker.forest.Advisors advisor
	set /ranks/advisor.joinedTo /online/channels/advisor
	load CommandList /commandSets/advisor
	set advisor.commands /commandSets/advisor
	
	cd advisor.commands
	load commands.Broadcast adsay
	set adsay.broadcast "<%o> ^@%m^$"
	set adsay.feedback "<%o> ^@%m^$"
	set adsay.scapeFor /online/channels/advisor
	conceal adsay
	load commands.Broadcast ademote
	set ademote.broadcast "<%o%s^@%m^$>"
	set ademote.feedback "<%o%s^@%m^$>"
	set ademote.scapeFor /online/channels/advisor
	conceal suemote
	load commands.Broadcast adthink
	set adthink.broadcast "<%o thinks . o O ( ^@%m^$ )>"
	set adthink.feedback "<%o thinks . o O ( ^@%m^$ )>"
	set adthink.scapeFor /online/channels/advisor
	conceal adthink
	load commands.Punctuation s
	set s.say /online/channels/advisor.commands/adsay
	set s.emote /online/channels/advisor.commands/ademote
	set s.think /online/channels/advisor.commands/adthink

	load CommandCategoryContainer block
	cd block.commands

	set .title block
	
	load commands.Block$scape advisor
	set advisor.blockMsg "You block the SU channel"
	set advisor.unblockMsg "You stop blocking the SU channel"
	set advisor.notinBlockMsg "If you're ever promoted to SU, it'll be blocked"
	set advisor.notinUnblockMsg "If you're ever promoted to SU, you won't be blocking the channel"
	set advisor.blockScapeMsg "<> %o blocks the SU channel"
	set advisor.unblockScapeMsg "<> %o stops blocking the SU channel"
	set advisor.scapeFor /online/channels/advisor
	set advisor.type advisor
	
	cd ....

# command setup for ranks
# by default, all of the commands are in the players container - shift from there

# Forest staff commands setup (snap 26Jul97)
out
cd /commandsets/base

ln ~me /ranks/mage

# mage commands
load CommandList /commandSets/mage
set /ranks/mage.commands /commandSets/mage
mv mv /ranks/mage.commands
mv ln /ranks/mage.commands
mv dump /ranks/mage.commands
mv ls /ranks/mage.commands
mv pwd /ranks/mage.commands
mv conceal /ranks/mage.commands
mv load /ranks/mage.commands
mv out /ranks/mage.commands
#mv many /ranks/mage.commands
mv cd /ranks/mage.commands
mv reveal /ranks/mage.commands
mv schedule /ranks/mage.commands
mv transfer /ranks/mage.commands
mv set /ranks/mage.commands
mv sort /ranks/mage.commands
mv sync /ranks/mage.commands
mv threads /ranks/mage.commands
mv yield /ranks/mage.commands
#mv travel /ranks/mage.commands
mv unbanish /ranks/mage.commands
mv limitActions /ranks/mage.commands
mv as /ranks/mage.commands
mv lag /ranks/mage.commands
mv newbies /ranks/mage.commands
mv dc /ranks/mage.commands
mv trace /ranks/mage.commands
mv sitesbanned /ranks/mage.commands
mv banish /ranks/mage.commands
mv newbieban /ranks/mage.commands
mv site /ranks/mage.commands
mv siteban /ranks/mage.commands
mv splat /ranks/mage.commands
mv unsplat /ranks/mage.commands
mv unban /ranks/mage.commands
mv display /ranks/mage.commands
load commands.StringSetAdd reserve
set reserve.stringSet /online/reservednames
load commands.StringSetAdd banname
set banname.stringSet /online/bannednames

mv player /ranks/mage.commands

cd /ranks/mage.commands

	#  these two commands are kind of hacks, useful
	#  mainly for playerfile conversion
load QuickMove qmv
load Swapout swapout

load commands.ViewLog vlog
load commands.Status status

# set up creation commands in appropriate ranks
cd /commandSets/base
load CommandCategoryContainer create
cd create.commands

	set .title create

	load commands.Alias commands
	set commands.command "commands create"

	load commands.Create$room room
	load commands.Create$exit exit

	cd /commandSets/mage
	load CommandCategoryContainer create
	cd create.commands
	set .title create
	load commands.Create$clan clan
	load commands.Create$object object
	load commands.Create$objecttype objecttype
	load commands.Create$player player

	sort .

cd /

sort /commandsets/base
sort /ranks/mage.commands

cd /config/connection
edit newbieB
.wipe
.end
edit newbieA
.wipe
This is a new talker, so, at the moment, we aren't asking for email addresses
when you log in.

Just ^hset a password^- and you should be able to save your character.  (If you
don't set a password, your character will be deleted as soon as you log out)

Once you've finished logging in and have the -> prompt, type 'password' to set
your password.
.end
edit newbieAskEmail
.wipe
.end

# cd /daemons
# web daemon disabled
# load WebConnectPort www 8888

sync /realm
sync /online/help
sync /online/footnotes
sync /sites
# sync /clans

cd
