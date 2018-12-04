#!/bin/bash

find .. -name Constants.cs -exec sed -i -e 's/androidkey/'"$ANDROID_APPCENTER_KEY"'/g' {} \;

if [[ "$APPCENTER_BRANCH" != "master" ]];
then
    find . -name AndroidManifest.xml -exec sed -i -e 's/com.microsoft.workbench/com.microsoft.workbenchDEV/g' {} \;
fi