# RageShield

RageShield is an open-source, crowdsourced rage-bait detection and skipping system for Android. Its purpose is to help users avoid deliberately provocative, outrage-driven content and endless rage loops on social media.

## Project Vision
RageShield uses a community-driven model inspired by SponsorBlock:
- **Crowdsourced Reports:** Users report rage-bait content.
- **Verification:** Community consensus determines whether content is verified.
- **Automatic Skipping:** Verified content can be automatically skipped for other users.
- **Privacy-First:** Local-first processing, anonymous contributions, and no unnecessary data collection.

## Supported Platforms
RageShield is architected for modular support across all major social media platforms:
- ✅ Facebook
- ✅ Instagram
- ✅ YouTube
- ✅ TikTok
- ✅ X / Twitter
- ✅ Threads
- ✅ Reddit

## Architecture
RageShield consists of two primary components:
1. **Android Application:** Uses an `AccessibilityService` and a modular `PlatformAdapter` system to detect content and perform skip actions safely.
2. **Backend API:** An open-source, self-hostable service (Kotlin/Ktor + PostgreSQL) for handling community reports, voting, and reputation management.

## Getting Started
RageShield is currently in active development. 

### Build Requirements
- Android Studio with JDK 17
- The project uses a Gradle wrapper for consistent builds. 

### Contributing
We welcome contributions! Please see our contribution guidelines (coming soon) and open an issue if you'd like to help with platform-specific adapters or the detection engine.

## License
This project is licensed under the [Apache 2.0 License](LICENSE).
