package cn.junbao.middleware.sdk.domain.service;

import cn.junbao.middleware.sdk.infrastructure.git.GitCommand;
import cn.junbao.middleware.sdk.infrastructure.openai.IAICodeRevice;

import java.io.IOException;

public abstract class AbstractOpenAICodeReviewService implements IOpenAICodeReviewService{

    protected final GitCommand gitCommand;

    protected final IAICodeRevice aiCodeRevice;
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getGitDiffCode() throws IOException, InterruptedException;

    protected abstract String codeReview(String diffCode) ;

    protected abstract String recordCodeReviewLog(String recommend);

    protected abstract void pushMessage(String codeReviewLogUrl);
}
