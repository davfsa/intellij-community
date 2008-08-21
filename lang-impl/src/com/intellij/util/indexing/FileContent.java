package com.intellij.util.indexing;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author Eugene Zhuravlev
*         Date: Mar 28, 2008
*/
public final class FileContent {
  private final VirtualFile myFile;
  private final String fileName;
  private final FileType myFileType;
  private final Charset myCharset;
  private byte[] myContent;
  private CharSequence myContentAsText = null;

  public static class IllegalDataException extends RuntimeException{
    public IllegalDataException(final String message) {
      super(message);
    }

    public IllegalDataException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }
  
  public FileContent(final VirtualFile file, final CharSequence contentAsText) {
    this(file, null, contentAsText);
  }
  
  public FileContent(final VirtualFile file, final byte[] content) {
    this(file, content, null);
  }
  
  private FileContent(final VirtualFile file, final byte[] content, final CharSequence contentAsText) {
    myFile = file;
    myFileType = FileTypeManager.getInstance().getFileTypeByFile(file);
    // remember name explicitly because the file could be renamed afterwards
    fileName = file.getName();
    myContent = content;
    myContentAsText = contentAsText;
    myCharset = file.getCharset();
  }

  public VirtualFile getFile() {
    return myFile;
  }

  public String getFileName() {
    return fileName;
  }

  public Charset getCharset() {
    return myCharset;
  }

  public byte[] getContent() {
    if (myContent == null) {
      if (myContentAsText != null) {
        try {
          myContent = myCharset != null? myContentAsText.toString().getBytes(myCharset.name()) : myContentAsText.toString().getBytes();
        }
        catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return myContent;
  }

  public CharSequence getContentAsText() {
    if (myFileType.isBinary()) {
      throw new IllegalDataException("Cannot obtain text for binary file type : " + myFileType.getDescription());
    }
    if (myContentAsText == null) {
      if (myContent != null) {
        try {
          myContentAsText = new String(myContent, myCharset.name());
        }
        catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return myContentAsText;
  }
}
