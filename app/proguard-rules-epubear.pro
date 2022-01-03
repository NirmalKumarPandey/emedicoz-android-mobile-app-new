-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep class **.R$*
-keepclasseswithmembernames class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
    private void init(android.content.Context);
}
-keepclassmembers class com.epubear.EpubearSdk {
    private java.lang.String CJK_FONT_REGULAR;
    private java.lang.String LATIN_FONT_REGULAR;
    private java.lang.String LATIN_FONT_ITALIC;
    private java.lang.String LATIN_FONT_BOLD;
    private java.lang.String HEBREW_FONT_REGULAR;
    private long mContextPtr;
    private long mControllerPtr;
    private int mDpi;
    private int mVersionCode;
    private java.lang.String mDataPath;
    private void documentReady();
    private void anchorClicked(java.lang.String);
    private void chapterReady(int, float);
    private void invalidateRequest();
    private void onPageCount();
    private void addToCItem(java.lang.String, java.lang.String);
    private void initDisplay();
    private long getMemoryUsed();
}
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
}

-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**
-dontnote android.support.v4.**
-dontnote android.support.v7.**
-dontwarn okio.**