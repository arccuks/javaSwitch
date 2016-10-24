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
    private static MyLog myLog;
    private final NetworkAdapter innerAdapter = new NetworkAdapter();
    private final NetworkAdapter outerAdapter = new NetworkAdapter();
    private int fileCount = 0;
    
    private final String innerJPanelName = "Inner Connections";
    private final String outerJPanelName = "Outer Connections";

    public Application() {
        super("Mana Help programma");
        initComponents();
        
        myLog = new MyLog(logTextPane);
        innerAdapter.setNetworkAdapterName("Local Area Connection");
        outerAdapter.setNetworkAdapterName("Wireless Network Connection");
        
        initComponentsLocal();
       
        appSettings = new AppSettings(this);
        appSettings.getPropValues();
                
        setAdapters();
        runRefresher();
        setButtonColors();
        setRDPTables();
    }          
    
    //<editor-fold defaultstate="collapsed" desc="SETTERI un GETTERI">        
    
    protected String getInnerConPath(){
        return innerConnectionPathTextField.getText();
    }
    
    protected String getOuterConPath(){
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
    
    public boolean autoProxy(){
        return autoProxyCheckBox.isSelected();
    }
    
    public NetworkAdapter getInnerAdapter() {
        return innerAdapter;
    }
    
    public NetworkAdapter getOuterAdapter() {
        return outerAdapter;
    }
    
    public String getInnerAdapterTextField() {
        return innerAdapterNameTextField.getText();
    }
    
    public String getOuterAdapterTextField() {
        return outerAdapterNameTextField.getText();
    }
    
    protected void setInnerConPath(String value){
        innerConnectionPathTextField.setText(value);
    }
    
    protected void setOuterConPath(String value){
        outerConnectionPathTextField.setText(value);
    }
    
    protected void setInnerAdapterTextField(String value){
        innerAdapterNameTextField.setText(value);
        innerAdapter.setNetworkAdapterName(value);
    }
    
    protected void setOuterAdapterTextField(String value){
        outerAdapterNameTextField.setText(value);
        outerAdapter.setNetworkAdapterName(value);
    }
    
    protected void setInnerAdapterEnabled(boolean InnerAdapterEnabled) {
        innerAdapter.setAdapterEnabled(InnerAdapterEnabled);
        setInnerButtonColor(innerAdapter.isAdapterEnabled());
    }
    
    protected void setOuterAdapterEnabled(boolean isOuterAdapterEnabled) {
        outerAdapter.setAdapterEnabled(isOuterAdapterEnabled);
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
        MyLog.logEvent("COLOR: Pogu krāsu uzstādīšana...", canLogColor());
        setInnerButtonColor(innerAdapter.isAdapterEnabled());
        setOuterButtonColor();
        setProxyButtonColor();
        MyLog.logEvent("COLOR: Pogu krāsu uzstādīšana pabeigta!", canLogColor());
    }
    
    // Uzseto krasu INNER tīkla pogai
    private void setInnerButtonColor(boolean isButtonPressed){
        if(isButtonPressed){
            internalConButton.setForeground(getColorGreenOK());
        } else {
            internalConButton.setForeground(Color.RED);
        }
    }
    
    // Uzseto krasu OUTER tīkla pogai
    private void setOuterButtonColor(){
//        MyLog.logEvent("COLOR: Ārējās pogas krāsas maiņa...");
        if(outerAdapter.isAdapterEnabled()){
            outerConButton.setForeground(getColorGreenOK());
        } else {
            outerConButton.setForeground(Color.RED);
        }
    }
    // Uzseto krasu OUTER tīkla pogai
    private void setOuterButtonColor(boolean isButtonPressed){
//        MyLog.logEvent("COLOR: Ārējās pogas krāsas maiņa...");
        if(isButtonPressed){
            outerConButton.setForeground(getColorGreenOK());
        } else {
            outerConButton.setForeground(Color.RED);
        }
    }
    
    // Uzseto krasu PROXY tīkla pogai
    private void setProxyButtonColor(){
//        MyLog.logEvent("COLOR: Proxy pogas krāsas maiņa...");
        if(Proxy.getProxyStatus()){
            proxyButton.setForeground(getColorGreenOK());
        } else {
            proxyButton.setForeground(Color.RED);
        }
    }
    // Uzseto krasu PROXY tīkla pogai
    private void setProxyButtonColor(boolean isButtonPressed){
//        MyLog.logEvent("COLOR: Proxy pogas krāsas maiņa...");
        if(isButtonPressed){
            proxyButton.setForeground(getColorGreenOK());
        } else {
            proxyButton.setForeground(Color.RED);
        }
    }
    
    private Color getColorGreenOK() {
        return Color.decode("#017701");
    }
    //</editor-fold>
    
    // Uzlieku savus uzstadijumus componentem
    private void initComponentsLocal() {
        MyLog.logEvent("Ielādējam komponenšu uzstādījumus..");
        logErrorStackTrace.setSelected(true);
        
        logPanel.setBorder(BorderFactory.createTitledBorder("Log Commands"));
        innerConPanel.setBorder(BorderFactory.createTitledBorder("Inner Network Connection"));
        outerConPanel.setBorder(BorderFactory.createTitledBorder("Outer Network Connection"));
        
        internalConButton.setBackground(Color.GRAY);
        outerConButton.setBackground(Color.GRAY);
        proxyButton.setBackground(Color.GRAY);
        
        logTextPane.setEditable(false);        
        internalJTextField.setEditable(false);
        outerJTextField.setEditable(false);
        
        internalConButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outerConButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        proxyButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        proxyStatusLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        internalJProgressBar1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outerJProgressBar1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        innerConTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        outerConTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(innerJPanelName), false);
        mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(outerJPanelName), false);
        
        PlainDocument doc = (PlainDocument) internalJTextField.getDocument();
        doc.setDocumentFilter(new MyIntFilter());
        
        PlainDocument doc1 = (PlainDocument) outerJTextField.getDocument();
        doc1.setDocumentFilter(new MyIntFilter());
        
        MyLog.logSuccess("Komponenšu uzstādījumi ielādēti!");
    }
    
    // Uzstādam sākuma adapterus
    private void setAdapters(){
        try {
            MyLog.logEvent("ADAPTER: Adapteru uzstādīšana...", canLogAdapter());
            
            ArrayList<String[]> tableData = new ArrayList<>();
            createArrayListFromCmd(NetworkAdapter.getAdapterStatus(), tableData);

            tableData.stream().map((data) -> {
                // Uzstādu iekšējo adapteru
                if (data.length > 2 && data[2].equals(innerAdapter.getNetworkAdapterName())) {
                    innerAdapter.setNetworkAdapterIndex(data[0]);
                    internalJTextField.setText(innerAdapter.getNetworkAdapterIndex());
                }
                //Uzstādu ārējo adapteru
                return data;
            }).filter((data) -> (data.length > 2 && data[2].equals(outerAdapter.getNetworkAdapterName()))).forEach((data) -> {
                outerAdapter.setNetworkAdapterIndex(data[0]);
                outerJTextField.setText(outerAdapter.getNetworkAdapterIndex());
            });
            MyLog.logEvent("ADAPTER: Adapteru uzstādīšana pabeigta!", canLogAdapter());
        } catch (IOException ex) {
            MyLog.logError(ex);
        }
    }
    
    // Izveido tabulu ar datiem no WMIC vaicājuma
    private void generateTable() {
        MyLog.logEvent("Atjauno tabulu...");
        try {
            ArrayList<String[]> tableData = new ArrayList<>();           
            createArrayListFromCmd(NetworkAdapter.getAdapterStatus(), tableData);
            setNetworkConTableData(tableData); 
            MyLog.logSuccess("Tabula atjaunota!");
        } catch (IOException ex) {
            MyLog.logError(ex);
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
                this.fileCount++;
            }
        }
    }
    
    private void getConTableInfo(String pathToProces,JTable table) {
        try {
            fileCount = 0;

            File f = new File(pathToProces); 
            String[] column = {"File Name"};
            ArrayList<String[]> rdpFileNames = new ArrayList<>();

            crawl(f, rdpFileNames);

            if(fileCount == 0) {
                MyLog.logError("Norādītajā mapē nav neviena rdp faila");
                return;
            }

            Object[][] data = getDataFromArrayList(rdpFileNames, fileCount, true);

            table.setModel(getDefaultTableModel(data,column,false));
            MyLog.logSuccess("RDP faili ielādēti!\n\t" + pathToProces);
        } catch (Exception ex) 
        {
            MyLog.logError("Nevar atrast failus, neprecizi norādīta adrese!");
            MyLog.logError(ex, canLogErrorStackTrace());
        }
    }
    
    private DefaultTableModel getDefaultTableModel(Object[][] data, String[] columns,Boolean editable) {
        return new DefaultTableModel(data ,columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
    }
    
    // @TODO so mosh var parsaukt/parstradat
    // lai varetu lietot automatisko pogu
    private void doProxy() {
        try {
            // Ja ir ieslēgts proxy serveris
            proxyStatusJProgressBar.setIndeterminate(true);
            if (Proxy.getProxyStatus()) {
                setProxyButtonColor(false);
                MyLog.logEvent("PROXY: Izslēgt Proxy...");
                Proxy.disableProxy();

                // Ja ir izslēgts proxy serveris
            } else {
                setProxyButtonColor(true);
                MyLog.logEvent("PROXY: Ieslēgt Proxy...");
                Proxy.enableProxy();
            }
        } catch (IOException ex) {
            MyLog.logError("application.Application.doProxy()");
            MyLog.logError(ex);
        }
    }
    
    // Atjauno info par proxy statusu
    private void refreshProxy(){
        
        // Ja sistēmā proxy ir ON, bet programma vēl to nezin
        if(Proxy.getProxyRealStatus()&& !Proxy.getProxyStatus()) {
            Proxy.setProxyStatus(true);
            setProxyButtonColor();
            proxyStatusJProgressBar.setValue(100);
            proxyStatusJProgressBar.setIndeterminate(false);
        } 
        
        // Ja sistēmā proxy ir OFF, bet programma vēl to nezin
        if(!Proxy.getProxyRealStatus() && Proxy.getProxyStatus()) {
            Proxy.setProxyStatus(false);
            setProxyButtonColor();
            proxyStatusJProgressBar.setValue(0);
            proxyStatusJProgressBar.setIndeterminate(false);
        }
    }
    
    // Atjauno statusu par adapteru statusu
    private void refreshAdapters() {
        try {
            ArrayList<String[]> tableData = new ArrayList<>();
            
            // Sagatavoju arrayListu no CMD atgrieztās informācijas
            createArrayListFromCmd(NetworkAdapter.getAdapterStatus(), tableData);

            // Izlēkāju cauri visiem adapteriem
            tableData.stream().filter((data) -> (data.length > 2 && (data[0]
                    .equals(innerAdapter.getNetworkAdapterIndex())
                    || data[0].equals(outerAdapter.getNetworkAdapterIndex())))).map((data) -> {
                        // Iekšējā tīkla adaptera pārbaude
                        // Pārbaudu vai salīdzināmais adapters ir aktīvais INNER
                        // adapters, kā arī pārbaudu vai ir jāvec update uz StrinGrida
                        if (data[0].equals(innerAdapter.getNetworkAdapterIndex()) && data[3]
                                .equals("TRUE") && !innerAdapter.isAdapterEnabled()){
                            internalJProgressBar1.setIndeterminate(false);
                            internalJProgressBar1.setValue(100);
                            setInnerAdapterEnabled(true);
                            mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(innerJPanelName), true);
                            innerConTable.setEnabled(true);
                            MyLog.logEvent("INNER: Iekšējais tīkls tiek ieslēgts..");
                            generateTable();
                            
                        }
                return data;
            }).map((data) -> {
                
                // Pārbaudu vai salīdzināmais adapters ir aktīvais INNER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                if (data[0].equals(innerAdapter.getNetworkAdapterIndex()) && data[3]
                        .equals("FALSE") && innerAdapter.isAdapterEnabled()){
                    internalJProgressBar1.setValue(0);
                    internalJProgressBar1.setIndeterminate(false);
                    setInnerAdapterEnabled(false);
                    mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(innerJPanelName), false);
                    innerConTable.setEnabled(false);
                    MyLog.logEvent("INNER: Iekšējais tīkls tiek izslēgts..");
                    generateTable();
                }
                return data;
            }).map((data) -> {
                
                // Ārējā tīkla adaptera pārbaude
                //Pārbaudu vai salīdzināmais adapters ir aktīvais OUTER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                if (data[0].equals(outerAdapter.getNetworkAdapterIndex()) && data[3]
                        .equals("TRUE") && !outerAdapter.isAdapterEnabled()) {
                    outerJProgressBar1.setValue(100);
                    outerJProgressBar1.setIndeterminate(false);
                    setOuterAdapterEnabled(true);
                    mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(outerJPanelName), true);
                    outerConTable.setEnabled(true);
                    MyLog.logEvent("OUTER: Ārējais tīkls tiek ieslēgts..");
                    generateTable();
                }
                
                //Pārbaudu vai salīdzināmais adapters ir aktīvais OUTER adapters,
                // kā arī pārbaudu vai ir jāvec update uz StrinGrida
                return data;
            }).filter((data) -> (data[0].equals(outerAdapter.getNetworkAdapterIndex()) && data[3]
                    .equals("FALSE") && outerAdapter.isAdapterEnabled())).map((_item) -> {
                outerJProgressBar1.setValue(0);
                outerJProgressBar1.setIndeterminate(false);
                return _item;
            }).map((_item) -> {
                setOuterAdapterEnabled(false);
                mainJTabbedPanel.setEnabledAt(mainJTabbedPanel.indexOfTab(outerJPanelName), false);
                outerConTable.setEnabled(false);
                return _item;
            }).forEach((_item) -> {
                MyLog.logEvent("OUTER: Ārējais tīkls tiek izslēgts..");
                generateTable();
            });
            
            tableData.clear();
            tableData.trimToSize();
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

                String[] returnArray = cmdData[i].trim().split("  ");
                tableData.add(returnArray);
            }
        }
    }
    
    private Object[][] getDataFromArrayList(ArrayList<String[]> array,int arraySize) {
        return getDataFromArrayList(array, arraySize, false);
    }
    
    private Object[][] getDataFromArrayList(ArrayList<String[]> array,int arraySize, boolean isSpecial) {
        Object[][] data = new String[arraySize][];
        for (int i = 0, a = 0; i < array.size(); i++) {
                String[] row = array.get(i);
            
                if (row.length > 2 || isSpecial) {
                data[a] = row;
                a++;
            }
        }
        return data;
    }
    
    private void setNetworkConTableData(ArrayList<String[]> array){
        String[] column = {"Index","NIC - nosaukums", "Adaptera nosaukums", "Status"};
        int arraySize = 0;
        
        for (int i = 0; i < array.size(); i++){
            if(array.get(i).length > 2)
                arraySize++;
        }
        
        Object[][] data = getDataFromArrayList(array, arraySize);
        
        adapterJTable.setModel(getDefaultTableModel(data,column,true));

        // Salieku kolonnu platumus, tādus, kādus vēlos
        adapterJTable.getColumnModel().getColumn(0).setMaxWidth(40);
        adapterJTable.getColumnModel().getColumn(2).setMaxWidth(200);
        adapterJTable.getColumnModel().getColumn(2).setMinWidth(180);
        adapterJTable.getColumnModel().getColumn(3).setMaxWidth(50);
    }
    
    private void setRDPTables() {
        if(!innerConnectionPathTextField.getText().isEmpty()) {
            MyLog.logEvent("Sākam INNER RDP failu ielādi..");
            getConTableInfo(getInnerConPath(), innerConTable);
        }

        if(!outerConnectionPathTextField.getText().isEmpty()) {
            MyLog.logEvent("Sākam OUTER RDP failu ielādi..");
            getConTableInfo(getOuterConPath(), outerConTable);

        }
    }
    
    private void innerNetworkSwitcher() {
        internalJProgressBar1.setIndeterminate(true);
        new Thread() {
            @Override
            public void run() {
                try {
                    if(innerAdapter.isAdapterEnabled()) {
                        setInnerButtonColor(false);
                        MyLog.logEvent("INTERNAL: Iekšējā adaptera izslēgšana ...", canLogAdapter());
                        innerAdapter.enableAdapter(false);
                    } else {
                        setInnerButtonColor(true);
                        MyLog.logEvent("INTERNAL: Iekšējā adaptera startēšana ...", canLogAdapter());
                        innerAdapter.enableAdapter(true);
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
                    if(outerAdapter.isAdapterEnabled()) {
                        setOuterButtonColor(false);
                        MyLog.logEvent("OUTER: Ārējā adaptera izslēgšana ...");
                        outerAdapter.enableAdapter(false);
                    } else {
                        setOuterButtonColor(true);
                        MyLog.logEvent("OUTER: Ārējā adaptera startēšana ...");
                        outerAdapter.enableAdapter(true);
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
        innerConMainPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        innerConTable = new javax.swing.JTable();
        outerConMainPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        outerConTable = new javax.swing.JTable();
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

        innerConTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        innerConTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                innerConTableMouseClicked(evt);
            }
        });
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
        outerConTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                outerConTableMouseClicked(evt);
            }
        });
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
        new Thread() {
            @Override
            public void run() {
                try {
                    String cmd = "cmd /c start inetcpl.cpl";
                    CMD.execCmd(cmd);
                    MyLog.logEvent(cmd, canLogCommands());
                } catch (IOException ex) {
                    MyLog.logError(ex);
                }
            }
        }.start();
    }//GEN-LAST:event_proxyStatusLabelMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {                    
            appSettings.setPropValues();
            appSettings.getPropValues();
            
            setButtonColors();
            setAdapters();          
            setRDPTables();
            
        } catch (Exception ex) {
            if(canLogErrorStackTrace())
                MyLog.logError(ex, canLogErrorStackTrace());
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

    private void outerConTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outerConTableMouseClicked
        int row = outerConTable.getSelectedRow();
        int col = outerConTable.getSelectedColumn();
        if (row >= 0 && col >= 0) {

            // Laizu jauna threda, lai CMD nenobloketu pasu Applikaciju
            new Thread() {
                @Override
                public void run() {
                    try {
                        String cmd = "cmd /c start mstsc \"" + getOuterConPath() + outerConTable.getValueAt(row, col) + "\"";
                        CMD.execCmd(cmd);
                        MyLog.logEvent(cmd, canLogCommands());
                    } catch (IOException ex) {
                        MyLog.logError(ex);
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_outerConTableMouseClicked

    private void innerConTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_innerConTableMouseClicked
        int row = innerConTable.getSelectedRow();
        int col = innerConTable.getSelectedColumn();
        if (row >= 0 && col >= 0) {

            // Laizu jauna threda, lai CMD nenobloketu pasu Applikaciju
            new Thread() {
                @Override
                public void run() {
                    try {
                        String cmd = "cmd /c start mstsc \"" + getInnerConPath() + innerConTable.getValueAt(row, col) + "\"";
                        CMD.execCmd(cmd);
                        MyLog.logEvent(cmd, canLogCommands());
//                                    execCmd("cmd /c start mstsc inner\\vzd.rdp");
//                                    execCmd("mstsc /v:10.219.4.218 /admin /f");
                    } catch (IOException ex) {
                        MyLog.logError(ex);
                    }
                }
            }.start();
        }  
    }//GEN-LAST:event_innerConTableMouseClicked

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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JCheckBox logAdapter;
    private javax.swing.JCheckBox logColor;
    private javax.swing.JCheckBox logCommands;
    private javax.swing.JCheckBox logErrorStackTrace;
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
