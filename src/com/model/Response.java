package com.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


public class Response implements Serializable {

    private static final long serialVersionUID = -8982153968540069681L;

    public static final String RESPONSE_MSG_SUCCESS = "Success";
    public static final String RESPONSE_MSG_FAILURE = "Failure";
    
    @Getter @Setter int responseCode;
    @Getter @Setter String responseMessage;
    
    public Response()
    {
        super();
    }
    
    public Response(int responseCode, String responseMessage)
    {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }
}