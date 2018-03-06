package com.retrieving.data;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;



public class HttpTool {
    private static Logger log = Logger.getLogger(HttpTool.class);

    /**
     * 发送post请求
     *
     * @param params     参数
     * @param requestUrl 请求地址
     * @return 返回结果
     * @throws IOException
     */
    public static void sendPost(String params, String requestUrl) {

        try {
            byte[] requestBytes = params.getBytes("utf-8"); // 将参数转为二进制流
            HttpClient httpClient = new HttpClient();// 客户端实例化
            PostMethod postMethod = new PostMethod(requestUrl);
            //设置请求头Authorization
//        postMethod.setRequestHeader("", " ");
            // 设置请求头  Content-Type
            postMethod.setRequestHeader("Content-Type", "application/json");
            InputStream inputStream = new ByteArrayInputStream(requestBytes, 0,
                    requestBytes.length);
            RequestEntity requestEntity = new InputStreamRequestEntity(inputStream,
                    requestBytes.length, "application/json; charset=utf-8"); // 请求体
            postMethod.setRequestEntity(requestEntity);
            httpClient.executeMethod(postMethod);// 执行请求
            InputStream soapResponseStream = postMethod.getResponseBodyAsStream();// 获取返回的流
            byte[] datas = null;
            try {
                datas = readInputStream(soapResponseStream);// 从输入流中读取数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            String result = new String(datas, "UTF-8");// 将二进制流转为String
            log.info("Request return result:" + result);
        } catch (IOException e) {
            log.error("Send the request exception" + e.getMessage());

        }
    }

    /**
     * 从输入流中读取数据
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }
}