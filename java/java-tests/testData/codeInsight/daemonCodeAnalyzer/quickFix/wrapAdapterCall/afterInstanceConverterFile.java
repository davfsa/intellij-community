// "Adapt argument using 'toPath()'" "true"
import java.io.*;
import java.nio.file.*;

class Test {
  void test(File file) throws IOException {
    Files.createFile(file.toPath());
  }
}