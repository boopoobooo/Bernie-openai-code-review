package cn.junbao.middleware.sdk;

import cn.junbao.middleware.sdk.model.ChatCompletionRequest;
import cn.junbao.middleware.sdk.model.ChatCompletionSyncResponse;
import cn.junbao.middleware.sdk.types.utils.BearerTokenUtils;
import com.alibaba.fastjson2.JSON;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OpenAiCodeReview {
    public static void main(String[] args) throws Exception {
        System.out.println("测试---openaiCodeReview-sdk");

        String diffCode = getGitBranchChangeCode();

        //chatGlM 评审代码
        String reviewLog = codeReviewGLM(diffCode.toString());
        System.out.println("评审日志："+reviewLog);

    }
    public static String getGitBranchChangeCode() throws Exception {
        //1. 代码检出
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null){
            diffCode.append(line);
        }
        System.out.println("diffCode = "+  diffCode.toString());

        int exitCode = process.waitFor();
        System.out.println("Exited with code: " + exitCode );
        return diffCode.toString();
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

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel("glm-4-plus");
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>(){
            private static final long serialVersionUID = -7988151926241837899L;
            {add((new ChatCompletionRequest.Prompt("user","你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为:")));
            add(new ChatCompletionRequest.Prompt("user",diffCode));
            }});

        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = JSON.toJSONString(chatCompletionRequest).getBytes(StandardCharsets.UTF_8);
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

        ChatCompletionSyncResponse response = JSON.parseObject(content.toString(), ChatCompletionSyncResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
