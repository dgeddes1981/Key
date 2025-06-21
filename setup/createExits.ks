# script created by exile on 20Jul97
# He wants you to know how much he hated doing it
# but he volunteered for it anyway *schmuck*

# Creating the exits for outside

# ******* island
cd ~outside/island

load Exit boat
set boat.autolook false
set boat.to ~outside/island
set boat.departRoom "%o leaves on the boat."
set boat.arriveRoom "%o has just stepped off the boat."
edit boat.description 
.wipe
You see a simple wooden boat with a set of oars.
.end

edit boat.through
.wipe
The behaviour of the boat changes erratically as you find it increasingly difficult to row away from the island.  An unsettling feeling overtakes you when the effort of leaving causes your body to ache.  In the back of your mind you know that going back to the island will make all the pain go away.  The island calls to you as if your return will ease it's isolation.

You heroically struggle against the very sea.  Then the world suddenly goes black.
.end
transfer boat $

# ******* beach
cd ~outside/beach
load Exit boat
set boat.autolook false
set boat.to ~outside/island
set boat.departRoom "%o takes a boat out into the Sea of Mists"
set boat.arriveRoom "%o has set foot on the shore of the Isle."
edit boat.description 
.wipe
You see a simple wooden boat with a set of oars.
.end
edit boat.through
.wipe
Your resolve to journey across the Sea of Mists wavers as the wood of the boat creaks when you set foot on the gangway.  An old gnarled fisherman laughs at your apprehension as he says "That boat will take yea where you want to go, have no doubt." and after that, with a chuckle he adds "It's not the sea that you should be worried about."  You begin to focus on him when he vanishes, his laughter ringing in your ears.  A sudden gush of wind whispers to you "I hope you know your way home!" and then subsides as quickly as it appeared.

The boat glides through the water with suprising ease as you work the oars.  In no time you reach the sandy shores of the isolated island.
.end
transfer boat $

load Exit stream
set stream.autolook false
set stream.to ~outside/lake
set stream.departRoom "%o follows the stream where ever it leads."
set stream.arriveRoom "%o is lead by the stream here."
edit stream.description
.wipe
The waters of the beach are channeled in such a way that it forms a little stream that leads away from the beach.
.end
edit stream.through
.wipe
The sounds of the sea become distant as you follow a stream of clear water.  Birds perch in trees nearby and chirp to you friendly as you pass through.  The further you travel along the stream the more relaxed you become.
.end
transfer stream $

load Exit shore
set shore.autolook false
set shore.to ~outside/village
set shore.departRoom "%o leaves footprints in the sand as %h walks away along the shore."
set shore.arriveRoom "%o shakes the sand off %i feet as %h enters the village."
edit shore.description
.wipe
You see the sea swell against the shore line.
.end
edit shore.through
.wipe
You enjoy the feeling of sand against your bare feet as you follow the shore towards the seaside village in the distance.  You see fishing boats just off the shore with locals waving welcome to you.
.end
transfer shore $

# ****** village
cd ~outside/village

load Exit shore
set shore.autolook false
set shore.to ~outside/beach
set shore.departRoom "%o heads out of the village to walk upon the shoreline."
set shore.arriveRoom "%o arrives from along the sea shore."
edit shore.description
.wipe
You see a sandy shore that winds around a small headland.
.end
edit shore.through
.wipe
As you walk along the shore your shoes get filled with warm sand. You decide to take your shoes off so that you can approach the water and splash in the surf.
.end
transfer shore $

load Exit road
set road.autolook false
set road.to ~outside/outlook
set road.departRoom "%o decides to leave the village by way of the road."
set road.arriveRoom "%o wearily trudges up the mountain."
edit road.description
.wipe
You see a wide, well travelled road that leads up through the mountains.
.end
edit road.through
.wipe
The road leading out of the village begins to slope rather steeply as it ascends into the mountians. Turning to face the village you can see the it lying before you, the people look like dolls that busily carry out their daily chores.
.end
transfer road $

