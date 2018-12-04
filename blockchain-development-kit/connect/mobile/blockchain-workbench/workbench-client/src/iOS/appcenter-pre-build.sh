#!/bin/bash

find .. -name Constants.cs -exec sed -i -e 's/ioskey/'"$IOS_APPCENTER_KEY"'/g' {} \;
find .. -name Info.plist -exec sed -i -e 's/APPSECRET/'"$IOS_APPCENTER_KEY"'/g' {} \;

if [[ "$APPCENTER_BRANCH" != "master" ]];
then
    find . -name Info.plist -exec sed -i -e 's/com.microsoft.workbench/com.microsoft.workbench.DEV/g' {} \;
fi