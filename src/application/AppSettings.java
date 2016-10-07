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
    
    private MyLog myLog = Application.myLog;
    private Application myApp = null;
    
    public AppSettings(Application app){
        try {
            myApp = app;
            propFilePath = new File(".").getCanonicalPath() + "\\";
        } catch (IOException ex) {
            myLog.logEvent("Nevarēju noteikt config faila ceļu!");
            if (myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);            
        }
    }
    
    private void createConfigFile() {
        try {
            myLog.logEvent("Izveidojam jaunu config failu:\n"
                    + propFilePath+propFileName);
            new File(propFilePath + propFileName).createNewFile();
            myLog.logEvent("Config fails izveidots!");
        } catch (IOException ex) {
            myLog.logEvent("Nevar izveidot failu!");
            if (myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);
        }
    }
    
    public void getPropValues()  {
        InputStream inputStream = null;
        try {
            myLog.logEvent("Meklējam config failu...");
            inputStream = new FileInputStream(propFilePath + propFileName);
            myLog.logEvent("Config fails atrasts!");
            
            Properties prop = new Properties();

            myLog.logEvent("Sākam config faila ielādi...");
            prop.load(inputStream);
            myLog.logEvent("Config fails ielādēts!");

            myLog.logEvent("Sākam vērtību izgūšanu no faila..");
            myApp.setInnerAdapterTextField(prop.getProperty("innerAdapterName"));
            myApp.setOuterAdapterTextField(prop.getProperty("outerAdapterName"));
            myApp.setLogErrorStackTrace(prop.getProperty("logErrorStackTrace"));
            myApp.setLogCommands(prop.getProperty("logCommands"));
            myApp.setLogColor(prop.getProperty("logColor"));
            myApp.setLogAdapter(prop.getProperty("logAdapter"));
            myLog.logEvent("Vērtību izgūšanu no faila pabeigta!");
        } catch (FileNotFoundException ex) {
            myLog.logEvent("Config fails nav atrasts!");
            if (myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);
            createConfigFile();
        } catch (Exception ex) {
            myLog.logEvent("Nevarēja ielādēt config failu!");
            if (myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    myLog.logEvent("Nevarēja aizvērt config failu!");
                    if (myApp.getLogStackTraceCheckBox().isSelected())
                        myLog.logError(ex);
                }
            }
        }
    }
    
    public void setPropValues() {
        OutputStream output = null;
        try {
            Properties prop = new Properties();
            output = new FileOutputStream(propFilePath + propFileName);

            myLog.logEvent("Sākam vērtību ievietošanu config failā..");
            
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
            prop.setProperty("logErrorStackTrace",String.valueOf(
                    myApp.isLogErrorStackTrace()));
            prop.setProperty("logCommands",String.valueOf(
                    myApp.isLogCommands()));
            prop.setProperty("logColor",String.valueOf(
                    myApp.isLogColor()));
            prop.setProperty("logAdapter",String.valueOf(
                    myApp.isLogAdapter()));
            
            prop.store(output, null);
            myLog.logEvent("Config fails veiksmīgi saglabāts!");
        } catch (FileNotFoundException ex) {
            myLog.logEvent("Nevarēja atrast failu!");
            if(myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);
        } catch (IOException ex) {
            myLog.logEvent("Radās problēmas saglabājot failu!");
            if(myApp.getLogStackTraceCheckBox().isSelected())
                myLog.logError(ex);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    myLog.logEvent("Nevarēja aizvērt config failu!");
                    if(myApp.getLogStackTraceCheckBox().isSelected())
                        myLog.logError(ex);
                }
            }
        }
    }
}