# ****** lake

cd ~outside/lake
load Exit stream
set stream.autolook false
set stream.to ~outside/beach
set stream.departRoom "%o goes to find the source of the stream."
set stream.arriveRoom "%o is lead by the stream here."
edit stream.description
.wipe
You see a stream that feeds into the lake.
.end
edit stream.through
.wipe
Leaving the tranquil surroundings of the lake you can feel you blood stirred by the sounds of the ocean and the salty taste of the breeze.
.end
transfer stream $

load Exit gorge
set gorge.autolook false
set gorge.to ~outside/gorge
set gorge.departRoom "%o disappears into the gorge."
set gorge.arriveRoom "%o escapes to the gorge to seek solace in the shadows."
edit gorge.description
.wipe
You see the entrance to the gorge.
.end
edit gorge.through
.wipe
The gentle landscape surrounding the lake gives way to two rising walls that encapsulate the gorge.
.end
transfer gorge $

# ****** vale

cd ~outside/vale
load Exit trail
set trail.autolook false
set trail.to ~outside/arches
set trail.departRoom "%o seeks the source of enlightenment."
set trail.arriveRoom "%o has arrived looking for answers to all their questions."
edit trail.description
.wipe
You see a trail before you.
.end
edit trail.through
As you follow the trail your thoughts become clearer. You approach what appears to be arches made from ancient stone.  The sky becomes overcast as a cloud obscures the sun.  You shiver a little bit.
.end
transfer trail $

load Exit rubble
set rubble.autolook false
set rubble.to ~ruins/base
set rubble.departRoom "%o goes to see the ruins."
set rubble.arriveRoom "%o has come to inspect the ruins."
edit rubble.description
.wipe
You see the rubble of what once was a road that lead to the
.end
edit rubble.through
.wipe
The terrain becomes rather hilly and wild. The forest encroaches onto the remainder of the old Dawnkeep road. The air around you hangs heavily as if a great sadness haunts the very trees. You think you can even smell the faint odour of smoke.
.end
transfer rubble $

# ****** arches

cd ~outside/arches
load Exit path
set path.autolook false
set path.to ~outside/outlook
set path.arriveRoom "%o appears with a confused look on their face."
set path.departRoom "%o leaves, deep in thought."
edit path.description
You see a simple path that leads towards the mountains.
.end
edit path.through
.wipe
As you leave the arches you find it difficult to remember all that has happened.  Your thoughts escape you and return to those of the mundane world.
.end
transfer path $

load Exit trail
set trail.autolook false
set trail.to ~outside/vale
set trail.departRoom "%o treks further along the trail."
set trail.arriveRoom "%o has arrived from along the trail." 
edit trail.description
.wipe
You see a trail that leads through the vale.
.end
edit trail.through
.wipe
The trail cuts through the many and varied flowers that grow freely in the vale.  The walk is pleasant and the weather warm, a sleepy sensation washes over your body.
.end
transfer trail $

# ****** outlook

cd ~outside/outlook
load Exit road
set road.autolook false
set road.to ~outside/village
set road.departRoom "%o descends down the road towards the coast."
set road.arriveRoom "%o rides into the village on the back of a cart."
edit road.description
.wipe
You see a worn road that leads down the mountain towards the coast.
.end
edit road.through
.wipe
The temperature becomes warmer as you descend the mountain side.  The brisk wind becomes a soft sea breeze.  You marvel at the climatic change that the mountains shelter creates.  You can see smoke from the village chimneys at the base, nestled in the protective care of the rocky mountain side and the wide open expanse of the sea.
.end
transfer road $

