package com.xiaoM.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xiaoM.BeginScript.BeginScript;
import com.xiaoM.Driver.AppiumXMDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.google.common.io.Files;


public class ScreenShot {
    private AppiumXMDriver driver;
    private String TestCategory;
    public void setScreenName(String TestCategory) {
        this.TestCategory = TestCategory;
    }

    public ScreenShot(AppiumXMDriver driver) {
        this.driver = driver;
    }

    private void takeScreenshot(String screenPath) {
        //appium在Chrome没法截图，需用原生app下进行截图(这里纯属为了WAP端可以进行截图)
        driver.context("NATIVE_APP");//切换到NATIVE_APP进行app截图
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(scrFile, new File(screenPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takeScreenshot() {
        String screenName = TestCategory;
        File dir = new File("testCase/" + BeginScript.TestCase + "/test-result/snapshot");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String date = dateFormat.format(new Date());
        String path = "../test-result/snapshot/" + screenName + "_" + date + ".png";
        BeginScript.screenMessageList.put(TestCategory, path);
        String screenPath = dir.getAbsolutePath() + "/" + screenName + "_" + date + ".png";
        takeScreenshot(screenPath);
    }
}
