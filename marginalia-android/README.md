# Marginalia Search for Android

Unofficial Android client for [Marginalia Search](https://marginalia-search.com/).

## Features

- Search via the official [Marginalia API v2](https://about.marginalia-search.com/article/api/) (`api2.marginalia-search.com`)
- Built-in **Web UI** tab (full `marginalia-search.com` experience in a WebView)
- Configurable API key, result count, and experimental NSFW reduction
- Share text from other apps into Marginalia to search

## Build

Requirements: JDK 17+, Android SDK 35.

```bash
export ANDROID_HOME="$HOME/android-sdk"
cd marginalia-android
./gradlew assembleDebug
```

The debug APK is at `app/build/outputs/apk/debug/app-debug.apk`.

## API key

The app defaults to the `public` API key for experimentation. That key is rate-limited. For regular use, request your own key from the Marginalia project and enter it in **Settings**.

Search results are subject to [CC-BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/) for non-commercial use, per Marginalia's API terms.

## License

This client source is provided alongside the parent repository. Marginalia Search itself is AGPL; this app only calls the public HTTP API and embeds the public website.
