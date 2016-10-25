/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author avitoli2
 */
public class Table {
    
    private int rowCount;
    private String[] column;
    
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
    
    public void rowCountInc() {
        this.rowCount++;
    }
    
    public int getRowCount() {
        return this.rowCount;
    }
    
    public void setColumn(String column, String splitter) {
        this.column = column.split(splitter);
    }
    
    public String[] getColumn() {
        return this.column;
    }
    
    public void setTable(JTable table, ArrayList dataList) {
        Object[][] data = getDataFromArrayList(dataList, getRowCount());
        table.setModel(getDefaultTableModel(data, getColumn(), Boolean.TRUE));
    }
    
    protected Object[][] getDataFromArrayList(ArrayList<String[]> array,int arraySize) {
        return getDataFromArrayList(array, arraySize, false);
    }
    
    protected Object[][] getDataFromArrayList(ArrayList<String[]> array,int arraySize, boolean isSpecial) {
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
    
    protected DefaultTableModel getDefaultTableModel(Object[][] data, String[] columns,Boolean editable) {
        return new DefaultTableModel(data ,columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
    }
}
