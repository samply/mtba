package de.samply.tasks;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemporalFilesDeleterDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(TemporalFilesDeleterDelegate.class);
  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {

    logger.info("Remove temporal files");
    //TODO

  }
}
