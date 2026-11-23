package com.ruoyi.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.opc.OpcProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class HttpClientUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    /** 远程系统登录接口，项目启动时及 token 失效时调用 */
    private static final String LOGIN_URL = "http://{}:{}/wyUser/userLogIn?account=device&password=device";

    /** 公共请求头，token 获取后动态写入 token / Authorization */
    private static final Map<String, String> head = new ConcurrentHashMap<String, String>();

    static {
        head.put("Content-Type", "application/x-www-form-urlencoded");
        head.put("Cache-Control", "no-cache");
    }

    private static volatile boolean tokenInitialized = false;
    private static final Object TOKEN_LOCK = new Object();

    /**
     * 项目启动时调用，初始化远程系统 token。
     */
    public static void initToken() {
        if (StrUtil.isBlank(OpcProperties.ip) || StrUtil.isBlank(OpcProperties.port)) {
            log.warn("远程系统 IP 或端口未配置，跳过 token 初始化");
            return;
        }
        refreshToken();
        tokenInitialized = true;
    }

    /** 首次业务请求时兜底初始化，避免启动时未能初始化成功 */
    private static void ensureToken() {
        if (!tokenInitialized) {
            synchronized (TOKEN_LOCK) {
                if (!tokenInitialized) {
                    refreshToken();
                    tokenInitialized = true;
                }
            }
        }
    }

    /**
     * 调用登录接口获取 token（原实例初始化块中的方法逻辑）。
     * 使用 synchronized 防止并发请求重复登录。
     */
    public static synchronized void refreshToken() {
        String url = StrUtil.format(LOGIN_URL, OpcProperties.dispatchIp, OpcProperties.dispatchPort);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setSocketTimeout(10000)
                    .build());
            // 登录请求本身不带 token，直接执行，避免与 executeWithToken 形成递归
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String body = entity == null ? null : EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject = JSON.parseObject(body);
            if (jsonObject != null && jsonObject.containsKey("token")) {
                String token = jsonObject.getString("token");
                if (StrUtil.isNotBlank(token)) {
                    head.put("token", token);
                    head.put("Authorization", "Bearer " + token);
                    log.info("远程系统 token 获取成功");
                }
            } else {
                log.warn("远程系统登录接口未返回 token，响应：{}", body);
            }
        } catch (Exception e) {
            log.error("获取远程系统 token 失败：" + url, e);
        } finally {
            closeQuietly(response, httpClient);
        }
    }

    /** 统一设置请求头（每次请求前刷新，避免使用过期 token） */
    private static void applyHeaders(HttpRequestBase request) {
        for (Entry<String, String> entry : head.entrySet()) {
            // 不覆盖请求已显式设置的 Content-Type（如 JSON 请求应为 application/json）
            if ("Content-Type".equalsIgnoreCase(entry.getKey()) && request.containsHeader("Content-Type")) {
                continue;
            }
            request.setHeader(entry.getKey(), entry.getValue());
        }
    }

    /** 执行请求；检测到 token 失效时自动重新登录并重试一次 */
    private static CloseableHttpResponse executeWithToken(CloseableHttpClient httpClient, HttpRequestBase request) throws IOException {
        ensureToken();
        applyHeaders(request);
        CloseableHttpResponse response = httpClient.execute(request);
        if (isTokenInvalid(response)) {
            log.warn("远程系统 token 已失效，重新获取后重试请求：{}", request.getURI());
            EntityUtils.consumeQuietly(response.getEntity());
            response.close();
            refreshToken();
            applyHeaders(request);
            response = httpClient.execute(request);
        }
        return response;
    }

    /** 判断响应是否表示 token 失效（HTTP 401/403 或响应体中的失效标识） */
    private static boolean isTokenInvalid(CloseableHttpResponse response) throws IOException {
        if (response == null) {
            return false;
        }
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_UNAUTHORIZED || status == HttpStatus.SC_FORBIDDEN) {
            return true;
        }
        HttpEntity entity = response.getEntity();
        if (entity == null || entity.getContentType() == null) {
            return false;
        }
        String contentType = entity.getContentType().getValue();
        // 仅解析 JSON/文本响应，避免文件下载时将二进制内容缓存进内存
        if (!contentType.contains("json") && !contentType.contains("text")) {
            return false;
        }
        BufferedHttpEntity buffered = new BufferedHttpEntity(entity);
        response.setEntity(buffered);
        String body = EntityUtils.toString(buffered, "UTF-8");
        if (StrUtil.isBlank(body)) {
            return false;
        }
        try {
            JSONObject json = JSON.parseObject(body);
            if (json == null) {
                return false;
            }
            String code = json.getString("code");
            if ("401".equals(code) || "403".equals(code) || "1001".equals(code)) {
                return true;
            }
            String msg = json.getString("msg");
            if (msg == null) {
                msg = json.getString("message");
            }
            if (msg != null && (msg.contains("token") || msg.contains("失效")
                    || msg.contains("过期") || msg.contains("未登录") || msg.contains("登录状态"))) {
                return true;
            }
        } catch (Exception ignored) {
            // 非 JSON 响应（如文件下载），不按 token 失效处理
        }
        return false;
    }

    private static void closeQuietly(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                log.error("关闭响应失败", e);
            }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error("关闭 HttpClient 失败", e);
            }
        }
    }

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
            // 设置请求头（统一由 applyHeaders 注入最新 token）
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = executeWithToken(httpClient, httpGet);
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
            // 设置请求头（统一由 applyHeaders 注入最新 token）
            // 配置请求参数实例
            RequestConfig config = RequestConfig.custom().setConnectTimeout(35000)// 设置连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 设置连接请求超时时间
                    .setSocketTimeout(60000)// 设置读取数据连接超时时间
                    .build();
            httpGet.setConfig(config);
            response = executeWithToken(httpClient, httpGet);
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
            // 设置请求头（统一由 applyHeaders 注入最新 token）
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = executeWithToken(httpClient, httpGet);
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
            // 设置请求头（统一由 applyHeaders 注入最新 token）
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = executeWithToken(httpClient, httpGet);
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
        // 设置请求头（统一由 executeWithToken 注入最新 token）
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
            httpResponse = executeWithToken(httpClient, httpPost);

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
        // 显式声明 JSON 请求体类型，避免被全局 head 的 form-urlencoded 覆盖导致服务端 @RequestBody 解析失败
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        // 设置请求头（统一由 executeWithToken 注入最新 token）
        try {
            // 封装post请求参数
            httpPost.setEntity(new ByteArrayEntity(json.getBytes("utf-8")));
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = executeWithToken(httpClient, httpPost);

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
        // 设置请求头（统一由 executeWithToken 注入最新 token）
        try {
            // 封装post请求参数
            httpPost.setEntity(httpEntity);
            // httpClient对象执行post请求,并返回响应参数对象
            httpResponse = executeWithToken(httpClient, httpPost);

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
