<!-- JITPACK BADGES:START -->
[![JitPack Latest](https://jitpack.io/v/BlueCodeSystems/Opensrp-client-native-form-new.svg)](https://jitpack.io/#BlueCodeSystems/Opensrp-client-native-form-new)
[![Build for latest tag (v3.1.4)](https://jitpack.io/v/BlueCodeSystems/Opensrp-client-native-form-new/v3.1.4.svg)](https://jitpack.io/#BlueCodeSystems/Opensrp-client-native-form-new/v3.1.4)
[![master-SNAPSHOT](https://jitpack.io/v/BlueCodeSystems/Opensrp-client-native-form-new/master-SNAPSHOT.svg)](https://jitpack.io/#BlueCodeSystems/Opensrp-client-native-form-new/master-SNAPSHOT)
<!-- JITPACK BADGES:END -->

# opensrp-client-native-form-new
An Android library for building JSON-driven OpenSRP forms with wizard workflows, validation rules, and multilingual support.

## Project Status
- Gradle wrapper 8.7 with Android Gradle Plugin 8.6.0 (`build.gradle`).
- Built and tested against JDK 17 as configured in `gradle.properties`.
- Kotlin plugin is not applied; modules compile Java 8 sources via `android.compileOptions`.
- No CI workflows are checked in—run Gradle locally or integrate with your preferred CI.
- Current branch: `main` (checked out on September 26, 2025); latest tag: `v3.1.4`.

## Features
- Define multi-step forms in JSON without hand-crafted layouts.
- Built-in validation for required, regex, numeric, and custom rule logic.
- Declarative constraints for visibility, skip logic, calculations, and repeating groups.
- Rich widget set: text, image capture, barcode/QR, trees, multiselect, OptiBP, and more.
- OpenSRP/OpenMRS metadata hooks plus optional translations and external rules integration.

## Requirements
- JDK 17.
- Android Gradle Plugin 8.6.0 and Gradle 8.7 (via `gradlew`).
- Kotlin: not required (pure Java modules targeting source/target compatibility 1.8).
- `compileSdk` 35, `targetSdk` 35, `minSdk` 28, and build tools 35.0.0 (see `android-json-form-wizard/build.gradle`).

## Install
Add the library from Maven Central (replace `<version>` with a published release from this repository).

<details>
<summary>Groovy DSL</summary>

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.bluecodesystems:opensrp-client-native-form-new:<version>' // see Releases
}
```
</details>

<details>
<summary>Kotlin DSL</summary>

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.bluecodesystems:opensrp-client-native-form-new:<version>") // see Releases
}
```
</details>

## Initialize
No global initialization is required for basic form rendering. When you manage form metadata in a repository or need runtime translation, configure the optional singleton:

```java
public class NativeFormsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NativeFormLibrary library = NativeFormLibrary.getInstance();
        library.setClientFormDao(clientFormRepository); // implements ClientFormContract.Dao
        library.setPerformFormTranslation(true);
    }
}
```

## Usage examples
### Launch a form from assets
```java
JSONObject formJson = FileSourceFactoryHelper.getFileSource("")
        .getFormFromFile(context, "child_enrollment");
if (formJson != null) {
    Intent intent = new Intent(context, JsonWizardFormActivity.class);
    intent.putExtra("json", formJson.toString());

    Form form = new Form();
    form.setWizard(true);
    form.setName("Child enrollment");
    form.setNextLabel(getString(R.string.next));
    form.setPreviousLabel(getString(R.string.previous));
    intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

    startActivityForResult(intent, REQUEST_CODE);
}
```

### Prefill field values before launch
```java
JSONObject stepOne = formJson.getJSONObject("step1");
JSONArray fields = stepOne.getJSONArray("fields");
for (int i = 0; i < fields.length(); i++) {
    JSONObject field = fields.getJSONObject(i);
    if ("ZEIR_ID".equals(field.optString("key"))) {
        field.put("value", entityId);
        break;
    }
}
```

### Capture the submitted payload
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
        String json = data.getStringExtra("json");
        if (json != null) {
            JSONObject submission = new JSONObject(json);
            // Persist or sync the submission payload here.
        }
    }
}
```

## Sample app
The `sample/` module exercises the core widgets and rules. Install it on a connected device or emulator with:

```bash
./gradlew :sample:installDebug
```

You can also open the project in Android Studio and run the `sample` configuration directly.

## Build & test
```bash
./gradlew clean assemble
./gradlew test
```

## Releases
See the repository Releases page for published versions, change notes, and migration guidance.

## Contributing
Issues and pull requests are welcome. Please:
- Build with the toolchain versions listed above before submitting changes.
- Include unit tests (`./gradlew test`) or explain skipped coverage in the PR.
- Align with OpenSRP coding conventions and reference any related Jira tickets when available.

## License
Apache License 2.0 – see `LICENSE` for full text.
