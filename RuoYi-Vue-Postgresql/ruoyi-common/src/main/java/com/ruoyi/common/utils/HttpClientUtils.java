package com.ruoyi.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

public class HttpClientUtils {

    private static Map<String,String> head = new HashMap<String,String>(){{
        put("Content-Type","application/x-www-form-urlencoded");
        put("Cache-Control","no-cache");
        put("Authorization","Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
    }};
    /**
     * 无参get请求
     * @param url
     * @return
     */
    public static String doGet(String url) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头
            for (String key : head.keySet()) {
              httpGet.setHeader(key,head.get(key));
            }
            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 带参get请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String doGet(String url,Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        List<NameValuePair> nvps = getEntity(paramMap);
        String result = "";

        try{
            httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(nvps);
            HttpGet httpGet = new HttpGet(builder.build());
            // 设置请求头
            for (String key : head.keySet()) {
                httpGet.setHeader(key,head.get(key));
            }
            // 配置请求参数实例
            RequestConfig config = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                    .setSocketTimeout(60000)// 设置读取数据连接超时时间
                    .build();
            httpGet.setConfig(config);
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        }catch (URISyntaxException e){
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static byte[] downLoadFile(String url) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        byte[] bytes = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头
            for (String key : head.keySet()) {
                httpGet.setHeader(key,head.get(key));
            }
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            bytes = readInputStream(content);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    public static byte[] downLoadFile(String url,Map<String,Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        List<NameValuePair> nvps = getEntity(paramMap);
        byte[] bytes = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(nvps);
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(builder.build());
            // 设置请求头
            for (String key : head.keySet()) {
                httpGet.setHeader(key,head.get(key));
            }
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            bytes = readInputStream(content);
        } catch (URISyntaxException e){
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    /**
     * post请求
     * @param url
     * @param paramMap
     * @return
     */
    public static String doPost(String url, Map<String, Object> paramMap) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        for (String key : head.keySet()) {
            httpPost.setHeader(key,head.get(key));
        }
        // 封装post请求参数
        if (null != paramMap && paramMap.size() > 0) {
            List<NameValuePair> nvps = getEntity(paramMap);
            // 为httpPost设置封装好的请求参数
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        try {
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);

            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 参数为json的post请求
     * @param url
     * @param json
     * @return
     */
    public static String doPost(String url, String json){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        for (String key : head.keySet()) {
            httpPost.setHeader(key,head.get(key));
        }
        try {
            // 封装post请求参数
            httpPost.setEntity(new ByteArrayEntity(json.getBytes("utf-8")));
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);

            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 上传文件的post请求
     * @param url
     * @param fileName
     * @param file
     * @param paramMap
     * @return
     */
    public static String doPost(String url,String fileName,File file,Map<String, Object> paramMap){
        Map<String,File> files = new HashMap<>();
        files.put(fileName,file);
        String result = doPost(url, files, paramMap);
        return result;
    }

    /**
     * 上传多个文件的post请求
     * @param url
     * @param files
     * @param paramMap
     * @return
     */
    public static String doPost(String url, Map<String,File> files,Map<String, Object> paramMap){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        String result = "";
        // 创建httpClient实例
        httpClient = HttpClients.createDefault();
        // 创建httpPost远程连接实例
        HttpPost httpPost = new HttpPost(url);
        //构建参数

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        //设备浏览器兼容
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        //设备格式
//        builder.setCharset(Consts.UTF_8);
//        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        //放入上传文件
        for (String key : files.keySet()) {
            builder.addBinaryBody(key,files.get(key));
        }
        //放入参数
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
        for(Entry<String,Object> entry : paramMap.entrySet()){
            if (entry !=null) builder.addTextBody(entry.getKey(),entry.getValue().toString(),contentType);
        }
        HttpEntity httpEntity = builder.build();
        // 配置请求参数实例
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                .setSocketTimeout(60000)// 设置读取数据连接超时时间
                .build();
        // 为httpPost实例设置配置
        httpPost.setConfig(requestConfig);
        // 设置请求头
        for (String key : head.keySet()) {
            httpPost.setHeader(key,head.get(key));
        }
        try {
            // 封装post请求参数
            httpPost.setEntity(httpEntity);
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = httpClient.execute(httpPost);

            // 从响应对象中获取响应内容
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != httpResponse) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static List<NameValuePair> getEntity(Map<String, Object> paramMap){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 通过map集成entrySet方法获取entity
        Set<Entry<String, Object>> entrySet = paramMap.entrySet();
        // 循环遍历，获取迭代器
        Iterator<Entry<String, Object>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Entry<String, Object> mapEntry = iterator.next();
            nvps.add(new BasicNameValuePair(mapEntry.getKey(), mapEntry.getValue().toString()));
        }
        return nvps;
    }

    public static byte[] readInputStream(InputStream inputStream) throws  IOException{
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        out.close();
        return out.toByteArray();
    }

}
