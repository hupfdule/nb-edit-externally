/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 *
 * @author mherrn
 */
public class NbcaAction extends AbstractAction {

  private final String title;
  private final String cmdLine;


  public NbcaAction(String title, String cmdLine) {
    this.title = title;
    this.cmdLine = cmdLine;
  }



  @Override
  public void actionPerformed(ActionEvent e) {
    new Executor().execute(cmdLine);
  }


  @Override
  public String toString() {
    return "NbcaAction{" + "title=" + title + ", cmdLine=" + cmdLine + '}';
  }


  public String getTitle() {
    return title;
  }


  public String getCmdLine() {
    return cmdLine;
  }



}
