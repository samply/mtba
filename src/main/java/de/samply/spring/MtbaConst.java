package de.samply.spring;

public class MtbaConst {

  // Environment Variables
  public final static String NEW_FILES_DIRECTORY = "NEW_FILES_DIRECTORY";
  public final static String TEMPORAL_DIRECTORY = "TEMPORAL_DIRECTORY";
  public final static String PERSIST_DIRECTORY = "PERSIST_DIRECTORY";


  // Spring Values
  public final static String HEAD_SV = "${";
  public final static String BOTTOM_SV = "}";
  public final static String NEW_FILES_DIRECTORY_SV = HEAD_SV + NEW_FILES_DIRECTORY + ":#{'./mtba-files/input'}" + BOTTOM_SV;
  public final static String TEMPORAL_DIRECTORY_SV = HEAD_SV + TEMPORAL_DIRECTORY + ":#{'./mtba-files/temp'}" + BOTTOM_SV;
  public final static String PERSIST_DIRECTORY_SV = HEAD_SV + PERSIST_DIRECTORY + ":#{'./mtba-files/persist'}" + BOTTOM_SV;

  // Camunda BP Constants
  public final static String PATHS_BUNDLE = "pathsBundle";
  public final static String IS_PATHS_BUNDLE_EMPTY = "isPathsBundleEmpty";

}
