package com.xiaoM.KeyWord.Appium;

import com.xiaoM.Driver.AppiumXMDriver;
import com.xiaoM.Element.LocationAppElement;
import com.xiaoM.Utils.BaiduOCR;
import com.xiaoM.Utils.Location;
import com.xiaoM.Utils.Log;
import com.xiaoM.Utils.Picture;
import org.openqa.selenium.WebElement;

public class GetModule {

    private Log log = new Log(this.getClass());
    private AppiumXMDriver driver;
    private String TestCategory;

    public GetModule(AppiumXMDriver driver, String TestCategory) {
        this.driver = driver;
        this.TestCategory = TestCategory;
    }

    /**
     * 获取控件
     * @param location
     * @return
     */
    public WebElement getElement(Location location) {
        log.info(TestCategory + "：获取控件 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        WebElement element;
        try {
            element = locationAppElement.waitForElement(location);
        } catch (Exception e) {
            throw e;
        }
        return element;
    }

    /**
     * 获取控件文本
     * @param location
     * @return
     */
    public String getElementText(Location location) {
        log.info(TestCategory + "：获取控件文本 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        String result;
        try {
            result = locationAppElement.waitForElement(location).getText();
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 获取控件尺寸
     * @param location
     * @return
     */
    public String getElementSize(Location location) {
        log.info(TestCategory + "：获取控件尺寸 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        String result;
        try {
            result = locationAppElement.waitForElement(location).getSize().toString();
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 获取控件图片
     * @param location
     * @return
     */
    public String getElementiPcture(Location location) throws Exception {
        log.info(TestCategory + "：获取控件图片 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        String result = null;
        try {
            WebElement element = locationAppElement.waitForElement(location);
            result = Picture.captureElement(driver, element);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 获取控件图片文本
     * @param location
     * @return
     */
    public String getElementiPctureText(Location location) throws Exception {
        log.info(TestCategory + "：获取控件图片文本 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        String result;
        try {
            WebElement element = locationAppElement.waitForElement(location);
            result = BaiduOCR.getPictureText(driver, element);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 获取控件属性
     * @param location
     * @return
     */
    public String getElementiAttribute(Location location){
        log.info(TestCategory + "：获取控件属性成功 [ " + location.getDescription() + " ]");
        LocationAppElement locationAppElement = new LocationAppElement(driver, TestCategory);
        String result;
        try {
            result = locationAppElement.waitForElement(location).getAttribute(location.getParameter());
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

}
