package cn.junbao.middleware.sdk.infrastructure.git;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


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
        ProcessBuilder logProcessBuilder = new ProcessBuilder("git", "log", "-1", "--pretty=format:%H");
        logProcessBuilder.directory(new File("."));
        Process logProcess = logProcessBuilder.start();

        BufferedReader logReader = new BufferedReader(new InputStreamReader(logProcess.getInputStream()));
        String latestCommitHash = logReader.readLine();
        logReader.close();
        logProcess.waitFor();

        ProcessBuilder diffProcessBuilder = new ProcessBuilder("git", "diff", latestCommitHash + "^" + latestCommitHash);
        diffProcessBuilder.directory(new File("."));
        Process diffProcess = diffProcessBuilder.start();

        StringBuilder diffCode = new StringBuilder();
        BufferedReader diffReader = new BufferedReader(new InputStreamReader(diffProcess.getInputStream()));
        String line = "";
        while ((line = diffReader.readLine())!= null){
            diffCode.append(line).append("\n");
        }
        diffReader.close();
        int exitCode = diffProcess.waitFor();
        if (exitCode != 0){
            throw new RuntimeException("git diff exit with Error , exitCode = " + exitCode);
        }
        return diffCode.toString();
    }

    public void commitAndPush(){
        //todo
    }
}
