package com.tm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.model.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;

@Controller
public class TaskController {

    @RequestMapping(value = "tm/ping", method = RequestMethod.GET)
    public @ResponseBody String respondToPing()
    {
        Logger.getLogger("tm").info("ping");
        return "Task manager responding to ping at " + System.currentTimeMillis();
    }
    
    @RequestMapping(value = "tm/writetoexcel", method = RequestMethod.POST)
    public @ResponseBody String respondToWriteToExcel(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        Logger.getLogger("tm").info("writetoexcel");
        
        Response responseObj = new Response(HttpServletResponse.SC_OK, Response.RESPONSE_MSG_SUCCESS);
        ObjectMapper objectMapper = new ObjectMapper();
        String exceldata = request.getParameter("exceldata");
        
        if(exceldata != null)
        {
            JSONArray excelJsonArray = new JSONArray();
            
            XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream out = new FileOutputStream(new File("employee.xlsx"));
            workbook.write(out);
            out.close();
        }
        String ResponseString = objectMapper.writeValueAsString(responseObj);
        return ResponseString;
    }
}
