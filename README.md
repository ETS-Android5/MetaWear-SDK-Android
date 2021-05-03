# MetaWear Android API
The MetaWear Android API is a library for interacting with [MbientLab's sensor boards](https://mbientlab.com/sensors/) on an Android device.  A minimum of Android 7.0 (SDK 24) is required to use this library, however for the best results, it is recommended that users be on **Android 10 (SDK 29) or higher**.  

# Setup
## Adding Compile Dependency
To add the library to your project, first, update the repositories closure to include the MbientLab Ivy Repo in the project's  
``build.gradle`` file.

```gradle
repositories {
    ivy {
        url "https://mbientlab.com/releases/ivyrep"
        layout "gradle"
    }
}
```

Then, add the compile element to the dependencies closure in the module's ``build.gradle`` file.

```gradle
dependencies {
    compile 'com.mbientlab:metawear:3.8.2'
}
```

If you are using SDK v3.3 or newer, you will need to enable Java 8 feature support the module's ``build.gradle`` file.  See this 
[page](https://developer.android.com/studio/write/java8-support.html) in the Android Studio user guide.

## Declaring the Service
Once your project has synced with the updated Gradle files, declare the MetaWear Bluetooth LE service in the module's *AndroidManifest.xml* file.
```xml
<application
    android:allowBackup="true"
    android:icon="@drawable/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme" >

    <service android:name="com.mbientlab.metawear.android.BtleService" />
    <!-- Other application info below i.e. activity definitions -->
</application>
```

## Binding the Service
Lastly, bind the service in your application and retrain a reference to the service's LocalBinder class.  This can be done in any activity or fragment that needs access to a MetaWearBoard object.

```java
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;

import com.mbientlab.metawear.android.BtleService;

public class ExampleActivity extends Activity implements ServiceConnection {
    private BtleService.LocalBinder serviceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///< Bind the service when the activity is created
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ///< Typecast the binder to the service's LocalBinder class
        serviceBinder = (BtleService.LocalBinder) service;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) { }
}
```
