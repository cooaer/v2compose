# GitHub Tag Release Automation Plan

## Goal

Automatically publish signed Android APKs to GitHub Releases when a `v*` tag is
pushed for a commit that is already on `master`.

## Implementation

- Add `.github/workflows/release.yml` to build and publish `fossRelease` and
  `googleRelease` APKs.
- Checkout the full repository history with recursive submodules so the
  `fruit-kt` composite build is available in CI.
- Reject tags whose commit is not reachable from `origin/master`.
- Restore release-only files from GitHub Secrets:
  - `ANDROID_RELEASE_KEYSTORE_BASE64`
  - `ANDROID_RELEASE_KEYSTORE_PASSWORD`
  - `ANDROID_RELEASE_KEY_ALIAS`
  - `ANDROID_RELEASE_KEY_PASSWORD`
  - `GOOGLE_SERVICES_JSON_RELEASE_BASE64`
- Build with:

```bash
./gradlew --no-configuration-cache :androidApp:assembleFossRelease :androidApp:assembleGoogleRelease
```

- When both flavors are built in a single Gradle invocation, Firebase Gradle
  tasks are disabled for non-Google variants so the foss APK remains independent
  of `google-services.json` and Crashlytics mapping upload.

- Upload the APKs to the tag release as:
  - `v2v-v2.0.0-foss.apk`
  - `v2v-v2.0.0-google.apk`

The file names are generated from the pushed tag, so `v2.1.0` produces
`v2v-v2.1.0-foss.apk` and `v2v-v2.1.0-google.apk`.

## Gradle Signing

`androidApp/build.gradle.kts` defines a release signing config from environment
variables. Release assemble or bundle tasks fail early if any signing variable is
missing, preventing unsigned APKs from being published.

Debug builds and non-release inspection tasks do not require signing secrets.

## Verification

- Run a non-release Gradle task without signing secrets to confirm local
  development still works.
- Run release assemble tasks with local equivalents of the CI environment
  variables to confirm signed APK generation.
- After the first pushed tag, verify the GitHub Release contains both renamed
  APK assets and that both APKs install successfully on Android.
