/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author avitoli2
 */
public class Application extends javax.swing.JFrame {
    
    private final AppSettings appSettings;
    private int fileCount = 0;
    
    private boolean innerAdapterEnabled = false;
    private boolean outerAdapterEnabled = false;
    private boolean proxyEnabled = false;
    
    protected static MyLog myLog;
    protected String innerAdapterConnection = "Local Area Connection";
    protected String outerAdapterConnection = "Wireless Network Connection";
    protected String innerConnectionPath = "D:\\Connections - Arejie\\";

    public Application() {
        super("Mana Help programma");
        initComponents();
        
//        Ja nepieviešams ātri izslēgt settings sadaļu
//        mainJTabbedPanel.setEnabledAt(1, false);

        logErrorStackTrace.setSelected(false);
        logCommands.setSelected(false);
        
        logPanel.setBorder(BorderFactory.createTitledBorder("Log Commands"));
        innerConPanel.setBorder(BorderFactory.createTitledBorder("Inner Network Connection"));
        outerConPanel.setBorder(BorderFactory.createTitledBorder("Outer Network Connection"));
        
        logJTextArea.setEditable(false);
//        myLog = new MyLog(logJTextArea);
        myLog = new MyLog(logTextPane);
        
        appSettings = new AppSettings(this);
        appSettings.getPropValues();
        
        internalConButton.setBackground(Color.BLACK);
        outerConButton.setBackground(Color.BLACK);
        proxyButton.setBackground(Color.BLACK);
        
        internalJTextField.setEditable(false);
        outerJTextField.setEditable(false);
        
        internalConButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outerConButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        proxyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        proxyStatusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        internalJProgressBar1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outerJProgressBar1.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
        setButtonColors();
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
        setAdapters();
        runRefresher();
        setRDPTables();
        
        mainJTabbedPanel.setEnabledAt(3, false);
        mainJTabbedPanel.setEnabledAt(4, false);
        
        PlainDocument doc = (PlainDocument) internalJTextField.getDocument();
        doc.setDocumentFilter(new MyIntFilter());
        
        PlainDocument doc1 = (PlainDocument) outerJTextField.getDocument();
        doc1.setDocumentFilter(new MyIntFilter());
        
        
        innerConTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                
                int row = innerConTable.getSelectedRow();
                int col = innerConTable.getSelectedColumn();
                if (row >= 0 && col >= 0) {
                    try {
                        execCmd("cmd /c start mstsc \"" + getInnerConPath() + innerConTable.getValueAt(row, col) + "\"");
//                        execCmd("cmd /c start mstsc inner\\vzd.rdp");
//                        execCmd("mstsc /v:10.219.4.218 /admin /f");
                    } catch (IOException ex) {
                        MyLog.logError(ex);
                    }
                }  
            }
        }
        );
        
        outerConTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                
                int row = outerConTable.getSelectedRow();
                int col = outerConTable.getSelectedColumn();
                if (row >= 0 && col >= 0) {
                    try {
                        execCmd("cmd /c start mstsc \"" + getOuterConPath() + outerConTable.getValueAt(row, col) + "\"");
                    } catch (IOException ex) {
                        MyLog.logError(ex);
                    }
                }  
            }
        }
        );
    }    
    
        // @TODO  Sis bus prieks atras connekcijas caur java!
