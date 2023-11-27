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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PathsBundleFactoryDelegate implements JavaDelegate {

    private final Logger logger = LoggerFactory.getLogger(PathsBundleFactoryDelegate.class);
    private PathsBundleManager pathsBundleManager;
    private TemporalDirectoryManager temporalDirectoryManager;
    private boolean isExecutedForTheFirstTime = true;

    public PathsBundleFactoryDelegate(
            @Value(MtbaConst.NEW_FILES_DIRECTORY_SV) String inputDirectory) {
        this.pathsBundleManager = new PathsBundleManagerImpl(inputDirectory);
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        if (isExecutedForTheFirstTime) {
            logger.info("Checking for new files...");
            isExecutedForTheFirstTime = false;
        }

        PathsBundle pathsBundle = pathsBundleManager.fetchNextPathsBundleFromInputFolder();
        delegateExecution.setVariable(MtbaConst.IS_PATHS_BUNDLE_EMPTY, pathsBundle.isPathBundleEmpty());

        if (!pathsBundle.isPathBundleEmpty()) {
            logger.info("Found new files");
            Path outputDirectory = temporalDirectoryManager.createTemporalDirectory();
            pathsBundleManager.movePathsBundleToOutputFolder(pathsBundle, outputDirectory);
            PathsBundleUtils.addPathsBundleAsVariable(delegateExecution, pathsBundle);
        }

    }

    @Autowired
    public void setTemporalDirectoryManager(TemporalDirectoryManager temporalDirectoryManager) {
        this.temporalDirectoryManager = temporalDirectoryManager;
    }

}
