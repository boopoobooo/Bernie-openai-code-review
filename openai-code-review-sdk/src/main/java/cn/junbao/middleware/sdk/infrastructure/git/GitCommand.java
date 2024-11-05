package cn.junbao.middleware.sdk.infrastructure.git;

import cn.junbao.middleware.sdk.types.utils.RandomStringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GitCommand {
    private final Logger logger = LoggerFactory.getLogger(GitCommand.class);
    private final String githubReviewLogUrl;
    private final String githubToken;
    private final String projectName;
    private final String branch;
    private final String author;
    private final String message;

    public GitCommand(String githubReviewLogUrl, String githubToken, String projectName, String branch, String author, String message) {
        this.githubReviewLogUrl = githubReviewLogUrl;
        this.githubToken = githubToken;
        this.projectName = projectName;
        this.branch = branch;
        this.author = author;
        this.message = message;
    }

    public String diff() throws IOException, InterruptedException {
        logger.info("[START] diff() get diffCode start:");
        // 获取到 git 提交记录中 最近一条提交的 哈希值
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();

        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String latestCommitHash = logReader.readLine();
        logReader.close();
        logProcess.waitFor();

        //比较 当前提交 和 上一次提交的 差异
        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", latestCommitHash + "^", latestCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();

        StringBuilder diffCode = new StringBuilder();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));
        String line;
        while ((line = diffReader.readLine()) != null) {
            diffCode.append(line).append("\n");
        }
        diffReader.close();

        int exitCode = diffProcess.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("[diff]Failed to get diff, exit code:" + exitCode);
        }

        return diffCode.toString();
    }

    public String  commitAndPush(String recommend) throws GitAPIException, IOException {
        logger.info("[START] commitAndPush start !");
        Git git = Git.cloneRepository()
                .setURI(githubReviewLogUrl + ".git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken, ""))
                .call();

        String fileFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File newFile = new File("repo/"+fileFolderName);
        if (!newFile.exists()){
            newFile.mkdirs();
        }

        String fileName = projectName + "-" + branch + "-" + author + System.currentTimeMillis() + "-" + RandomStringUtils.randomNumeric(4) + ".md";
        File file = new File(fileFolderName, fileName);
        try (FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(recommend);
        }

        git.add().addFilepattern(fileFolderName +"/"+ fileName).call();
        git.commit().setMessage("git add codeReview log , fileName = "+ fileName).call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubToken,"")).call();
        logger.info("git #commitAndPush done , fileName = "+ fileFolderName + "-" + fileName);
        logger.info("[END] commitAndPush END !");
        return githubReviewLogUrl + "/blob/main/" + fileFolderName +"/"+ fileName;

    }
}
