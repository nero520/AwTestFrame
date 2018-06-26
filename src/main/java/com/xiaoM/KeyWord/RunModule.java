package com.xiaoM.KeyWord;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.xiaoM.BeginScript.BeginScript;
import com.xiaoM.Driver.AppiumXMDriver;
import com.xiaoM.Utils.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RunModule {
    private AppiumXMDriver driver;
    private String TestCategory;
    private Map<String, Object> returnMap;
    private ExtentTest extentTest;
    private String DeviceName;

    public RunModule(AppiumXMDriver driver, String TestCategory, Map<String, Object> returnMap, ExtentTest extentTest,String DeviceName) {
        this.driver = driver;
        this.TestCategory = TestCategory;
        this.returnMap = returnMap;
        this.extentTest = extentTest;
        this.DeviceName = DeviceName;
    }

    public Object moduleMethod(Location location2) throws Exception {
        String moudlePath = BeginScript.ProjectPath + "/testCase/" + BeginScript.TestCase + "/module.xlsx";
        String sheetName = location2.getValue();
        InputStream is = new FileInputStream(moudlePath);
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        workbook.close();
        StringBuilder sb = null;
        String[][] moduleStep = IOMananger.readExcelDataXlsx(workbook, sheetName);
        int b = 0;
        try {
            for (int a = 1; a < moduleStep.length; a++) {
                b=a;
                List<String> parameteres = new ArrayList<>(Arrays.asList(moduleStep[a]));
                Location location = new Location();
                location.setLocation(parameteres);
                sb = new StringBuilder();
                if (location.getIsRun().equals("YES")) {
                    String Step = sheetName + "." + location.getStep();
                    String Description = location.getDescription();
                    String Action = location.getAction();
                    String Value = location.getValue();
                    String Parameter = location.getParameter();
                    if (Parameter.contains("${")) {
                        Parameter = Match.replaceKeys(returnMap, Parameter);
                        location.setParameter(Parameter);
                    }
                    if (Value.contains("${")) {
                        Value = Match.replaceKeys(returnMap, Value);
                        location.setValue(Value);
                    }
                    sb.append("[模块]: " + location2.getDescription() + "\r\n");
                    sb.append("[步骤]: " + Step + "\r\n");
                    sb.append("[步骤描述]: " + Description + "\r\n");
                    sb.append("[关键字]:" + Action + "\r\n");
                    sb.append("[属性值]:" + Value + "\r\n");
                    sb.append("[参数]：" + Parameter + "\r\n");
                    ElementAction elementAction = new ElementAction(driver, TestCategory, returnMap, extentTest,DeviceName);
                    Object result = elementAction.action(location);
                    sb.append("[返回值]：" + result);
                    returnMap.put(Step, result);
                    if(result.toString().toLowerCase().equals("false")){
                        extentTest.log(Status.FAIL, "<pre>" + sb.toString() + "</pre>");
                    }else if(Action.toLowerCase().contains("check")) {
                        extentTest.log(Status.PASS, "<pre>" + sb.toString() + "</pre>");
                    }else {
                        extentTest.log(Status.INFO, "<pre>" + sb.toString() + "</pre>");
                    }
                }
            }
        } catch (Exception e) {
            ScreenShot screenShot = new ScreenShot(driver);
            screenShot.setScreenName(TestCategory);
            screenShot.takeScreenshot();
            sb.append("[异常截图如下]：");
            extentTest.fail("<pre>" + sb.toString() + "</pre>", MediaEntityBuilder.createScreenCaptureFromPath(BeginScript.screenMessageList.get(TestCategory)).build());
            extentTest.error(e);
            FailStep.dealWithMoubleFailStep(b, moduleStep, extentTest,location2);
            throw e;
        }
        return true;
    }
}
