# Forest Online Help v1.00 chapter 9
cd /online/help
#
# (miscellaneous) (Koschei 21/04/99 1.00)
#
load Screen miscellaneous
set miscellaneous.title Miscellaneous
set miscellaneous.author Koschei
edit miscellaneous.text
.wipe

Miscellaneous
-------------

As is typical, not everything can be programmed, categorised, or easily referenced ^g<footnote 19>^-. Because of this, we have to present some stuff in this section.

sub topics

  ^hcolour^-    (9.1)     using colours
  ^hpage^-      (9.2)     how to get someone's attention
  ^hcountries^- (9.3)     the country domain codes for IPs

.end
many miscellaneous 9 misc
# <footnote 19: I'm sorry. I just couldn't help quoting the X-Files movie.>

#
# (miscellaneous/colour) (aiffiona 25/04/99 1.00)
#
load Screen colour
set colour.title Colour
set colour.author aiffiona
edit colour.text
.wipe
Colour
------

Colour is still available, but there are different commands to set and add colour. Instead of 5 billion help files, and lots of help spam, help on colours can be found by typing the command with no parameters. For example, just typing "colour" gives you help on how to change the colour of the differnt channels.

^hcolour^-   - how to colour channels
^hiscolour^- - what colours there are and how to get colour on your screen
^hterm^-     - what term do you have set

See also the help on ^hcolourcodes^- for the appropriate codes to embed into text to colourise it.

Colour can now be used in prefixes.  However be warned: at the end of using a colour, if ^^- is not used it will cause every thing you say to turn to that colour

For example:
    ^^G^GKeyed~in Aiff says i love key^-
as compared to:
    ^^G^GKeyed~in^^-^-  Aiff says i love key

When it comes to using underlines in prefixes, it is preferred that you didn't, as many telnet clients appear to not support it fully and it has a habit of not terminating properly on those clients and having the rest of the line being underlined despite one turning it off (which ^hreally^- annoys some people).
.end
many colour col cols colour 9.1 colours colors color
#
# (miscellaneous/colourCodes) (aiffiona 25/04/99 1.00)
#
load Screen colourcodes
set colourcodes.title Colour codes
set colourcodes.author Koschei
edit colourcodes.text
.wipe
Colour Codes
------------

 ^r ^^r - Red ^-         ^R ^^R - BrightRed ^-
 ^y ^^y - Brown ^-       ^Y ^^Y - BrightYellow ^-
 ^g ^^g - Green ^-       ^G ^^G - Bright Green ^-
 ^b ^^b - Blue ^-        ^B ^^B - Bright Blue ^-
 ^c ^^c - Cyan ^-        ^C ^^C - Bright Cyan ^-
 ^m ^^m - Magenta ^-     ^M ^^M - Bright Magenta ^-
 
 ^h ^^h - Hilite        ^^_ - ^_underline^-^N
 ^d ^^d - Dark
 
 ^hNOTE:^- On some systems ^^M will appear pink.
 ^^- - Terminate colour level.   ^^n terminate ^hall^- colour.

.end
many colourcodes codes
#
# (miscellaneous/page) (Koschei 21/04/99 1.00)
#
load Screen page
set page.title Getting someone's attention
set page.author Koschei
edit page.text
.wipe
How to Get Someone's Attention
------------------------------

There are two main methods of getting someone's attention.

^hwake^- <name>

   This sends a generic wake message to <name>.

^hpage^- <name> <message>

   This sends a specialised wake message to <name>


Due to the noisy nature of these, one must have a two minute gap between successive wakes or pages.

.end
many page wake attention 9.2
#
# (miscellaneous/sitecodes) (Koschei 22/04/99 1.00)
#
load Screen sitecodes
set sitecodes.title Country codes
set sitecodes.author
edit sitecodes.text
.wipe
Country codes for IP addresss
-----------------------------

Up to date copies of this list may be found at <http://www.bcpl.lib.md.us/~jspath/isocodes.html>. This site is not run by anyone who is related in some way to Forest (at least, not that we know of).

