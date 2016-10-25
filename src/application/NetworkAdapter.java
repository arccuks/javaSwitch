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
public class NetworkAdapter {
    private boolean networkAdapterEnabled;
    private String networkAdapterName;
    private String networkAdapterIndex;

    public String getNetworkAdapterIndex() {
        return networkAdapterIndex;
    }

    public void setNetworkAdapterIndex(String networkAdapterIndex) {
        this.networkAdapterIndex = networkAdapterIndex;
    }

    public String getNetworkAdapterName() {
        return networkAdapterName;
    }

    public void setNetworkAdapterName(String networkAdapterName) {
        this.networkAdapterName = networkAdapterName;
    }

    public boolean isAdapterEnabled() {
        return networkAdapterEnabled;
    }

    public void setAdapterEnabled(boolean adapterEnabled) {
        this.networkAdapterEnabled = adapterEnabled;
    }
    
    public void enableAdapter(boolean enable) throws IOException {
        String cmd = "cmd /c start wmic path win32_networkadapter "
            + "where index=" + networkAdapterIndex
            + " call " + (enable ? "enable" : "disable");
        CMD.execCmd(cmd);
    }    
    
    //GLOBAL STATIC
    
    public static String[] getAdapterStatus() throws IOException {
        String cmd = "wmic nic get name, index, "
                    + "NetConnectionID, netenabled";
        return CMD.execCmd(cmd).split("\n");
    }
    
    public static void openInternetOptions() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String cmd = "cmd /c start inetcpl.cpl";
                    CMD.execCmd(cmd);
                    MyLog.logEvent(cmd, true);
                } catch (IOException ex) {
                    MyLog.logError(ex);
                }
            }
        }.start();
    }
}
