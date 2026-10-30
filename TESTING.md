# Testing

Checked on 30 October 2026 using JDK 21, Android SDK 37 and an Android 15 (API 35) emulator with a 720 x 1280 display.

## Build and static checks

- Debug APK: built successfully.
- `testDebugUnitTest`: 3 tests passed, no failures.
- `lintDebug`: passed with no errors. Advisory warnings remain for the supplied bitmap resource locations, launcher-icon recommendations, autofill and backup configuration.

## Emulator checks

`MunicipalityFlowTest`: 4 tests passed, no failures.

| Check | Result |
| --- | --- |
| Uppercase, lowercase, mixed case and surrounding spaces | Correctly normalised |
| Empty input, spaces and Guangzhou | Error shown on the main screen |
| Explicit intent to Municipality | Correct activity opened |
| Four names, images and information-array entries | Matched the selected municipality |
| Title, information text and Visit site colours | Matched the required colour resources |
| Visit site button visibility | Visible within each detail layout |
| Four website actions | Correct ACTION_VIEW and URL, no explicit component |
| Missing, blank or unsupported intent extra | Activity closed safely |
| No browser available | Exception handled without a crash |

The website test intercepts outgoing intents to check their action, URI and lack of an explicit component. It does not claim to test every browser or a full website page load. The no-browser case is simulated by the test monitor throwing `ActivityNotFoundException`.

The official website endpoints were also requested separately. Beijing, Shanghai and Chongqing responded over HTTPS with a browser user agent. Tianjin's HTTPS endpoint failed certificate validation in this environment; its official HTTP endpoint responded successfully.

The five screenshots in `docs/screenshots` were captured from the running emulator and visually checked. The detail screens keep the website button below an independently scrollable information section.

The local build environment required a temporary drive mapping because Java could not resolve paths through the restricted Windows Documents directory. This was an environment workaround; no compiler or lint checks were disabled in the project.
