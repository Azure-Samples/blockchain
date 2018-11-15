# Building 
[Index](Index.md)

This build uses the gradle wrapper and should build provided there is a 
1.8 JDK on the build path. You can check this running the version command as 
below. 

Details on the currently supported JVM versions for Corda are at, 
https://docs.corda.net/getting-set-up.html, though the purposes of building this 
library any stable 1.8 JDK should be sufficient.

```bash
./gradlew --version 

------------------------------------------------------------
Gradle 4.9
------------------------------------------------------------

Build time:   2018-07-16 08:14:03 UTC
Revision:     efcf8c1cf533b03c70f394f270f46a174c738efc

Kotlin DSL:   0.18.4
Kotlin:       1.2.41
Groovy:       2.4.12
Ant:          Apache Ant(TM) version 1.9.11 compiled on March 23 2018
JVM:          1.8.0_181 (Oracle Corporation 25.181-b13)
OS:           Mac OS X 10.12.6 x86_64
```

To build locally, without affecting the other components

```bash
./gradlew clean test jar
```

To update the dependencies locally 

```bash
./build.sh 
```