load Exit gorge
set gorge.autolook false
set gorge.to ~outside/gorge
set gorge.departRoom "%o goes to see what is at the bottom of the gorge."
set gorge.arriveRoom "%o has come to get a closer look inside the gorge."
edit gorge.description
.wipe
You see simple dirt track leading away from the outlook.
.end
edit gorge.through
.wipe
The gound undulates as you walk further away from the outlook.  You have to concentrate so as not to slip and hurt yourself on the rocks.  Up ahead you notice a massive gash in the earth, like the mountain has collapsed under its own weary weight.
.end
transfer gorge $

load Exit lane
set lane.autolook false
set lane.to ~outside/southroad
set lane.departRoom "%o takes the lane to the Great Southern Road."
set lane.arriveRoom "%o steps off the lane onto the Great Southern Road."
edit lane.description
.wipe
You see a sturdy laneway that leads down the mountain.
.end
edit lane.through
.wipe
The lane slopes and winds down the mountains, the sounds of the city greet your ears.
.end
transfer lane $

load Exit pathway
set pathway.autolook false
set pathway.to ~outside/waterfall
set pathway.departRoom "%o leaves via the path to seek the waterfall."
set pathway.arriveRoom "%o goes and stands on the edge, watching the water flow into the river below."
edit pathway.description
.wipe
You do not notice anything special about the pathway.
.end
edit pathway.through
.wipe
The sound of rushing water gets stronger as you move further along the pathway.  You notice that there is a fine mist that is suspended in the air making your clothing damp and cold.
.end
transfer pathway $

load Exit track
set track.autolook false
set track.to ~outside/arches
set track.departRoom "%o continues their journey along the path."
set track.arriveRoom "%o is drawn here from the mountains."
edit track.description
.wipe
You see a simple track that leads down into the grassy vale.
.end
edit track.through
.wipe
The track is simple yet worn.  You feel that you know this path well but you cannot remember having come down here before.  The mountain peaks loom behind you as the gentle descent leads into a wide, lush vale.  Appearing quite suddenly in your view you notice seven stone archways.  The stone calls to you silently.
.end
transfer track $

# ****** gorge

cd ~outside/gorge
load Exit track
set track.autolook false
set track.to ~outside/outlook
set track.departRoom "%o follows a dirt track towards the summit of the mountains."
set track.arriveRoom "%o climbs out from the gorge to join you on the outlook."
edit track.description
.wipe
You see a rocky track that leads up the mountain.
.end
edit track.through
.wipe
The way up the mountain is a tough walk littered with loose rocks.  There are times when you cannot tell where the track begins and the ends.  Nevertheless you slowly, yet steadily, climb higher and higher.
.end
transfer track $

load Exit floor
set floor.autolook false
set floor.to ~outside/lake
set floor.departRoom "%o wanders along the floor of the gorge to the source of the water."
set floor.arriveRoom "%o appears, happily walking from the shade of the gorge."
edit floor.description
.wipe
The floor of the gorge has been worn smooth by the weather throughout the ages creating a smooth surface.
.end
edit floor.through
.wipe
You hear the sounds of your echoing foot falls as you make your way through the bottom of the gorge.  You feel the cool air soothing all your ills and a tranquil feeling washes over you as you near the end of the gorge.
.end
transfer floor $

# ****** southroad

cd ~outside/southroad
load Exit laneway
set laneway.autolook false
set laneway.to ~outside/outlook
set laneway.departRoom "%o leaves the Great Southern Road to follow the laneway up the mountain."
set laneway.arriveRoom "%o trudges up the laneway to see the sights from the outlook."
edit laneway.description
.wipe
You see a laneway that branches from the main road and leads up the mountain.
.end
edit laneway.through
.wipe
You follow the branch in the road away from the direction of the city and begin a sturdy climb along a laneway.  The wind gets stronger the closer you get to the top of the mountain.
.end
transfer laneway $

