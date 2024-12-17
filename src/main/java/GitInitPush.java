import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GitInitPush {

    public static void main(String[] args) {
        String serverUser = "mastersi";
        String serverIp = "192.168.124.28";
        String serverPassword = "masterSI2023";
        String remoteGitPath = "/srv/git/OOP/2024_25/test7_zn";
        String localPath = "C:\\Users\\Zarko\\Documents\\MyFiles";

        try {
            // 1. Povezivanje na server putem JSch
            JSch jsch = new JSch();
            Session session = jsch.getSession(serverUser, serverIp, 22);
            session.setPassword(serverPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            System.out.println("Povezan na server " + serverIp);

            // 2. Inicijalizacija Git repozitorijuma na serveru
            String initCommand = "echo '" + serverPassword + "' | sudo -S git init " + remoteGitPath;
            executeCommandOnServer(session, initCommand);

            // 3. Lokalna inicijalizacija i commit
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(localPath));
            processBuilder.command("cmd.exe", "/c", "git init && git add . && git commit -m \"Initial commit\" --allow-empty");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            printProcessOutput(process);
            System.out.println("Lokalni git init i commit izlaz: " + exitCode);

            // Uklanjanje postojećeg remote-a ako postoji
            processBuilder.command("cmd.exe", "/c", "git remote remove origin");
            process = processBuilder.start();
            process.waitFor();

            // 4. Dodavanje remote i push
            processBuilder.command(
                    "cmd.exe",
                    "/c",
                    "git remote add origin ssh://" + serverUser + "@" + serverIp + ":" + remoteGitPath
            );
            process = processBuilder.start();
            exitCode = process.waitFor();
            printProcessOutput(process);
            System.out.println("Dodavanje remote izlaz: " + exitCode);

            // Postavljanje GIT_SSH_COMMAND za detaljan SSH log
            processBuilder.command("cmd.exe", "/c", "set GIT_SSH_COMMAND=ssh -v && git push -u origin master");
            process = processBuilder.start();
            exitCode = process.waitFor();
            printProcessOutput(process);
            System.out.println("Push izlaz: " + exitCode);

            System.out.println("Kod uspešno push-ovan na server.");
            session.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeCommandOnServer(Session session, String command) {
        try {
            var channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // Setuj ulazni stream
            ((ChannelExec) channel).setInputStream(System.in);
            ((ChannelExec) channel).setErrStream(System.err);
            var input = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (input.available() > 0) {
                    int i = input.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("Exit status: " + channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            channel.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printProcessOutput(Process process) throws IOException {
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
