/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.OnStop;


/**
 *
 * @author mherrn
 */
@OnStop
public class Shutdown implements Runnable {

  @Override
  public void run() {
    //TODO: Deregister actions in IDE
//    String msg = "Mir Stoppet...";
//    NotifyDescriptor nd = new NotifyDescriptor.Message(msg, NotifyDescriptor.INFORMATION_MESSAGE);
//    DialogDisplayer.getDefault().notify(nd);
  }

}