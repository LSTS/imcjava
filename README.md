imcjava
=======

Java bindings for IMC

[![Build Status](https://travis-ci.org/LSTS/imcjava.png)](https://travis-ci.org/LSTS/imcjava)

## Compilation
- ```./gradlew```, this will create the libimc and libimcsender to be used in Neptus (output on dist), same as running 
```./gradlew dist libsender``` 

- ```./gradlew coreTools```, this will create some core tools (output on dist/tools)

- ```./gradlew clean```, this clean all generated files, leaving only the source

## IMC
To update the IMC run from GitHub or from local folder (settings adjusted in ```settings.gradle```):

- ```./gradlew updateIMC```, this is the **preferred way**, change in the ```settings.gradle``` 
the following parameters:
  - ```gradle.ext.imcGitHubRepo = 'LSTS/imc'``` with the repository on the GitHub
  - ```gradle.ext.imcGitHubBranch = '9c3b296'``` with the commit hash

- ```./gradlew updateIMCFromFolder```, this is an alternative way for local generation, change in the
```settings.gradle``` the following parameters:
  - ```gradle.ext.imcFilePath = '../IMC'```  to point to a git repository of IMC locally
