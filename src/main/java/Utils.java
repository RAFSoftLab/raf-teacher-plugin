import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;




public class Utils {

    public static void pushCurrentProject(String remoteURL, String branchName, String commitMessage) {
        // Pribavljanje trenutnog projekta
        Project currentProject = ProjectManager.getInstance().getOpenProjects()[0];
        String projectPath = currentProject.getBasePath();
        Path projectPathReal = Paths.get(projectPath);
        System.out.println("Putanja projekta: " + projectPath);

        if (projectPath != null) {
            // Provera da li trenutni direktorijum nije već tmp_git_clone
            if (!projectPath.endsWith("tmp_git_clone")) {
                Path tmpDir = Paths.get(new File(projectPath, "tmp_git_clone").getAbsolutePath());

                try {
                    // Kreiranje privremenog direktorijuma
                    if (!Files.exists(tmpDir)) {
                        Files.createDirectories(tmpDir);
                    } else {
                        System.out.println("Vec postoji taj dir!");
                    }

                    // Kloniranje u privremeni direktorijum
                    if (!isRepositoryCloned(tmpDir)) {
                        System.out.println("Kloniram repo!");
                        GitRepoManager.cloneRepository(remoteURL, tmpDir.toString());
                    } else {
                        System.out.println("Repozitorijum je već kloniran!");
                    }

                    // Kopiranje sadržaja trenutnog projekta u tmp direktorijum

                    copyProjectContents(projectPathReal, tmpDir);
                    // Push operacija
                    System.out.println("Pushujem iz "+tmpDir.toString());
                    GitRepoManager.pushToRepository(tmpDir.toString(), branchName, commitMessage);

                } catch (IOException e) {
                    System.out.println("Greška tokom rada sa direktorijumom: " + e.getMessage());
                } finally {
                    // Brisanje tmp direktorijuma nakon push-a
                    deleteDirectory(tmpDir.toFile());
                }
            } else {
                System.out.println("Greška: Trenutni direktorijum je već tmp_git_clone.");
            }
        } else {
            System.out.println("Greška: Nije moguće pronaći putanju projekta.");
        }
    }



    // Pomoćna funkcija za proveru da li je repozitorijum već kloniran
    private static boolean isRepositoryCloned(Path directory) {
        return Files.exists(directory.resolve(".git"));
    }


    private static void copyProjectContents(Path sourcePath, Path targetPath) {
        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Skip the tmp_git_clone directory
                    if (dir.equals(targetPath)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    Path targetDir = targetPath.resolve(sourcePath.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = targetPath.resolve(sourcePath.relativize(file));
                    Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("Greška tokom kopiranja sadržaja: " + e.getMessage());
        }
    }



    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}

