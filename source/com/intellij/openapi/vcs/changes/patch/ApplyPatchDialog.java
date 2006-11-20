/*
 * Copyright (c) 2000-2006 JetBrains s.r.o. All Rights Reserved.
 */

/*
 * Created by IntelliJ IDEA.
 * User: yole
 * Date: 17.11.2006
 * Time: 17:09:11
 */
package com.intellij.openapi.vcs.changes.patch;

import com.intellij.openapi.diff.impl.patch.FilePatch;
import com.intellij.openapi.diff.impl.patch.PatchReader;
import com.intellij.openapi.diff.impl.patch.PatchSyntaxException;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vcs.VcsBundle;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.Alarm;
import com.intellij.util.ui.UIUtil;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApplyPatchDialog extends DialogWrapper {
  private JPanel myRootPanel;
  private TextFieldWithBrowseButton myFileNameField;
  private JLabel myStatusLabel;
  private TextFieldWithBrowseButton myBaseDirectoryField;
  private JSpinner myStripLeadingDirectoriesSpinner;
  private List<FilePatch> myPatches;
  private Alarm myLoadPatchAlarm = new Alarm(Alarm.ThreadToUse.SHARED_THREAD);
  private String myLoadPatchError = null;
  private String myDetectedBaseDirectory = null;
  private int myDetectedStripLeadingDirs = -1;
  private final Project myProject;

  public ApplyPatchDialog(Project project) {
    super(project, true);
    myProject = project;
    setTitle(VcsBundle.message("apply.patch.dialog.title"));
    final FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
      @Override
      public boolean isFileSelectable(VirtualFile file) {
        return file.getFileType() == StdFileTypes.PATCH;
      }
    };
    myFileNameField.addBrowseFolderListener("Select Patch File", "", project, descriptor);
    myFileNameField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      protected void textChanged(DocumentEvent e) {
        updateOKAction();
        myStatusLabel.setForeground(UIUtil.getLabelForeground());
        myStatusLabel.setText("Loading...");
        myPatches = null;
        myLoadPatchAlarm.cancelAllRequests();
        myLoadPatchAlarm.addRequest(new Runnable() {
          public void run() {
            checkLoadPatches();
          }
        }, 400);
      }
    });

    myBaseDirectoryField.setText(project.getProjectFile().getParent().getPresentableUrl());
    myBaseDirectoryField.addBrowseFolderListener("Select Base Directory", "", project, new FileChooserDescriptor(false, true, false, false, false, false));

    init();
  }

  private void checkLoadPatches() {
    final String fileName = myFileNameField.getText().replace(File.separatorChar, '/');
    final VirtualFile patchFile = LocalFileSystem.getInstance().findFileByPath(fileName);
    if (patchFile == null) {
      queueUpdateStatus("Cannot find patch file");
      return;
    }
    myPatches = new ArrayList<FilePatch>();
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      public void run() {
        PatchReader reader;
        try {
          reader = new PatchReader(patchFile);
        }
        catch (IOException e) {
          queueUpdateStatus("Error opening patch file: " + e.getMessage());
          return;
        }
        while(true) {
          FilePatch patch;
          try {
            patch = reader.readNextPatch();
          }
          catch (PatchSyntaxException e) {
            queueUpdateStatus("Error loading patch file: " + e.getMessage());
            return;
          }
          if (patch == null) {
            break;
          }
          myPatches.add(patch);
        }
        
        autoDetectBaseDirectory();
      }
    });
    queueUpdateStatus(null);
  }

  private void autoDetectBaseDirectory() {
    for(FilePatch patch: myPatches) {
      VirtualFile fileToPatch = patch.findFileToPatch(getBaseDirectory(), 0);
      if (fileToPatch == null) {
        // TODO: smarter logic
        if (detectDirectoryByName(patch.getBeforeName())) break;
        if (detectDirectoryByName(patch.getAfterName())) break;
      }
    }
  }

  private boolean detectDirectoryByName(final String patchFileName) {
    String[] nameComponents = patchFileName.split("/");
    final String patchName = nameComponents[nameComponents.length - 1];
    final PsiFile[] psiFiles = PsiManager.getInstance(myProject).getShortNamesCache().getFilesByName(patchName);
    if (psiFiles.length == 1) {
      PsiDirectory parent = psiFiles [0].getContainingDirectory();
      boolean matches = true;
      for(int i=nameComponents.length-2; i >= 0; i--) {
        if (parent.getVirtualFile() == myProject.getProjectFile().getParent()) {
          myDetectedStripLeadingDirs = i+1;
          myDetectedBaseDirectory = parent.getVirtualFile().getPresentableUrl();
          return true;
        }
        if (!parent.getName().equals(nameComponents [i])) {
          matches = false;
          break;
        }
        parent = parent.getParentDirectory();
      }
      if (matches) {
        myDetectedBaseDirectory = parent.getVirtualFile().getPresentableUrl();
        myDetectedStripLeadingDirs = 0;
        return true;
      }
    }
    return false;
  }

  private void queueUpdateStatus(final String s) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          queueUpdateStatus(s);
        }
      });
      return;
    }
    if (myDetectedBaseDirectory != null) {
      myBaseDirectoryField.setText(myDetectedBaseDirectory);
      myDetectedBaseDirectory = null;
    }
    if (myDetectedStripLeadingDirs != -1) {
      myStripLeadingDirectoriesSpinner.setValue(myDetectedStripLeadingDirs);
      myDetectedStripLeadingDirs = -1;
    }
    myLoadPatchError = s;
    if (s == null) {
      myStatusLabel.setText(" ");
    }
    else {
      myStatusLabel.setText(s);
      myStatusLabel.setForeground(Color.red);
    }
    updateOKAction();
  }


  @Override
  protected void dispose() {
    myLoadPatchAlarm.dispose();
    super.dispose();
  }

  private void updateOKAction() {
    setOKActionEnabled(myFileNameField.getText().length() > 0 && myLoadPatchError == null);
  }

  @Override
  protected void doOKAction() {
    if (myPatches == null) {
      myLoadPatchAlarm.cancelAllRequests();
      checkLoadPatches();
    }
    if (myLoadPatchError == null) {
      super.doOKAction();
    }
  }

  @Nullable
  protected JComponent createCenterPanel() {
    return myRootPanel;
  }

  public List<FilePatch> getPatches() {
    return myPatches;
  }

  public VirtualFile getBaseDirectory() {
    return LocalFileSystem.getInstance().findFileByPath(myBaseDirectoryField.getText().replace(File.separatorChar, '/'));
  }

  public int getStripLeadingDirectories() {
    return ((Integer) myStripLeadingDirectoriesSpinner.getValue()).intValue();
  }
}