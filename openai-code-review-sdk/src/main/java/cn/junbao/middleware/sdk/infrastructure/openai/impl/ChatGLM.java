package cn.junbao.middleware.sdk.infrastructure.openai.impl;

import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionRequestDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionSyncResponseDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.IAICodeRevice;
import cn.junbao.middleware.sdk.types.utils.BearerTokenUtils;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ChatGLM implements IAICodeRevice {

    private final Logger logger = LoggerFactory.getLogger(ChatGLM.class);

    private final String apiHost;
    private final String apiKeySecret;

    public ChatGLM( String apiHost , String apiKeySecret) {
        this.apiHost = apiHost;
        this.apiKeySecret = apiKeySecret;
    }

    @Override
    public ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws IOException {
        String token = BearerTokenUtils.getToken(apiKeySecret);
        logger.info("[ChatGLM] #completions token = "+token);
        URL url = new URL(apiHost);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization","Bearer "+token);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(requestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input,0,input.length);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine ;

        while ((inputLine = reader.readLine()) != null){
            content.append(inputLine);
        }
        reader.close();
        connection.disconnect();

        return JSON.parseObject(content.toString(),ChatCompletionSyncResponseDTO.class);
    }
}
