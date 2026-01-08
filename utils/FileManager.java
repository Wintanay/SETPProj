package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static List<String> readAllLines(String pathStr) throws IOException {
        Path path = Paths.get(pathStr);
        ensureFile(path);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static void appendLine(String pathStr, String line) throws IOException {
        Path path = Paths.get(pathStr);
        ensureFile(path);
        Files.write(path, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    public static void overwrite(String pathStr, List<String> lines) throws IOException {
        Path path = Paths.get(pathStr);
        ensureFile(path);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static void ensureFile(Path path) throws IOException {
        if (Files.notExists(path)) {
            Path parent = path.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
        }
    }

    public static List<String> safeRead(String path) {
        try {
            return readAllLines(path);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
