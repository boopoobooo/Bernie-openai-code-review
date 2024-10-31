package cn.junbao.middleware.sdk.domain.service.impl;

import cn.junbao.middleware.sdk.domain.service.AbstractOpenAICodeReviewService;

import java.io.IOException;

public class OpenAiCodeReviewService extends AbstractOpenAICodeReviewService {
    @Override
    protected String getGitDiffCode() throws IOException, InterruptedException {
        return gitCommand.diff();
    }

    @Override
    protected String codeReview(String diffCode) {
        return null;
    }

    @Override
    protected String recordCodeReviewLog(String recommend) {
        return null;
    }

    @Override
    protected void pushMessage(String codeReviewLogUrl) {

    }
}
