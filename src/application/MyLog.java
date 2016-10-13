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
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 *
 * @author avitoli2
 */
public final class MyLog {

        MyLog(JTextPane txtArea){
            MyLog.logTextArea = txtArea;
        }

        private static JTextPane logTextArea;   
        private static final DateFormat DATE_FORMAT = 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        public static void logEvent(String message) {
            logEvent(message, true);
        }
        
        public static void logEvent(String message, Boolean canAdd) {
            if(canAdd)appendToPane(logTextArea,"[" + getDate() + "] " + message + "\n", Color.BLACK);
        }
        
        public static void logSuccess(String message) {
            logSuccess(message, true);
        }
        
        public static void logSuccess(String message, Boolean canAdd) {
            if(canAdd)appendToPane(logTextArea,"[" + getDate() + "] " + message + "\n", Color.decode("#017701"));
        }

        public static void logError(String message) {
            appendToPane(logTextArea,"[" + getDate() + "] " + message + "\n", Color.RED);
        }
        
        public static void logError(Exception ex) {
            logError(ex, true);
        }
        
        public static void logError(Exception ex, Boolean canAdd) {
            if(canAdd) {
                appendToPane(logTextArea, "[" + getDate() + "] " + ex.getMessage() + "\n", Color.RED);
                for (StackTraceElement error : ex.getStackTrace()) {
                    appendToPane(logTextArea, "\t" + error.toString() + "\n", Color.RED);
                }
            }
        }

        private static String getDate() {
            return DATE_FORMAT.format(new Date());
        }

        public static void testLogEvent(){
            logEvent("Test");
        }
        
        private static void appendToPane(JTextPane tp, String msg, Color c)
        {
            StyledDocument doc = (StyledDocument)tp.getDocument();
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

            aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
            aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

            int len = tp.getDocument().getLength();

            try {
                doc.insertString(len, msg, aset);
            } catch (BadLocationException ex) {
                MyLog.logError(ex);
            }
        }
}
