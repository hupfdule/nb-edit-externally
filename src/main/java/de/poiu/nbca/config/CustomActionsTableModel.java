/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;


/**
 *
 * @author mherrn
 */
public class CustomActionsTableModel extends AbstractTableModel {

  public static class Entry {
    String title= "";
    String cmdLine= "";
  }


  private final List<Entry> entries= Collections.synchronizedList(new ArrayList<Entry>());

  @Override
  public int getRowCount() {
    return this.entries.size();
  }


  @Override
  public int getColumnCount() {
    return 2;
  }


  @Override
  public String getValueAt(int rowIndex, int columnIndex) {
    final Entry e= this.entries.get(rowIndex);
    switch (columnIndex) {
      case 0: return e.title;
      case 1: return e.cmdLine;
      default: throw new IllegalArgumentException("Invalid index "+columnIndex+". Highest possible column index is "+(this.getColumnCount() - 1)+".");
    }
  }


  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    final String newValue;
    if (aValue == null) {
      newValue= "";
    } else if (aValue instanceof String) {
      newValue= (String) aValue;
    } else {
      throw new IllegalArgumentException("Invalid value: "+aValue);
    }

    final Entry e= this.entries.get(rowIndex);
    switch (columnIndex) {
      case 0:
        e.title= newValue;
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      case 1:
        e.cmdLine= newValue;
        fireTableCellUpdated(rowIndex, columnIndex);
        break;
      default:
        throw new IllegalArgumentException("Invalid index "+columnIndex+". Highest possible column index is "+(this.getColumnCount() - 1)+".");
    }
  }


  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }


  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }


  @Override
  public String getColumnName(int column) {
    switch(column) {
      case 0: return "Title";
      case 1: return "Command line";
      default: throw new IllegalArgumentException("Invalid index "+column+". Highest possible column index is "+(this.getColumnCount() - 1)+".");
    }
  }


  public void add(final Entry e) {
    this.entries.add(e);
    fireTableRowsInserted(this.entries.size() -1, this.entries.size() -1);
  }


  public void addRow() {
    this.entries.add(new Entry());
    fireTableRowsInserted(this.entries.size() -1, this.entries.size() -1);
  }


  public void removeRow(final int rowIndex) {
    this.entries.remove(rowIndex);
    fireTableRowsDeleted(rowIndex, rowIndex);
  }


  public boolean isRowEmpty(final int row) {
    final Entry e= this.entries.get(row);
    return e.title.trim().isEmpty()
      && e.cmdLine.trim().isEmpty();
  }


  public void clear() {
    final int size= this.entries.size();
    this.entries.clear();
    fireTableRowsDeleted(0, Math.max(0, size-1));
  }
}
