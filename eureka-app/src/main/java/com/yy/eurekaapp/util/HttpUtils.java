package com.yy.eurekaapp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import org.springframework.util.StringUtils;


public class HttpUtils {

	private final static Logger log = LoggerFactory.getLogger(HttpUtils.class);
	/** 发送http请求超时时间 */
	private static int timeout = Integer.valueOf(60000);

	/**
	 * 
	 * 在HttpServletRequest获取提交数据
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestMsg(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("UTF-8");
		InputStream is = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			 is = request.getInputStream();
			byte[] buffer = new byte[256];

			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}

			byte[] readBytes = bos.toByteArray();
			return new String(readBytes, "UTF-8");
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}finally {
			try{
				if(is!=null){
					is.close();
				}

			}catch (IOException e){
				throw new IOException(e.getMessage());
			}
		}
	}
	/**
	 * 字符串转map
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> getStringRequestByMap(HttpServletRequest request) throws IOException {
		Map<String, Object> map = null;
		String result = getRequestMsg(request);
		if(!StringUtils.isEmpty(result)){
			map = new HashMap<String, Object>();
			String[] prama = result.split("&");
			if(!StringUtils.isEmpty(prama)){
				for(String backStr : prama){
					String[] myMap = backStr.split("=");
					if(!StringUtils.isEmpty(myMap) && myMap.length == 1){
						map.put(myMap[0], null);
					}else{
						map.put(myMap[0], myMap[1]);
					}
				}
			}
		}
		
		return map;
	}
	/**
	 * 将收到的数据放入到map
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TreeMap<String, String> getReq2Map(HttpServletRequest request) {
		TreeMap<String, String> treeMap = new TreeMap<>();
		Enumeration<String> names = request.getParameterNames();
		log.info("getReq2Map()....................." + names);
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			treeMap.put(name, request.getParameter(name));
		}
		log.info("getReq2Map()....................." + JSONObject.toJSONString(treeMap));
		return treeMap;
	}
	
	/**
	 * 
	 * 在HttpServletRequest获取提交数据
	 * 穿过的的参数中已是UTF-8的编码 则不需要重新编码
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getRequestMsgUTF(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("UTF-8");
		InputStream is = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			is = request.getInputStream();
			byte[] buffer = new byte[256];

			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}

			byte[] readBytes = bos.toByteArray();
			return new String(readBytes);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}finally {
			try {
				if(is!=null){
					is.close();
				}
			}catch (IOException e){
				log.error(e.getMessage());
			}
		}
	}
	

	/**
	 * 
	 * servlet中发送响应数据
	 * 
	 * @param response
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static void sendResponse(HttpServletResponse response, String msg) throws IOException {

		OutputStream os = null;
		try {
			byte[] data = msg.getBytes("UTF-8");
			response.setContentLength(data.length);
			response.setCharacterEncoding("UTF-8");
			response.setHeader("HTTP-Version", "HTTP/1.1");
			os = response.getOutputStream();
			os.write(data);
			os.flush();
		} catch (IOException e) {

			throw new IOException(e);
		}finally {
			try {
				if(os!=null){
					os.close();
				}
			}catch (IOException e){
				log.error("sendResponse 报错:"+e);
			}

		}
	}

	public static String sendPost(String url, String data) {
		return sendPost(url, data, "", "UTF-8");
	}
	
	public static String sendPost(String url, String data,String encodeing) throws Exception {
		return sendPost(url, data, "", encodeing);
	}
	
	/**
	 * 发送http post请求，编码方式UTF-8
	 * 
	 * @param data
	 * @return
	 * @throws MsgsendException
	 */
	public static String sendPost(String url, String data, String contentType,String encodeing) {

		OutputStream outputStream = null;
		HttpURLConnection conn = null;
		ByteArrayOutputStream bos = null;//接收数据
		InputStream inputStream = null;
		try {
			URL remoteUrl = new URL(url);
			conn = (HttpURLConnection) remoteUrl.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setConnectTimeout(timeout);
			conn.setReadTimeout(timeout);
			if(!StringUtils.isEmpty(contentType)){
				conn.setRequestProperty("Content-type", contentType);
			}

			
		
			// 发送数据
			byte[] datas = data.getBytes("UTF-8");
			if(!StringUtils.isEmpty(encodeing)){
				datas = data.getBytes(encodeing);
			}
			outputStream = conn.getOutputStream();
			outputStream.write(datas, 0, datas.length);
			outputStream.flush();
			// 读取返回数据
			 inputStream = conn.getInputStream();

			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			byte[] readBytes = bos.toByteArray();
			
			return new String(readBytes, "UTF-8");
		} catch (MalformedURLException e) {
			log.error("发送[{}]http post异常",url, e);
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			log.error("发送[{}]http post异常",url, e);
			throw new RuntimeException(e.getMessage());
		} finally {
			
			try {
				if(outputStream!=null)
					outputStream.close();

			} catch (IOException e) {
				log.error("发送http post异常", e);
			}
			try {
				if(bos!=null)
					bos.close();
			} catch (IOException e) {
				log.error("发送http post异常", e);
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			}catch (IOException e){
				log.error("发送http post异常", e);
			}
		}
	}


