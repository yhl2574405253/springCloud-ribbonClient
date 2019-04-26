package cn.et;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RibbonController {
    @Autowired
    RestTemplate restTemplate;

    /**
     * 用来测试是什么算法
     */
    @Autowired
    private LoadBalancerClient loadBalancer;

    /**
     * 用来测试用ribbon通过post调用服务
     * @return
     */
    @RequestMapping("/test1")
    public String send(){
        try {
//          设置请求头
            HttpHeaders headers =new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

//          设置请求体
            Map map =new HashMap();
            map.put("receiveName", "2574405253@qq.com");
            map.put("title", "测试ribbon发送邮件");
            map.put("content", "ribbon hello");
            HttpEntity request = new HttpEntity(map,headers);

            String result = restTemplate.postForObject("http://mail-center/test1", request, String.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    /**
     * 用来测试时什么算法
     * @return
     */
    @RequestMapping("test2")
    public String Test1(){
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < 10; i++) {
            //从两个idServer中选择一个  这里涉及到选择算法
            ServiceInstance ss = loadBalancer.choose("mail-center");
            sb.append(ss.getUri().toString()+"<br/>");
        }
        return sb.toString();
    }

    @RequestMapping("RibbonTest")
    @HystrixCommand(fallbackMethod = "error")
    public String test3(String name){
        String forObject = restTemplate.getForObject("http://mail-center/test2/{name}", String.class,name);
        return forObject;
    }

    public String error(String name){
        return name +"  Ribbon调用的mail-center服务已断开";
    }

}
