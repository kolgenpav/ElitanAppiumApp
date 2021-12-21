package ua.com.elitan.util;

import io.appium.java_client.remote.MobileCapabilityType;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

public class ElitanUtils {

    public static File getAppFile(String appName) {
        File classpathRoot = new File(System.getProperty("user.dir"));
        File appDir = new File(classpathRoot, "apps");
        File app = null;
        try {
            app = new File(appDir.getCanonicalPath(), appName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return app;
    }

    /**
     * Set common Desired Capabilities.
     *
     * @param isRealDevice if test run on real device
     * @return Desired Capabilities instance with common properties settled.
     */
    public static DesiredCapabilities getCommonDesiredCapabilities(boolean isRealDevice) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        if (isRealDevice) {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "device");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "9.0");
        } else {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android10Phone");
            capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.0");
        }
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");

        return capabilities;
    }
}
