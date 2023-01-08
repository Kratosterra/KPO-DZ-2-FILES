import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class Graph {
    private final ArrayList<FileNode> nodeFiles;
    private ArrayList<Path> finalFiles;
    private final ArrayList<Path> rawPaths;
    private final int[][] matrix;

    public Graph(ArrayList<Path> files) {
        this.nodeFiles = new ArrayList<>();
        try {
            for (Path i : files) {
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
        finalFiles = topologicalKahnSort();
        return true;
    }

    public ArrayList<Path> topologicalKahnSort() {
        ArrayList<Path> ans = new ArrayList<>();
        int[] degree = new int[matrix.length];
        for (int[] ints : matrix) {
            ArrayList<Integer> flow = new ArrayList<>();
            int j = 0;
            while (j < matrix.length) {
                if (ints[j] >= 1) {
                    flow.add(j);
                }
                j++;
            }
            for (int node : flow) {
                degree[node]++;
            }
        }
        Queue<Integer> checkList = new LinkedList<>();
        for (int i = 0; i < matrix.length; i++) {
            if (degree[i] == 0)
                checkList.add(i);
        }
        Vector<Integer> order = new Vector<>();
        if (!checkList.isEmpty()) {
            do {
                int now_pool = checkList.poll();
                order.add(now_pool);
                ArrayList<Integer> adjustment = new ArrayList<>();
                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[now_pool][j] >= 1) {
                        adjustment.add(j);
                    }
                }
                for (int node : adjustment) {
                    if (--degree[node] == 0)
                        checkList.add(node);
                }
            } while (!checkList.isEmpty());
        }
        for (int i : order) {
            ans.add(rawPaths.get(i));
        }
        return ans;
    }

    private boolean checkIsValid() {
        return isOriented() && !isPseudo() && !containsLoops() && !containsBigLoops();
    }

    private boolean dfs(FileNode begin, FileNode now) {
        if (begin.getPath() == now.getPath()) {
            return true;
        }
        boolean ans = false;
        for (Path node : now.getDependFiles()) {
            ans = ans || dfs(begin, nodeFiles.get(rawPaths.indexOf(node)));
        }
        return ans;
    }

    private boolean containsBigLoops() {
        boolean ans = false;
        for (Path now : rawPaths) {
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
                if (j > 1) {
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
        for (FileNode i : nodeFiles) {
            for (Path j : i.getDependFiles()) {
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
