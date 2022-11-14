package de.samply.utils;

import static org.junit.jupiter.api.Assertions.*;

import de.samply.file.bundle.PathsBundle;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PathsBundleUtilsTest {

  private static final int FILES_NUMBER = 5;
  private PathsBundle pathsBundle;

  @BeforeEach
  void setUp() throws IOException {
    List<Path> randomPaths = RandomPathGenerator.createRandomCsvPaths(FILES_NUMBER);
    pathsBundle = new PathsBundle();
    pathsBundle.addPaths(randomPaths);
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(pathsBundle.getDirectory().toFile());
  }

  @Test
  void testConvert() {
    JSONArray pathsBundleJson = PathsBundleUtils.convert(pathsBundle);
    PathsBundle generatedPathsBundle = PathsBundleUtils.convert(pathsBundleJson);
    HashSet<Path> generatedPaths = new HashSet<>(generatedPathsBundle.getAllPaths());
    pathsBundle.getAllPaths().forEach(path -> assertTrue(generatedPaths.contains(path)));
  }

}