	/**
	 * 加入相关超时时间
	 * @param url
	 * @param data
	 * @param contentType
	 * @param encodeing
	 * @param timeoutTime
	 * @return
	 */
	public static String sendPost(String url, String data, String contentType,String encodeing,int timeoutTime) {

		OutputStream outputStream = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			URL remoteUrl = new URL(url);
			conn = (HttpURLConnection) remoteUrl.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setConnectTimeout(timeoutTime);
			conn.setReadTimeout(timeoutTime);
			if(!StringUtils.isEmpty(contentType)){
				conn.setRequestProperty("Content-type", contentType);
			}



			// 发送数据
			byte[] datas = data.getBytes("UTF-8");
			if(!StringUtils.isEmpty(encodeing)){
				datas = data.getBytes(encodeing);
			}
			outputStream = conn.getOutputStream();
			outputStream.write(datas, 0, datas.length);
			outputStream.flush();
			// 读取返回数据
			inputStream = conn.getInputStream();

			String res = null;
			try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[256];
				int len = 0;
				while ((len = inputStream.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				byte[] readBytes = bos.toByteArray();
				res = new String(readBytes, "UTF-8");
			}

			return res;
		} catch (MalformedURLException e) {
			log.error("发送[{}]http post异常",url, e);
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			log.error("发送[{}]http post异常",url, e);
			throw new RuntimeException(e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("发送http post异常", e);
				}
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			}catch (IOException e){
				log.error("发送http post异常", e);
			}

		}
	}
	

	public static String sendGet(String url, String params) {
		String result = "";
		InputStream inputStream = null;
		ByteArrayOutputStream bos=null;
		try {
			String urlNameString = url;
			if (params != null && (!"".equals(params))) {
				urlNameString = url + "?" + params;
			}

			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();

			// 定义 BufferedReader输入流来读取URL的响应
			inputStream = connection.getInputStream();

			bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[256];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			byte[] readBytes = bos.toByteArray();
			result = new String(readBytes, "UTF-8");
			bos.close();
		} catch (Exception e) {
			log.error("发送GET请求出现异常", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if(bos!=null)
					bos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 时间转换机制 yyyy-MM-dd HH:mm:ss 24小时制
	 * 
	 * @param str
	 * @return
	 */
	public static Date stringToDateYYYYMMDD(String str) {
		try {
			if (null == str || "".equals(str)) {
				return null;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(str);
			return date;
		} catch (Exception e) {
			log.error("处理请求数据异常");
			e.printStackTrace();
		}
		return null;
	}

	
	

	
	
	/**
	 * 把请求参数解析到MAP中
	 * @param hsr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TreeMap<String ,String> getReqStringValue2Map(HttpServletRequest hsr) {
		TreeMap<String ,String> params = new TreeMap<String ,String> () ;
		Map<String, String[]> names = hsr.getParameterMap() ;
		log.info("请求转换参数名称 : " + JSONObject.toJSONString(names));
		log.info("请求转换参数值: " + JSONObject.toJSONString(hsr.getParameterMap()));
		if (!StringUtils.isEmpty(names)) {
			for (String key : names.keySet()) {
				String [] values = names.get(key) ;
				if(!StringUtils.isEmpty(values)) {
					params.put(key,values[0]) ;
				} else {
					params.put(key,"") ;
				}
			}
		}
		log.info(" -end-- "  + params);
		return params ;
	}
	/**
	 * 把请求参数解析到MAP中
	 * @param hsr
	 * @return
	 * 			TreeMap<String ,Object>
	 * @throws IOException 
	 */
	public static Map<String ,Object> getReqObjectValueMap(HttpServletRequest hsr) throws IOException {
		Map<String ,Object> params = new HashMap<String ,Object>();
		String jsonStr = getRequestMsg(hsr);
		JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
		if(!StringUtils.isEmpty(jsonStr)){
			Set<String> set = jsonObject.keySet();
			Iterator<String> it = set.iterator();  
			
			while (it.hasNext()) {
				String key = it.next() ;
				params.put(key, jsonObject.get(key)) ;
			}
		}	
		return params ;
	}
	
	/**
	 * 把请求参数解析到MAP中
	 * @param hsr
	 * @return
	 * 			TreeMap<String ,Object>
	 * @throws IOException 
	 */
	public static Map<String ,String> getReqStringtValueMap(HttpServletRequest hsr) throws IOException {
		Map<String ,String> params = new HashMap<String ,String>();
		String jsonStr = getRequestMsg(hsr);
		JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
		if(!StringUtils.isEmpty(jsonStr)){
			Set<String> set = jsonObject.keySet();
			Iterator<String> it = set.iterator();  
			
			while (it.hasNext()) {
				String key = it.next() ;
				params.put(key, jsonObject.getString(key)) ;
			}
		}else{
			params = getReq2Map(hsr);
		}
		return params ;
	}
	
	
	public static String toParam(Map<String, Object> map){
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if(entry.getValue() != null ){
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		return sb.toString();
	}

	/**
	 * 根据request请求获取相关ip地址
	 * @param httpServletRequest
	 * @return
	 */
	public static String getIp(HttpServletRequest httpServletRequest){
		String ip;
		ip = httpServletRequest.getHeader("x-forwarded-for");
		//针对IP是否使用代理访问进行处理
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getRemoteAddr();
		}
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(","));
		}
		return ip;
	}

	/**
	 * 判断ip是否是局域网ip
	 * @param ip
	 * @return true 是局域网ip false 非局域网ip
	 */
	public static boolean internalIp(String ip) {
		if(Pattern.matches("192.168.*", ip))
			return true;
		if(Pattern.matches("172\\.([1][6-9]|[2]\\d|3[01])(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}", ip))
			return true;
		if(Pattern.matches("10.*", ip))
			return true;
		if(ip.equals("0:0:0:0:0:0:0:1"))
			return true;
		return false;
		
	}
	
}
