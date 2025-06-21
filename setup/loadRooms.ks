# creating system rooms
# script created 20Jul97 by exile
# made subtle-friendly 26Jul97 by exile

cd /realm
load Landscape ruins
load Landscape city
load Landscape outside
load Landscape caves

# building the ruins
#include ruins/avenue.description
#include ruins/base.description
#include ruins/gate.description
#include ruins/yard.description

# building outside
#include outside/beach.description
#include outside/island.description
#include outside/lake.description
#include outside/outlook.description
#include outside/vale.description
#include outside/village.description
#include outside/gorge.description
#include outside/waterfall.description
#include outside/cave.description
#include outside/southroad.description
#include outside/arches.description

# building the cave system
#include caves/volcano.description

# building the City
#include city/council.description
#include city/dungeon.description
#include city/garden.description
#include city/graveyard.description
#include city/hall.description
#include city/library.description
#include city/study.description
#include city/tavern.description
#include city/townsquare.description
#include city/entrance.description
#include city/warRoom.description

# make insertion rooms
out
ln ~city/garden realm.entryRooms
ln ~outside/village realm.entryRooms
ln ~city/square realm.entryRooms
ln ~outside/outlook realm.entryRooms
ln ~city/hall realm.entryRooms
ln ~city/entrance realm.entryRooms
