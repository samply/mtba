package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathsBundlePersisterDelegate implements JavaDelegate {

  private String persistDirectory;
  private final Logger logger = LoggerFactory.getLogger(PathsBundlePersisterDelegate.class);
  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Persisting path bundle");
    PathsBundle pathsBundle = (PathsBundle) delegateExecution.getVariable(MtbaConst.PATHS_BUNDLE);
    //TODO
  }

  @Autowired
  public void setPersistDirectory(@Value(MtbaConst.PERSIST_DIRECTORY_SV) String persistDirectory) {
    this.persistDirectory = persistDirectory;
  }

}
