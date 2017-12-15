# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class org.apache.cayenne.configuration.server.MainCayenneServerModuleProvider
-keepclasseswithmembers public class org.apache.cayenne.log.Slf4jJdbcEventLogger
-keepclassmembers public class org.apache.cayenne.log.Slf4jJdbcEventLogger
-keep public class org.apache.cayenne.log.Slf4jJdbcEventLogger
-dontobfuscate

-dontwarn com.google.errorprone.annotations.*

-dontwarn org.apache.cayenne.access.types.*
-dontwarn org.apache.cayenne.configuration.server.JNDIDataSourceFactory
-dontwarn org.apache.cayenne.datasource.PoolAwareConnection
-dontwarn org.apache.cayenne.dba.oracle.*
-dontwarn org.apache.cayenne.tx.TransactionConnectionDecorator
-dontwarn org.apache.cayenne.cache.*
-dontwarn org.apache.cayenne.configuration.web.*
-dontwarn org.apache.cayenne.remote.*
-dontwarn org.apache.cayenne.remote.hessian.*
-dontwarn org.apache.cayenne.remote.hessian.service.*
-dontwarn org.apache.cayenne.remote.service.*
-dontwarn org.apache.cayenne.rop.*

-dontwarn org.apache.velocity.anakia.*
-dontwarn org.apache.velocity.convert.WebMacro
-dontwarn org.apache.velocity.runtime.log.*
-dontwarn org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader
-dontwarn org.apache.velocity.servlet.*
-dontwarn org.apache.velocity.texen.ant.*

-dontwarn org.apache.commons.collections.BeanMap
