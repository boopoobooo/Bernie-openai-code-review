package cn.junbao.middleware.sdk;

import cn.junbao.middleware.sdk.domain.service.impl.OpenAiCodeReviewService;
import cn.junbao.middleware.sdk.infrastructure.git.GitCommand;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionRequestDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionSyncResponseDTO;
import cn.junbao.middleware.sdk.domain.model.Message;
import cn.junbao.middleware.sdk.infrastructure.openai.impl.ChatGLM;
import cn.junbao.middleware.sdk.infrastructure.weixin.WeiXinMessage;
import cn.junbao.middleware.sdk.types.utils.BearerTokenUtils;
import cn.junbao.middleware.sdk.types.utils.WXAccessTokenUtils;
import com.alibaba.fastjson2.JSON;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class OpenAiCodeReview {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiCodeReview.class);

    //配置信息
    //1. 微信配置信息:
    private static final String WEIXIN_APPID = "wxde4ec457feb6dd87";
    private static final String WEIXIN_SECRET = "66e694731e30f3020266d9f6159680ce";
    private static final String WEIXIN_TOUSER = "omDlA6zKW08YwGTp9ZCEBY6t8cOY";
    private static final String WEIXIN_TEMPLATE_ID = "I0GUueCOsgfCVrCfcKGzG26PxT1QCcLDNVklMVnqkMk";

    //2. chatGLM 配置
    private String CHATGLM_APIHOST = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private String CHATGLM_APIKEYSECRET = "";

    //3. GitHub配置
    private String GITHUB_REVIEW_LOG_URI;
    private String github_token;

    // 工程配置 - github Action自动获取
    private String github_project;
    private String github_branch;
    private String github_author;

    public static void main(String[] args) {
        logger.info("[START] openAI Code Review start!");
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
                );

        WeiXinMessage weiXinMessage = new WeiXinMessage(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );

        ChatGLM chatGLM = new ChatGLM(
                getEnv("API_HOST"),
                getEnv("API_KEY_SECRET")
        );
        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand,chatGLM,weiXinMessage);
        openAiCodeReviewService.exec();

        logger.info("[END] openAI Code Review Done!");
    }


    public static String getEnv(String key){
        String value= System.getenv(key);
        if (null == value){
            throw new RuntimeException("get System env value is null");
        }
        return value;
    }





    /// 注释代码： 工程重构，不再使用，仅作为参考
    /*
    public static void main(String[] args) throws Exception {
        System.out.println("测试---openaiCodeReview-sdk");
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (null == githubToken){
            throw new RuntimeException("github Token is null");
        }

        //获取git分支变更代码
        String diffCode = getGitBranchChangeCode();

        //chatGlM 评审代码
        String reviewLog = codeReviewGLM(diffCode.toString());
        System.out.println("评审日志："+reviewLog);

        //编写日志
        String logUrl = writeLog(githubToken, reviewLog);
        System.out.println("writeLog：" + logUrl);

        // 微信公众号消息通知
        pushWXMessage(logUrl);

    }

    public static void pushWXMessage(String logUrl  ){
        String accessToken = WXAccessTokenUtils.getAccessToken();
        System.out.println("accessToken : "+ accessToken);

        String formatDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        //微信请求参数信息
        Message message = new Message();
        message.setUrl(logUrl);
        message.put("auditTime",formatDate);
        message.put("message","代码评审日志:"+ logUrl);

        String url = String.format("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s",accessToken);
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

    public static String  writeLog(String token , String log) throws  Exception{
        Git git = Git.cloneRepository()
                .setURI("https://github.com/boopoobooo/Bernie-openai-code-review-log.git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();

        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File dateFolder = new File("repo/" + dateFolderName);
        if (!dateFolder.exists()){
            dateFolder.mkdirs();
        }

        String fileName = generateRandomString(12)+".md";
        File newFile = new File(dateFolder, fileName);
        try(FileWriter fileWriter = new FileWriter(newFile)){
            fileWriter.write(log);
        }

        git.add().addFilepattern(dateFolderName + "/" + fileName).call();
        git.commit().setMessage("Add new File").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token,"")).call();
        return "https://github.com/boopoobooo/Bernie-openai-code-review-log/blob/main/" + dateFolderName + "/" + fileName;


    }
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }*/
}
