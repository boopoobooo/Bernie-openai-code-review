package cn.junbao.middleware.sdk.domain.service;

import cn.junbao.middleware.sdk.infrastructure.git.GitCommand;
import cn.junbao.middleware.sdk.infrastructure.openai.IAICodeRevice;
import cn.junbao.middleware.sdk.infrastructure.weixin.WeiXinMessage;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractOpenAICodeReviewService implements IOpenAICodeReviewService{

    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAICodeReviewService.class);

    protected final GitCommand gitCommand;

    protected final IAICodeRevice aiCodeRevice;

    protected final WeiXinMessage weiXinMessage;

    public AbstractOpenAICodeReviewService(GitCommand gitCommand, IAICodeRevice aiCodeRevice, WeiXinMessage weiXinMessage) {
        this.gitCommand = gitCommand;
        this.aiCodeRevice = aiCodeRevice;
        this.weiXinMessage = weiXinMessage;
    }

    @Override
    public void exec() {
        try {
            //1. 获取评审代码
            String diffCode = getGitDiffCode();
            //2. 进行ai代码评审
            String recommend = codeReview(diffCode);
            //3. 写入评审日志
            String codeReviewLogUrl = recordCodeReviewLog(recommend);
            //4. 评审结果通知
            pushMessage(codeReviewLogUrl);
        } catch (Exception e ){
            logger.error("AI-codeReview ERROR!! ", e );
        }
    }

    protected abstract String getGitDiffCode() throws IOException, InterruptedException;

    protected abstract String codeReview(String diffCode) throws IOException;

    protected abstract String recordCodeReviewLog(String recommend) throws GitAPIException, IOException;

    protected abstract void pushMessage(String codeReviewLogUrl);
}
