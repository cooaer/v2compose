# F-Droid 2.0.0 Release Readiness Plan

## Goal

Prepare V2X 2.0.0 for the F-Droid release pipeline by making the `fossRelease`
variant buildable from a clean command line checkout, removing tracked Firebase
configuration, and documenting the downstream fdroiddata release update.

## Repository Changes

- Set Android `versionName` to `2.0.0` and `versionCode` to `200`.
- Source the in-app displayed version from `BuildKonfig.VERSION_NAME` instead of
  a duplicated hard-coded constant.
- Keep the shared Compose resources assets workaround, but make the app lint
  vital tasks depend on `:shared:copyAndroidMainComposeResourcesToAndroidAssets`
  so Gradle 9 does not fail on implicit task dependencies.
- Use the repository `fruit-kt` submodule as the default composite build, with
  local `../fruit-kt` kept as a developer fallback when the submodule is absent.
- Remove `androidApp/src/googleRelease/google-services.json` from git tracking.
  The file remains ignored for local Google builds.
- Update F-Droid metadata for the 2.0.0 release and synchronize the displayed
  app title to `V2X`.

## F-Droid Data Follow-Up

After tagging the release, update `fdroiddata/metadata/io.github.v2compose.yml`
with a new build block:

```yaml
- versionName: 2.0.0
  versionCode: 200
  commit: <v2.0.0 tag corresponding full commit hash>
  submodules: true
  gradle:
    - foss
```

The old `subdir: app` setting does not apply to 2.0.0 because the Android app
module is now `androidApp` and the root Gradle task resolves
`assembleFossRelease` correctly. `submodules: true` is required so F-Droid checks
out the `fruit-kt` source submodule used by the composite build.

If retaining upstream binary verification, publish a GitHub Release asset that
matches the existing `Binaries` template:

```text
https://github.com/cooaer/v2compose/releases/download/v2.0.0/v2v-v2.0.0-foss.apk
```

The APK must be signed with the existing certificate allowed by fdroiddata.

## Verification Commands

Run these before tagging:

```bash
./gradlew --no-configuration-cache :androidApp:assembleFossRelease
./gradlew --no-configuration-cache :shared:compileKotlinIosSimulatorArm64
./gradlew --no-configuration-cache :androidApp:dependencyInsight --configuration fossReleaseRuntimeClasspath --dependency firebase
./gradlew --no-configuration-cache :androidApp:dependencyInsight --configuration fossReleaseRuntimeClasspath --dependency play-services
git ls-files androidApp/src/googleRelease/google-services.json
```

Expected dependency insight result for Firebase and Play Services:

```text
No dependencies matching given input were found in configuration ':androidApp:fossReleaseRuntimeClasspath'
```

Expected `git ls-files` result for `google-services.json`: no output.

## Release Checklist

- Create and push the `v2.0.0` tag after all verification passes.
- Upload `v2v-v2.0.0-foss.apk` to the GitHub Release if upstream binary
  verification remains enabled in fdroiddata.
- Open an fdroiddata merge request for version `2.0.0 (200)`.
- Confirm the F-Droid build log uses the `foss` flavor and does not include
  Firebase, GMS, or Play Services dependencies.
