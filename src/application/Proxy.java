/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.IOException;

/**
 *
 * @author avitoli2
 */
public class Proxy {
    
    private static boolean proxyEnabled;
    
    public static void setProxyStatus(boolean status) {
        proxyEnabled = status;
    }
    
    public static boolean getProxyStatus(){
        return proxyEnabled;
    }
    
    // Parbauda proxy servera statusu, no windows registra
    public static boolean getProxyRealStatus() {
        return WindowsRegistry.readRegistry("HKEY_CURRENT_USER\\Software\\"
                    + "Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                    "ProxyEnable")
                .equals("0x1");
    }
    
    public static void disableProxy() throws IOException {
        String cmd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft"
            + "\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable "
            + "/t REG_DWORD /d 0 /f";
        CMD.execCmd(cmd);
    }
    
    public static void enableProxy() throws IOException {
        String cmd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft"
                + "\\Windows\\CurrentVersion\\Internet Settings\" "
                + "/v ProxyEnable /t REG_DWORD /d 1 /f";
        CMD.execCmd(cmd);
    }
}
