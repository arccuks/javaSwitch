/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTable;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author avitoli2
 */
public class RDCTable extends Table {
    
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public void openConnection(JTable table) {
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if (row >= 0 && col >= 0) {

            String cmd = "cmd /c start mstsc \"" + getFilePath() + table.getValueAt(row, col) + "\"";
            // Laizu jauna threda, lai CMD nenobloketu pasu Applikaciju
            new Thread() {
                @Override
                public void run() {
                    try {
                        CMD.execCmd(cmd);
                        MyLog.logEvent(getFilePath() + table.getValueAt(row, col) + "; CLOSED");
//                                    execCmd("cmd /c start mstsc inner\\vzd.rdp");
//                                    execCmd("mstsc /v:10.219.4.218 /admin /f");
                    } catch (IOException ex) {
                        MyLog.logError(ex);
                    }
                }
            }.start();
            MyLog.logEvent(cmd, true);
        }  
    }
    
    public void setJTable(JTable table) {
        try {
            setRowCount(0);

            File f = new File(getFilePath()); 
            
            ArrayList<String[]> rdpFileNames = new ArrayList<>();

            crawl(f, rdpFileNames);

            if(getRowCount() == 0) {
                MyLog.logError("Norādītajā mapē nav neviena rdp faila");
                return;
            }

            Object[][] data = getDataFromArrayList(rdpFileNames, getRowCount(), true);

            table.setModel(getDefaultTableModel(data,getColumn(),false));
            MyLog.logSuccess("RDP faili ielādēti!\n\t" + getFilePath());
        } catch (Exception ex) 
        {
            MyLog.logError("Nevar atrast failus, neprecizi norādīta adrese!");
            MyLog.logError(ex, true);
        }
    }
    
    private void crawl(File f, ArrayList<String[]> rdpFileNames) {
//        System.out.println(FilenameUtils.getExtension(f.getAbsolutePath()));
        if (f.isDirectory()) {
        	File[] subFiles = f.listFiles();
            for (File subFile : subFiles) {
                crawl(subFile, rdpFileNames);
            }
        } else {
            if(FilenameUtils.getExtension(f.getAbsolutePath())
                    .toLowerCase()
                    .equals("rdp")) {
                String[] add = {f.getName()};
                rdpFileNames.add(add);
                rowCountInc();
            }
        }
    }
    
    
}