load Exit tavern
set tavern.autolook false
set tavern.to ~city/tavern
set tavern.departRoom "%o goes to the tavern for a drink and a yarn."
set tavern.arriveRoom "%o steps into the tavern looking for something to quench their thirst."
edit tavern.description
.wipe
You see before you the establishment called the Bitter Brother.  Being the oldest tavern in the realm (except for the original tavern that was destroyed in the Great Exodus) it has become renowned throughout the land as a fine place to spend ones time.  The large doors appear to be well used and the sounds of people having a splendid time reach your ears.
.end
edit tavern.through
.wipe
You swing the doors open to be instantly greeted by the warm and cheery faces of the regulars.  The barkeep waves for you to come and be served.
.end
transfer tavern $

load Exit entrance
set entrance.autolook false
set entrance.to ~city/entrance
set entrance.departRoom "%o enters the City."
set entrance.arriveRoom "%o has entered the City."
edit entrance.description
.wipe
You see the entrance to the City standing before you with doors wide open.  The sounds of the city beckon for you to enter.
.end
edit entrance.through
.wipe
You walk past the Bitter Brother and fall into a line behind the many other visitors waiting to enter the City.  In no time at all you are next in line to enter.
.end
transfer entrance $

# ****** entrance

cd ~city/entrance
load Exit road
set road.autolook false
set road.to ~outside/southroad
set road.arriveRoom "%o has left the City to experience what life on the road is like."
set road.departRoom "%o leaves the City for the unknown of the wilderness."
edit road.description
.wipe
You see the Great Southern Road stretching off into the distance.
.end
edit road.through
.wipe
You step onto the Great Southern Road.
.end
transfer road $

load Exit square
set square.autolook false
set square.to ~city/square
set square.arriveRoom "%o has come from out of the city."
set square.departRoom "%o goes to see what the gossip is in the town square."
edit square.description
.wipe
You see the town square of the city.  There are many people of all different walks of life discussing various topics. 
.end
edit square.through
.wipe
You approach the town square and instantly people welcome you and encourage you to stop and chat with them for a while.
.end
transfer square $

# ****** waterfall

cd ~outside/waterfall
load Exit water
set water.autolook false
set water.to ~city/garden
set water.departRoom "%o steps into the water and is swept off the edge."
set water.arriveRoom "%o climbs out of the river, coughing and spluttering."
edit water.description
.wipe
You see massive amounts of water flowing freely over the edge of the cliff.
.end
edit water.through
.wipe
Your stomach leaps into your mouth and your heart skips a beat as you are swept over the edge of the cliff by the torrent of water.  You can't quite remember what happened next and then you find yourself washed up on the shore of the river.  Your clothes are soaking wet and you keep coughing up water.
.end
transfer water $

load Exit pathway
set pathway.autolook false
set pathway.to ~outside/outlook
set pathway.departRoom "%o steps away from the edge of the waterfall and follows the path back to the outlook."
set pathway.arriveRoom "%o arrives a bit damp from the waterfall."
edit pathway.description
.wipe
You see a pathway that goes back to the outlook.
.end
edit pathway.through
.wipe
You step away from the edge and feel immediate relief from the giddy sensation of looking down from such a height.  You return back to the relative safety of the outlook.
.end
transfer pathway $

# ****** cave
# ****** volcano

# ****** yard

cd ~ruins/yard
load Exit gate
set gate.autolook false
set gate.to ~ruins/gate
set gate.arriveRoom "%o arrives from the remains yard."
set gate.departRoom "%o heads in the direction of the gate."
edit gate.description
.wipe
You see the burnt out husk of the once famous gate.
.end
edit gate.through
.wipe
You slowly walk towards the old gate.
.end
transfer gate $

load Exit avenue
set avenue.autolook false
set avenue.to ~ruins/avenue
set avenue.arriveRoom "%o has come to see what is left of the old estates."
set avenue.departRoom "%o leaves to walk along the old avenue."
edit avenue.description
.wipe
You see the avenue that was well known for its beautiful houses and estates.
.end
edit avenue.through
.wipe
You leave the decaying remains of the courtyard behind you as you try to find some part of the old castle that has remained untouched by the destruction of the riots and the panic of the exodus.  You move towards the avenue where you hope to catch a glimpse of the splendor of a bygone day.
.end
transfer avenue $

