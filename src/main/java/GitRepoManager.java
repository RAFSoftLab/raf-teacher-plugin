import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
     * Pushes all files from a local directory directly to a remote Git repository over HTTP.
     *
     * @param remoteRepoURL The HTTP URL of the remote Git repository, including the repository path.
     *                      Example: "http://raf@192.168.124.28:/srv/git/Luka/2024_25/Prvi ispit"
     * @param localDir      The path to the local directory whose files will be pushed to the remote repository.
     *                      Example: "/path/to/local-directory"
     * @throws IOException   If an I/O error occurs while accessing the directories.
     * @throws GitAPIException If a Git operation fails.
     */
    public static void initializeAndPushToRemote(String remoteRepoURL, String localDir) {
        try {
            // Open the local directory as a Git repository or initialize it
            File localDirFile = new File(localDir);
            if (!localDirFile.isDirectory()) {
                throw new IllegalArgumentException("Local directory does not exist: " + localDir);
            }

            Git git;
            if (new File(localDir, ".git").exists()) {
                // If already a Git repository, open it
                git = Git.open(localDirFile);
                System.out.println("Opened existing Git repository at: " + localDir);
            } else {
                // Initialize a new Git repository
                git = Git.init().setDirectory(localDirFile).call();
                System.out.println("Initialized new Git repository at: " + localDir);
            }

            // Add all files in the local directory to the Git repository
            git.add().addFilepattern(".").call();

            // Commit the changes with a message
            git.commit().setMessage("Initial commit from local directory").call();

            // Set the remote repository URL
            git.remoteAdd()
                    .setName("origin")
                    .setUri(new org.eclipse.jgit.transport.URIish(remoteRepoURL))
                    .call();

            // Push the files to the remote repository
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            Config.SERVER_USERNAME,
                            Config.SERVER_PASSWORD))
                    .setRemote("origin")
                    .call();

            System.out.println("Files pushed successfully to the remote repository at: " + remoteRepoURL);

        } catch (IOException | GitAPIException e) {
            System.err.println("Error initializing and pushing to remote repository: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Downloads all Git repositories from a remote directory to a local directory.
     *
     * @param remoteBaseURL The base HTTP URL of the remote directory containing Git repositories.
     *                      Example: "http://192.168.124.28:/srv/git/Luka/2024_25/Prvi ispit/Studentska_resenja"
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

    /**
     * Clones a Git repository from a remote URL to a local directory.
     *
     * @param remoteRepoURL The HTTP URL of the remote Git repository.
     *                      Example: "http://192.168.124.28:/srv/git/Luka/2024_25/Prvi ispit/Studentska_resenja/student_1"
     * @param localDir      The path to the local directory where the repository will be cloned.
     *                      Example: "/path/to/local-repo"
     * @throws GitAPIException If a Git operation fails.
     */
    private static void cloneRepository(String remoteRepoURL, String localDir) throws GitAPIException {
        Git.cloneRepository()
                .setURI(remoteRepoURL)
                .setDirectory(new File(localDir))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                        Config.SERVER_USERNAME,
                        Config.SERVER_PASSWORD))
                .call();

        System.out.println("Cloned repository: " + remoteRepoURL + " to " + localDir);
    }
}
