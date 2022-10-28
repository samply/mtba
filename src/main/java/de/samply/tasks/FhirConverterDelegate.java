package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.utils.PathsBundleUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FhirConverterDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(FhirConverterDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        //TODO
        logger.info("Convert to FHIR");
        PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
    }
}
