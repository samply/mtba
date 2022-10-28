package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BlazeExporterDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(BlazeExporterDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        logger.info("Export to Blaze");
        PathsBundle pathsBundle = (PathsBundle) delegateExecution.getVariable(MtbaConst.PATHS_BUNDLE);
        //TODO

    }
}
