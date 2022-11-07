package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.nio.file.Path;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FhirConverterDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(FhirConverterDelegate.class);
    private String dataMutationFile;

    public FhirConverterDelegate(@Value(MtbaConst.FHIR_BUNDLE_CSV_FILENAME_SV) String dataMutationFile) {
        this.dataMutationFile = dataMutationFile;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        //TODO
        logger.info("Convert to FHIR");
        PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
        Path path = pathsBundle.getPath(dataMutationFile);

    }
}
