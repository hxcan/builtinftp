package com.stupidbeauty.builtinftp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import android.net.Uri;
import android.content.Context;
import android.os.AsyncTask;
import com.stupidbeauty.ftpserver.lib.FtpServer;
import java.net.BindException;
import com.stupidbeauty.ftpserver.lib.EventListener;

public class BuiltinFtpServer
{
  private ErrorListener errorListener=null; //!< Error listener.
  private FtpServerErrorListener ftpServerErrorListener=null; //!< The ftp server error listner. Chen xin.
  private int port=1421; //!< Port.
  private FtpServer ftpServer=null; //!< Ftp server object.
  private boolean allowActiveMode=true; //!<  Whether to allow active mode.
  private EventListener eventListener=null; //!< Event listener.

  public void setEventListener(EventListener eventListener)
  {
    this.eventListener=eventListener;
        
    ftpServer.setEventListener(eventListener);
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
        
    private BuiltinFtpServer() {
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
      ftpServerErrorListener=new FtpServerErrorListener(this);
  
      ftpServer = new FtpServer("0.0.0.0", port, context, allowActiveMode, ftpServerErrorListener); // 创建服务器。

      File rootDirectory=context.getFilesDir(); // The files dirctory.
      File parentDirectory=rootDirectory.getParentFile(); // Get parent directory.
      ftpServer.setRootDirectory(parentDirectory); // 设置根目录。
    } //public void start()
}
