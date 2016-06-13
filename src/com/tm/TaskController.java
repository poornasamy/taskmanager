package com.tm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.Response;
import com.util.FtpFile;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

@Controller
public class TaskController {

    @RequestMapping(value = "tm/ping", method = RequestMethod.GET)
    public @ResponseBody String respondToPing()
    {
        Logger.getLogger("tm").info("ping");
        return "Task manager responding to ping at " + System.currentTimeMillis();
    }
    
    @RequestMapping(value = "tm/uploadexcel", method = RequestMethod.POST)
    public @ResponseBody String respondToWriteToExcel(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        boolean isUploaded = false;
        Logger.getLogger("tm").info("uploadexcel");
        
        String fileConfig = request.getParameter("fileconfig");
        
        if(fileConfig != null)
        {
            JSONObject fileConfigJson = new JSONObject(fileConfig);
            
            XSSFWorkbook workbook = new XSSFWorkbook();
            String fileName = fileConfigJson.getString("filename");
            File LocalFile = new File(fileName);
            FileOutputStream out = new FileOutputStream(LocalFile);
            workbook.write(out);
            out.close();
            
            String serverName = fileConfigJson.getString("servername");
            String userName = fileConfigJson.getString("username");
            String password = fileConfigJson.getString("password");
            FtpFile ftpFileObj = new FtpFile(serverName, userName, password);
            ftpFileObj.connect();
            
            String remoteFileName = fileConfigJson.getString("remotefilename");
            InputStream inStreamObj = new FileInputStream(LocalFile);
            isUploaded = ftpFileObj.upload(remoteFileName, inStreamObj);
            
        }
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, (isUploaded ? Response.RESPONSE_MSG_SUCCESS : Response.RESPONSE_MSG_FAILURE));
        ObjectMapper objectMapper = new ObjectMapper();
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        
        return ResponseString;
    }
}
