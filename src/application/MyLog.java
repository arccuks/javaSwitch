/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author avitoli2
 */
public final class MyLog {

        MyLog(JTextPane txtArea){
            setLogTextArea(txtArea);
        }

        private static JTextPane logTextArea;   
        private static final DateFormat dateFormat = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        public static void setLogTextArea(JTextPane logTextArea) {
            MyLog.logTextArea = logTextArea;
        }

        public static void logEvent(String message) {
            appendToPane(logTextArea,"[" + getDate() + "] " + message + "\n", Color.BLACK);
        }

        public static void logError(String message) {
            appendToPane(logTextArea,"[" + getDate() + "] " + message + "\n", Color.RED);
        }
        
        public static void logError(Exception ex) {
            appendToPane(logTextArea, "[" + getDate() + "] " + ex.getMessage() + "\n", Color.RED);
            for (StackTraceElement error : ex.getStackTrace()) {
                appendToPane(logTextArea, "\t" + error.toString() + "\n", Color.RED);
            }
        }

        private static String getDate() {
            return dateFormat.format(new Date());
        }

        public static void testLogEvent(){
            logEvent("Test");
        }
        
        private static void appendToPane(JTextPane tp, String msg, Color c)
        {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

            int len = tp.getDocument().getLength();
            tp.setCaretPosition(len);
            tp.setCharacterAttributes(aset, false);
            tp.replaceSelection(msg);
        }
}
