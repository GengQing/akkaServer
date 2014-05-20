package com.good.akkaserver.event;

import java.util.HashMap;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;

public class ObjectPoolManage {
	
	private static HashMap<Class,StackObjectPool> pools = new HashMap<Class,StackObjectPool>();
    
    /**
     * This class is not instanceable.
     */
    private ObjectPoolManage() {
    }
    
    /**
     * Get a ObjectPool instance from the objectPools, if not found, then lazilly 
     * create a new one.
     * @param clazz the object type
     * @return the ObjectPool instance
     */
    public static synchronized StackObjectPool getPool(Class clasz,int size) {
    	StackObjectPool pool = pools.get(clasz);
        if(pool == null) {
        	PoolableObjectFactory fa = null;
        	try{
        		fa = (PoolableObjectFactory)clasz.newInstance();
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
            pool = new StackObjectPool(fa,size,100);
            pools.put(clasz, pool);
        }
        return pool;
    }

}
