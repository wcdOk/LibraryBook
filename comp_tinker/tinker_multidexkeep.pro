
#tinker multidex keep patterns:
-keep public class * implements com.tencent.tinker.entry.ApplicationLifeCycle {
    <init>(...);
    void onBaseContextAttached(android.content.Context);
}

-keep public class com.tencent.tinker.entry.ApplicationLifeCycle {
    *;
}

-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    <init>(...);
}

-keep public class * extends android.app.Application {
     <init>();
     void attachBaseContext(android.content.Context);
}

-keep class com.tencent.tinker.loader.TinkerTestAndroidNClassLoader {
    <init>(...);
}

#your dex.loader patterns here
-keep class tinker.sample.android.app.SampleApplication {
    <init>(...);
}

-keep class com.tencent.tinker.loader.** {
    <init>(...);
}

-keep class android.support.test.internal** { *; }
-keep class org.junit.** { *; }
-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application{
     public <fields>;
     public <methods>;
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference