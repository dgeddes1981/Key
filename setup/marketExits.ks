# script created by Noble on 22Feb00

# Linking off the square

# cd ~city/square

# load Exit market
# cd market
# set .autolook true
# set .to ~market/market
# set .departRoom "%o heads off to the market to do a little shopping."
# set .arriveRoom "%o has entered the market looking for a bargain."
# edit .description 
# .wipe
# The City Market looks even busier then the Square.
# .end
# edit .through
# .wipe
# You decide its time to do some shopping and head off to the hustle and bustle of the City Market.
# .end
# transfer . $


# Creating the exits for market

# ------- market
cd ~market/market

# load Exit road
# cd road
# set .autolook false
# set .to ~city/square
# set .departRoom "%o heads back to the square, slightly less wealthy."
# set .arriveRoom "%o strolls down the main road from the market."
# edit .description 
# .wipe
# A wide road that leads straight up to the City Square.
# .end
# edit .through
# .wipe
# You stuff your new belongings in your inventory and head off back to the main square.
# .end
# transfer . $
# cd ..

load Exit marble
cd marble
set .autolook true
set .to ~market/rainas
set .departRoom "%o runs up Raina Road."
set .arriveRoom "%o has come to wander on Raina Road."
edit .description 
.wipe
A marble pathway leading up to Raina Road.
.end
edit .through
.wipe
You wander up the marble pathways of Raina Road.
.end
transfer . $
cd ..

load Exit sugar
cd sugar
set .autolook true
set .to ~market/hee
set .departRoom "%o heads off to Hee Street."
set .arriveRoom "%o strolls onto Hee Street."
edit .description 
.wipe
A sugar coated street, heading onto Hee Street.
.end
edit .through
.wipe
You arrive on Hee Street. Your stomach growls in anticipation.
.end
transfer . $
cd ..

load Exit pavement
cd pavement
set .autolook true
set .to ~market/saville
set .departRoom "%o strolls up to Saville Row."
set .arriveRoom "%o has come from the market."
edit .description 
.wipe
A wide pavement that clears onto Saville Row.
.end
edit .through
.wipe
You visit the business district of Saville Row.
.end
transfer . $
cd ..

load Exit frost
cd frost
set .autolook true
set .to ~market/spleeen
set .departRoom "%o slides onto Spleeen Street."
set .arriveRoom "%o enters from the market."
edit .description 
.wipe
A frost covered road.
.end
edit .through
.wipe
You slip and slide your way along a frosty walkway, heading onto Spleeen Street.
.end
transfer . $
cd ..

load Exit dark
cd dark
set .autolook true
set .to ~market/fish
set .departRoom "%o flies up to Fish Drive."
set .arriveRoom "%o has come from the market."
edit .description 
.wipe
The path darkens slightly as it winds up to Fish Drive.
.end
edit .through
.wipe
You notice the light dull a little as you head up to Fish Drive.
.end
transfer . $
cd ..



# ------- rainas
cd ~market/rainas

load Exit market
cd market
set .autolook true
set .to ~market/market
set .departRoom "%o heads back to the market."
set .arriveRoom "%o appears from Raina Road."
edit .description 
.wipe
A marble path leading back to the central market.
.end
edit .through
.wipe
You head back into the central market area.
.end
transfer . $
cd ..

load Exit clara
cd clara
set .autolook true
set .to ~market/clara
set .departRoom "%o enters Clara's Jewellery Shop."
set .arriveRoom "%o enters the shop."
edit .description 
.wipe
A small Jewellery Store.
.end
edit .through
.wipe
You open the door to Clara's and head on in.
.end
transfer . $
cd ..

load Exit cafe
cd cafe
set .autolook true
set .to ~market/cafe
set .departRoom "%o enters Cafe Luciano's"
set .arriveRoom "%o comes in off Raina Road."
edit .description 
.wipe
A small Cafe, smelling of coffee.
.end
edit .through
.wipe
As you open the door to Luciano's you are hit by the strong smell of coffee.
.end
transfer . $
cd ..



# ------- clara
cd ~market/clara

