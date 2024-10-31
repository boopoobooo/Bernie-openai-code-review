package cn.junbao.middleware.sdk.infrastructure.openai;

import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionRequestDTO;
import cn.junbao.middleware.sdk.infrastructure.openai.DTO.ChatCompletionSyncResponseDTO;

public interface IAICodeRevice {
    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO requestDTO) throws Exception;
}