//        try {
//            execCmdN("mstsc /v:10.219.4.218 /admin /f");
//        } catch (IOException ex) {
//            MyLog.logError(ex);
//        }        
    
    
//    private void execCmdN(String cmd) throws IOException {
//        Runtime.getRuntime().exec(cmd);
//    }
    
    
    //<editor-fold defaultstate="collapsed" desc="SETTERI un GETTERI">        
    private String getInternalAdapterIndex(){
        return internalJTextField.getText();
    }
    
    private String getOuterAdapterIndex(){
        return outerJTextField.getText();
    }
    
    public String getInnerAdapterTextField(){
        return innerAdapterNameTextField.getText();
    }
    
    public String getOuterAdapterTextField(){
        return outerAdapterNameTextField.getText();
    }
    
    public String getInnerConPath(){
        return innerConnectionPathTextField.getText();
    }
    
    public String getOuterConPath(){
        return outerConnectionPathTextField.getText();
    }
    
    public boolean canLogErrorStackTrace(){
        return logErrorStackTrace.isSelected();
    }
    
    public boolean canLogCommands(){
        return logCommands.isSelected();
    }
    
    public boolean canLogAdapter(){
        return logAdapter.isSelected();
    }
    
    public boolean canLogColor(){
        return logColor.isSelected();
    }
    
    public boolean canLogAutoProxy(){
        return autoProxyCheckBox.isSelected();
    }
    
    public boolean isInnerAdapterEnabled() {
        return innerAdapterEnabled;
    }
    
    public boolean isProxyEnabled(){
        return proxyEnabled;
    }
    
    public boolean isOuterAdapterEnabled() {
        return outerAdapterEnabled;
    }
    
    protected void setInnerConPath(String value){
        innerConnectionPathTextField.setText(value);
    }
    
    protected void setOuterConPath(String value){
        outerConnectionPathTextField.setText(value);
    }
    
    protected void setInnerAdapterTextField(String value){
        innerAdapterNameTextField.setText(value);
        this.innerAdapterConnection = value;
    }
    
    protected void setOuterAdapterTextField(String value){
        outerAdapterNameTextField.setText(value);
        this.outerAdapterConnection = value;
    }
    
    protected void setInnerAdapterEnabled(boolean InnerAdapterEnabled) {
        this.innerAdapterEnabled = InnerAdapterEnabled;
        setInnerButtonColor();
    }
    
    protected void setProxyEnabled (boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
        setProxyButtonColor();
    }
    
    protected void setOuterAdapterEnabled(boolean isOuterAdapterEnabled) {
        this.outerAdapterEnabled = isOuterAdapterEnabled;
        setOuterButtonColor();
    }
    
    protected void setLogErrorStackTrace(String status) {
        this.logErrorStackTrace.setSelected(Boolean.valueOf(status));
    }
    
    protected void setLogCommands(String status) {
        this.logCommands.setSelected(Boolean.valueOf(status));
    }
    
    protected void setLogColor(String status) {
        this.logColor.setSelected(Boolean.valueOf(status));
    }
    
    protected void setLogAdapter(String status) {
        this.logAdapter.setSelected(Boolean.valueOf(status));
    }
    
    protected void setAutoProxy(String status) {
        this.autoProxyCheckBox.setSelected(Boolean.valueOf(status));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="BUTTON COLORS">
    private void setButtonColors(){
        if (logColor.isSelected())
            MyLog.logEvent("COLOR: Pogu krāsu uzstādīšana...");
        setInnerButtonColor();
        setOuterButtonColor();
        setProxyButtonColor();
        if (logColor.isSelected())
            MyLog.logEvent("COLOR: Pogu krāsu uzstādīšana pabeigta!");
    }
    
    // Uzseto krasu INNER tīkla pogai
    private void setInnerButtonColor(){
//        MyLog.logEvent("COLOR: Iekšējās pogas krāsas maiņa...");
        if(isInnerAdapterEnabled()){
            internalConButton.setForeground(Color.GREEN);
        } else {
            internalConButton.setForeground(Color.RED);
        }
    }
    // Uzseto krasu INNER tīkla pogai
    private void setInnerButtonColor(boolean isButtonPressed){
//        MyLog.logEvent("COLOR: Iekšējās pogas krāsas maiņa...");
        if(isButtonPressed){
            internalConButton.setForeground(Color.GREEN);
        } else {
            internalConButton.setForeground(Color.RED);
        }
    }
    
    // Uzseto krasu OUTER tīkla pogai
    private void setOuterButtonColor(){
//        MyLog.logEvent("COLOR: Ārējās pogas krāsas maiņa...");
        if(isOuterAdapterEnabled()){
            outerConButton.setForeground(Color.GREEN);
        } else {
            outerConButton.setForeground(Color.RED);
        }
    }
    // Uzseto krasu OUTER tīkla pogai
    private void setOuterButtonColor(boolean isButtonPressed){
//        MyLog.logEvent("COLOR: Ārējās pogas krāsas maiņa...");
        if(isButtonPressed){
            outerConButton.setForeground(Color.GREEN);
        } else {
            outerConButton.setForeground(Color.RED);
        }
    }
    
    // Uzseto krasu PROXY tīkla pogai
    private void setProxyButtonColor(){
//        MyLog.logEvent("COLOR: Proxy pogas krāsas maiņa...");
        if(isProxyEnabled()){
            proxyButton.setForeground(Color.GREEN);
        } else {
            proxyButton.setForeground(Color.RED);
        }
    }
    // Uzseto krasu PROXY tīkla pogai
    private void setProxyButtonColor(boolean isButtonPressed){
//        MyLog.logEvent("COLOR: Proxy pogas krāsas maiņa...");
        if(isButtonPressed){
            proxyButton.setForeground(Color.GREEN);
        } else {
            proxyButton.setForeground(Color.RED);
        }
    }
    //</editor-fold>
    
    // Palaižu cmd komandas
    private String execCmd(String cmd) throws IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    // Parbauda proxy servera statusu, no windows registra
    private boolean getProxyStatus() {
        return WindowsRegistry.readRegistry("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings", "ProxyEnable").equals("0x1");
    }
    
    // Uzstādam sākuma adapterus
    private void setAdapters(){
        try {
            if (logAdapter.isSelected())
                MyLog.logEvent("ADAPTER: Adapteru uzstādīšana...");
            String[] cmdData = 
                    execCmd("wmic nic get name, index, NetConnectionID, netenabled")
                            .split("\n");
            ArrayList<String[]> tableData = new ArrayList<>();
            
            createArrayListFromCmd(cmdData, tableData);

            tableData.stream().map((data) -> {
                // Uzstādu iekšējo adapteru
                if (data.length > 2 && data[2].equals(innerAdapterConnection)) {
                    internalJTextField.setText(data[0]);
                }
                //Uzstādu ārējo adapteru
                return data;
            }).filter((data) -> (data.length > 2 && data[2].equals(outerAdapterConnection))).forEach((data) -> {
                outerJTextField.setText(data[0]);
            });
            if (logAdapter.isSelected())
                MyLog.logEvent("ADAPTER: Adapteru uzstādīšana pabeigta!");
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }
    
    // Izveido tabulu ar datiem no WMIC vaicājuma
    private void generateTable() {
        MyLog.logEvent("Atjauno tabulu...");
        try {
            String[] cmdData = execCmd("wmic nic get name, index, NetConnectionID, netenabled").split("\n");
            ArrayList<String[]> tableData = new ArrayList<>();
            
            createArrayListFromCmd(cmdData, tableData);
            setTableData(tableData); 
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }
    
    private void crawl(File f, int fileCount, ArrayList<String[]> rdpFileNames) {
//        System.out.println(FilenameUtils.getExtension(f.getAbsolutePath()));
        if (f.isDirectory()) {
        	File[] subFiles = f.listFiles();
        	for (int i = 0; i < subFiles.length; i++) {
                    crawl(subFiles[i], this.fileCount, rdpFileNames);
        	}
        } else {
            if(FilenameUtils.getExtension(f.getAbsolutePath())
                    .toLowerCase()
                    .equals("rdp")) {
                String[] add = {f.getName()};
                rdpFileNames.add(add);
                this.fileCount++;
            }
        }
    }
    
    private void connectionTableLinks(String pathToProces,JTable table) {
        fileCount = 0;
        
        File f = new File(pathToProces);        
        ArrayList<String[]> rdpFileNames = new ArrayList<>();
        
        crawl(f, fileCount, rdpFileNames);
        
        if(fileCount == 0) {
            MyLog.logEvent("Norādītajā mapē nav neviena rdp faila");
            return;
        }
        
        Object[][] data = new String[fileCount][];
        for (int i = 0, a = 0; i < rdpFileNames.size(); i++) {
                String[] row = rdpFileNames.get(i);
                data[a] = row;
                a++;
        }
        
        String[] column =
            {"File Name"};
        
        DefaultTableModel tm = new DefaultTableModel(data ,column) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        table.setModel(tm);
    }
    
    // @TODO so mosh var parsaukt/parstradat
    // lai varetu lietot automatisko pogu
    private void doProxy() {
        try {
            // Ja ir ieslēgts proxy serveris
            proxyStatusJProgressBar.setIndeterminate(true);
            if (isProxyEnabled()) {
                setProxyButtonColor(false);
                MyLog.logEvent("PROXY: Izslēgt Proxy...");
                String cmd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft"
                        + "\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable "
                        + "/t REG_DWORD /d 0 /f";
                if (logCommands.isSelected())
                    MyLog.logEvent(cmd);
                execCmd(cmd);

                // Ja ir izslēgts proxy serveris
            } else {
                setProxyButtonColor(true);
                MyLog.logEvent("PROXY: Ieslēgt Proxy...");
                String cmd = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft"
                        + "\\Windows\\CurrentVersion\\Internet Settings\" "
                        + "/v ProxyEnable /t REG_DWORD /d 1 /f";
                if (logCommands.isSelected())
                    MyLog.logEvent(cmd);
                execCmd(cmd);
            }
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }
    
    // Atjauno info par proxy statusu
    private void refreshProxy(){
        
        // Ja sistēmā proxy ir ON, bet programma vēl to nezin
        if(getProxyStatus() && !isProxyEnabled()) {
            setProxyEnabled(true);
            proxyStatusJProgressBar.setValue(100);
            proxyStatusJProgressBar.setIndeterminate(false);
        } 
        
        // Ja sistēmā proxy ir OFF, bet programma vēl to nezin
        if(!getProxyStatus() && isProxyEnabled()) {
            setProxyEnabled(false);
            proxyStatusJProgressBar.setValue(0);
            proxyStatusJProgressBar.setIndeterminate(false);
        }
    }
    
    // Atjauno statusu par adapteru statusu
    private void refreshAdapters() {
        try {
            String[] cmdData = execCmd("wmic nic get name, index, "
                    + "NetConnectionID, netenabled").split("\n");
            ArrayList<String[]> tableData = new ArrayList<>();
            
            // Sagatavoju arrayListu no CMD atgrieztās informācijas
            createArrayListFromCmd(cmdData, tableData);

            // Izlēkāju cauri visiem adapteriem
            tableData.stream().filter((data) -> (data.length > 2 && (data[0]
                    .equals(getInternalAdapterIndex())
                    || data[0].equals(getOuterAdapterIndex())))).map((data) -> {
                        // Iekšējā tīkla adaptera pārbaude
                        // Pārbaudu vai salīdzināmais adapters ir aktīvais INNER
                        // adapters, kā arī pārbaudu vai ir jāvec update uz StrinGrida
                        if (data[0].equals(getInternalAdapterIndex()) && data[3]
                                .equals("TRUE") && !isInnerAdapterEnabled()){
                            internalJProgressBar1.setIndeterminate(false);
                            internalJProgressBar1.setValue(100);
                            setInnerAdapterEnabled(true);
                            mainJTabbedPanel.setEnabledAt(3, true);
                            MyLog.logEvent("INNER: Iekšējais tīkls ieslēdzās..");
                            generateTable();
                            
                        }
                return data;
            }).map((data) -> {
                
                // Pārbaudu vai salīdzināmais adapters ir aktīvais INNER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                if (data[0].equals(getInternalAdapterIndex()) && data[3]
                        .equals("FALSE") && isInnerAdapterEnabled()){
                    internalJProgressBar1.setValue(0);
                    internalJProgressBar1.setIndeterminate(false);
                    setInnerAdapterEnabled(false);
                    mainJTabbedPanel.setEnabledAt(3, false);
                    MyLog.logEvent("INNER: Iekšējais tīkls izslēdzās..");
                    generateTable();
                }
                return data;
            }).map((data) -> {
                
                // Ārējā tīkla adaptera pārbaude
                //Pārbaudu vai salīdzināmais adapters ir aktīvais OUTER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                if (data[0].equals(getOuterAdapterIndex()) && data[3]
                        .equals("TRUE") && !isOuterAdapterEnabled()) {
                    outerJProgressBar1.setValue(100);
                    outerJProgressBar1.setIndeterminate(false);
                    setOuterAdapterEnabled(true);
                    mainJTabbedPanel.setEnabledAt(4, true);
                    MyLog.logEvent("OUTER: Ārējais tīkls ieslēdzās..");
                    generateTable();
                }
                
                //Pārbaudu vai salīdzināmais adapters ir aktīvais OUTER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                return data;
            }).filter((data) -> (data[0].equals(getOuterAdapterIndex()) && data[3]
                    .equals("FALSE") && isOuterAdapterEnabled())).map((_item) -> {
                outerJProgressBar1.setValue(0);
                outerJProgressBar1.setIndeterminate(false);
                return _item;
            }).map((_item) -> {
                setOuterAdapterEnabled(false);
                mainJTabbedPanel.setEnabledAt(4, false);
                return _item;
            }).forEach((_item) -> {
                MyLog.logEvent("OUTER: Ārējais tīkls izslēdzās..");
                generateTable();
            });
            
            tableData.clear();
            tableData.trimToSize();
            cmdData = null;
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }
    
    private void runRefresher() {
        MyLog.logEvent("Refresher Started!");
        
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {

                    // Atjauno proxy statusu
                    refreshProxy();                   

                    // Atjauno adapteru statusu + tabulu uz info update
                    refreshAdapters();
            try {
                Thread.sleep(1000);

            } catch (InterruptedException ex) {
                MyLog.logError(ex);
            }
              
            }, 0, 1, TimeUnit.SECONDS);
    }
    
    private void createArrayListFromCmd(String[] cmdData, ArrayList<String[]> tableData){
        for (int i = 1; i <= cmdData.length - 1; i++) {
            do {
                cmdData[i] = cmdData[i].replace("\r", "");
            } while (cmdData[i].contains("\r"));

            if (cmdData[i].contains("  ")) {
                do {
                    cmdData[i] = cmdData[i].replace("   ", "  ");
                } while (cmdData[i].contains("   "));

                String[] test3 = cmdData[i].trim().split("  ");
                tableData.add(test3);
            }
        }
    }
    
    private void setTableData(ArrayList<String[]> array){
        String[] column = {"Index","NIC - nosaukums", "Adaptera nosaukums", "Status"};
        int arraySize = 0;
        
        for (int i = 0; i < array.size(); i++){
            if(array.get(i).length > 2)
                arraySize++;
        }
        
        Object[][] data = new String[arraySize][];
        for (int i = 0, a = 0; i < array.size(); i++) {
                String[] row = array.get(i);
            if (row.length > 2) {
                data[a] = row;
                a++;
            }
        }

        DefaultTableModel tm = new DefaultTableModel(data ,column) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        adapterJTable.setModel(tm);

        // Salieku kolonnu platumus, tādus, kādus vēlos
        adapterJTable.getColumnModel().getColumn(0).setMaxWidth(40);
        adapterJTable.getColumnModel().getColumn(2).setMaxWidth(200);
        adapterJTable.getColumnModel().getColumn(2).setMinWidth(180);
        adapterJTable.getColumnModel().getColumn(3).setMaxWidth(50);
    }
    
    private void setRDPTables() {
        if(!innerConnectionPathTextField.getText().isEmpty()) {
            connectionTableLinks(getInnerConPath(), innerConTable);
        }

        if(!outerConnectionPathTextField.getText().isEmpty()) {
            connectionTableLinks(getOuterConPath(), outerConTable);
        }
    }
    
    private void innerNetworkSwitcher() {
        internalJProgressBar1.setIndeterminate(true);
        new Thread() {
            @Override
            public void run() {
                try {
                    if(isInnerAdapterEnabled()) {
                        setInnerButtonColor(false);
                        MyLog.logEvent("INTERNAL: Iekšējā adaptera izslēgšana ...");
                        String cmd = "cmd /c start wmic path win32_networkadapter "
                                + "where index=" + getInternalAdapterIndex() 
                                + " call disable";
                        if (logCommands.isSelected())
                            MyLog.logEvent(cmd);
                        execCmd(cmd);
                    } else {
                        setInnerButtonColor(true);
                        MyLog.logEvent("INTERNAL: Iekšējā adaptera startēšana ...");
                        String cmd = "cmd /c start wmic path win32_networkadapter "
                                + "where index=" + getInternalAdapterIndex() 
                                + " call enable";
                        if (logCommands.isSelected())
                            MyLog.logEvent(cmd);
                        execCmd(cmd);
                    }
                    
                    if(autoProxyCheckBox.isSelected())
                        doProxy();

                } catch (IOException ex) {
                    MyLog.logError(ex);
                }
            };
        }.start();
    }
    
    private void outerNetworkSwitcher() {
        outerJProgressBar1.setIndeterminate(true);
        new Thread() {
            @Override
            public void run() {
                try {
                    if(isOuterAdapterEnabled()) {
                        setOuterButtonColor(false);
                        MyLog.logEvent("OUTER: Ārējā adaptera izslēgšana ...");
                        String cmd = "cmd /c start wmic path win32_networkadapter "
                                + "where index=" + getOuterAdapterIndex() 
                                + " call disable";
                        if (logCommands.isSelected())
                            MyLog.logEvent(cmd);
                        execCmd(cmd);
                    } else {
                        setOuterButtonColor(true);
                        MyLog.logEvent("OUTER: Ārējā adaptera startēšana ...");
                        String cmd = "cmd /c start wmic path win32_networkadapter "
                                + "where index=" + getOuterAdapterIndex() 
                                + " call enable";
                        if (logCommands.isSelected())
                            MyLog.logEvent(cmd);
                        execCmd(cmd);
                    }

                } catch (IOException ex) {
                    MyLog.logError(ex);
                }
            };
        }.start();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainJTabbedPanel = new javax.swing.JTabbedPane();
        netConJPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        internalConButton = new javax.swing.JButton();
        outerConButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        proxyButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        adapterJTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        settingsJPanel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        logPanel = new javax.swing.JPanel();
        logErrorStackTrace = new javax.swing.JCheckBox();
        logCommands = new javax.swing.JCheckBox();
        logColor = new javax.swing.JCheckBox();
        logAdapter = new javax.swing.JCheckBox();
        innerConPanel = new javax.swing.JPanel();
        internalJTextField = new javax.swing.JTextField();
        innerAdapterNameTextField = new javax.swing.JTextField();
        innerConnectionPathTextField = new javax.swing.JTextField();
        outerConPanel = new javax.swing.JPanel();
        outerJTextField = new javax.swing.JTextField();
        outerAdapterNameTextField = new javax.swing.JTextField();
        outerConnectionPathTextField = new javax.swing.JTextField();
        autoProxyCheckBox = new javax.swing.JCheckBox();
        logJPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logJTextArea = new javax.swing.JTextArea();
        innerConMainPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        innerConTable = new javax.swing.JTable();
        outerConMainPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        outerConTable = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        logTextPane = new javax.swing.JTextPane();
        proxyStatusJProgressBar = new javax.swing.JProgressBar();
        proxyStatusLabel = new javax.swing.JLabel();
        internalJProgressBar1 = new javax.swing.JProgressBar();
        outerJProgressBar1 = new javax.swing.JProgressBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        internalConButton.setText("Iekšējais Tīkls ON/OFF");
        internalConButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internalConButtonActionPerformed(evt);
            }
        });

        outerConButton.setText("Ārējais Tīkls ON/OFF");
        outerConButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outerConButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Tīklu veidi:");

        jLabel5.setText("Proxy's:");

        proxyButton.setText("Proxy ON/OFF");
        proxyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proxyButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(internalConButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(outerConButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(proxyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(internalConButton)
                    .addComponent(proxyButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(outerConButton)
                .addGap(40, 40, 40))
        );

        adapterJTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(adapterJTable);

        jButton1.setText("Atjaunot Tabulu");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout netConJPanelLayout = new javax.swing.GroupLayout(netConJPanel);
        netConJPanel.setLayout(netConJPanelLayout);
        netConJPanelLayout.setHorizontalGroup(
            netConJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(netConJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(netConJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(netConJPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE))
                .addContainerGap())
        );
        netConJPanelLayout.setVerticalGroup(
            netConJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(netConJPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(netConJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPanel.addTab("Network Connection", netConJPanel);

        jButton2.setText("Saglabāt");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        logErrorStackTrace.setText("Log Error Stack Trace");

        logCommands.setText("Log Commands");

        logColor.setText("Log Color");

        logAdapter.setText("Log Adapter");

        javax.swing.GroupLayout logPanelLayout = new javax.swing.GroupLayout(logPanel);
        logPanel.setLayout(logPanelLayout);
        logPanelLayout.setHorizontalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logErrorStackTrace)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logCommands)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logAdapter)
                .addContainerGap(236, Short.MAX_VALUE))
        );
        logPanelLayout.setVerticalGroup(
            logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(logPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(logErrorStackTrace)
                    .addComponent(logCommands)
                    .addComponent(logColor)
                    .addComponent(logAdapter))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout innerConPanelLayout = new javax.swing.GroupLayout(innerConPanel);
        innerConPanel.setLayout(innerConPanelLayout);
        innerConPanelLayout.setHorizontalGroup(
            innerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(innerConPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(internalJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(innerAdapterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(innerConnectionPathTextField)
                .addContainerGap())
        );
        innerConPanelLayout.setVerticalGroup(
            innerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(innerConPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(innerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(internalJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(innerAdapterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(innerConnectionPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout outerConPanelLayout = new javax.swing.GroupLayout(outerConPanel);
        outerConPanel.setLayout(outerConPanelLayout);
        outerConPanelLayout.setHorizontalGroup(
            outerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outerConPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outerJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(outerAdapterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outerConnectionPathTextField)
                .addContainerGap())
        );
        outerConPanelLayout.setVerticalGroup(
            outerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outerConPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outerConPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outerJTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outerAdapterNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outerConnectionPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        autoProxyCheckBox.setText("Auto Enable Proxy with Inner Connection");
        autoProxyCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                autoProxyCheckBoxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout settingsJPanelLayout = new javax.swing.GroupLayout(settingsJPanel);
        settingsJPanel.setLayout(settingsJPanelLayout);
        settingsJPanelLayout.setHorizontalGroup(
            settingsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(settingsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsJPanelLayout.createSequentialGroup()
                        .addGroup(settingsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(logPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(outerConPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(innerConPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(settingsJPanelLayout.createSequentialGroup()
                        .addGroup(settingsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(autoProxyCheckBox)
                            .addComponent(jButton2))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        settingsJPanelLayout.setVerticalGroup(
            settingsJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsJPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(innerConPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outerConPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(autoProxyCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        mainJTabbedPanel.addTab("Settings", settingsJPanel);

        logJTextArea.setColumns(20);
        logJTextArea.setRows(5);
        jScrollPane1.setViewportView(logJTextArea);

        javax.swing.GroupLayout logJPanelLayout = new javax.swing.GroupLayout(logJPanel);
        logJPanel.setLayout(logJPanelLayout);
        logJPanelLayout.setHorizontalGroup(
            logJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
        );
        logJPanelLayout.setVerticalGroup(
            logJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
        );

        mainJTabbedPanel.addTab("Log", logJPanel);

        innerConTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(innerConTable);

        javax.swing.GroupLayout innerConMainPanelLayout = new javax.swing.GroupLayout(innerConMainPanel);
        innerConMainPanel.setLayout(innerConMainPanelLayout);
        innerConMainPanelLayout.setHorizontalGroup(
            innerConMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(innerConMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );
        innerConMainPanelLayout.setVerticalGroup(
            innerConMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(innerConMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPanel.addTab("Inner Connections", innerConMainPanel);

        outerConTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(outerConTable);

        javax.swing.GroupLayout outerConMainPanelLayout = new javax.swing.GroupLayout(outerConMainPanel);
        outerConMainPanel.setLayout(outerConMainPanelLayout);
        outerConMainPanelLayout.setHorizontalGroup(
            outerConMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outerConMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                .addContainerGap())
        );
        outerConMainPanelLayout.setVerticalGroup(
            outerConMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outerConMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 518, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainJTabbedPanel.addTab("Outer Connections", outerConMainPanel);

        jScrollPane5.setViewportView(logTextPane);

        mainJTabbedPanel.addTab("Log", jScrollPane5);

        proxyStatusLabel.setText("Proxy Status: (?)");
        proxyStatusLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                proxyStatusLabelMouseClicked(evt);
            }
        });

        internalJProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                internalJProgressBar1MouseClicked(evt);
            }
        });

        outerJProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                outerJProgressBar1MouseClicked(evt);
            }
        });

        jLabel3.setText("Iekšējā Tīkla Status:");

        jLabel4.setText("Ārējā Tīkla Status:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainJTabbedPanel)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(proxyStatusJProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(proxyStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(internalJProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(outerJProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainJTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proxyStatusLabel)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(proxyStatusJProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(internalJProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outerJProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void proxyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_proxyButtonActionPerformed
        doProxy();
    }//GEN-LAST:event_proxyButtonActionPerformed

    private void outerConButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outerConButtonActionPerformed
        outerNetworkSwitcher();
    }//GEN-LAST:event_outerConButtonActionPerformed

    private void internalConButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internalConButtonActionPerformed
        innerNetworkSwitcher();
    }//GEN-LAST:event_internalConButtonActionPerformed

    private void proxyStatusLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_proxyStatusLabelMouseClicked
        try {
            execCmd("cmd /c start inetcpl.cpl");
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }//GEN-LAST:event_proxyStatusLabelMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            appSettings.setPropValues();
            appSettings.getPropValues();
            
            setButtonColors();
            setAdapters();          
            setRDPTables();
            
        //execCmd("cmd /c start inner\\vzd.rdp");
        } catch (Exception ex) {
            if(canLogErrorStackTrace())
                MyLog.logError(ex);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        generateTable();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void autoProxyCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_autoProxyCheckBoxStateChanged
        if(autoProxyCheckBox.isSelected()) {
            proxyButton.setEnabled(false);
        } else {
            proxyButton.setEnabled(true);
        }
    }//GEN-LAST:event_autoProxyCheckBoxStateChanged

    private void internalJProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_internalJProgressBar1MouseClicked
        innerNetworkSwitcher();
    }//GEN-LAST:event_internalJProgressBar1MouseClicked

    private void outerJProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outerJProgressBar1MouseClicked
        outerNetworkSwitcher();
    }//GEN-LAST:event_outerJProgressBar1MouseClicked

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Application.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Application app = new Application();
            app.setVisible(true);            
            app.setResizable(false);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable adapterJTable;
    private javax.swing.JCheckBox autoProxyCheckBox;
    private javax.swing.JTextField innerAdapterNameTextField;
    private javax.swing.JPanel innerConMainPanel;
    private javax.swing.JPanel innerConPanel;
    private javax.swing.JTable innerConTable;
    private javax.swing.JTextField innerConnectionPathTextField;
    private javax.swing.JButton internalConButton;
    private javax.swing.JProgressBar internalJProgressBar1;
    private javax.swing.JTextField internalJTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JCheckBox logAdapter;
    private javax.swing.JCheckBox logColor;
    private javax.swing.JCheckBox logCommands;
    private javax.swing.JCheckBox logErrorStackTrace;
    private javax.swing.JPanel logJPanel;
    private javax.swing.JTextArea logJTextArea;
    private javax.swing.JPanel logPanel;
    private javax.swing.JTextPane logTextPane;
    private javax.swing.JTabbedPane mainJTabbedPanel;
    private javax.swing.JPanel netConJPanel;
    private javax.swing.JTextField outerAdapterNameTextField;
    private javax.swing.JButton outerConButton;
    private javax.swing.JPanel outerConMainPanel;
    private javax.swing.JPanel outerConPanel;
    private javax.swing.JTable outerConTable;
    private javax.swing.JTextField outerConnectionPathTextField;
    private javax.swing.JProgressBar outerJProgressBar1;
    private javax.swing.JTextField outerJTextField;
    private javax.swing.JButton proxyButton;
    private javax.swing.JProgressBar proxyStatusJProgressBar;
    private javax.swing.JLabel proxyStatusLabel;
    private javax.swing.JPanel settingsJPanel;
    // End of variables declaration//GEN-END:variables
}
