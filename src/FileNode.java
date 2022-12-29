import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.Charset.defaultCharset;

public class FileNode {
    private final Path file;
    private final ArrayList<Path> dependFiles;

    public Path getPath() {
        return file;
    }

    public ArrayList<Path> getDependFiles() {
        return dependFiles;
    }

    public FileNode(Path file, ArrayList<Path> allFiles) {
        this.file = file;
        dependFiles = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(file, defaultCharset());
        } catch (IOException e) {
            System.out.println("Пороизошла ошибка при чтении одного из файлов для нахождения зависимостей!");
            e.printStackTrace();
            throw new RuntimeException();
        }
        try {
            for (String content : lines) {
                if (content.contains("require")) {
                    String folderFile = content.substring(content.indexOf('‘') + 1, content.lastIndexOf('’'));
                    for (Path i : allFiles) {
                        String name = String.valueOf(i.getFileName());
                        name = name.substring(0, name.lastIndexOf('.'));
                        if (folderFile.contains(name)) {
                            dependFiles.add(i);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Пороизошла ошибка при работе с файлом из каталога!");
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public String toString() {
        return  file.getFileName() + " зависит от: " + dependFiles.toString() + '\n';
    }
}
