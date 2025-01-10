import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
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
import java.nio.file.Paths;
import java.util.Vector;

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
     * Downloads all student work from the server without using git.
     *
     * @param examPath Path on server (e.g., "/Luka/2024_25/Prvi_ispit/15")
     * @param localBaseDir Local directory where files will be downloaded
     */
    public static void downloadAllStudentWork(String examPath, String localBaseDir) {
        try {
            String host = Config.SERVER_HOST;
            int port = 22;
            String username = Config.SERVER_USERNAME;
            String password = Config.SERVER_PASSWORD;

            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // SFTP Channel for file transfer
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String remotePath = "/srv/git" + examPath + "/Studentska_resenja";

            // List all student directories
            Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(remotePath);
            for (ChannelSftp.LsEntry entry : list) {
                if (!entry.getAttrs().isDir() || entry.getFilename().equals(".") || entry.getFilename().equals("..")) {
                    continue;
                }

                String studentDir = entry.getFilename();
                String studentLocalPath = Paths.get(localBaseDir, studentDir).toString();
                String studentRemotePath = remotePath + "/" + studentDir;

                System.out.println("Downloading files for student: " + studentDir);

                // Create local directory
                new File(studentLocalPath).mkdirs();

                // Download all files recursively
                downloadDirectory(sftpChannel, studentRemotePath, studentLocalPath);
            }

            sftpChannel.disconnect();
            session.disconnect();

            System.out.println("All student work downloaded to: " + localBaseDir);

            // Create IntelliJ project structure
            System.out.println("Creating IntelliJ project structure...");
            createIntellijProject(localBaseDir);
            System.out.println("IntelliJ project created. You can now open " + localBaseDir + " as a project in IntelliJ IDEA");

        } catch (Exception e) {
            System.err.println("Error downloading student work: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void downloadDirectory(ChannelSftp sftpChannel, String sourceDir, String destDir)
            throws SftpException {
        Vector<ChannelSftp.LsEntry> list = sftpChannel.ls(sourceDir);
        for (ChannelSftp.LsEntry entry : list) {
            String filename = entry.getFilename();
            if (filename.equals(".") || filename.equals("..")) {
                continue;
            }

            String sourcePath = sourceDir + "/" + filename;
            String destPath = Paths.get(destDir, filename).toString();

            if (entry.getAttrs().isDir()) {
                // Create directory and recurse
                new File(destPath).mkdirs();
                downloadDirectory(sftpChannel, sourcePath, destPath);
            } else {
                // Download file
                new File(destPath).getParentFile().mkdirs();
                sftpChannel.get(sourcePath, destPath);
            }
        }
    }

    public static void createIntellijProject(String baseDir) {
        try {
            // Create .idea directory
            File ideaDir = new File(baseDir, ".idea");
            ideaDir.mkdirs();

            // Create modules.xml
            String modulesXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<project version=\"4\">\n" +
                    "  <component name=\"ProjectModuleManager\">\n" +
                    "    <modules>\n";

            File[] studentDirs = new File(baseDir).listFiles(File::isDirectory);
            for (File studentDir : studentDirs) {
                String moduleName = studentDir.getName();
                modulesXml += String.format("      <module fileurl=\"file://$PROJECT_DIR$/%s/%s.iml\" filepath=\"$PROJECT_DIR$/%s/%s.iml\" />\n",
                        moduleName, moduleName, moduleName, moduleName);

                // Create module .iml file
                createModuleIml(studentDir);
            }

            modulesXml += "    </modules>\n" +
                    "  </component>\n" +
                    "</project>";

            Files.write(new File(ideaDir, "modules.xml").toPath(), modulesXml.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createModuleIml(File moduleDir) throws IOException {
        String iml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<module type=\"JAVA_MODULE\" version=\"4\">\n" +
                "  <component name=\"NewModuleRootManager\" inherit-compiler-output=\"true\">\n" +
                "    <exclude-output />\n" +
                "    <content url=\"file://$MODULE_DIR$\">\n" +
                "      <sourceFolder url=\"file://$MODULE_DIR$/src\" isTestSource=\"false\" />\n" +
                "    </content>\n" +
                "    <orderEntry type=\"inheritedJdk\" />\n" +
                "    <orderEntry type=\"sourceFolder\" forTests=\"false\" />\n" +
                "  </component>\n" +
                "</module>";

        Files.write(new File(moduleDir, moduleDir.getName() + ".iml").toPath(), iml.getBytes());
    }
}
