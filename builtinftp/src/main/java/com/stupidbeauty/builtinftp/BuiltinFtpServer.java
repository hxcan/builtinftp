package com.stupidbeauty.builtinftp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import android.net.Uri;
import android.provider.Settings;
import android.content.Intent;
import android.os.Environment;
import 	android.provider.DocumentsContract;
import android.content.Context;
import android.os.AsyncTask;
import com.stupidbeauty.ftpserver.lib.FtpServer;
import java.net.BindException;
import com.stupidbeauty.ftpserver.lib.EventListener;

public class BuiltinFtpServer
{
  private ErrorListener errorListener=null; //!< Error listener.
  private FtpServerErrorListener ftpServerErrorListener = null; //!< The ftp server error listner.
  private int port=1421; //!< Port.
  private FtpServer ftpServer=null; //!< Ftp server object.
  private boolean allowActiveMode=true; //!<  Whether to allow active mode.
  private EventListener eventListener=null; //!< Event listener.

  public void setEventListener(EventListener eventListener)
  {
    this.eventListener=eventListener;

    if (ftpServer!=null) // The ftp exists
    {
      ftpServer.setEventListener(eventListener);
    } // if (ftpServer!=null) // The ftp exists
  } //public void setEventListener(EventListener eventListener)
    
  public void setErrorListener(ErrorListener errorListener)    
  {
    this.errorListener = errorListener;
  } //public void setErrorListener(ErrorListener errorListener)    
    
  public void onError(Integer errorCode) 
  {
    if (errorListener!=null)
    {
      errorListener.onError(errorCode); // Report error.
    }
    else // Not listener
    {
      Exception ex = new BindException();
      throw new RuntimeException(ex);
    }
  } //public void onError(Integer errorCode)
    
  /**
  * Set to allow or not allow active mode.
  */
  public void setAllowActiveMode(boolean allowActiveMode)
  {
    this.allowActiveMode=allowActiveMode;
  } //private void setAllowActiveMode(allowActiveMode)
    
  public void setPort(int port)
  {
    this.port=port;
  } //public void setPort(int port)
        
  private BuiltinFtpServer()
  {
  }

  public BuiltinFtpServer(Context context)
  {
    this.context = context;
  }

  private Context context; //!< Context.

  /**
  * Start the bultin ftp server.
  */
  public void start()
  {
    ftpServerErrorListener = new FtpServerErrorListener(this);

    File externalDir = new File(context.getFilesDir(), "external");
    if (!externalDir.exists())
    {
      externalDir.mkdirs();
    }

    ftpServer = new FtpServer("0.0.0.0", port, context, allowActiveMode, ftpServerErrorListener);

    File externalFilesDir = context.getExternalFilesDir(null);
    if (externalFilesDir != null)
    {
      File parentDir = externalFilesDir.getParentFile();
      if (parentDir != null)
      {
        Uri uri = Uri.fromFile(parentDir);
        ftpServer.mountVirtualPath("/files/external", uri);
      }
    }

    if (eventListener != null)
    {
      ftpServer.setEventListener(eventListener);
    }

    File rootDirectory = context.getFilesDir();
    File parentDirectory = rootDirectory.getParentFile();
    ftpServer.setRootDirectory(parentDirectory);
  }

  /**
  * Mount virtual path.
  */
  public void mountVirtualPath(String path , Uri uri)
  {
    boolean takePermission = true; // Take permsion by default.

    mountVirtualPath(path, uri, takePermission);
  } // public void mountVirtualPath(String path , Uri uri)

  /**
  * Mount virtual path.
  */
  public void mountVirtualPath(String path , Uri uri, boolean takePermission)
  {
    ftpServer.mountVirtualPath(path, uri, takePermission);
  } // public void mountVirtualPath(String path , Uri uri)
}