load Exit door
cd door
set .autolook true
set .to ~market/rainas
set .departRoom "%o exits the shop."
set .arriveRoom "%o comes out of Clara's."
edit .description 
.wipe
A fine door leading out onto Raina Road.
.end
edit .through
.wipe
You leave the finery of Clara's and head onto Raina Road.
.end
transfer . $
cd ..



# ------- cafe
cd ~market/cafe

load Exit front
cd front
set .autolook true
set .to ~market/rainas
set .departRoom "%o leaves the cafe, by the front door."
set .arriveRoom "%o steps out from the cafe."
edit .description 
.wipe
The front door , which leads onto Raina Road.
.end
edit .through
.wipe
You take a deep breath of fresh air as you head back out to Raina Road.
.end
transfer . $
cd ..

load Exit back
cd back
set .autolook true
set .to ~market/hee
set .departRoom "%o leaves the cafe, by the back door."
set .arriveRoom "%o steps out from the cafe."
edit .description 
.wipe
The back door , which leads onto Hee Street.
.end
edit .through
.wipe
The pleasant air is a nice welcome after being in the Cafe.
.end
transfer . $
cd ..



# ------- hee
cd ~market/hee

load Exit market
cd market
set .autolook true
set .to ~market/market
set .departRoom "%o goes off to the market."
set .arriveRoom "%o has come from Hee Street."
edit .description 
.wipe
A sugary little lane that leads back to the market.
.end
edit .through
.wipe
You walk back down the sugar road to the market.
.end
transfer . $
cd ..

load Exit cafe
cd cafe
set .autolook true
set .to ~market/cafe
set .departRoom "%o enters Cafe Luciano's"
set .arriveRoom "%o comes in off Hee Street."
edit .description 
.wipe
A small Cafe, smelling of coffee.
.end
edit .through
.wipe
You squint your eyes as you step out onto Hee Street.
.end
transfer . $
cd ..

load Exit maccas
cd maccas
set .autolook true
set .to ~market/maccas
set .departRoom "%o enters McForest."
set .arriveRoom "%o has come for some fine dining at McForest."
edit .description 
.wipe
A small path that winds right beneath the big M.
.end
edit .through
.wipe
You duck in to McForest for a fast bite to eat.
.end
transfer . $
cd ..

load Exit shoppe
cd shoppe
set .autolook true
set .to ~market/pacman
set .departRoom "%o enters the Food Shoppe."
set .arriveRoom "%o is dragged into the Food Shoppe by %i stomach. "
edit .description 
.wipe
A short path that is scattered with litter bins on either side.
.end
edit .through
.wipe
Hungrily you step into the Food Shoppe.
.end
transfer . $
cd ..

load Exit bakery
cd bakery
set .autolook true
set .to ~market/bakery
set .departRoom "%o enters the Bakery"
set .arriveRoom "%o is led by %i nose into the Bakery."
edit .description 
.wipe
A narrow trail that leads up to a pleasant smelling Bakery.
.end
edit .through
.wipe
Resisting the temptation no longer, you step into the Bakery.
.end
transfer . $
cd ..



# ------- maccas
cd ~market/maccas

load Exit door
cd door
set .autolook true
set .to ~market/hee
set .departRoom "%o leaves McForest."
set .arriveRoom "%o has come out of McForest."
edit .description 
.wipe
The door leading back out onto Hee Street.
.end
edit .through
.wipe
Full of Fast Food, you head back to Hee Street.
.end
transfer . $
cd ..



# ------- pacman
cd ~market/pacman

load Exit door
cd door
set .autolook true
set .to ~market/hee
set .departRoom "%o leaves the Food Shoppe."
set .arriveRoom "%o has come out of the Food Shoppe."
edit .description 
.wipe
The door leading back out onto Hee Street.
.end
edit .through
.wipe
Feeling quite full, you decide its time to leave the Food Shoppe.
.end
transfer . $
cd ..



# ------- bakery
cd ~market/bakery

load Exit door
cd door
set .autolook true
set .to ~market/hee
set .departRoom "%o leaves the Bakery."
set .arriveRoom "%o has come out of the Bakery."
edit .description 
.wipe
The door leading back out onto Hee Street.
.end
edit .through
.wipe
Taste buds a-tingling, you head back out onto Hee Street.
.end
transfer . $
cd ..



