package de.samply.file.csv;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

public class TestUtils {

  public static List<Map<String, String>> fetchFileValues(Path path) throws IOException {
    List<Map<String, String>> result = new ArrayList<>();
    List<String> headers = new ArrayList<>();

    AtomicInteger counter = new AtomicInteger(0);
    try (Stream<String> lines = Files.lines(path)) {
      lines.forEach(line -> {
        if (counter.getAndIncrement() == 0) { //Headers
          Arrays.stream(line.split(MtbaConst.DEFAULT_CSV_DELIMITER))
              .forEach(header -> headers.add(header));
        } else {
          String[] elements = line.split(MtbaConst.DEFAULT_CSV_DELIMITER);
          Map<String, String> headerValueMap = new HashMap<>();
          result.add(headerValueMap);
          for (int i = 0; i < elements.length; i++) {
            headerValueMap.put(headers.get(i), elements[i]);
          }
        }
      });
    }

    return result;
  }

  public static Set<String> getRandomHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();

    boolean atLeastOneHeader = false;
    for (String header : Files.readAllLines(path).get(0).split(MtbaConst.DEFAULT_CSV_DELIMITER)) {
      if (!atLeastOneHeader || Math.random() < 0.5) {
        headers.add(header);
        atLeastOneHeader = true;
      }
    }

    return headers;

  }

  public static Set<String> getAllHeaders(Path path) throws IOException {
    HashSet<String> headers = new HashSet<>();
    for (String header : Files.readAllLines(path).get(0).split(MtbaConst.DEFAULT_CSV_DELIMITER)) {
      headers.add(header);
    }
    return headers;
  }

  public static int getNumberOfLines(Path path) throws IOException {
    return ((int) Files.readAllLines(path).stream().filter(line -> line.length() > 0).count()) - 1;
  }

  public static void deleteDirectory(PathsBundle pathsBundle) throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }
}
