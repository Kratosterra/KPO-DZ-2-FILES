import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Merge {
    private final String pathOfRootFolder;
    private final String pathOfResultFile;

    public Merge(String rootFolderPath, String resultFilePath) {
        this.pathOfRootFolder = rootFolderPath;
        this.pathOfResultFile = resultFilePath;
    }

    public boolean merge() {
        ArrayList<Path> allFiles = getFiles();
        allFiles = doSortWithRules(allFiles);
        if (allFiles.size() == 0) {
            System.out.println("Произошла ошибка, обнаружены циклы или файлов нет!");
            return false;
        }
        printListOfFiles(allFiles);
        Path answerFilePath = getFileToWrite();
        merge(allFiles, answerFilePath);
        return true;
    }

    private ArrayList<Path> getFiles() {
        ArrayList<Path> allFiles = new ArrayList<>();
        try {
            Path start = Paths.get(pathOfRootFolder);
            Files.walk(start)
                    .filter(Files::isRegularFile)
                    .sorted()
                    .forEach(allFiles::add);
        } catch (IOException e) {
            System.out.println("Пороизошла ошибка при взятии и сортировке файлов!");
            e.printStackTrace();
        }
        return allFiles;
    }

    private ArrayList<Path> doSortWithRules(ArrayList<Path> allFiles) {
        Graph graph;
        try {
            graph = new Graph(allFiles);
        } catch (RuntimeException e){
            System.out.println("Произошла ошибка при создании графа из файлов!");
            return new ArrayList<>();
        }
        return !graph.doSort() ? new ArrayList<>() : graph.getFinalList();
    }

    private static void printListOfFiles(ArrayList<Path> allFiles) {
        System.out.println("Список файлов!");
        int numberRow = 1;
        for (Path i : allFiles) {
            System.out.print(numberRow++);
            System.out.print(") " + i.getFileName());
            System.out.println();
        }
    }

    private Path getFileToWrite() {
        Path pathToAnswerFile = Paths.get(pathOfResultFile);
        File answerFile = new File(pathOfResultFile);
        if (!answerFile.isFile() || answerFile.isDirectory()) {
            try {
                Files.createFile(pathToAnswerFile);
            } catch (IOException e) {
                System.out.println("Ошибка при создании результирующего файла!");
                e.printStackTrace();
            }
        } else {
            try {
                Files.newBufferedWriter(pathToAnswerFile, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.out.println("Ошибка при создании результирующего файла!");
                e.printStackTrace();
            }
        }
        return pathToAnswerFile;
    }

    private void merge(ArrayList<Path> allFiles, Path resultFilePath) {
        if (allFiles != null && resultFilePath != null) {
            for (Path file : allFiles) {
                ArrayList<String> lines = new ArrayList<>();
                try {
                    lines.addAll(Files.readAllLines(file));
                } catch (IOException e) {
                    System.out.println("Ошибка при сборке результирующего файла!");
                    e.printStackTrace();
                }
                try {
                    Files.write(resultFilePath, lines, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.out.println("Ошибка при записи в результирующий файл!");
                    e.printStackTrace();
                }
            }
        }
    }
}