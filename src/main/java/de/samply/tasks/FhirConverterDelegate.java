package de.samply.tasks;

import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import de.samply.utils.PathsBundleUtils;
import java.io.IOException;
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
    private String scriptInterpreter;

    public FhirConverterDelegate(
        @Value(MtbaConst.FHIR_BUNDLE_CSV_FILENAME_SV) String dataMutationFile,
        @Value(MtbaConst.FHIR_BUNDLE_CSV_SCRIPT_INTERPRETER_SV) String scriptInterpreter
        ) {
        this.dataMutationFile = dataMutationFile;
        this.scriptInterpreter = scriptInterpreter;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        //TODO
        logger.info("Convert to FHIR");
        PathsBundle pathsBundle = PathsBundleUtils.getPathsBundleVariable(delegateExecution);
        Path path = pathsBundle.getPath(dataMutationFile);

    }

    private void executeScript(Path path) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(scriptInterpreter, path.toAbsolutePath().toString());
        Process process = processBuilder.start();
        int statusCode = process.waitFor();
        //TODO
        System.out.println();
    }
}
