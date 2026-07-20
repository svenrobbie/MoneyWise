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
