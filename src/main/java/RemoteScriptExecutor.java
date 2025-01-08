import com.jcraft.jsch.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class RemoteScriptExecutor {


    /**
     * Runs a command or script on a remote server using SSH (via JSch) with username + password authentication.
     * The final command to be executed is constructed by concatenating {@code remoteScriptPart1} and {@code remoteScriptPart2}.
     *
     * <p>Example usage:
     * <pre>{@code
     * String output = runRemoteScript(
     *     "100.000.000.00", 22, "username", "password",
     *     "script part 1 ",
     *     "script part 2"
     * );
     * System.out.println(output);
     * }</pre>
     *
     * @param host               The remote host.
     * @param port               The SSH port.
     * @param username           The SSH username.
     * @param password           The SSH password.
     * @param remoteScriptPart1  The first part of the command.
     * @param remoteScriptPart2  The second part of the command.
     * @return The combined output (stdout + stderr) from executing the remote command. If the remote command returns a
     *         non-zero exit code, that status is appended to the returned output.
     * @throws JSchException        if an SSH error occurs (e.g., authentication fails, no route to host).
     * @throws IOException          if an I/O error occurs while reading the command output streams.
     * @throws InterruptedException if the thread is interrupted while waiting for the remote command to finish.
     */
    public static String runRemoteScript(
            String host,
            int port,
            String username,
            String password,
            String remoteScriptPart1,
            String remoteScriptPart2
    ) throws JSchException, IOException, InterruptedException {

        String remoteScript = remoteScriptPart1 + remoteScriptPart2;

        // 1. Create a JSch instance
        JSch jsch = new JSch();

        // 2. Configure session (username, host, port)
        Session session = jsch.getSession(username, host, port);

        // 3. Provide password-based authentication
        session.setPassword(password);

        // 4. Configure host key checking
        Properties config = new Properties();
        // For a quick demo, disable host key checking (not recommended in production)
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        // 5. Connect the session
        session.connect();

        // 6. Open a channel for "exec"
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(remoteScript);

        // 7. Set up streams to capture output
        InputStream stdout = channel.getInputStream();
        InputStream stderr = channel.getErrStream();

        // 8. Execute the command
        channel.connect();

        // 9. Read the output
        StringBuilder outputBuilder = new StringBuilder();
        try (BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
             BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr))) {

            String line;
            while ((line = stdoutReader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            while ((line = stderrReader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
        }

        // 10. Wait until the channel closes so we have a proper exit code
        while (!channel.isClosed()) {
            Thread.sleep(100);
        }

        int exitStatus = channel.getExitStatus();
        channel.disconnect();
        session.disconnect();

        // Optionally check the exit status
        if (exitStatus != 0) {
            outputBuilder.append("Remote script exited with status: ").append(exitStatus).append("\n");
        }

        return outputBuilder.toString();
    }
}