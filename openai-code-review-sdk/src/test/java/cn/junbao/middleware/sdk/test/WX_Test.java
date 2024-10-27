package cn.junbao.middleware.sdk.test;

import cn.junbao.middleware.sdk.types.utils.WXAccessTokenUtils;
import com.alibaba.fastjson2.JSON;
import org.junit.Test;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WX_Test {
    @Test
    public void test_wxMessage(){
        String accessToken = WXAccessTokenUtils.getAccessToken();
        System.out.println("accessToken : "+ accessToken);
        //微信请求参数信息
        Message message = new Message();
        message.put("auditTime","20241027");
        message.put("message","消息测试测试");

        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s",accessToken);

        System.out.println("===");
        System.out.println(JSON.toJSONString(message));
        System.out.println("===");
        sendPostRequest(url, JSON.toJSONString(message));

    }
    private static void sendPostRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Message {
        private String touser = "omDlA6zKW08YwGTp9ZCEBY6t8cOY";
        private String template_id = "I0GUueCOsgfCVrCfcKGzG26PxT1QCcLDNVklMVnqkMk";
        private String url = "https://github.com/boopoobooo/Bernie-openai-code-review/blob/master/readme.md";
        private Map<String, Map<String, String>> data = new HashMap<>();

        public void put(String key, String value) {
            data.put(key, new HashMap<String, String>() {
                {
                    put("value", value);
                }
            });
        }

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Map<String, String>> getData() {
            return data;
        }

        public void setData(Map<String, Map<String, String>> data) {
            this.data = data;
        }
    }

}
