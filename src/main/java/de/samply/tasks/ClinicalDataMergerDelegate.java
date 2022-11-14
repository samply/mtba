package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.file.bundle.PathsBundleManager;
import de.samply.file.bundle.PathsBundleManagerImpl;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import de.samply.utils.TemporalDirectoryManager;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClinicalDataMergerDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(ClinicalDataMergerDelegate.class);
  private TemporalDirectoryManager temporalDirectoryManager;

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {

    logger.info("Fetch clinical data and merge");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    PathsBundleManager pathsBundleManager = new PathsBundleManagerImpl(pathsBundle.getDirectory());
    pathsBundle = pathsBundleManager.copyPathsBundleToOutputFolder(pathsBundle,
        temporalDirectoryManager.createTemporalDirectory());
    //TODO

  }

  @Autowired
  public void setTemporalDirectoryManager(
      TemporalDirectoryManager temporalDirectoryManager) {
    this.temporalDirectoryManager = temporalDirectoryManager;
  }
}
