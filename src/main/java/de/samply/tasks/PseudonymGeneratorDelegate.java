package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PseudonymGeneratorDelegate implements JavaDelegate {

  private final Logger logger = LoggerFactory.getLogger(PseudonymGeneratorDelegate.class);

  @Override
  public void execute(DelegateExecution delegateExecution) throws Exception {
    logger.info("Generate pseudonyms");
    PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    System.out.println("hello");
    //TODO

  }
}
