package cn.junbao.middleware.sdk.test;

import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionRequestDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionSyncResponseDTO;
import cn.junbao.middleware.sdk.types.utils.BearerTokenUtils;
import com.alibaba.fastjson2.JSON;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ApiTest {

    @Test
    public void test(){
        String apiKeySecret = "978f055fb6094e16863a5904be5eadcf.q1EoS2jIGub1L86A";
        String token = BearerTokenUtils.getToken(apiKeySecret);
        System.out.println(token);
    }

    @Test
    public void http_test() throws Exception {
        String result = codeReviewGLM("Integer.parseInt(\"abc\")");

        System.out.println(result);
    }

    public static String codeReviewGLM(String diffCode) throws  Exception{
        String apiKeySecret = "978f055fb6094e16863a5904be5eadcf.q1EoS2jIGub1L86A";
        String token = BearerTokenUtils.getToken(apiKeySecret);

        System.out.println(token);
        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization","Bearer "+token);
        connection.setRequestProperty("Content-Type","application/json");
        connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        connection.setDoOutput(true);

        ChatCompletionRequestDTO chatCompletionRequestDTO = new ChatCompletionRequestDTO();
        chatCompletionRequestDTO.setModel("glm-4-plus");
        chatCompletionRequestDTO.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>(){
            private static final long serialVersionUID = -7988151926241837899L;
            {add((new ChatCompletionRequestDTO.Prompt("user","你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为:")));
                add(new ChatCompletionRequestDTO.Prompt("user",diffCode));
            }});

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequestDTO).getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }
        int responseCode = connection.getResponseCode();
        System.out.println("responseCode: "+responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine ;

        while ((inputLine = reader.readLine()) != null){
            content.append(inputLine);
        }
        reader.close();
        connection.disconnect();

        ChatCompletionSyncResponseDTO response = JSON.parseObject(content.toString(), ChatCompletionSyncResponseDTO.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
