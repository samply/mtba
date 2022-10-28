package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CBioportalExporterDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(CBioportalExporterDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {

        logger.info("Export to cBioPortal");
        PathsBundle pathsBundle = (PathsBundle) delegateExecution.getVariable(MtbaConst.PATHS_BUNDLE);
        //TODO
    }
}
