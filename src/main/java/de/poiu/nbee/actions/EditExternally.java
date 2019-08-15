/*
 * Copyright 2019 Marco Herrn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.poiu.nbee.actions;

import de.poiu.nbee.config.Prefs;
import de.poiu.nbee.config.Prefs.CmdType;
import de.poiu.nbee.parser.CmdlineParser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.options.OptionsDisplayer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataShadow;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

import static de.poiu.nbee.config.Prefs.CmdType.EDIT_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.CmdType.OPEN_EXTERNALLY_CMD;
import static de.poiu.nbee.config.Prefs.NETBEANS_PREFS_ID;


/**
 * An action to edit the current file in an external editor.
 * <p>
 * This calls the command line the user has configured for this action.
 * If the command line is not configured yet, the user is informed and gets the opportunity to open
 * the configuration dialog for configuring it.
 * <p>
 * Two types of command need to be configured. The first one, "edit externally", is used when the
 * current editor is available and the current location of the cursor in this is known.
 * If the cursor position is not known for the current file, the "open externally" command is used
 * that doesn't support location information to be configured.
 *
 * @author Marco Herrn
 */
@ActionID(
  category = "Tools",
  id = "de.poiu.nbee.EditExternally"
)
@ActionRegistration(
  iconBase = "de/poiu/nbee/icons/edit-externally.png",
  displayName = "#CTL_EditExternally"
)
@ActionReferences({
//  @ActionReference(path = "Menu/Tools", position = 0), //redundant if "UI/ToolActions/Files" is registered
  @ActionReference(path = "Toolbars/File", position = 1000),
  @ActionReference(path = "Editors/Toolbars/Default", position = 10000),
  @ActionReference(path = "Editors/Popup", position = 1425, separatorBefore = 1422),
//  @ActionReference(path = "Editors/TabActions"), // disabled. Always opens the _current_ editor, not the clicked one. Same problem with 'Copy file path'
  @ActionReference(path = "UI/ToolActions/Files"), // Menu/Tools _and_ Tools popup menu"?
})
@Messages({
  "CTL_EditExternally=Edit Externally",
  "# {0} - the file to be edited",
  "CTL_Editing_Status=Editing file {0} in vim",
  "# {0} - the file to be opened",
  "# {1} - the reasonf for error",
  "CTL_Editing_Error=Error opening vim editor for {0}: {1}"})
public final class EditExternally implements ActionListener {

  private static final Logger LOGGER= Logger.getLogger(EditExternally.class.getName());


