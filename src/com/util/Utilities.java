package com.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utilities {
    public static String getStringFromInputStream (InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String str = null;
        
        while((str = br.readLine()) != null)
        {
            sb.append(str);
        }   
        
        return sb.toString();
        
    }
}
