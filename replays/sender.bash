#!/bin/bash

# compile imcreplay tool
cd .. && ./gradlew imcsender && cp dist/tools/imcreplay*.jar replays && cd -
java -jar imcreplay*.jar comap_startup.json udp://127.0.0.1:6002
