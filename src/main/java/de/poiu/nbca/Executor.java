/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.poiu.nbca;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;



/**
 *
 * @author mherrn
 */
@Messages({
  "# {0} - the file to be edited",
  "CTL_Editing_Status=Editing file {0} in vim",
  "# {0} - the file to be opened",
  "# {1} - the reasonf for error",
  "CTL_Editing_Error=Error opening vim editor for {0}: {1}"})
public class Executor {
  private static final Logger LOGGER= Logger.getLogger(Executor.class.getName());

  //FIME; Different executors or execute methods:
  //       - Execute command for currently edited file
  //       - Execute and replace selection
  //       - Execute command for selected node (file or folder)
  public void execute(final Cmd cmd) {
    final DataObject dataObject= TopComponent.getRegistry().getActivated().getLookup().lookup(DataObject.class);

//    String msg1 = "There is something you should know... "+getFileObjectFrom(dataObject)+" "+getCurrentEditor()+" "+(getCurrentEditor() instanceof StyledDocument);
//    NotifyDescriptor nd = new NotifyDescriptor.Message(msg1, NotifyDescriptor.INFORMATION_MESSAGE);
//    DialogDisplayer.getDefault().notify(nd);

    final FileObject file = getFileObjectFrom(dataObject);
    if (file == null) {
      LOGGER.log(Level.INFO, "Ignoring execution request, since no current file was found");
      return;
    }

    final JTextComponent editor= getCurrentEditor();
    if (editor == null) {
      LOGGER.log(Level.INFO, "Ignoring execution request, since no current editor was found");
      return;
    }

    if (!(editor.getDocument() instanceof StyledDocument)) {
      LOGGER.log(Level.INFO, "Ignoring execution request, since current editors document doesn't contain a StyledDocument, but instead a {0}", editor.getDocument().getClass());
      return;
    }

    final StyledDocument sdocument = (StyledDocument) editor.getDocument();
    final int caret= editor.getCaretPosition();
    final int line0= NbDocument.findLineNumber(sdocument, caret);
    final int column0= NbDocument.findLineColumn(sdocument, caret);
    final String selectedText= editor.getSelectedText();

    final File actualFile= FileUtil.toFile(file);

    final String cmdString= cmd.getCmdLine()
      .replace("${file}", actualFile.getAbsolutePath())
      .replace("${fileName}", file.getNameExt())
      .replace("${fileBasename}", file.getName())
      .replace("${fileExt}", file.getExt())
      .replace("${line0}", String.valueOf(line0))
      .replace("${line}", String.valueOf(line0 + 1))
      .replace("${column0}", String.valueOf(column0))
      .replace("${column}", String.valueOf(column0 + 1))
      .replace("${selectedText}", selectedText != null ? selectedText : "")
      .replace("${selectionStart}", String.valueOf(editor.getSelectionStart()))
      .replace("${selectionEnd}", String.valueOf(editor.getSelectionEnd()))
      ;
    final String[] command= CmdlineParser.parse(cmdString);

//    String msg2 = cmdString;
//    NotifyDescriptor nd2 = new NotifyDescriptor.Message(msg2, NotifyDescriptor.INFORMATION_MESSAGE);
//    DialogDisplayer.getDefault().notify(nd2);

    try {
      final String msg= Bundle.CTL_Editing_Status(file.getPath());
      StatusDisplayer.getDefault().setStatusText(msg);

      Runtime.getRuntime().exec(command);
    } catch (IOException ex) {
      final String msg = Bundle.CTL_Editing_Error(file.getPath(), ex.getLocalizedMessage());
      StatusDisplayer.getDefault().setStatusText(msg);
      Exceptions.printStackTrace(ex);
    }
  }


  private FileObject getFileObjectFrom(DataObject dataObject) {
    if (dataObject == null) {
      return null;
    }

    if (dataObject instanceof DataShadow){
      dataObject = ((DataShadow) dataObject).getOriginal();
    }

    return dataObject.getPrimaryFile();
  }


  private JTextComponent getCurrentEditor() {
    final Node[] currentNodes= TopComponent.getRegistry().getCurrentNodes();
    if (null == currentNodes) {
      return null;
    }
    for (Node node : currentNodes) {
      final EditorCookie ec = node.getLookup().lookup(EditorCookie.class);
      if (ec != null) {
        final JEditorPane[] editorPanes = ec.getOpenedPanes();
        if (editorPanes != null && editorPanes.length > 0) {
          LOGGER.log(Level.WARNING, "More than one editor pane found for current editor ({0} panes)", editorPanes.length);
          return editorPanes[0];
        }
      }
    }
    return null;
  }
}
