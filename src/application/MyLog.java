/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

/**
 *
 * @author avitoli2
 */
public final class MyLog {

        MyLog(JTextArea txtArea){
            setLogTextArea(txtArea);
        }

        private JTextArea logTextArea;   
        private final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        public JTextArea getLogTextArea() {
            return logTextArea;
        }

        public void setLogTextArea(JTextArea logTextArea) {
            this.logTextArea = logTextArea;
        }

        public void logEvent(String message) {
            getLogTextArea().append("[" + getDate() + "] " + message + "\n");
        }

        public void logError(Exception ex) {
            getLogTextArea().append("[" + getDate() + "] " + ex.getMessage() + "\n");
            for (StackTraceElement error : ex.getStackTrace()) {
                getLogTextArea().append("\t" + error.toString() + "\n");
            }
        }

        private String getDate() {
            return dateFormat.format(new Date());
        }

        public void testLogEvent(){
            logEvent("Test");
        }
    }
