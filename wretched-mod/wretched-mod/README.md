# Wretched (Fabric, Minecraft 1.21.1)

Adds the **Wretched**: a blocky, segmented, wall-climbing centipede with glowing red eyes and a drill tail.

- Spawns in dark caves (below sea level), groups of 1-2
- 22 HP, 4 armor, 3 damage; its bite inflicts Weakness (3s / 6s / 10s by difficulty)
- Climbs walls like a spider; body ripples as it moves, mandibles snap when it has a target
- Drops 0-2 Wretched Drill Tips; spawn egg is in the Spawn Eggs tab
- Test it: `/summon wretched:wretched`

## Build
Requires JDK 21.

1. Add the Gradle wrapper once (this zip only ships `gradle-wrapper.properties`):
   `gradle wrapper --gradle-version 8.14`
   (or copy `gradlew`, `gradlew.bat` and `gradle/wrapper/gradle-wrapper.jar` from the official Fabric example mod)
2. `./gradlew runClient` to try it in-game, or `./gradlew build` for a jar in `build/libs/`.

Version numbers are in `gradle.properties`; `https://fabricmc.net/develop` has the current set if any fail to resolve.

## Tweaking
- Stats, goals, bite effect: `entity/WretchedEntity.java`
- Shape and animation: `client/WretchedEntityModel.java`
- Colours, eye positions, cracks: `tools/generate_textures.py` (then re-run it; needs Pillow)
- Rename the `com.example.wretched` package and the `wretched` mod id to whatever you like.
