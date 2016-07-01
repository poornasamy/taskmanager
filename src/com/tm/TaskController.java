package com.tm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.Employee;
import com.model.Response;
import com.util.FtpFile;
import com.util.HttpManager;
import com.util.MemCache;
import com.util.Utilities;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

@Controller
public class TaskController {

    Logger logger = Logger.getLogger(this.getClass());
    @RequestMapping(value = "tm/ping", method = RequestMethod.GET)
    public @ResponseBody String respondToPing()
    {
        logger.info("ping");
        return "Task manager responding to ping at " + System.currentTimeMillis();
    }
    
    @RequestMapping(value = "tm/uploadexcel", method = RequestMethod.POST)
    public @ResponseBody String respondToWriteToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        boolean isUploaded = false;
        logger.info("uploadexcel");
        
        String fileConfig = request.getParameter("fileconfig");
        
        if(fileConfig != null)
        {
            JSONObject fileConfigJson = new JSONObject(fileConfig);
            
            String dataURL = fileConfigJson.getString("dataurl");
            InputStream dataURLInputStreamObj = new URL(dataURL).openStream();
            String dataURLString = Utilities.getStringFromInputStream(dataURLInputStreamObj);
            logger.info("dataURLString = " + dataURLString);
            
            JSONArray dataURLJsonArray = new JSONArray(dataURLString);
            
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            XSSFRow row;
            ArrayList<String> rowColumnAL = new ArrayList<String>();
            int rowIdx = 0;
            int cellIdx = 0;
            
            for(int idx = 0; idx < dataURLJsonArray.length(); idx++)
            {
                JSONObject rowJson = dataURLJsonArray.getJSONObject(idx);
                Iterator<String> keys = rowJson.keys();
                
                while(keys.hasNext())
                {
                    String tempCellvalue = keys.next();
                    
                    if(!rowColumnAL.contains(tempCellvalue))
                    {
                        rowColumnAL.add(tempCellvalue);
                    }   
                }
            }
            
            row = sheet.createRow(rowIdx++);
            for(String cellData:rowColumnAL)
            {
                Cell cell = row.createCell(cellIdx++);
                cell.setCellValue(cellData);
            }
            
            for(int idx = 0; idx < dataURLJsonArray.length(); idx++)
            {
                JSONObject rowJson = dataURLJsonArray.getJSONObject(idx);
                ArrayList<String> rowDatanAL = new ArrayList<String>();
                
                for(String cellData:rowColumnAL)
                {
                    rowDatanAL.add(rowJson.optString(cellData));
                }
                
                row = sheet.createRow(rowIdx++);
                cellIdx = 0;
                for(String cellData:rowDatanAL)
                {
                    Cell cell = row.createCell(cellIdx++);
                    cell.setCellValue(cellData);
                }
            }
            
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
    
    @RequestMapping(value = "tm/putintomemcache", method = RequestMethod.GET)
    public @ResponseBody String respondToPutInToMemcache(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.info("putintomemcache");
        boolean putInMemCache = false;
        String servers = request.getParameter("servers");
        MemcachedClient client = MemCache.getMemcachedClient(servers);
        
        if(client != null)
        {
            MemCache memCacheObj = new MemCache(client);
            String key = request.getParameter("key");
            int index = Integer.parseInt(request.getParameter("index"));
            String value = request.getParameter("value");
            
            memCacheObj.set(key, index, value);
            putInMemCache = true;
        }
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, (putInMemCache ? Response.RESPONSE_MSG_SUCCESS : Response.RESPONSE_MSG_FAILURE));
        ObjectMapper objectMapper = new ObjectMapper();
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        
        return ResponseString;
    }
    
    @RequestMapping(value = "tm/getfrommemcache", method = RequestMethod.GET)
    public @ResponseBody String respondToGetFromMemcache(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.info("getfrommemcache");
        
        String toRet = null;
        String servers = request.getParameter("servers");
        MemcachedClient client = MemCache.getMemcachedClient(servers);
        
        if(client != null)
        {
            MemCache memCacheObj = new MemCache(client);
            String key = request.getParameter("key");
            
            toRet = memCacheObj.get(key);
        }
        
        return toRet;
    }
    
    @RequestMapping(value = "tm/shutdownmemcache", method = RequestMethod.GET)
    public @ResponseBody String respondToShutDownMemcache(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.info("shut down memcache client");
        boolean shutdownMemCache = false;
        String servers = request.getParameter("servers");
        MemcachedClient client = MemCache.getMemcachedClient(servers);
        if(client != null)
        {
            MemCache memCacheObj = new MemCache(client);
            shutdownMemCache = memCacheObj.shutdown();
        }
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, (shutdownMemCache ? Response.RESPONSE_MSG_SUCCESS : Response.RESPONSE_MSG_FAILURE));
        ObjectMapper objectMapper = new ObjectMapper();
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        
        return ResponseString;
    }
    
    @RequestMapping(value = "tm/makehttprequest", method = RequestMethod.GET)
    public @ResponseBody String respondToMakeHttpRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.info("make http request");
        
        String url = request.getParameter("url");
        HttpManager httpManager = new HttpManager(url, null);
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, httpManager.makeRequest());
        ObjectMapper objectMapper = new ObjectMapper();
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        
        return ResponseString;
    }
    
    @RequestMapping(value = "tm/readvalue", method = RequestMethod.GET)
    public @ResponseBody String respondToReadValue(HttpServletRequest request,
                                                   HttpServletResponse response,
                                                   @RequestParam(required = true) String dataurl) throws Exception
    {
        logger.info("read value");
        ObjectMapper objectMapper = new ObjectMapper();
        URL urlObj = new URL(dataurl);
        Employee[] employeeObj = objectMapper.readValue(urlObj, Employee[].class);
        
        for(int i = 0; i < employeeObj.length; i++)
        {
            logger.info("emp id = " + employeeObj[i].getId());
            logger.info("emp name = " + employeeObj[i].getName());
        }
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, Response.RESPONSE_MSG_SUCCESS);
        
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        
        return ResponseString;
    }
}
