package de.samply.utils;


import de.samply.file.bundle.PathsBundle;
import de.samply.spring.MtbaConst;
import java.nio.file.Paths;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.json.JSONArray;

public class PathsBundleUtils {

  public static JSONArray convert (PathsBundle pathsBundle){
    return new JSONArray(pathsBundle.getAllPaths());
  }

  public static PathsBundle convert (JSONArray pathsBundle){
    PathsBundle result = new PathsBundle();
    if (pathsBundle != null){
      pathsBundle.iterator().forEachRemaining(path -> result.addPath(Paths.get((String)path)));
    }
    return result;
  }

  public static void addPathsBundleAsVariable (DelegateExecution delegateExecution, PathsBundle pathsBundle){
    delegateExecution.setVariable(MtbaConst.PATHS_BUNDLE, convert(pathsBundle).toString());
  }

  public static PathsBundle getPathsBundleVariable (DelegateExecution delegateExecution){
    String pathBundle = (String) delegateExecution.getVariable(MtbaConst.PATHS_BUNDLE);
    return (pathBundle != null) ? convert (new JSONArray(pathBundle)) : new PathsBundle();
  }

}
