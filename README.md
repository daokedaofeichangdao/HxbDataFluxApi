# spiderPlugin

## 目标  
为解决爬取过程中通过Spring RestTemplate重复性发送http/https请求的工作，使得该过程可以通过fiddler代理爬取(通过二次开发c#API)到的请求格式化文件自动生成相关的发送请求代码(基础的爬取API)，并分析出请求之间的相关关系找到可变参数体现在生成代码上;  

## 技术相关  
该项目使用的主要技术是freemarker，通过freemarker模板生成可用的java代码;
所有相关配置都在config.properties里面

## 功能实现情况  
分析可变参数主要是针对请求相同的url时，url后挂着的key-value参数和requestBody里面key-value参数的可变情况；  
很多特殊的情况目前没有覆盖，目前覆盖的情况大体如下：  
1. 两套数据上对应的两个请求没有任何变化，会直接生成一个请求函数，所以涉及到的参数都是硬编码在代码里面；  
2. url后面挂着的key-value参数在多个对应的请求中有变化，requestBody中的key-value参数没有变化；  
3. url后面挂着的key-value参数在多个对应的请求中没变化，requestBody中的key-value参数有变化；  
4. 情况2和情况3任意变化的组合方式都是支持的；  
5. 如果reuqestBody中是json格式，目前不支持深度分析json内容中具体字段的变化，仅支持简单的整个json内容比对；  

## 注意  
* 内容中所有的url是指整个url的一部分(full url:http://www.xxx.com/a/b/c?n=123&y=456 那么url是指http://www.xxx.com/a/b/c);  
* 如果url相同的请求有很多个，工具会根据url后挂着的key-value参数名+requestBody中参数名分组，生成多个函数，来支持参数组合变化的情况， 
  目前不支持多个相同的url采用了不同的请求方式(如：同时采用了GET And POST);

## 样例整理  
情况4.样例整理  

    @MethodInfo(fileName={"131456142771442989_userLogin.do.json","131456142771442990_userLogin.do.json"},params= {"password=batistuta&xiaobingbing","random=123456&liwei1341","validate_code=4529&9999","cust_no=shawn&liwei1341"})
    public ResponseEntity<String> doPOSTuserLogindo(
        CookieStoreRestTemplate restTemplate,
        String password,
        String random,
        String validate_code,
        String cust_no
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http");
        headers.set("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36");
        headers.set("Referer", "http");
        headers.set("Host", "www.hzgjj.gov.cn");
        headers.set("Pragma", "no-cache");
        headers.set("DNT", "1");
        headers.set("Accept-Encoding", "gzip, deflate");
        headers.set("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.set("Cache-Control", "no-cache");
        headers.set("X-Prototype-Version", "1.6.0.3");
        headers.set("Accept-Language", "zh-CN,zh;q=0.8");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("_", "");
        map.add("random", random);
        ResponseEntity<String> response = restTemplate.exchange("http://www.hzgjj.gov.cn:8080/WebAccounts/userLogin.do?user_type=3&cust_type=2&password=" + URLEncoder.encode(password,"UTF-8") + "&validate_code=" + URLEncoder.encode(validate_code,"UTF-8") + "&cust_no=" + URLEncoder.encode(cust_no,"UTF-8") + "", HTTP.POST, new HttpEntity<>(map, headers), String.class);

        return response;
    }
情况5.样例整理  

    @MethodInfo(fileName={"131459607063421029_PsnGetUserProfile.do.json","131459607063421028_PsnGetUserProfile.do.json"},params={"requestBodyJson={\"header\":{\"local\":\"zh_CN\",\"agent\":\"WEB15\",\"bfw-ctrl\":\"json\",\"version\":\"\",\"device\":\"\",\"platform\":\"\",\"plugins\":\"\",\"page\":\"\",\"ext\":\"\",\"cipherType\":\"0\"},\"request\":[{\"id\":16,\"method\":\"LoginPre\",\"conversationId\":\"7a76344b-465c-4377-b263-a5097de66666\",\"params\":{\"loginName\":\"qxt4833253@sina.com\",\"activ\":\"300000002\",\"state\":\"TUFDPWY4LWJjLTEyLTY4LWNjLWJkO0lQPTE5Mi4xNjguMTIuMjEyO0RJU0tJRD03MGUzOGY1ZTtC\\r\\nT0NHVUlEPXswNEI5Q0MwNy05NjMzLTRERDUtQjNGNC00ODg2N0Y5N0UwMzl9O09TPTE1MDYzKHg4\\r\\nNik7SUU9MTEuMC4xNTA2My40MTM7U1RBVEVDT0RFPTAwMDAwMDAwOw==\",\"password\":\"e0fOS6p5Qzicdd2Yv/zBRN3Xmn5lABOBroVz3dqOaw8=\",\"password_RC\":\" Iqxl0RWcbzqZOipRyHKWgjYnZIpFhau2mNb21VSmTGWYPoZ0aOB5Nn3zicsaZ9Pu5s2J8WkSlys\\r\\nhaoyNL1CjewDZQud6sCG6F8VjSeyyAMUla1k9pikBoWL8etg0OfkNRV8cFo CYCsUN6/3qWk8w==\",\"segment\":\"1\",\"devicePrint\":\"version=3.4.0.0_2&pm_fpua=mozilla/5.0 (compatible; msie 10.0; windows nt 6.2; trident/6.0)|5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)|Win32|0|x86|zh-CN|16384&pm_fpsc=24|1920|1080|1040&pm_fpsw=abk=10,0,15063,0|dht=11,296,15063,0|ieh=11,296,15063,0|iee=10,0,15063,296|wmp=12,0,10011,16384|obp=11,296,15063,0|oex=10,0,15063,0&pm_fptz=8&pm_fpln=lang=zh-CN|syslang=zh-CN|userlang=zh-CN&pm_fpjv=1&pm_fpco=1&pm_fpasw=&pm_fpan=Microsoft Internet Explorer&pm_fpacn=Mozilla&pm_fpol=true&pm_fposp=&pm_fpup=&pm_fpsaw=1920&pm_fpspd=24&pm_fpsbd=0&pm_fpsdx=96&pm_fpsdy=96&pm_fpslx=96&pm_fpsly=96&pm_fpsfse=true&pm_fpsui=0&pm_os=Windows&pm_brmjv=10&pm_br=Explorer&pm_inpt=1530&pm_expt=-1\"}}]}&{\"header\":{\"local\":\"zh_CN\",\"agent\":\"WEB15\",\"bfw-ctrl\":\"json\",\"version\":\"\",\"device\":\"\",\"platform\":\"\",\"plugins\":\"\",\"page\":\"\",\"ext\":\"\",\"cipherType\":\"0\"},\"request\":[{\"id\":16,\"method\":\"LoginPre\",\"conversationId\":\"7a76344b-465c-4377-b263-a5097de65807\",\"params\":{\"loginName\":\"qxt4833253@sina.com\",\"activ\":\"300000002\",\"state\":\"TUFDPWY4LWJjLTEyLTY4LWNjLWJkO0lQPTE5Mi4xNjguMTIuMjEyO0RJU0tJRD03MGUzOGY1ZTtC\\r\\nT0NHVUlEPXswNEI5Q0MwNy05NjMzLTRERDUtQjNGNC00ODg2N0Y5N0UwMzl9O09TPTE1MDYzKHg4\\r\\nNik7SUU9MTEuMC4xNTA2My40MTM7U1RBVEVDT0RFPTAwMDAwMDAwOw==\",\"password\":\"e0fOS6p5Qzicdd2Yv/zBRN3Xmn5lABOBroVz3dqOaw8=\",\"password_RC\":\" Iqxl0RWcbzqZOipRyHKWgjYnZIpFhau2mNb21VSmTGWYPoZ0aOB5Nn3zicsaZ9Pu5s2J8WkSlys\\r\\nhaoyNL1CjewDZQud6sCG6F8VjSeyyAMUla1k9pikBoWL8etg0OfkNRV8cFo CYCsUN6/3qWk8w==\",\"segment\":\"1\",\"devicePrint\":\"version=3.4.0.0_2&pm_fpua=mozilla/5.0 (compatible; msie 10.0; windows nt 6.2; trident/6.0)|5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)|Win32|0|x86|zh-CN|16384&pm_fpsc=24|1920|1080|1040&pm_fpsw=abk=10,0,15063,0|dht=11,296,15063,0|ieh=11,296,15063,0|iee=10,0,15063,296|wmp=12,0,10011,16384|obp=11,296,15063,0|oex=10,0,15063,0&pm_fptz=8&pm_fpln=lang=zh-CN|syslang=zh-CN|userlang=zh-CN&pm_fpjv=1&pm_fpco=1&pm_fpasw=&pm_fpan=Microsoft Internet Explorer&pm_fpacn=Mozilla&pm_fpol=true&pm_fposp=&pm_fpup=&pm_fpsaw=1920&pm_fpspd=24&pm_fpsbd=0&pm_fpsdx=96&pm_fpsdy=96&pm_fpslx=96&pm_fpsly=96&pm_fpsfse=true&pm_fpsui=0&pm_os=Windows&pm_brmjv=10&pm_br=Explorer&pm_inpt=1530&pm_expt=-1\"}}]}","_locale=zh_US&zh_CN"})
    public ResponseEntity<String> doPOSTPsnGetUserProfiledo(
        CookieStoreRestTemplate restTemplate,
        String requestBodyJson,
        String _locale
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "*/*");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("Cache-Control", "no-cache");
        headers.set("X-id", "17");
        headers.set("Referer", "https");
        headers.set("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
        headers.set("Host", "ebsnew.boc.cn");
        headers.set("Accept-Language", "zh-CN");
        headers.set("Accept-Encoding", "gzip, deflate");
        headers.set("bfw-ctrl", "json");
        headers.set("Content-Type", "text/json;");
        String map = requestBodyJson;
        ResponseEntity<String> response = restTemplate.exchange("https://ebsnew.boc.cn/BII/PsnGetUserProfile.do?_locale=" + URLEncoder.encode(_locale,"UTF-8") + "", HTTP.POST, new HttpEntity<>(map, headers), String.class);

        return response;
    }
