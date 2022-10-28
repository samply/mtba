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
   *
   * @param filesNumber number of files to be created.
   * @return List of files.
   * @throws IOException IO Exception.
   */
  public static List<Path> createRandomCsvPaths(int filesNumber) throws IOException {

    List<Path> pathList = new ArrayList<>();

    Path testDirectory = createRandomDirectory();

    for (int i = 0; i < filesNumber; i++) {
      Path randomPath = createRandomPath(testDirectory, i + 1);
      pathList.add(randomPath);
    }

    return pathList;
  }

  /**
   * Create random directory.
   *
   * @return Path of new random directory.
   * @throws IOException IO Exception.
   */
  public static Path createRandomDirectory() throws IOException {
    return createRandomDirectory(DEFAULT_DIRECTORY);
  }

  /**
   * Create random directory.
   *
   * @return Path of new random directory.
   * @throws IOException IO Exception.
   */
  public static Path createRandomDirectory(String rootDirectory) throws IOException {
    return Files.createTempDirectory(Paths.get(rootDirectory), TEMP_DIRECTORY_PREFIX);
  }

  /**
   * Creates random csv file.
   *
   * @param directory Directory where the file is created.
   * @param index     Number to be added to file prefix.
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

  public static List<String> generateRandomContent() {

    List<String> randomContent = new ArrayList<>();

    Random random = new Random();

    int rowsNumber = random.nextInt(10) + 5;
    int columnsNumber = random.nextInt(10) + 5;

    randomContent.add(generateHeaders(0, columnsNumber));
    randomContent.addAll(generateRandomContent(rowsNumber, columnsNumber));

    return randomContent;

  }

  public static int generateRandomNumber(){
    return new Random().nextInt(10) + 3;
  }

  public static String generateHeaders(int initialCounter, int columnsNumber) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < columnsNumber; i++) {
      stringBuilder.append(getHeader(i + initialCounter));
      if (i < columnsNumber - 1) {
        stringBuilder.append(Constants.DEFAULT_DELIMITER);
      }
    }
    return stringBuilder.toString();
  }

  public static List<String> generateRandomContent(int rowsNumber, int columnsNumber) {

    List<String> randomContent = new ArrayList<>();

    Random random = new Random();

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
        if (j < columnsNumber - 1) {
          stringBuilder.append(Constants.DEFAULT_DELIMITER);
        }

      }

      randomContent.add(stringBuilder.toString());

    }

    return randomContent;

  }


  /**
   * Generate header.
   *
   * @param column Column index.
   * @return Header title.
   */
  public static String getHeader(int column) {
    return "HEAD-" + column;
  }

}