# ------- saville
cd ~market/saville

load Exit market
cd market
set .autolook true
set .to ~market/market
set .departRoom "%o heads back to the market."
set .arriveRoom "%o walks down from Saville Row."
edit .description 
.wipe
The road builds up and becomes the main Market district.
.end
edit .through
.wipe
You leave Saville Row for the more crowded market area.
.end
transfer . $
cd ..

load Exit tailor
cd tailor
set .autolook true
set .to ~market/ben
set .departRoom "%o enters Ben's Tailor Shoppe."
set .arriveRoom "%o arrives to sample Ben's material."
edit .description 
.wipe
Ben's Tailor Shoppe, where you can find all your tailoring needs.
.end
edit .through
.wipe
Hoping to find some quality clothing, you enter Ben's Tailor Shoppe.
.end
transfer . $
cd ..

load Exit style
cd style
set .autolook true
set .to ~market/silky
set .departRoom "%o enters Silky's Store."
set .arriveRoom "%o enters, hoping to acquire some 'style'."
edit .description 
.wipe
Silky's Store, where you can find all your stylish needs.
.end
edit .through
.wipe
Feeling the need to be more 'stylish', you enter Silky's Store.
.end
transfer . $
cd ..

load Exit footwear
cd footwear
set .autolook true
set .to ~market/steed
set .departRoom "%o enters Steed's Footwear Shoppe."
set .arriveRoom "%o comes in looking for some quality footwear."
edit .description 
.wipe
Steed's Footwear Shoppe, where you can find all your footwear needs.
.end
edit .through
.wipe
Hoping to improve your current footwear condition, you enter Steed's Footwear Shoppe.
.end
transfer . $
cd ..

load Exit cloaks
cd cloaks
set .autolook true
set .to ~market/grant
set .departRoom "%o enters Grant's Cloak Shoppe."
set .arriveRoom "Bright light shines in the room as %o enters."
edit .description 
.wipe
Grant's Cloak Shoppe, where you can find all your cloaking needs.
.end
edit .through
.wipe
Feeling it is time for a new cloak, you enter Grant's Cloak Shoppe.
.end
transfer . $
cd ..

load Exit ladies
cd ladies
set .autolook true
set .to ~market/winifred
set .departRoom "%o enters Winifred's Shoppe."
set .arriveRoom "Winifred welcomes %o as %h enters the shoppe."
edit .description 
.wipe
Winifred's Shoppe, where you can find all your feminine needs.
.end
edit .through
.wipe
Looking for that little feminine something, you head into Winifred's Shoppe.
.end
transfer . $
cd ..

load Exit trinkets
cd trinkets
set .autolook true
set .to ~market/shoppe
set .departRoom "%o enters the Olde Clothing Shoppe."
set .arriveRoom "%o has entered the Olde Clothing Shoppe."
edit .description 
.wipe
The Olde Clothing Shoppe, where you can find all your err, trinkety needs. ;)
.end
edit .through
.wipe
Feeling a need to pick up some trinkets and novelty items, you enter the Olde Clothing Shoppe.
.end
transfer . $
cd ..



# ------- ben
cd ~market/ben

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves Ben's Tailor Shoppe."
set .arriveRoom "%o has come out of Ben's Tailor Shoppe."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
You leave the Tailor Shoppe for Saville Row.
.end
transfer . $
cd ..



# ------- silky
cd ~market/silky

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves Silky's Store."
set .arriveRoom "%o has come out of Silky's Store."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
Gathering your new belongings you head out onto the street.
.end
transfer . $
cd ..



# ------- steed
cd ~market/steed

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves Steed's Footwear Shoppe."
set .arriveRoom "%o has come out of Steed's Footwear Shoppe."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
Sporting your new footwear, you walk out onto Saville Row.
.end
transfer . $
cd ..



# ------- grant
cd ~market/grant

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves Grant's Cloak Shoppe."
set .arriveRoom "%o has come out of Grant's Cloak Shoppe."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
Wrapped in your new cloak, you step out into the brightness of Saville Row.
.end
transfer . $
cd ..



