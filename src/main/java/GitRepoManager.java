import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class GitRepoManager {

    /**
     * Clones a remote repository to a local directory
     *
     * @param remoteUrl The URL of the remote repository
     * @param localPath The local path where to clone the repository
     * @return The Git instance for the cloned repository
     */
    public static void cloneRepository(String remoteUrl, String localPath) {
        try {
            System.out.println("Cloning repository from: " + remoteUrl);
            Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(new File(localPath))
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_GIT_USERNAME,
                            Config.SERVER_PASSWORD))
                    .call();
            System.out.println("Repository cloned successfully to: " + localPath);
        } catch (GitAPIException e) {
            System.err.println("Error cloning repository: " + e.getMessage());
            throw new RuntimeException("Failed to clone repository", e);
        }
    }

    /**
     * Pushes changes to a remote repository
     *
     * @param localPath The path to the local repository
     * @param branchName The name of the branch to push
     * @param commitMessage The commit message
     */
    public static void pushToRepository(String localPath, String branchName, String commitMessage) {
        try {
            System.out.println("Opening repository at: " + localPath);
            Git git = Git.open(new File(localPath));

            // Add all changes
            System.out.println("Adding changes...");
            git.add().addFilepattern(".").call();

            // Commit changes
            System.out.println("Committing changes...");
            git.commit()
                    .setMessage(commitMessage)
                    .call();

            // Push to remote
            System.out.println("Pushing to remote...");
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_GIT_USERNAME,
                            Config.SERVER_PASSWORD))
                    .setRemote("origin")
//                    .setRefSpecs(git.getRepository().getRefDatabase().getRefsByPrefix("refs/heads/" + branchName).get(0))
                    .call();

            System.out.println("Changes pushed successfully");
        } catch (IOException | GitAPIException e) {
            System.err.println("Error pushing to repository: " + e.getMessage());
            throw new RuntimeException("Failed to push changes", e);
        }
    }


    /**
     * Downloads all Git repositories from a remote directory to a local directory.
     *
     * @param remoteBaseURL The base HTTP URL of the remote directory containing Git repositories.
     *                      Example: "http://192.100.100.10:/srv/git/Luka/2024_25/Prvi_ispit/Studentska_resenja"
     * @param localBaseDir  The local directory where the repositories will be cloned.
     *                      Example: "/path/to/local-dir"
     * @throws Exception If an error occurs while retrieving or cloning repositories.
     */
    public static void downloadAllRepos(String remoteBaseURL, String localBaseDir) throws Exception {
        // Ensure the local directory exists
        File localBaseDirFile = new File(localBaseDir);
        if (!localBaseDirFile.exists() && !localBaseDirFile.mkdirs()) {
            throw new Exception("Failed to create local directory: " + localBaseDir);
        }

        // Fetch the list of directories (repositories) from the remote server
        URL url = new URL(remoteBaseURL + "/list-repos"); // Assumes server provides a repo listing
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String repoName;

            while ((repoName = reader.readLine()) != null) {
                String repoURL = remoteBaseURL + "/" + repoName;
                File localRepoDir = new File(localBaseDir, repoName);

                // Clone each repository into the local directory
                cloneRepository(repoURL, localRepoDir.getAbsolutePath());
            }
            reader.close();

            System.out.println("All repositories downloaded successfully to: " + localBaseDir);
        } else {
            throw new Exception("Failed to fetch repository list. HTTP code: " + connection.getResponseCode());
        }
    }

//    /**
//     * Clones a Git repository from a remote URL to a local directory.
//     *
//     * @param remoteRepoURL The HTTP URL of the remote Git repository.
//     *                      Example: "http://192.168.124.28:/srv/git/Luka/2024_25/Prvi ispit/Studentska_resenja/student_1"
//     * @param localDir      The path to the local directory where the repository will be cloned.
//     *                      Example: "/path/to/local-repo"
//     * @throws GitAPIException If a Git operation fails.
//     */
//    private static void cloneRepository(String remoteRepoURL, String localDir) throws GitAPIException {
//        Git.cloneRepository()
//                .setURI(remoteRepoURL)
//                .setDirectory(new File(localDir))
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
//                        Config.SERVER_USERNAME,
//                        Config.SERVER_PASSWORD))
//                .call();
//
//        System.out.println("Cloned repository: " + remoteRepoURL + " to " + localDir);
//    }
}
