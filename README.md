# IntelliJ IDEA Community Edition [![official JetBrains project](http://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
These instructions will help you build IntelliJ IDEA Community Edition from source code, which is the basis for IntelliJ Platform development.
The following conventions will be used to refer to directories on your machine:
* `<USER_HOME>` is your home directory.
* `<IDEA_HOME>` is the root directory for the IntelliJ source code.

## Getting IntelliJ IDEA Community Edition Source Code
IntelliJ IDEA Community Edition source code is available from `github.com/JetBrains/intellij-community` by either cloning or
downloading a zip file (based on a branch) into `<IDEA_HOME>`. The default is the *master* branch. 

The master branch contains the source code which will be used to create the next major version of IntelliJ IDEA. The branch names
and build numbers for older releases of IntelliJ IDEA can be found on the page of
[Build Number Ranges](https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html).

These Git operations can also be done through the [IntelliJ IDEA user interface](https://www.jetbrains.com/help/idea/using-git-integration.html).

_**Speed Tip:**_ If the complete repository history isn't needed then using a shallow clone (`git clone --depth 1`) will save significant time.

_**On Windows:**_ Two git options are required to check out sources on Windows. Since it's a common source of Git issues on Windows anyway, those options could be set globally (execute those commands before cloning any of intellij-community/android repositories):

* `git config --global core.longpaths true`
* `git config --global core.autocrlf input`

IntelliJ IDEA Community Edition requires additional Android modules from separate Git repositories. To clone these repositories,
run one of the `getPlugins` scripts located in the `<IDEA_HOME>` directory. These scripts clone their respective *master* branches. Make sure you are inside the `<IDEA_HOME>` directory when running those scripts, so the modules get cloned inside the `<IDEA_HOME>` directory.
* `getPlugins.sh` for Linux or macOS.
* `getPlugins.bat` for Windows.

_**Note:**_ Always `git checkout` the `intellij-community` and `android` Git repositories to the same branches/tags. 

## Building IntelliJ Community Edition
Version 2020.1 or newer of IntelliJ IDEA Community Edition or IntelliJ IDEA Ultimate Edition is required to build and develop
for the IntelliJ Platform.

### Opening the IntelliJ Source Code for Build
Using IntelliJ IDEA **File | Open**, select the `<IDEA_HOME>` directory. 
* If IntelliJ IDEA displays an error about a missing or out of date required plugin (e.g. Kotlin),
  [enable, upgrade, or install that plugin](https://www.jetbrains.com/help/idea/managing-plugins.html) and restart IntelliJ IDEA.
* If IntelliJ IDEA displays an error about a Gradle configuration not found,
  [refresh the Gradle projects](https://www.jetbrains.com/help/idea/jetgradle-tool-window.html). 

### IntelliJ Build Configuration
1. Configure a JDK named "**corretto-11**", pointing to installation of JDK 11. It's recommended to use Amazon Corretto JDK, but other 
   distributions based on OpenJDK should work as well. You may [download it directly](https://www.jetbrains.com/help/idea/sdk.html#jdk-from-ide) 
   from Project Structure dialog.    
2. If the _Maven_ plugin is disabled, [add the path variable](https://www.jetbrains.com/help/idea/absolute-path-variables.html)
   "**MAVEN_REPOSITORY**" pointing to `<USER_HOME>/.m2/repository` directory.
3. _**Speed Tip:**_ If you have enough RAM on your computer,
   [configure the compiler settings](https://www.jetbrains.com/help/idea/specifying-compilation-settings.html)
   to enable the "Compile independent modules in parallel" option. Also, increase build process heap size:
   * if you use IntelliJ IDEA 2020.3 or newer, set "User-local build process heap size" to 2048. 
   * if you use IntelliJ IDEA 2020.2 or older, copy value from "Shared build process VM options" to "User-local build process VM options" and add `-Xmx2G` to it.
    
    These changes will greatly reduce compilation time.

### Building the IntelliJ Application Source Code
To build IntelliJ IDEA Community Edition from source, choose **Build | Build Project** from the main menu.

To build installation packages, run the `installers.cmd` command in `<IDEA_HOME>` directory. `installers.cmd` will work on both Windows and Unix systems.

Options to build installers are passed as system properties to `installers.cmd` command.
You may find the list of available properties in [BuildOptions.groovy](platform/build-scripts/groovy/org/jetbrains/intellij/build/BuildOptions.groovy)

Examples (`./` should be added only for Linux/macOS):
 * Build installers only for current operating system: `./installers.cmd -Dintellij.build.target.os=current`
 * Build source code _incrementally_ (do not build what was already built before): `./installers.cmd -Dintellij.build.incremental.compilation=true`

`installers.cmd` is used just to run [OpenSourceCommunityInstallersBuildTarget](build/scripts/OpenSourceCommunityInstallersBuildTarget.kt) from the command line.
You may call it directly from IDEA, see run configuration `Build IDEA Community Installers (current OS)` for an example.

## Running IntelliJ IDEA
To run the IntelliJ IDEA built from source, choose **Run | Run** from the main menu. This will use the preconfigured run configuration "**IDEA**".

To run tests on the build, apply these setting to the **Run | Edit Configurations... | Templates | JUnit** configuration tab:
  * Working dir: `<IDEA_HOME>/bin`
  * VM options: 
    * `-ea` 
    * `-Didea.config.path=../test-config`
    * `-Didea.system.path=../test-system`
 
You can find other helpful information at [https://www.jetbrains.com/opensource/idea](https://www.jetbrains.com/opensource/idea).
The "Contribute Code" section of that site describes how you can contribute to IntelliJ IDEA.

## Running IntelliJ IDEA on CI/CD environment

To run tests outside of IntelliJ IDEA, run the `tests.cmd` command in `<IDEA_HOME>` directory. `tests.cmd` will work on both Windows and Unix systems.

Options to run tests are passed as system properties to `tests.cmd` command.
You may find the list of available properties in [TestingOptions.groovy](platform/build-scripts/groovy/org/jetbrains/intellij/build/TestingOptions.groovy)

Examples (`./` should be added only for Linux/macOS):
* Build source code _incrementally_ (do not build what was already built before): `./tests.cmd -Dintellij.build.incremental.compilation=true`
* Run a specific test: `./tests.cmd -Dintellij.build.test.patterns=com.intellij.util.ArrayUtilTest`

`tests.cmd` is used just to run [CommunityRunTestsBuildTarget](build/scripts/CommunityRunTestsBuildTarget.kt) from the command line.
You may call it directly from IDEA, see run configuration `tests in community` for an example.