ad     Andorra, Principality of
ae     United Arab Emirates
af     Afghanistan, Islamic State of
ag     Antigua and Barbuda
ai     Anguilla
al     Albania
am     Armenia
an     Netherlands Antilles
ao     Angola
aq     Antarctica
ar     Argentina
arpa   Old style Arpanet
as     American Samoa
at     Austria
au     Australia
aw     Aruba
az     Azerbaidjan
ba     Bosnia-Herzegovina
bb     Barbados
bd     Bangladesh
be     Belgium
bf     Burkina Faso
bg     Bulgaria
bh     Bahrain
bi     Burundi
bj     Benin
bm     Bermuda
bn     Brunei Darussalam
bo     Bolivia
br     Brazil
bs     Bahamas
bt     Bhutan
bv     Bouvet Island
bw     Botswana
by     Belarus
bz     Belize
ca     Canada
cc     Cocos (Keeling) Islands
cd     Congo, The Democratic Republic of the
cf     Central African Republic
cg     Congo
ch     Switzerland
ci     Ivory Coast (Cote D'Ivoire)
ck     Cook Islands
cl     Chile
cm     Cameroon
cn     China
co     Colombia
com    Commercial
cr     Costa Rica
cs     Former Czechoslovakia
cu     Cuba
cv     Cape Verde
cx     Christmas Island
cy     Cyprus
cz     Czech Republic
de     Germany
dj     Djibouti
dk     Denmark
dm     Dominica
do     Dominican Republic
dz     Algeria
ec     Ecuador
edu    Educational
ee     Estonia
eg     Egypt
eh     Western Sahara
er     Eritrea
es     Spain
et     Ethiopia
fi     Finland
fj     Fiji
fk     Falkland Islands
fm     Micronesia
fo     Faroe Islands
fr     France
fx     France (European Territory)
ga     Gabon
gb     Great Britain
gd     Grenada
ge     Georgia
gf     French Guyana
gh     Ghana
gi     Gibraltar
gl     Greenland
gm     Gambia
gn     Guinea
gov    USA Government
gp     Guadeloupe (French)
gq     Equatorial Guinea
gr     Greece
gs     S. Georgia & S. Sandwich Isls.
gt     Guatemala
gu     Guam (USA)
gw     Guinea Bissau
gy     Guyana
hk     Hong Kong
hm     Heard and McDonald Islands
hn     Honduras
hr     Croatia
ht     Haiti
hu     Hungary
id     Indonesia
ie     Ireland
il     Israel
in     India
int    International
io     British Indian Ocean Territory
iq     Iraq
ir     Iran
is     Iceland
it     Italy
jm     Jamaica
jo     Jordan
jp     Japan
ke     Kenya
kg     Kyrgyz Republic (Kyrgyzstan)
kh     Cambodia, Kingdom of
ki     Kiribati
km     Comoros
kn     Saint Kitts & Nevis Anguilla
kp     North Korea
kr     South Korea
kw     Kuwait
ky     Cayman Islands
kz     Kazakhstan
la     Laos
lb     Lebanon
lc     Saint Lucia
li     Liechtenstein
lk     Sri Lanka
lr     Liberia
ls     Lesotho
lt     Lithuania
lu     Luxembourg
lv     Latvia
ly     Libya
ma     Morocco
mc     Monaco
md     Moldavia
mg     Madagascar
mh     Marshall Islands
mil    USA Military
mk     Macedonia
ml     Mali
mm     Myanmar
mn     Mongolia
mo     Macau
mp     Northern Mariana Islands
mq     Martinique (French)
mr     Mauritania
ms     Montserrat
mt     Malta
mu     Mauritius
mv     Maldives
mw     Malawi
mx     Mexico
my     Malaysia
mz     Mozambique
na     Namibia
nato   NATO (this was purged in 1996 - see hq.nato.int)
nc     New Caledonia (French)
ne     Niger
net    Network
nf     Norfolk Island
ng     Nigeria
ni     Nicaragua
nl     Netherlands
no     Norway
np     Nepal
nr     Nauru
nt     Neutral Zone
nu     Niue
nz     New Zealand
om     Oman
org    Non-Profit Making Organisations (sic)
pa     Panama
pe     Peru
pf     Polynesia (French)
pg     Papua New Guinea
ph     Philippines
pk     Pakistan
pl     Poland
pm     Saint Pierre and Miquelon
pn     Pitcairn Island
pr     Puerto Rico
pt     Portugal
pw     Palau
py     Paraguay
qa     Qatar
re     Reunion (French)
ro     Romania
ru     Russian Federation
rw     Rwanda
sa     Saudi Arabia
sb     Solomon Islands
sc     Seychelles
sd     Sudan
se     Sweden
sg     Singapore
sh     Saint Helena
si     Slovenia
sj     Svalbard and Jan Mayen Islands
sk     Slovak Republic
sl     Sierra Leone
sm     San Marino
sn     Senegal
so     Somalia
sr     Suriname
st     Saint Tome (Sao Tome) and Principe
su     Former USSR
sv     El Salvador
sy     Syria
sz     Swaziland
tc     Turks and Caicos Islands
td     Chad
tf     French Southern Territories
tg     Togo
th     Thailand
tj     Tadjikistan
tk     Tokelau
tm     Turkmenistan
tn     Tunisia
to     Tonga
tp     East Timor
tr     Turkey
tt     Trinidad and Tobago
tv     Tuvalu
tw     Taiwan
tz     Tanzania
ua     Ukraine
ug     Uganda
uk     United Kingdom
um     USA Minor Outlying Islands
us     United States
uy     Uruguay
uz     Uzbekistan
va     Holy See (Vatican City State)
vc     Saint Vincent & Grenadines
ve     Venezuela
vg     Virgin Islands (British)
vi     Virgin Islands (USA)
vn     Vietnam
vu     Vanuatu
wf     Wallis and Futuna Islands
ws     Samoa
ye     Yemen
yt     Mayotte
yu     Yugoslavia
za     South Africa
zm     Zambia
zr     Zaire
zw     Zimbabwe
.end
many sitecodes country countries
