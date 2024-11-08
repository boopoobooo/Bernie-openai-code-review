package cn.junbao.middleware.sdk.domain.service.impl;

import cn.junbao.middleware.sdk.domain.model.Model;
import cn.junbao.middleware.sdk.domain.service.AbstractOpenAICodeReviewService;
import cn.junbao.middleware.sdk.infrastructure.git.GitCommand;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionRequestDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionSyncResponseDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.IAICodeRevice;
import cn.junbao.middleware.sdk.infrastructure.weixin.DTO.TempleteMessageDTO;
import cn.junbao.middleware.sdk.infrastructure.weixin.WeiXinMessage;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenAiCodeReviewService extends AbstractOpenAICodeReviewService {
    public OpenAiCodeReviewService(GitCommand gitCommand, IAICodeRevice aiCodeRevice, WeiXinMessage weiXinMessage) {
        super(gitCommand, aiCodeRevice, weiXinMessage);
    }

    @Override
    protected String getGitDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) throws IOException {
        ChatCompletionRequestDTO chatCompletionRequest = new ChatCompletionRequestDTO();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequestDTO.Prompt>() {
            private static final long serialVersionUID = -7988151926241837899L;

            {
                add(new ChatCompletionRequestDTO.Prompt("user", "你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码如下:"));
                add(new ChatCompletionRequestDTO.Prompt("user", diffCode));
            }
        });

        ChatCompletionSyncResponseDTO completions = aiCodeRevice.completions(chatCompletionRequest);
        ChatCompletionSyncResponseDTO.Message message = completions.getChoices().get(0).getMessage();
        return message.getContent();
    }

    @Override
    protected String recordCodeReviewLog(String recommend) throws GitAPIException, IOException {
        return gitCommand.commitAndPush(recommend);
    }

    @Override
    protected void pushMessage(String codeReviewLogUrl) {
        Map<String,Map<String,String >> data = new HashMap<>();
        TempleteMessageDTO.put(data,TempleteMessageDTO.TemplateKey.REPO_NAME.getCode(), gitCommand.getProjectName());
        TempleteMessageDTO.put(data,TempleteMessageDTO.TemplateKey.BRANCH_NAME.getCode(), gitCommand.getBranch());
        TempleteMessageDTO.put(data,TempleteMessageDTO.TemplateKey.COMMIT_AUTHOR.getCode(), gitCommand.getAuthor());
        TempleteMessageDTO.put(data,TempleteMessageDTO.TemplateKey.COMMIT_MESSAGE.getCode(), gitCommand.getMessage());

        weiXinMessage.sendTemplateMessage(codeReviewLogUrl,data);
    }
}
