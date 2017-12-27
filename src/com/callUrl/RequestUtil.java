package com.callUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RequestUtil {

	public static final String ENCODING = "UTF-8";
	
	@SuppressWarnings("unused")
	private final static String CONTENT_TYPE_APPLICATION_JSON = "application/json";


	public static String post(Map<String, String> paramMap, String url) throws Exception {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				nvps.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(100000)
					.setConnectionRequestTimeout(100000).setSocketTimeout(300000).build();
			httpPost.setConfig(requestConfig);
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
			CloseableHttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = null;
			try {
				if (response != null) {
					entity = response.getEntity();
					if (entity != null) {
						result = EntityUtils.toString(entity, ENCODING);
					}
				}
//				System.out.println(response.getStatusLine());
				// do something useful with the response body
				// and ensure it is fully consumed

				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		return result;
	}

}
