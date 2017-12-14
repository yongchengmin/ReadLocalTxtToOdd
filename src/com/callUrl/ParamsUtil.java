package com.callUrl;

import java.util.HashMap;
import java.util.Map;

public class ParamsUtil {
	
	public static Map<String,String> convertObjectToStringParams(Map<String,Object> paramMap)
	{
		Map<String,String>postMap = new HashMap<String,String>();
		for(String key:paramMap.keySet())
		{
			postMap.put(key, paramMap.get(key).toString());
		}
		return postMap;
		
	}

}
