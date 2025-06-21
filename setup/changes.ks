#: Key setup script
#: $Id: changes.ks,v 1.1.1.1 1999/10/07 19:58:22 pdm Exp $

#: setting up default commands...

cd /commandsets/base

load commands.SetSession session
load commands.Comments comments
load commands.ConstrainedSet comment

cd comment

set .displayProperty sessionComment
set .setProperty sessionComment
set .in $
set .on me
set .blankFeedback "Your comment is currently '%v'"
set .filledFeedback "Your comment now reads: %v"
set .canbeblank true

load commands.ISO3166 sitecodes

edit defect.feedback
.wipe
Program defect noted, thanks!

.end

set /online/footnotes/8.value "Footnote 8:  Wizard^G'^-s First Rule: People are stupid."

set /commandsets/base/grab/enter "%t spins rapidly into being."

load Screen /online/logoutMsg
edit /online/logoutMsg.text
.wipe
Thanks for playing.
.end

cd

# June

load Memo /online/footnotes/17
set /online/footnotes/17.value "Footnote 17:  Yes, you can now state on your tax return that Forest is an educational toy, allowing you to learn your a-z's!"