# ------- winifred
cd ~market/winifred

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves Winifred's Shoppe."
set .arriveRoom "%o has come out of Winifred's Shoppe."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
Shopping bags safely tucked under your arms, you head out onto Saville Row.
.end
transfer . $
cd ..



# ------- shoppe
cd ~market/shoppe

load Exit door
cd door
set .autolook true
set .to ~market/saville
set .departRoom "%o leaves the Olde Clothing Shoppe."
set .arriveRoom "%o has come out of the Olde Clothing Shoppe."
edit .description 
.wipe
The door leading back out onto Saville Row.
.end
edit .through
.wipe
You farewell the shopkeeper as you exit the store.
.end
transfer . $
cd ..

load Exit gents
cd gents
set .autolook true
set .to ~market/gents
set .departRoom "%o ducks into The Gents Shoppe."
set .arriveRoom "%o enters The Gents Shoppe."
edit .description 
.wipe
The Gents Shoppe adjoins the Olde Clothing Store.
.end
edit .through
.wipe
You quickly pop into The Gents Shoppe, leaving the Olde Clothing Shoppe behind.
.end
transfer . $
cd ..



# ------- gents
cd ~market/gents

load Exit door
cd door
set .autolook true
set .to ~market/shoppe
set .departRoom "%o leaves The Gents Shoppe."
set .arriveRoom "%o comes back out of The Gents Shoppe."
edit .description 
.wipe
This door leads back into the adjoining store, the Olde Clothing Shoppe.
.end
edit .through
.wipe
You leave The Gents Shoppe and head back into the Olde Clothing Shoppe.
.end
transfer . $
cd ..



# ------- spleeen
cd ~market/spleeen

load Exit market
cd market
set .autolook true
set .to ~market/market
set .departRoom "%o leaves the warmth of Spleeen Street for the market."
set .arriveRoom "%o enters the market from Spleeen Street."
edit .description 
.wipe
A frosty path that leads into the market.
.end
edit .through
.wipe
The path lightens somewhat as you get further towards the main market.
.end
transfer . $
cd ..

load Exit misc
cd misc
set .autolook true
set .to ~market/qixotl
set .departRoom "%o enters Qixotl's Shoppe of Miscellany."
set .arriveRoom "%o enters the shoppe from Spleeen Street."
edit .description 
.wipe
The path leads straight up to Qixotl's Shoppe of Miscellany.
.end
edit .through
.wipe
You step into Qixotl's Shoppe of Miscellany to browse through the rare items on offer.
.end
transfer . $
cd ..

load Exit toys
cd toys
set .autolook true
set .to ~market/jonny
set .departRoom "%o enters the Toy Shoppe."
set .arriveRoom "%o runs into the toy shoppe with a childish glint in %i eyes."
edit .description 
.wipe
Every child's dream , the Toy Shoppe stands before you.
.end
edit .through
.wipe
Gleefully you bounce into the Toy Shoppe.
.end
transfer . $
cd ..



# ------- shirley
cd ~market/shirley

load Exit side
cd side
set .autolook true
set .to ~market/qixotl
set .departRoom "%o leaves for Qixotl's Shoppe of Miscellany."
set .arriveRoom "%o enters from Shirley's Shoppe of Miscellany."
edit .description 
.wipe
The side door of the shop leads into Qixotl's Shoppe of Miscellany.
.end
edit .through
.wipe
You go to pay Shirley's brother a visit in the Shoppe of Miscellany.
.end
transfer . $
cd ..

load Exit door
cd door
set .autolook true
set .to ~market/fish
set .departRoom "%o steps outside onto Fish Drive."
set .arriveRoom "%o steps out from Shirley's Shoppe of Miscellany."
edit .description 
.wipe
The main entrance to the Shoppe, which comes from Fish Drive.
.end
edit .through
.wipe
You decide you have had enough novelty shopping and step out onto Fish Drive.
.end
transfer . $
cd ..



# ------- qixotl
cd ~market/qixotl

