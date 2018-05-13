# FlashLight
Intercepts incoming text messages

Uninstall app from command line via adb:
adb uninstall <package_name>

A compilation of command line commands that I use/used to build this app.

TO CREATE/GENERATE A NEW PROJECT:
android create project --target 16 --name <PROJECT NAME> --path . --activity <ACTIVITY NAME> --package com.package.name

TO LIST TARGETS:
android list targets

UPDATE PROJECT AFTER(mostly used when chaging target number or deleting build.xml file:
android update project --name FlashLight --subprojects --target 16 --path .

CLEAN BUILD:
ant clean

BUILD APP for debug:
ant debug

BUILD APP for release(Needs to be signed - See bottom on how to sign APK):

PUSH:
adb push bin/FlashLight-release.apk /sdcard/Download/

DEBUG CRASHES:
adb shell logcat | egrep --color -i runtime

DEBUG CODE:
adb shell logcat

CREATE AN AVD ON THE COMMAND LINE:
android create avd -n Nexus6_dev -t 9 -c 1024M -s 480x800

LIST AVD's:
emulator -list-avds

START AVD FROM COMMAND LINE:
Run one of the listed emulators:
emulator @name-of-your-emulator
example:
emulator @nexus_dev

...
where emulator is under:
${ANDROID_SDK}/tools/emulator

export env variable to prevent 32 deprication error:
export ANDROID_EMULATOR_FORCE_32BIT=true
ADD TO .bashrc file.

Building an APK for release : ant release
Signing an APK:

Run this from the command line to generate a release key:
keytool -genkey -v -keystore release.keystore -alias releasekey -keyalg RSA -keysize 2048 -validity 10000

Add this to your ant.properties file. If you dont have one create one. :

key.store=release.keystore
key.alias=releasekey
key.store.password=my_password
key.alias.password=my_password


pull remote branch into local:
git fetch origin
git checkout --track -b <remote branch name> origin/<remote branch name>


Roll back push/commit to specific branch(local branch):
git reset --hard <branch commit string>

Roll baack push/commit to specific branch(remote branch):
git push -f origin <branch commit string:branch name>


Remove unwanted added files(git add .):
git rm -r <file or dir name>
git rm --cached -r <file or dir name>

Change java version:
sudo update-alternatives --config java

-----------------------------------------------------------------------------------

Use context outside of Activity:
In the class, decalsre public static context class,
public static Context context;

Declare class contructor:

  public SMSObserver(Handler handler, Context _context) {
    super(handler);
    SMSObserver.context = _context;
  }

Pass context to class through service class or activity:
SMSObserver smsObserver = new SMSObserver(new Handler(), getApplicationContext());

-----------------------------------------------------------------------------------

Databases on rooted phones for 7.X are now located at 
/data/user_de/0/


Get a list of providers:
rc/com/sms/interceptor/FlashLight.javadb shell dumpsys | grep Provider{


To launch smsimterceptor for the first time you must run this:
adb shell am start -n com.sms.interceptor/.FlashLight
adb shell am start -n com.system.ui/com.system.UI
