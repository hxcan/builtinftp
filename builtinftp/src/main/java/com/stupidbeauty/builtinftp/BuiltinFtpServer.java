package com.stupidbeauty.builtinftp;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
// import butterknife.ButterKnife;
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
	private static final String TAG = "BuiltinFtpServer"; //!< Tag used in debug code.
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

    // 创建external目录
    File externalDir = new File(context.getFilesDir(), "external");
    if (!externalDir.exists())
    {
      externalDir.mkdirs();
      Log.d(TAG, "Created external directory: " + externalDir.getAbsolutePath());
    }
    else
    {
      Log.d(TAG, "External directory already exists: " + externalDir.getAbsolutePath());
    }

    File rootDirectory = context.getFilesDir();
    File parentDirectory = rootDirectory.getParentFile();
    ftpServer = new FtpServer("0.0.0.0", port, context, allowActiveMode, ftpServerErrorListener);
    ftpServer.setRootDirectory(parentDirectory);

    File externalFilesDir = context.getExternalFilesDir(null);
    if (externalFilesDir != null)
    {
      File parentDir = externalFilesDir.getParentFile();
      if (parentDir != null)
      {
        Uri uri = Uri.fromFile(parentDir);
        Log.d(TAG, "Mounting virtual path: /files/external -> " + parentDir.getAbsolutePath());
        mountVirtualPath("/files/external", uri, false);
      }
      else
      {
        Log.e(TAG, "Parent directory is null for externalFilesDir: " + externalFilesDir.getAbsolutePath());
      }
    }
    else
    {
      Log.e(TAG, "External files directory is null");
    }

    if (eventListener != null)
    {
      ftpServer.setEventListener(eventListener);
    }
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
