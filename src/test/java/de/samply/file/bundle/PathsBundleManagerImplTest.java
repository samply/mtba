package de.samply.file.bundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.samply.utils.RandomPathGenerator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class PathsBundleManagerImplTest {

  private static final int FILES_NUMBER = 5;
  private PathsBundleManager pathsBundleManager;
  private Path inputFolderPath;
  private Path outputFolderPath;

  @BeforeAll
  void setUp() throws IOException {

    inputFolderPath = RandomPathGenerator.createRandomPaths(FILES_NUMBER).get(0).getParent();
    outputFolderPath = RandomPathGenerator.createRandomDirectory();

    pathsBundleManager = new PathsBundleManagerImpl(inputFolderPath, outputFolderPath);

  }

  @AfterAll
  void tearDown() throws IOException {
    deleteDirectory(inputFolderPath);
    deleteDirectory(outputFolderPath);
  }

  private void deleteDirectory(Path directory) throws IOException {

    Files.walk(directory).filter(Files::isRegularFile).forEach(path -> {
      try {
        Files.delete(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    Files.delete(directory);

  }

  @Test
  void test() throws PathsBundleManagerException, IOException {

    assertTrue(pathsBundleManager.isNextPathsBundleInInputFolder());
    PathsBundle pathsBundle = pathsBundleManager.fetchNextPathsBundleFromInputFolder();
    assertEquals(FILES_NUMBER, pathsBundle.getAllPaths().size());

    pathsBundleManager.movePathsBundleToOutputFolder(pathsBundle);
    int numberOfFilesInInputFolder = getNumberOfFiles(inputFolderPath);
    int numberOfFilesInOutputFolder = getNumberOfFiles(outputFolderPath);

    assertEquals(0, numberOfFilesInInputFolder);
    assertEquals(FILES_NUMBER, numberOfFilesInOutputFolder);

  }

  private int getNumberOfFiles(Path directory) throws IOException {
    return Files.walk(directory).collect(Collectors.toList()).size() - 1;
  }

}
