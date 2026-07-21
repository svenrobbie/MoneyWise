-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class com.moneywise.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.moneywise.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.moneywise.data.**$$serializer { *; }
-keepclassmembers class com.moneywise.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.moneywise.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# WorkManager / Room - WorkManager uses reflection to instantiate WorkDatabase_Impl
-keep class androidx.work.impl.WorkDatabase_Impl {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep class * extends androidx.work.ListenableWorker$Factory { *; }
-keep class androidx.work.impl.** {
    public <init>(...);
}

# General Android
-dontoptimize
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
