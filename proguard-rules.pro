# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.moneywise.**$$serializer { *; }
-keepclassmembers class com.moneywise.** { *** Companion; }
-keepclasseswithmembers class com.moneywise.** { kotlinx.serialization.KSerializer serializer(...); }

# Jetpack Compose
-keep class androidx.compose.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Room / WorkManager - must keep constructors for reflection-based instantiation
-keep class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDao { *; }
-keep class * extends androidx.work.Worker {
    public <init>(...);
}
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep class * extends androidx.work.ListenableWorker$Factory { *; }
-keep class androidx.work.WorkManagerInitializer {
    public <init>();
}
-keep class androidx.work.impl.WorkDatabase_Impl {
    public <init>();
}
-keep class androidx.work.impl.** {
    public <init>(...);
}

# App data classes (kotlinx.serialization)
-keep class com.moneywise.data.** { *; }
-keep class com.moneywise.worker.** { *; }

# General Android
-dontoptimize
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
