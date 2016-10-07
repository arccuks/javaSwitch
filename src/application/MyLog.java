/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *
 * @author avitoli2
 */
public final class MyLog {

        MyLog(JTextArea txtArea){
            setLogTextArea(txtArea);
        }

        private static JTextArea logTextArea;   
        private static final DateFormat dateFormat = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        public static void setLogTextArea(JTextArea logTextArea) {
            MyLog.logTextArea = logTextArea;
        }

        public static void logEvent(String message) {
            MyLog.logTextArea.append("[" + getDate() + "] " + message + "\n");
        }

        public static void logError(Exception ex) {
            MyLog.logTextArea.append("[" + getDate() + "] " + ex.getMessage() + "\n");
            for (StackTraceElement error : ex.getStackTrace()) {
                MyLog.logTextArea.append("\t" + error.toString() + "\n");
            }
        }

        private static String getDate() {
            return dateFormat.format(new Date());
        }

        public static void testLogEvent(){
            logEvent("Test");
        }
    }