# ****** gate

cd ~ruins/gate
load Exit base
set base.autolook false
set base.to ~ruins/base
set base.departRoom "%o leaves the gate area to walk around outside the walls."
set base.arriveRoom "%o has come to inspect the castle exterior."
edit base.description
.wipe
You see the weary form of the old gate now charred and bent.  Beyond the gate lies the base of the castle.
.end
edit base.through
.wipe
You pass under the blackened stonework of the gateway and leave the castle to it's own sad memories.
.end
transfer base $

load Exit yard
set yard.autolook false
set yard.to ~ruins/yard
set yard.arriveRoom "%o has just arrived from the old gate."
set yard.departRoom "%o has left to see what %h can find in the remains of the yard."
edit yard.description
.wipe
You see the silent remains of the old castle courtyard.
.end
edit yard.through
.wipe
Your footsteps echo loudly off the walls as you make your way towards the barren courtyard.
.end
transfer yard $

# ****** avenue

cd ~ruins/avenue
load Exit yard
set yard.autolook false
set yard.to ~ruins/yard
set yard.arriveRoom "%o has just returned after walking along the avenue."
set yard.departRoom "%o leaves the ruins of the estates to return to the courtyard."
edit yard.description
.wipe
You see the courtyard in the distance.  It is strangely quiet.
.end
edit yard.through
.wipe
You quickly leave the avenue.  A chilling reminder that misfortune strikes everyone, it is perhaps as fair as it is painful.

You return to the courtyard of Dawnkeep.
.end
transfer yard $

# ****** base

cd ~ruins/base
load Exit rubble
set rubble.autolook false
set rubble.to ~outside/vale
set rubble.departRoom "%o leaves all the memories behind and returns to the wilderness."
set rubble.arriveRoom "%o has come from the source of the rubble road."
edit rubble.description
.wipe
You see the decayed remains of road that lead into the castle Dawnkeep.
.end
edit rubble.through
.wipe
You feel a terrible sadness as you leave the ruins of the once beautiful castle.  The wilderness has started to reclaim its' scarred land leaving very little evidence of the mighty walls and stone architecture of man.

With a sigh you begin the hilly journey back to civilisation.
.end
transfer rubble $

load Exit gate
set gate.autolook false
set gate.to ~ruins/gate
set gate.departRoom "%o goes to further inspect the damage inside the ruins."
set gate.arriveRoom "%o enters the ruins of castle Dawnkeep."
edit gate.description
.wipe
You see the weary form of the old gate now charred and bent.
.end
edit gate.through
.wipe
The charred remains of the gate loom overhead as you approach.
.end
transfer gate $

# ****** tavern

cd ~city/tavern
load Exit road
set road.autolook false
set road.to ~outside/southroad
set road.arriveRoom "%o has just stepped out of the bar looking most pleased with themselves."
set road.departRoom "%o has decided that there is more to life than sitting in the tavern and has begun their search on the Great Southern Road."
edit road.description
.wipe
As the tavern doors swing open you catch a glimpse of the Great Southern Road.
.end
edit road.through
.wipe
You stand up to leave and the locals call your name and ask you to stay a while longer.  For a moment you are almost convinced that there is nothing outside that really needs you attention that badly that you have to leave right away.  With great effort you force yourself to walk through the doors and step onto the Great Southern Road.  The sun blinds you for a minute because it takes you a while to adjust from the dingy lighting of the tavern.
.end
transfer road $

