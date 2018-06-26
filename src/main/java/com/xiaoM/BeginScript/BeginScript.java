package com.xiaoM.BeginScript;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.xiaoM.Utils.*;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Element;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

public class BeginScript {
    private Log log ;
    public static ExtentReports extent;
    public static XSSFWorkbook workbook;
    public static XSSFWorkbook DeviceConfig;
    public static Map<String, String[]> DataBaseConfig = new HashMap<>();
    /*public static Map<String, String[]> OcrConfig = new HashMap<>();*/
    public static String[][] RunCase;//执行测试case
    public static Map<String, String> screenMessageList = new HashMap<>();
    public static Map<String, String> logList = new HashMap<>();
    public static Map<String, String> RmPicture = new HashMap<>();
    public static List<String> deviceList = new ArrayList<>();
    public static String Log_Level;
    public static String DeviceType;//设备类型
    public static String ResetApp;//是否重置应用
    public static String AppName;//Android APP的文件名
    public static String Resource_Monitoring;
    public static String PackageName;//Android APP的包名
    public static String Activity;//Android APP的Activity
    public static String bundleId;//IOS应用的标识名
    public static String ProjectPath;//工程路径
    public static String TestCase;//测试用例所在的表
    public static String CasePath;

    @BeforeSuite
    public void init(){
        try {
            //读取配置文件
            Element config = XmlUtils.readConfigXml();
            //获取操作系统
            String os = System.getProperty("os.name");
            if (os.contains("Mac")) {
                String appiumPath = "/usr/local/lib/node_modules/appium/build/lib/main.js";
                System.setProperty(AppiumServiceBuilder.APPIUM_PATH, appiumPath);
            }
            //获取测试设备
            if(config.elementText("Devices").contains(",")){
                String[] devices = config.elementText("Devices").split(",");
                deviceList.addAll(Arrays.asList(devices));
            }else{
                deviceList.add(config.elementText("Devices"));
            }
            Log_Level = config.elementText("Appium-Server");
            ProjectPath = new File(System.getProperty("user.dir")).getPath();// 工程根目录
            TestCase = XmlUtils.getTestCase();
            CasePath = ProjectPath + "/testCase/" + TestCase + "/main.xlsx";
            DeviceType = config.elementText("DeviceType");
            ResetApp = config.element("DeviceType_Android").elementText("NoRestApp");
            AppName = config.element("DeviceType_Android").elementText("AppName");
            Resource_Monitoring = config.element("DeviceType_Android").element("ResourceMonitoring").attribute("type").getValue();
            PackageName = config.element("DeviceType_Android").element("ResourceMonitoring").elementText("PackageName");
            Activity = config.element("DeviceType_Android").element("ResourceMonitoring").elementText("Activity");
            bundleId = config.element("DeviceType_IOS").elementText("bundleId");

            extent = ExtentManager.createHtmlReportInstance();//初始化测试报告
            workbook = IOMananger.getCaseExcel(CasePath);//获取测试用例Excel内容
            DeviceConfig = IOMananger.getDeviceExcel();//获取测试设备Excel内容
            RunCase = IOMananger.runTime("TestCases");//获取具体需要执行的测试用例
            DataBaseConfig = BaseConfig.getDataBaseConfigXlsx();//获取数据库配置
            /* OcrConfig = BaseConfig.getOcrConfigXlsx();//获取百度在线文字识别配置*/
            log = new Log(this.getClass());

        } catch (Exception e) {
            ExtentTest extentTest = extent.createTest("启动测试失败");
            extentTest.fail(e.getMessage());
            extent.flush();
            System.exit(0);
        }
    }

    @DataProvider(parallel = true)
    public Object[][] TestCases() {
        return RunCase;
    }

    @Test(dataProvider = "TestCases")
    public void runCase(String ID, String Type, String Description, String CaseName) throws Exception {
        String RunDevice = UseDevice.getDevice();//获取设备
        String TestCategory = ID + "_" + RunDevice + "_" + CaseName;
        log.info(TestCategory + " --- Start");
        ExtentTest extentTest = extent.createTest(TestCategory, Description);//测试报告增加一个节点
        extentTest.assignCategory(RunDevice);//根据设备来分类
        try {
            new Run().runCase(RunDevice, Type, CaseName, TestCategory, extentTest);
            log.info(TestCategory + " --- Pass");
        } catch (Exception e) {
            log.error(TestCategory + " --- Fail");
            throw e;
        }finally {
            UseDevice.addDevice(RunDevice);
        }
    }

    @AfterSuite
    public void afterSuite() {
        File file = new File("Temp/");
        IOMananger.deleteDirectory(file);
        extent.flush();
    }
}
