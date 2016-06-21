package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

public class FtpFile {
    private FTPClient ftpClient = null;
    private String serverName = null;
    private String userName = null;
    private String password = null;
    
    public FtpFile(String serverName, String userName, String password)
    {
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
    }
    
    public void connect() throws SocketException, IOException
    {
        this.ftpClient = new FTPClient();
        this.ftpClient.connect(this.serverName);
        boolean isLoggedIn = this.ftpClient.login(this.userName, this.password);
        this.ftpClient.enterLocalPassiveMode();
        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        
        if(isLoggedIn)
        {
            Logger.getLogger("tm").info("FTP Logged in successfully");
        }
        else
        {
            Logger.getLogger("tm").info("FTP Logged in failed");
        }
    }
    
    public boolean upload(String remoteFile, InputStream LocalInputStream) throws IOException
    {
        boolean isUploaded = this.ftpClient.storeFile(remoteFile, LocalInputStream);
        
        if(isUploaded)
        {
            Logger.getLogger("tm").info("File uploaded successfully");
        }
        else
        {
            Logger.getLogger("tm").info("File uploaded failed. ReplyCode is " + this.ftpClient.getReplyCode() + " ReplyString is " + this.ftpClient.getReplyString());
        }
        return isUploaded;
    }
}
