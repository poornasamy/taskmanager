package com.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpManager {
    private String url;
    private String requestBody;
    
    public HttpManager(String url, String requestBody) {
        this.url = url;
        this.requestBody = requestBody;
    }
    public String makeRequest() throws Exception {
        String responseString = null;
        HttpUriRequest request = null; 
                
        if(requestBody == null)
        {
            byte[] bytes = requestBody.getBytes("UTF-8");
            ByteArrayEntity input = new ByteArrayEntity(bytes);
            input.setContentType("application/json");
            HttpPost postRequest = new HttpPost(url);
            postRequest.setEntity(input);
            request = postRequest;
        }
        else
        {
            request = new HttpGet(url);
        }
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse httpResponse = client.execute(request);
        responseString = EntityUtils.toString(httpResponse.getEntity());
                
        return responseString;
    }
}
