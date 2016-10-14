/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author avitoli2
 */
public class AppSettings {
        
    private String propFilePath = "";
    private final String propFileName = "config.properties";
    
    private Application myApp = null;
    
    public AppSettings(Application app){
        try {
            myApp = app;
            propFilePath = new File(".").getCanonicalPath() + "\\";
        } catch (IOException ex) {
            MyLog.logError("Nevarēju noteikt config faila ceļu!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());           
        }
    }
    
    private void createConfigFile() {
        try {
            MyLog.logEvent("Izveidojam jaunu config failu:\n"
                    + propFilePath+propFileName);
            new File(propFilePath + propFileName).createNewFile();
            MyLog.logSuccess("Config fails izveidots!");
        } catch (IOException ex) {
            MyLog.logError("Nevar izveidot failu!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());
        }
    }
    
    public void getPropValues()  {
        InputStream inputStream = null;
        try {
            MyLog.logEvent("Meklējam config failu...");
            inputStream = new FileInputStream(propFilePath + propFileName);
            MyLog.logSuccess("Config fails atrasts!\n\t" + propFilePath + propFileName);
            
            Properties prop = new Properties();

            MyLog.logEvent("Sākam config faila ielādi...");
            prop.load(inputStream);
            MyLog.logSuccess("Config fails ielādēts!");

            MyLog.logEvent("Sākam vērtību uzstādīšanu no faila..");
            myApp.setInnerAdapterTextField(prop.getProperty("innerAdapterName"));
            myApp.setOuterAdapterTextField(prop.getProperty("outerAdapterName"));
            myApp.setLogErrorStackTrace(prop.getProperty("logErrorStackTrace"));
            myApp.setLogCommands(prop.getProperty("logCommands"));
            myApp.setLogColor(prop.getProperty("logColor"));
            myApp.setLogAdapter(prop.getProperty("logAdapter"));
            myApp.setAutoProxy(prop.getProperty("autoProxy"));
            myApp.setInnerConPath(prop.getProperty("innerConPath"));
            myApp.setOuterConPath(prop.getProperty("outerConPath"));
            MyLog.logSuccess("Vērtību uzstādīšanu pabeigta!");
        } catch (FileNotFoundException ex) {
            MyLog.logError("Config fails nav atrasts!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());
            createConfigFile();
        } catch (Exception ex) {
            MyLog.logError("Nevarēja ielādēt config failu!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    MyLog.logError("Nevarēja aizvērt config failu!");
                    MyLog.logError(ex, myApp.canLogErrorStackTrace());
                }
            }
        }
    }
    
    public void setPropValues() {
        MyLog.logEvent("Config faila saglabāšana ...");
        
        OutputStream output = null;
        try {
            Properties prop = new Properties();
            output = new FileOutputStream(propFilePath + propFileName);

            MyLog.logEvent("Sākam vērtību ievietošanu config failā..");
            
            prop.setProperty("innerAdapterName", (
                    (myApp.getInnerAdapterTextField().isEmpty()) ? 
                            myApp.innerAdapterConnection :
                            myApp.getInnerAdapterTextField()
                    ));
            prop.setProperty("outerAdapterName", (
                    (myApp.getOuterAdapterTextField().isEmpty()) ? 
                            myApp.outerAdapterConnection :
                            myApp.getOuterAdapterTextField()
                    ));
            prop.setProperty("innerConPath", (
                    (myApp.getInnerConPath().isEmpty()) ? 
                            "" :
                            myApp.getInnerConPath()
                    ));
            prop.setProperty("outerConPath", (
                    (myApp.getOuterConPath().isEmpty()) ? 
                            "" :
                            myApp.getOuterConPath()
                    ));
            
            prop.setProperty("logErrorStackTrace",String.valueOf(
                    myApp.canLogErrorStackTrace()));
            prop.setProperty("logCommands",String.valueOf(
                    myApp.canLogCommands()));
            prop.setProperty("logColor",String.valueOf(
                    myApp.canLogColor()));
            prop.setProperty("logAdapter",String.valueOf(
                    myApp.canLogAdapter()));
            prop.setProperty("autoProxy",String.valueOf(
                    myApp.canLogAutoProxy()));
            
            prop.store(output, null);
            MyLog.logSuccess("Config fails veiksmīgi saglabāts!");
        } catch (FileNotFoundException ex) {
            MyLog.logError("Nevarēja atrast failu!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());
        } catch (IOException ex) {
            MyLog.logError("Radās problēmas saglabājot failu!");
            MyLog.logError(ex, myApp.canLogErrorStackTrace());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    MyLog.logError("Nevarēja aizvērt config failu!");
                    MyLog.logError(ex, myApp.canLogErrorStackTrace());
                }
            }
        }
    }
}