load Exit square
set square.autolook false
set square.to ~city/square
set square.arriveRoom "%o has come to see what the gossip is like in the town square."
set square.departRoom "%o quickly leaves with a curse when %h realise that %h is late for work."
edit square.description
.wipe
You can't actually see the town square because you are sitting in the tavern.  In order to see the townsquare you would have to go outside.
.end
edit square.through
.wipe
You finish off your drink and try to ease yourself out of the conversation you were having with a dotty old man who is droning on about the old days.  You weave your way through the smoke filled room and walking through the doors are blinded by the light of day.
.end
transfer square $

# ****** garden

cd ~city/garden
load Exit square
set square.autolook false
set square.to ~city/square
set square.departRoom "%o leaves the peace of the garden for the noise of the city square."
set square.arriveRoom "%o has just come from the garden."
edit square.description
.wipe
You see the busy townsquare.
.end
edit square.through
.wipe
You regretfully decide to leave this magnificant garden to join with the rest of society in the town square.
.end
transfer square $

load Exit study
set study.autolook false
set study.to ~city/study
set study.departRoom "%o takes some of the tranquility of the garden into the study."
set study.arriveRoom "%o enters through the garden door."
edit study.description
.wipe
You see the wing of the library that makes up the study.
.end
edit study.through
.wipe
You open the door that leads into the study and step inside, your mind clear and at peace.
.end
transfer study $

# ****** library

cd ~city/library
load Exit square
set square.autolook false
set square.to ~city/square
set square.arriveRoom "%o walks down from the library steps."
set square.departRoom "%o leaves for the city square."
edit square.description
.wipe
You see the City Square before you.
.end
edit square.through
.wipe
You borrow the book you are currently reading and take it with you out into the City Square.
.end
transfer square $

load Exit study
set study.autolook false
set study.to ~city/study
set study.arriveRoom "%o has found a book %h wishes to study and has brought it with %i into the room."
set study.departRoom "%o takes some books into the quiet of the Study."
edit study.description
.wipe
You see a wing of the library that had been set aside for quiet, personal study.
.end
edit study.through
.wipe
You take the book you have just found into the study.  You notice a deafening silence.
.end
transfer study $

# ****** study

cd ~city/study
load Exit library
set library.autolook false
set library.to ~city/library
set library.departRoom "%o goes to find an ancient tome in the library."
set library.arriveRoom "%o has decided that %h has had enough study and returns to the library."
edit library.description
.wipe
You see rows of books stacked neatly on the shelves of the library. The room is very quiet.
.end
edit library.through
.wipe
You leave the study and find yourself amoungst many old and dusty books.
.end
transfer library $

load Exit garden
set garden.autolook false
set garden.to ~city/garden
set garden.departRoom "%o leaves to wander through the gardens."
set garden.arriveRoom "%o has arrived, smelling like flowers."
edit garden.description
.wipe
You see the sun shining on the lush folliage of the garden trees and flowers.
.end
edit garden.through
.wipe
The quiet of the study is quickly replaced by the rustling of leaves and the soft sounds of footfalls on grass.  The gentle scent of a variety of flowers wafts in the air.
.end
transfer garden $

# ****** hall

cd ~city/hall
load Exit square
set square.autolook false
set square.to ~city/square
set square.departRoom "%o has left the hall to see what is happening in the townsquare."
set square.arriveRoom "%o emmerges from the Great Hall."
edit square.description
.wipe
You see that the townsquare is crowded with people.
.end
edit square.through
.wipe
You leave the warm circle of light being cast from the fireplace and instantly feel the chill of the shadows.  You hurry towards the door and make your way into the crowded townsquare.
.end
transfer square $

load Exit warroom
set warroom.autolook false
set warroom.to ~city/warroom
set warroom.departRoom "%o goes to organise their plan of attack."
set warroom.arriveRoom "%o has come to devise a cunning plan."
edit warroom.description
.wipe
You see a simple door that is closed and hidden in shadows, the contents of the room are kept secret from your eyes.
.end
edit warroom.through
.wipe
You wade through the shadows and open the door.  The wooden door opens smoothly and you step inside.
.end
transfer warroom $