  @Override
  public void actionPerformed(ActionEvent ev) {
    LOGGER.entering("EditExternally", "actionPerformed", ev);

    final DataObject dataObject= this.getCurrentDataObject();
    final FileObject file= this.getFileObjectFrom(dataObject);
    if (file == null) {
      LOGGER.log(Level.INFO, "Ignoring execution request, since no current file was found");
      return;
    }

    final JTextComponent editor= getCurrentEditor();
    final StyledDocument sdocument = editor != null ? (StyledDocument) editor.getDocument() : null;

    final CmdType cmdType;
    if (editor == null) {
      LOGGER.log(Level.INFO, "Calling 'open external' command since not current editor was found.");
      cmdType= OPEN_EXTERNALLY_CMD;
    } else if (sdocument == null) {
      LOGGER.log(Level.INFO, "Calling 'open external' command since current editors document doesn't contain a StyledDocument, but instead a {0}.", editor.getDocument().getClass());
      cmdType= OPEN_EXTERNALLY_CMD;
    } else {
      LOGGER.log(Level.INFO, "Calling 'edit external' command with location information of current editor");
      cmdType= EDIT_EXTERNALLY_CMD;
    }

    final Prefs prefs= Lookup.getDefault().lookup(Prefs.class);
    final String cmdLine= prefs.load(cmdType.name());

    if (cmdLine == null || cmdLine.trim().isEmpty()) {
      this.openOptionsPanel(cmdType);
    } else {
      final File actualFile= FileUtil.toFile(file);

      final CmdlineParser cmdlineParser= new CmdlineParser();

      cmdlineParser
        .replace("${file}", actualFile.getAbsolutePath())
        .replace("${fileName}", file.getNameExt())
        .replace("${fileBasename}", file.getName())
        .replace("${fileExt}", file.getExt())
        ;

      if (cmdType == EDIT_EXTERNALLY_CMD) {
        final int caret= editor.getCaretPosition();
        final int line0= NbDocument.findLineNumber(sdocument, caret);
        final int column0= NbDocument.findLineColumn(sdocument, caret);
        final String selectedText= editor.getSelectedText();

        cmdlineParser
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
      }

      final String[] command= cmdlineParser.parse(cmdLine.trim());

      LOGGER.log(Level.INFO, "Calling command {0}", Arrays.toString(command));

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
  }


  /**
   * Opens the Options Panel for configuring this plugin.
   * <p>
   * This should be called if a certain <code>CmdType</code> is not configured yet.
   * <p>
   * It informs the user of the missing configuration and provides the opportunity to open the
   * Options Panel for this plugin.
   *
   * @param cmdType the command type that is not configured yet
   */
  private void openOptionsPanel(final CmdType cmdType) {
    final String action= cmdType == EDIT_EXTERNALLY_CMD ? "edit" : "open";
    final String msg= "<html>No command to "+action+" file externally is defined yet.<br/>Open configuration panel now?</html>";
    final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
    final Object result = DialogDisplayer.getDefault().notify(nd);
    if (NotifyDescriptor.YES_OPTION == result) {
      OptionsDisplayer.getDefault().open("Advanced"+ "/" + NETBEANS_PREFS_ID);
    }
  }


  /**
   * Returns the current DataObject.
   * @return the current DataObject
   */
  private DataObject getCurrentDataObject() {
    return TopComponent.getRegistry().getActivated().getLookup().lookup(DataObject.class);
  }


  /**
   * Returns the actual <code>FileObject</code> for the given <code>DataObject</code>
   * @param dataObject the <code>DataObject</code> for which to return the <code>FileObject</code>
   * @return the <code>FileObject</code> for the given <code>DataObject</code>
   */
  private FileObject getFileObjectFrom(DataObject dataObject) {
    if (dataObject == null) {
      return null;
    }

    if (dataObject instanceof DataShadow){
      dataObject = ((DataShadow) dataObject).getOriginal();
    }

    return dataObject.getPrimaryFile();
  }


  /**
   * Tries to find the current editor.
   * <p>
   * If no current editor can be found for the currently selected file (or node), <code>null</code>
   * is returned
   *
   * @return the current editor or <code>null</code> if no editor could be found.
   */
  private JTextComponent getCurrentEditor() {
    final Node[] currentNodes= TopComponent.getRegistry().getCurrentNodes();
    if (null == currentNodes) {
      LOGGER.log(Level.INFO, "No current node");
      return null;
    }

    // FIXME: At the moment this supports only opening 1 file.
    //        If multiple nodes are selected only one of them will be opened!
    for (Node node : currentNodes) {
      final EditorCookie ec = node.getLookup().lookup(EditorCookie.class);
      if (ec != null) {
        // FIXME: This is disabled, since NbDocument.fineRecentEditorPane() doesn't always return an
        //        editor. For example after restarting netbeans, it returns null, even though the
        //        selected node has an open editor somewhere.
//        final JEditorPane editorPane= NbDocument.findRecentEditorPane(ec);
//        if (editorPane != null) {
//          return editorPane;
//        }
        final JEditorPane[] editorPanes = ec.getOpenedPanes();
        if (editorPanes == null) {
          continue;
        } else if (editorPanes.length == 1) {
          return editorPanes[0];
        } else {
          LOGGER.log(Level.WARNING, "More than one editor pane found for current editor ({0} panes)", editorPanes.length);
          return editorPanes[0];
        }
      }
    }
    return null;
  }
}
