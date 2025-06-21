cd /
load te0.te0 te0
edit te0.headline
<center><img src="/te0/images/forestribbon.gif" height=205 width=220></center>
.end


#---  the te0editors  ----------------------------------------------------

	cd /commandSets
	load CommandList te0editor
	cd te0editor

	load CommandCategoryContainer te0
	cd te0.commands

	set .title te0
		
	load commands.te0.Approve approve
	load commands.te0.Reject reject
	load commands.te0.Submissions list
	
	

	cd /online/channels
	
	load te0.te0Editor te0editor
	set te0editor.commands /commandSets/te0editor
	
	cd te0editor.commands
	load commands.Broadcast edsay
	set edsay.broadcast "|%o| ^@%m^$"
	set edsay.feedback "|%o| ^@%m^$"
	set edsay.scapeFor /online/channels/te0editor
	conceal edsay
	load commands.Broadcast edemote
	set edemote.broadcast "|%o%s^@%m^$|"
	set edemote.feedback "|%o%s^@%m^$|"
	set edemote.scapeFor /online/channels/te0editor
	conceal edemote
	load commands.Broadcast edthink
	set edthink.broadcast "|%o thinks . o O ( ^@%m^$ )|"
	set edthink.feedback "|%o thinks . o O ( ^@%m^$ )|"
	set edthink.scapeFor /online/channels/te0editor
	conceal edthink
	load commands.Punctuation d
	set d.say /online/channels/te0editor.commands/edsay
	set d.emote /online/channels/te0editor.commands/edemote
	set d.think /online/channels/te0editor.commands/edthink

	load CommandCategoryContainer block
	cd block.commands

	set .title block
	
	load commands.Block$scape te0editor
	set te0editor.blockMsg "You block the te0 channel"
	set te0editor.unblockMsg "You stop blocking the te0 channel"
	set te0editor.notinBlockMsg "If you ever join te0, it'll be blocked"
	set te0editor.notinUnblockMsg "If you ever join te0, you won't be blocking the channel"
	set te0editor.blockScapeMsg "|| %o blocks the te0 channel"
	set te0editor.unblockScapeMsg "|| %o stops blocking the te0 channel"
	set te0editor.scapeFor /online/channels/te0editor
	set te0editor.type te0editor

#------------------------------------------------------------------
	
cd