load Exit door
cd door
set .autolook true
set .to ~market/spleeen
set .departRoom "%o steps outside onto Spleeen Street."
set .arriveRoom "%o steps out from Qixotl's Shoppe of Miscellany."
edit .description 
.wipe
The main entrance to the Shoppe, which comes from Spleeen Street.
.end
edit .through
.wipe
You decide you have had enough novelty shopping and step out onto Spleeen Street.
.end
transfer . $
cd ..

load Exit side
cd side
set .autolook true
set .to ~market/shirley
set .departRoom "%o leaves for Shirley's Shoppe of Miscellany."
set .arriveRoom "%o enters from Qixotl's Shoppe of Miscellany."
edit .description 
.wipe
The side door of the shop leads into Shirley's Shoppe of Miscellany.
.end
edit .through
.wipe
You go to pay Qixotl's sister a visit in the Shoppe of Miscellany.
.end
transfer . $
cd ..



# ------- jonny
cd ~market/jonny

load Exit front
cd front
set .autolook true
set .to ~market/spleeen
set .departRoom "%o leaves the shoppe with toys tucked under both arms."
set .arriveRoom "%o comes out of the Toy Shoppe, childishly satisfied."
edit .description 
.wipe
The front door of the shoppe , leading out to Spleeen Street.
.end
edit .through
.wipe
You decide you have had enough fun for one day and head back out to Spleeen Street.
.end
transfer . $
cd ..

load Exit back
cd back
set .autolook true
set .to ~market/cynthia
set .departRoom "%o sneaks off into Cynthia's Shoppe."
set .arriveRoom "%o has come for some toys with a bit more fuzziness."
edit .description 
.wipe
The back door leads into Cynthia's Shoppe, home of the cute and cuddly.
.end
edit .through
.wipe
Seeking some cute and fuzziness, you enter Cynthia's Shoppe.
.end
transfer . $
cd ..



# ------- cynthia
cd ~market/cynthia

load Exit door
cd door
set .autolook true
set .to ~market/jonny
set .departRoom "%o seeks some more serious toys in the Toy Shoppe."
set .arriveRoom "%o has come from Cynthia's, in search of more toys."
edit .description 
.wipe
The door leads from one toy shoppe to the next.
.end
edit .through
.wipe
You move from Cynthia's into the Toy Shoppe, still surrounded by toys.
.end
transfer . $
cd ..



# ------- fish
cd ~market/fish

load Exit market
cd market
set .autolook true
set .to ~market/market
set .departRoom "%o heads back up to the Market."
set .arriveRoom "%o has arrived at the market from Fish Drive."
edit .description 
.wipe
This path gets brighter the closer it gets to the main market.
.end
edit .through
.wipe
As you head back to the market, you notice how much brighter it seems to become.
.end
transfer . $
cd ..

load Exit misc
cd misc
set .autolook true
set .to ~market/shirley
set .departRoom "%o enters Shirley's Shoppe of Miscellany."
set .arriveRoom "%o enters the shoppe from Fish Drive."
edit .description 
.wipe
This trail heads off into Shirley's Shoppe of Miscellany.
.end
edit .through
.wipe
You plan to buy some rare little trinkets as you enter Shirley's Shoppe of Miscellany.
.end
transfer . $
cd ..

load Exit florist
cd florist
set .autolook true
set .to ~market/ethel
set .departRoom "%o enters Ethel's Flower Shoppe."
set .arriveRoom "%o comes in off Fish Drive."
edit .description 
.wipe
A path that is surrounded on all sides by flowers of all description winds up to Ethel's Flower Shoppe.
.end
edit .through
.wipe
You make your way up a pleasant smelling path and enter Ethel's Flower Shoppe.
.end
transfer . $
cd ..



# ------- ethel
cd ~market/ethel

load Exit door
cd door
set .autolook true
set .to ~market/fish
set .departRoom "%o steps out onto Fish Drive."
set .arriveRoom "%o exits the Flower Shoppe."
edit .description 
.wipe
The main door leads back out to Fish Drive.
.end
edit .through
.wipe
As you exit the Flower Shoppe, you notice that Fish Drive doesn't smell nearly as nice.
.end
transfer . $
cd ..



cd



