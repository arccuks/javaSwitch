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
    
    public static void setProxyEnabled(boolean status) {
        proxyEnabled = status;
    }
    
    public static boolean isProxyEnabled(){
        return proxyEnabled;
    }
    
    // Parbauda proxy servera statusu, no windows registra
    public static boolean getProxyRealStatus() {
        return WindowsRegistry.readRegistry("HKEY_CURRENT_USER\\Software\\"
                    + "Microsoft\\Windows\\CurrentVersion\\Internet Settings",
                    "ProxyEnable")
                .equals("0x1");
    }
    
    public static void enableProxy(boolean enable) throws IOException {
        String cmd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft"
                + "\\Windows\\CurrentVersion\\Internet Settings\" "
                + "/v ProxyEnable /t REG_DWORD /d " +(enable ? "1" : "0")+ " /f";
        CMD.execCmd(cmd);
    }
}
