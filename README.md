# T4 Launcher
[![CircleCI](https://circleci.com/gh/rsteube/t4-launcher.svg?style=svg)](https://circleci.com/gh/rsteube/t4-launcher)

Android Launcher simply consisting of a scrollable list of apps with [predictive text](https://en.wikipedia.org/wiki/Predictive_text) filtering using 4 Buttons.

![](fastlane/metadata/android/en-US/images/phoneScreenshots/sample.png)

_Example for input `[A_F][G_L][M_R][S_Z]`_
- short press launches app
- long press opens app settings
- back button removes last input
- home button clears input
- long press back button reloads list

_This is basically a rewrite of [minimalistic-launcher](https://github.com/Collinux/minimalist-launcher) in Kotlin with added functionality._
