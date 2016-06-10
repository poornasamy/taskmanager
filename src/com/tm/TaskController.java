package com.tm;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskController {

    @RequestMapping(value = "tm/ping", method = RequestMethod.GET)
    public @ResponseBody String respondToPing()
    {
        Logger.getLogger("tm").info("ping");
        return "Task manager responding to ping at " + System.currentTimeMillis();
    }
}
