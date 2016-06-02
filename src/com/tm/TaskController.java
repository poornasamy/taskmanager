package com.tm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TaskController {

    @RequestMapping(value = "pim/ping", method = RequestMethod.GET)
    public @ResponseBody String respondToPing()
    {
        return "Task manager responding to ping at " + System.currentTimeMillis();
    }
}
