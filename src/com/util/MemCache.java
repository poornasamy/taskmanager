package com.util;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemCache {
    private MemcachedClient client;
    
    public MemCache(MemcachedClient client) {
        this.client = client;
    }
    
    public static MemcachedClient getMemcachedClient(String servers) throws Exception { 
         MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers)); 
         return builder.build(); 
     }
    
    public void set(String paramString, int paramInt,
            Object paramObject) throws Exception {
        this.client.set(paramString, paramInt, paramObject);
    }
    
    public String get(String paramString) throws Exception {
        return this.client.get(paramString);
    }
    
    public boolean shutdown() throws Exception {
        if(this.client != null)
        {
            this.client.shutdown();
        }
        return this.client.isShutdown();
    }
}