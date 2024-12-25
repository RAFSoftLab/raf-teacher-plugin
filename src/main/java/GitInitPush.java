import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GitInitPush {

    public static void pushToServer() {
        String localPath = "C:\\Users\\Zarko\\Documents\\MajFajls";
        String remotePath = "http://raf@192.168.124.28:/srv/git/OOP/2024_25/isp2";
        String branchName = "main"; // ili grana koju želiš da koristiš
        String commitMessage = "Initial commit";
        // Test

        try (Git git = Git.init().setDirectory(new File(localPath)).call()) {
            // Add all files to the repository
            git.add().addFilepattern(".").call();

            // Create a commit
            git.commit().setMessage(commitMessage).call();

            // Set the remote URL
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new URIish(remotePath))
                    .call();

            // Push to the repository
            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                    Config.SERVER_USERNAME,
                    Config.SERVER_PASSWORD));
            pushCommand.call();

            System.out.println("Push to repository completed successfully.");
        } catch (GitAPIException  | URISyntaxException e) {
            System.err.println("Error pushing to repository: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        pushToServer();
    }
}

class Config {
    public static final String SERVER_USERNAME = "raf";
    public static final String SERVER_PASSWORD = "masterSI2023";
    public static final String HTTP_REPO_URL = "http://raf@192.168.124.28:/srv/git/OOP/2024_25/isp2";
    public static final String SSH_REPO_URL = "mastersi@192.168.124.28:/srv/git/OOP/2024_25/isp2";
}
