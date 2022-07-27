package de.samply.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

public class RandomPathGenerator {

  public static final String TEMP_DIRECTORY_PREFIX = "tempDir";
  public static final String DEFAULT_DIRECTORY = ".";
  public static final String FILE_PREFIX = "tempFile-";

  /**
   * Create random csv files.
   * @param filesNumber number of files to be created.
   * @return List of files.
   * @throws IOException IO Exception.
   */
  public static List<Path> createRandomPaths(int filesNumber) throws IOException {

    List<Path> pathList = new ArrayList<>();

    Path testDirectory = Files.createTempDirectory(Paths.get(DEFAULT_DIRECTORY),
        TEMP_DIRECTORY_PREFIX);

    for (int i = 0; i < filesNumber; i++) {
      Path randomPath = createRandomPath(testDirectory, i + 1);
      pathList.add(randomPath);
    }

    return pathList;
  }

  /**
   * Creates random csv file.
   * @param directory Directory where the file is created.
   * @param index Number to be added to file prefix.
   * @return Random csv file.
   * @throws IOException IO Exception.
   */
  public static Path createRandomPath(Path directory, Integer index) throws IOException {

    String extension = (index != null && index > 0) ? String.valueOf(index) : "";

    Path file = directory.resolve(FILE_PREFIX + extension);
    List<String> randomContent = generateRandomContent();
    Files.write(file, randomContent);

    return file;

  }

  private static List<String> generateRandomContent() {

    List<String> randomContent = new ArrayList<>();

    Random random = new Random();

    int rowsNumber = random.nextInt(10) + 5;
    int columnsNumber = random.nextInt(10) + 5;

    List<String> randomWords = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      String randomWord = RandomStringUtils.randomAlphabetic(5);
      randomWords.add(randomWord);
    }

    for (int i = 0; i < rowsNumber; i++) {

      StringBuilder stringBuilder = new StringBuilder();
      for (int j = 0; j < columnsNumber; j++) {

        String randomWord = randomWords.get(random.nextInt(randomWords.size()));
        stringBuilder.append(randomWord);
        stringBuilder.append('\t');

      }

      randomContent.add(stringBuilder.toString());

    }

    return randomContent;

  }

}
