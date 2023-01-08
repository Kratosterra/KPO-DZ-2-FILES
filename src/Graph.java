import java.io.File;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;

public class Graph {
    private final ArrayList<FileNode> nodeFiles;
    private ArrayList<Path> finalFiles;
    private final ArrayList<Path> rawPaths;
    private final int[][] matrix;

    public Graph(ArrayList<Path> files) {
        this.nodeFiles = new ArrayList<>();
        try {
            for (Path i: files) {
                nodeFiles.add(new FileNode(i, files));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
        rawPaths = files;
        matrix = new int[nodeFiles.size()][nodeFiles.size()];
        finalFiles = files;
    }

    boolean doSort() {
        createMatrix();
        System.out.println(this);
        if (!checkIsValid()) {
            return false;
        }
        finalFiles = bfsRecursion();
        return true;
    }

    private void bfsSearchRecursion(int[][] matrix, ArrayDeque<Integer> queue, boolean[] used, ArrayList<Path> finalArray)
    {
        // Если очередь пуста - выходим.
        if (queue.isEmpty()) return;
        // Получаем вершину из очереди.
        int vertex = queue.getFirst();
        // Удаляем.
        queue.pop();
        // Проходимся по всем смежным с данной вершиной.
        for (int i = 0; i < matrix.length; i++)
        {
            if (matrix[vertex][i] != 0) {
                // Если мы еще не использовали вершину.
                if (!used[i])
                {
                    // Помечаем ее как посещенную и добавляем назад очереди.
                    used[i] = true;
                    queue.push(i);
                    finalArray.add(rawPaths.get(i));
                }
            }
        }
        // Вызываем еще раз.
        bfsSearchRecursion(matrix, queue, used, finalArray);
    }

    private ArrayList<Path> bfsRecursion() {
        ArrayList<Path> finalArray = new ArrayList<>();
        boolean[] used = new boolean[matrix.length];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        // Запускаем рекурсивный проход по вершинам.
        for (int i = 0; i < matrix.length; i++)
        {
            // Для каждой вершины помечаем ее как найденную и добавляем в конец очереди, вызывая
            // функцию поиска.
            if (!used[i])
            {
                used[i] = true;
                queue.push(i);
                bfsSearchRecursion(matrix, queue, used, finalArray);
                finalArray.add(rawPaths.get(i));
            }
        }
        Collections.reverse(finalArray);
        return finalArray;
    }


    private boolean checkIsValid() {
        return isOriented() && !isPseudo() && !containsLoops() && !containsBigLoops();
    }

    private boolean dfs(FileNode begin, FileNode now) {
        if (begin.getPath() == now.getPath()) {
            return true;
        }
        boolean ans = false;
        for (Path node: now.getDependFiles()) {
            ans = ans || dfs(begin, nodeFiles.get(rawPaths.indexOf(node)));
        }
        return ans;
    }

    private boolean containsBigLoops() {
        boolean ans = false;
        for (Path now: rawPaths) {
            for (Path node : nodeFiles.get(rawPaths.indexOf(now)).getDependFiles()) {
                ans = ans || dfs(nodeFiles.get(rawPaths.indexOf(now)), nodeFiles.get(rawPaths.indexOf(node)));
            }
        }
        return ans;
    }

    private boolean containsLoops() {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                if (i == j) {
                    if (matrix[i][j] > 0) {
                        System.out.println("В файле обнаружены ссылки на себя!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPseudo() {
        for (int[] i : matrix) {
            for (int j : i) {
                if (j > 1){
                    System.out.println("Обнаружены двойные ссылки из одного файла!");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isOriented() {
        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                if (i != j && matrix[i][j] == matrix[j][i] && matrix[i][j] != 0 && matrix[j][i] != 0) {
                    System.out.println("Обнаружены перекрестные ссылки из файла в файл и обратно!");
                    return false;
                }
            }
        }
        return true;
    }

    void createMatrix() {
        for (FileNode i: nodeFiles) {
            for (Path j: i.getDependFiles()) {
                matrix[rawPaths.indexOf(j)][rawPaths.indexOf(i.getPath())] += 1;
            }
        }
    }

    ArrayList<Path> getFinalList() {
        return finalFiles;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\n\nИнформация о файлах\n----------------------------------------------\n");
        for (FileNode i : nodeFiles) {
            str.append(i.toString());
        }
        str.append("\n----------------------------------------------");
        str.append("""
                
                Матрица смежности на основе обратной связи.\040
                Пути идут из файла, который требуется, в файл, который требует.
                ----------------------------------------------
                \t\t\t\t""");
        for (int i = 0; i < matrix.length; ++i) {
            str.append(" ").append(rawPaths.get(i).getFileName());
        }
        str.append("\n");
        for (int i = 0; i < matrix.length; ++i) {
            str.append(rawPaths.get(i).getFileName());
            for (int j = 0; j < matrix[i].length; ++j) {
                str.append("\t\t\t").append(matrix[i][j]);
            }
            str.append("\n");
        }
        str.append("\n----------------------------------------------");
        return str.toString();
    }
}