# ****** square

cd ~city/square
load Exit tavern
set tavern.autolook false
set tavern.to ~city/tavern
set tavern.departRoom "%o dashes into the Bitter Brother for a drink."
set tavern.arriveRoom "%o has entered the tavern for a drink and a yarn with the regulars."
edit tavern.description
.wipe
You see the famous tavern, The Bitter Brother.  The establishment looks very well patroned.
.end
edit tavern.through
.wipe
The sunny day becomes but a smokey remnant as you enter the establishment.  Smiling faces great you, creating a light of their own.  You make your way to a table and order a drink and a meal.
.end
transfer tavern $

load Exit entrance
set entrance.autolook false
set entrance.to ~city/entrance
set entrance.arriveRoom "%o has come to the entrance to meet someone."
ser entrance.departRoom "%o ventures to the entrance of the city to ponder what lies beyond."
edit entrance.description
.wipe
You see the open gates of the City and the wilderness beyond.
.end
edit entrance.through
.wipe
You feel the call of the wild in your blood as you make your way to the entrance of the city.  You decide that today might be a good day to discover what lies outside the protective walls.
.end
transfer entrance $

load Exit hall
set hall.autolook false
set hall.to ~city/hall
set hall.departRoom "%o goes to sit by the fire in the Great Hall."
set hall.arriveRoom "%o enters the Hall and waits for their eyes to adjust."
edit hall.description
.wipe
You see the Great Hall before you.  The exterior of the building looks older than the rest of the city and you might be inclined to wonder why.
.end
edit hall.through
.wipe
The first thing you notice when you enter the hall is the damp darkness.  Then as your eyes adjust you can see the outlines of many people talking in hushed voices.
.end
transfer hall $

load Exit council
set council.autolook false
set council.to ~city/council
set council.departRoom "%o goes to seek the council."
set council.arriveRoom "%o has come to hear the council speak."
edit council.description
.wipe
You see a large door that is colourfully decorated with the shields of the council.
.end
edit council.through
.wipe
You stride towards the council chambers and pushing firmly against the large door, you enter.
.end
transfer council $

load Exit garden
set garden.autolook false
set garden.to ~city/garden
set garden.arriveRoom "%o has come to spend some quiet time in the eden setting."
set garden.departRoom "%o leaves to spend some time in the scenic beauty of the gardens."
edit garden.description
.wipe
You wonder if the roses smell as good as they look.
.end
edit garden.through
.wipe
You stroll towards the peaceful surroundings of the gardens.  The noise of the city vanishes as the gentle scents purfume the air you breathe.
.end
transfer garden $

load Exit library
set library.autolook false
set library.to ~city/library
set library.arriveRoom "%o has come to see what ancient knowledge the library holds."
set library.departRoom "%o goes to look for a good book in the library."
edit library.description
.wipe
You see the grand architecture of the library grace the city skyline.  The doors are open and you can see many shelves, heavily laden, with books.
.end
edit library.through
.wipe
You walk towards the doors of the library hoping that inside you will find some piece of ancient wisdom.
.end
transfer library $

# ****** council

cd ~city/council
load Exit square
set square.autolook false
set square.to ~city/square
set square.arriveRoom "%o strides out from the council chambers."
set square.departRoom "%o leaves the chambers for the townsquare."
edit square.description
.wipe
You see the door that leads to the townsquare.
.end
edit square.through
.wipe
You leave the Council Chambers.
.end
transfer square $

# ****** warroom

cd ~city/warroom
load Exit hall
set hall.autolook false
set hall.to ~city/hall
set hall.arriveRoom "%o enters the Hall, eyes gleaming in the firelight."
set hall.departRoom "%o leaves to execute some fiendish plot."
edit hall.description
.wipe
You see the door that leads back to the Great Hall.
.end
edit hall.through
.wipe
You leave your plotting to return to the Great Hall.
.end
transfer hall $

cd
