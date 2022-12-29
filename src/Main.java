import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


public class Main {
    private static String folderPath = "test";
    private static String answerFilePath = "output/ans.txt";

    public static void main(String[] args) {
        getPaths();
        if (checkExistence(folderPath, answerFilePath)) {
            Merge merger = new Merge(folderPath, answerFilePath);
            if (!merger.merge()) {
                System.out.println("Произошла ошибка, завершение программы!");
            } else {
                System.out.println("\nОтвет в файле: " + answerFilePath);
            }
        } else {
            System.out.println("Произошла ошибка, завершение программы!");
        }
    }

    private static void getPaths() {
        Scanner in = new Scanner(System.in);
        int num;
        while (true) {
            printMenu();
            try {
                num = in.nextInt();
            } catch (Exception e) {
                in.next();
                num = 0;
            }
            if (num == 1) {
                System.out.print("Введите путь до рабочей папки: ");
                folderPath = in.next();
                System.out.print("Введите путь до итогового файла: ");
                answerFilePath = in.next();
                break;
            } else if (num == 2) {
                break;
            } else {
                num = 0;
            }
        }
    }

    private static boolean checkExistence(String folderPath, String answerFilePath) {
        boolean folderIsExists = false;
        Path file = Paths.get(answerFilePath);
        Path path = Paths.get(folderPath);
        if (!Files.exists(file)) {
            System.out.println("Внимание! Файл вывода не существует или программа не имеет к нему доступа! Он будет создан.");
        }
        if (Files.exists(path)) {
            folderIsExists = true;
        } else {
            System.out.println("Директория работы не существует или программа не имеет к ней доступа!");
        }
        return folderIsExists;
    }

    private static void printMenu() {
       System.out.println("""
                -------------------------------------------------------
                                      𝙼𝙴𝙽𝚄
                -------------------------------------------------------
                                
                [1] Выбрать пути самостоятельно
                [2] Использовать значения по умолчанию
                                
                Введите число:\s""");
    }
}