package de.samply.utils;

import de.samply.spring.MtbaConst;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemporalDirectoryManager {

  private String rootTemporalDirectory;

  public TemporalDirectoryManager(@Value(MtbaConst.TEMPORAL_DIRECTORY_SV) String rootTemporalDirectory) {
    this.rootTemporalDirectory = rootTemporalDirectory;
  }

  public Path createTemporalDirectory(){
    try {
      createRootTemporalDirectoryIfItDoesNotExist();
      return RandomPathGenerator.createRandomDirectory(rootTemporalDirectory);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createRootTemporalDirectoryIfItDoesNotExist(){
    Path path = Paths.get(rootTemporalDirectory);
    if (!Files.exists(path)){
      path.toFile().mkdir();
    }
  }

}
