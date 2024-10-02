# ARSCLib
## Android 二进制资源【读/写】的 java 库
该库基于 androidfw/ResourceTypes.h 的 AOSP 结构开发，完全替代 aapt/aapt2

#### 读取、写入、修改和创建————（Read, write, modify and create
* 资源表（resources.arsc）————Resource table (resources.arsc)
* 二进制 xml 文件（AndroidManifest.xml 和资源 xml）————Binary xml files (AndroidManifest.xml & resource xml)

#### 转换为 JSON 字符串（用于混淆的资源）————Convert from/to json string (for obfuscated resources)

* 将资源解码为可读的 JSON————Decodes resources to readable json
* 将 JSON 格式的资源 编码/构建为二进制资源————Encodes/Builds sources in json format to binary resources

#### 转换为 XML 字符串（用于未混淆的资源）————Convert from/to XML string (for un-obfuscated resources)

* 将资源解码为源代码————Decodes resources to source code
* 将源 XML 编码/构建为二进制资源————Encodes/Builds source XML to binary resources

##### _NOTES:_

_1- 将资源解码为 XML 要求所有源名称都应清晰且有效————Decoding resources to XML requires all source names should be un-obfuscated and valid_

_2- 假定此库的用户对 android 源 XML 语法有很好的了解，因此在编码/构建期间，它不会像 aapt/aapt2 那样频繁地验证或抛出 XML 语法错误。
例如，您可以在某些地方设置错误的值，而不会妨碍成功构建。
您可以在 AndroidManifest.xml 上设置， package="Wrong 😂 (package) name!" 然后您必须知道这些值是否被 android 设备接受。————

User of this lib is assumed to have good knowledge of android source XML syntax, thus
during encoding/building it does not validate or throw XML syntax errors as often as aapt/aapt2. For
example, you are allowed to set wrong values on some places and doesn't prevent from
successful building. On AndroidManifest.xml you can set  ``` package="Wrong 😂 (package) name!" ```
then you have to know such values are acceptable by android devices._



#### Example application
_Check this tool developed using this library_
[https://github.com/REAndroid/APKEditor](https://github.com/REAndroid/APKEditor)

#### Works on all java supported platforms (Android, Linux, Mac, Windows)


* Maven
 ```gradle
repositories {
    mavenCentral()
}
dependencies {
    implementation("io.github.reandroid:ARSCLib:+")
}
```
* Jar

```gradle
dependencies {
    implementation(files("$rootProject.projectDir/libs/ARSCLib.jar"))
}
```
#### Build jar

```ShellSession
git clone https://github.com/REAndroid/ARSCLib.git
cd ARSCLib
./gradlew jar
# Built jar will be placed ./build/libs/ARSCLib-x.x.x.jar
```

#### Examples
<details><summary> <code><b>Java example</b></code></summary>

```java   
import com.reandroid.apk.AndroidFrameworks;
import com.reandroid.apk.ApkModule;
import com.reandroid.apk.FrameworkApk;
import com.reandroid.archive.ByteInputSource;
import com.reandroid.arsc.chunk.PackageBlock;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock;
import com.reandroid.arsc.chunk.xml.ResXmlAttribute;
import com.reandroid.arsc.chunk.xml.ResXmlElement;
import com.reandroid.arsc.coder.EncodeResult;
import com.reandroid.arsc.coder.ValueCoder;
import com.reandroid.arsc.value.Entry;

import java.io.File;
import java.io.IOException;

public class ARSCLibExample {

    public static void createNewApk() throws IOException {

        ApkModule apkModule = new ApkModule();

        TableBlock tableBlock = new TableBlock();
        AndroidManifestBlock manifest = new AndroidManifestBlock();

        apkModule.setTableBlock(tableBlock);
        apkModule.setManifest(manifest);

        FrameworkApk framework = apkModule.initializeAndroidFramework(
                AndroidFrameworks.getLatest().getVersionCode());

        PackageBlock packageBlock = tableBlock.newPackage(0x7f, "com.example");

        Entry appIcon = packageBlock.getOrCreate("", "drawable", "ic_launcher");

        EncodeResult color = ValueCoder.encode("#006400");
        appIcon.setValueAsRaw(color.valueType, color.value);

        Entry appNameDefault = packageBlock.getOrCreate("", "string", "app_name");
        appNameDefault.setValueAsString("My Application");

        Entry appNameDe = packageBlock.getOrCreate("-de", "string", "app_name");
        appNameDe.setValueAsString("Meine Bewerbung");

        Entry appNameRu = packageBlock.getOrCreate("-ru-rRU", "string", "app_name");
        appNameRu.setValueAsString("Мое заявление");

        manifest.setPackageName("com.example");
        manifest.setVersionCode(100);
        manifest.setVersionName("1.0.0");
        manifest.setIconResourceId(appIcon.getResourceId());
        manifest.setCompileSdkVersion(framework.getVersionCode());
        manifest.setCompileSdkVersionCodename(framework.getVersionName());
        manifest.setPlatformBuildVersionCode(framework.getVersionCode());
        manifest.setPlatformBuildVersionName(framework.getVersionName());

        manifest.addUsesPermission("android.permission.INTERNET");
        manifest.addUsesPermission("android.permission.READ_EXTERNAL_STORAGE");

        //all appName entries created above have the same resource ids
        manifest.setApplicationLabel(appNameDefault.getResourceId());

        ResXmlElement mainActivity = manifest.getOrCreateMainActivity("android.app.Activity");
        ResXmlAttribute labelAttribute = mainActivity
                .getOrCreateAndroidAttribute(AndroidManifestBlock.NAME_label, AndroidManifestBlock.ID_label);
        labelAttribute.setValueAsString("Hello World");

        //Android os requires at least one dex file on base apk
        ByteInputSource dummyDex = new ByteInputSource(new byte[0], "classes.dex");
        apkModule.add(dummyDex);

        File outFile = new File("test_out.apk");
        apkModule.writeApk(outFile);
        //Sign and install
    }
}
```
</details>

