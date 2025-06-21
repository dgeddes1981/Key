load commands.Broadcast kdsay
set kdsay.broadcast "<%o> ^@%m^$"
set kdsay.feedback "<%o> ^@%m^$"
set kdsay.scapeFor /online/channels/keydev
conceal kdsay

load commands.Broadcast kdemote
set kdemote.broadcast "<%o%s^@%m^$>"
set kdemote.feedback "<%o%s^@%m^$>"
set kdemote.scapeFor /online/channels/keydev
conceal kdemote

load commands.Broadcast kdthink
set kdthink.broadcast "<%o thinks . o O ( ^@%m^$ )>\"
set kdthink.feedback "<%o thinks . o O ( ^@%m^$ )>\"
set kdthink.scapeFor /online/channels/keydev
conceal kdthink

load commands.Punctuation k
set k.say /commandsets/keydev/kdsay
set k.emote /commandsets/keydev/kdemote
set k.think /commandsets/keydev/kdthink

