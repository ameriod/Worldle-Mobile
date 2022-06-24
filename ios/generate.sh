#!/bin/sh

cd ../
./gradlew assembleCommonDebugXCFramework
cd ios

tuist generate --open
