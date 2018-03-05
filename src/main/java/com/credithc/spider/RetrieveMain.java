package com.credithc.spider;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RetrieveMain {
    /**
     * 获取的Json数据存入文件的路径
     */
    private String fileNamePath;
    /**
     * 需要写如文件信息的路径
     */
    private String path;
    /**
     * 需要写入信息的文件名
     */
    private String fileName;
    /**
     * 读取文件的路径
     */
    private String readerFilesPath;
    /**
     * print logs
     */
    private Logger log = Logger.getLogger(Test.class);
    /**
     * 存储AF1001请求记录
     */
    private List<String> af1001 = new ArrayList<>();
    /**
     * 存储AF1002请求记录
     */
    private List<String> af1002 = new ArrayList<>();
    /**
     * 发送请求的url
     */
    private String url;
    private File[] files = null;

    public void postEntrance() {
        // 读取配置文件
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(RetrieveMain.class.getClassLoader().getResourceAsStream("config.properties"), "UTF-8"));
            readerFilesPath = (String) properties.get("readerFilesPath");
            path = (String) properties.get("path");
            fileName = (String) properties.get("fileName");
            url = (String) properties.get("url");
            if (readerFilesPath == null || StringUtils.isBlank(readerFilesPath) || path == null || StringUtils.isBlank(path) || fileName == null || StringUtils.isBlank(fileName) || url == null || StringUtils.isBlank(url)) {
                log.error("config.properties" + "配置文件内容错误");
            } else {
                if (checkFileExist()) {
                    createFile(fileName, path);
                    af1001.stream().forEach(it -> {
                        System.out.print("00000000000000000000000000000000000000000" + it);
                        HttpTool.sendPost(it, url);
                    });
                    af1002.stream().forEach(it -> {
                        HttpTool.sendPost(it, url);
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件
     *
     * @return true 文件存在
     * false 文件不存在
     */
    private boolean checkFileExist() {
        File directory = new File(readerFilesPath);//文件路径(包括文件名称)
        files = directory.listFiles();
        log.info(readerFilesPath + "该目录下对象个数：" + files.length);
        if (files.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 创建文件
     *
     * @param fileName 名称文件
     * @param tpath    文件路径
     */

    public void createFile(String fileName, String tpath) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = dateFormat.format(now);
        fileNamePath = tpath + fileName + currentTime + ".txt";
        File f = new File(fileNamePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
            writeFileContent();
        } catch (IOException e) {
            log.error(fileNamePath + ":" + "Error creating file" + e.getMessage());
        }
    }

    /**
     * 将获得的请求json存储
     *
     * @return
     */

    private void writeFileContent() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(fileNamePath));
            int k = 0;
            StringBuffer buffer = new StringBuffer("");
            for (int i = 0; i < files.length; i++) {
                try {
                    if (files[i].isDirectory()) continue;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[i])));
                    while (reader.ready()) {
                        String readerLine = reader.readLine();
                        String regEx = ":\\{\"applicants\":.*";
                        Pattern p = Pattern.compile(regEx);
                        Matcher matcher = p.matcher(readerLine);

                        while (matcher.find()) {
                            k = k + 1;
                            matcher.groupCount();
                            System.out.print(matcher.group() + "\n");
                            if (matcher.group().contains("AF1001")) {
                                af1001.add(matcher.group());
                            } else {
                                af1002.add(matcher.group());
                            }
                            buffer.append(matcher.group() + "\n");
                        }
                    }
                    log.info(buffer.toString());
                    pw.write(buffer.toString() + "以上内容是从" + files[i].getName() + "文件中获取的请求记录总数" + k + "条" + "\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.flush();
            pw.close();
        }
    }
}
