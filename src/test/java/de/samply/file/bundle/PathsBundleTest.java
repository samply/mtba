package de.samply.file.bundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.samply.utils.Constants;
import de.samply.utils.EitherUtils.ThrowingConsumer;
import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@TestInstance(Lifecycle.PER_CLASS)
class PathsBundleTest {

  private static final int FILES_NUMBER = 5;
  private static final String LINE_ELEMENT_PREFIX = "@@@";

  private final Logger logger = LoggerFactory.getLogger(PathsBundleTest.class);
  private PathsBundle pathsBundle;


  @BeforeAll
  void setUp() throws IOException {
    List<Path> randomPaths = RandomPathGenerator.createRandomCsvPaths(FILES_NUMBER);
    pathsBundle = new PathsBundle();
    pathsBundle.addPaths(randomPaths);
  }

  @AfterAll
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }

  @Test
  void getDirectory() {

    Path directory = pathsBundle.getDirectory();
    assertTrue(
        directory.getFileName().toString().startsWith(RandomPathGenerator.TEMP_DIRECTORY_PREFIX));

  }

  @Test
  void getAllPaths() {
    assertEquals(FILES_NUMBER, pathsBundle.getAllPaths().size());
  }

  @Test
  void applyToAllPaths() throws IOException {

    pathsBundle.applyToAllPaths(new ElementsExtender());

    for (Path path : pathsBundle.getAllPaths()) {
      for (String line : Files.readAllLines(path)) {
        for (String element : line.split(Constants.DEFAULT_DELIMITER)) {
          assertTrue(element.startsWith(LINE_ELEMENT_PREFIX));
        }
      }
    }


  }

  private class ElementsExtender implements ThrowingConsumer<Path, IOException> {

    @Override
    public void accept(Path path) throws IOException {

      List<String> newLines = getLinesAndAddPrefix(path);
      Files.write(path, newLines);

    }

    private List<String> getLinesAndAddPrefix(Path path) throws IOException {

      List<String> newLines = new ArrayList<>();
      for (String line : Files.readAllLines(path)) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String element : line.split(Constants.DEFAULT_DELIMITER)) {
          stringBuilder.append(LINE_ELEMENT_PREFIX);
          stringBuilder.append(element);
          stringBuilder.append(Constants.DEFAULT_DELIMITER);
        }
        newLines.add(stringBuilder.toString());
      }

      return newLines;
    }
  }

}
