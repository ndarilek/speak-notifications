# Speak Notifications

This Android app speaks incoming notifications. It requires notification access, and prompts for said access to be available if it is not.

I threw this hack together over a weekend to replace functionality that appears to have been lost from TalkBack when Android migrated to its new notification display. Unfortunately, many notifications no longer generate `TYPE_NOTIFICATION_STATE_CHANGED` `AccessibilityEvents`. This is probably a good move in the long run, as the existing system of arbitrarily attaching a single string to notifications seems a bit hacky, but I wish a solution like this had already been in place so I didn't have to spend a weekend rushing to replace lost functionality. Because it isn't integrated into TalkBack, it can't honor TalkBack's active state and other settings. It does respect Do Not Disturb, though, and shouldn't speak during calls or other circumstances where the notification system is told to suppress audible alerts.

This isn't meant to be highly configurable, and if you need more or less verbose notification speech, fork and modify. If your changes are universally applicable and don't significantly increase the maintenance burden, I'll take them as pull requests.

This is my first Kotlin app. Please excuse any non-idiomatic code. I'll tweak it over time as I learn new things.

To install:

`./gradlew installDebug`

Then click the _Speak Notifications_ item in your apps menu. This will prompt you to enable notification access, as well as start the helper automatically on boot.
