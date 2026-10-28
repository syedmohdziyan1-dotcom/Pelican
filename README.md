# Pelican Utility — Fabric 1.21.5

Client-side utility mod source project.

## Features
- SpawnerESP and StorageESP toggles
- Black/white GUI with light-purple enabled states
- RMB keybind to open/close the panel (registered in Minecraft Controls)
- Storage block inclusion filters and through-wall toggle
- ESP outlines for loaded block entities within 96 blocks

## Build locally
Requires JDK 21 and Gradle 8.12 or newer:

```sh
gradle build
```

## Build automatically on GitHub (free for public repositories)
1. Create a new repository at https://github.com/new. For the easiest setup, make it **Public**.
2. Extract this ZIP on your device.
3. Upload the **contents of this folder** (the `build.gradle`, `settings.gradle`, `gradle.properties`, `src` folder, `.github` folder, and this README) to the repository root. Do not upload only the ZIP file and do not leave everything nested inside another `pelican` folder.
4. Open the repository's **Actions** tab. The `Build Fabric Mod` workflow should start after the files are pushed. If needed, open the workflow and choose **Run workflow**.
5. Open the completed workflow run, scroll to **Artifacts**, and download `pelican-utility-fabric-1.21.5`.
6. Extract the artifact ZIP. The mod JAR will be inside it, under `build/libs/` (or directly in the artifact, depending on the upload layout).

The workflow runs `gradle build` with Java 21 and Gradle 8.12, then uploads the JAR as an artifact for 14 days. A successful workflow is required before the mod can be considered compiled. If the build fails, open the workflow log and share the error so it can be fixed.

## Notes
This source targets Yarn mappings for Minecraft 1.21.5. The through-wall option uses render-state depth testing and may need adjustment for compatibility with other render mods. RMB is a mouse keybind and may conflict with normal right-click use; change it in Options → Controls if needed. This is source code, not a verified compiled JAR.
