/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.event.SwingPropertyChangeSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 *
 * @author mherrn
 */
public class NbcaAction extends AbstractAction {

  private final Cmd cmd;


  public NbcaAction(final Cmd cmd) {
    this.cmd= cmd;
  }



  @Override
  public void actionPerformed(ActionEvent e) {
    final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(cmd, NotifyDescriptor.INFORMATION_MESSAGE);
    DialogDisplayer.getDefault().notify(nd);

    new Executor().execute(cmd);
  }


  public Cmd getCmd() {
    return cmd;
  }


  @Override
  public String toString() {
    return "NbcaAction{" + "cmd=" + cmd + '}';
  }





}
