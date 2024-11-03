package cn.junbao.middleware.sdk.infrastructure.weixin;


import cn.junbao.middleware.sdk.infrastructure.weixin.DTO.TempleteMessageDTO;
import cn.junbao.middleware.sdk.types.utils.WXAccessTokenUtils;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class WeiXinMessage {
    private final Logger logger = LoggerFactory.getLogger(WeiXinMessage.class);

    private final String appId;
    private final String secret;
    private final String touser;
    private final String templateId;

    public WeiXinMessage(String appId, String secret, String touser, String templateId) {
        this.appId = appId;
        this.secret = secret;
        this.touser = touser;
        this.templateId = templateId;
    }

    public void sendTemplateMessage (String logUrl, Map<String ,Map<String ,String >> data){
        String accessToken = WXAccessTokenUtils.getAccessToken(appId, secret);
        TempleteMessageDTO templeteMessageDTO = new TempleteMessageDTO(touser, templateId);
        templeteMessageDTO.setUrl(logUrl);
        templeteMessageDTO.setData(data);
        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s",accessToken);
        sendPostRequest(url, JSON.toJSONString(templeteMessageDTO));

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
}
