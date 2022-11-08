package de.samply.spring;

public class MtbaConst {

  // ID Manager Constants
  public final static String ID_MANAGER_GET_IDS_PATH = "/paths/translator/getIds";
  public final static String ID_MANAGER_API_KEY_HEADER = "apiKey";

  // Environment Variables
  public final static String ID_MANAGER_API_KEY = "ID_MANAGER_API_KEY";
  public final static String ID_MANAGER_URL = "ID_MANAGER_URL";
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE = "ID_MANAGER_PSEUDONYM_ID_TYPE";
  public final static String ID_MANAGER_PAGE_SIZE = "ID_MANAGER_PAGE_SIZE";
  public final static String LOCAL_ID_CSV_FILENAME = "LOCAL_ID_CSV_FILENAME";
  public final static String LOCAL_ID_CSV_LOCAL_ID_HEADER = "LOCAL_ID_CSV_LOCAL_ID_HEADER";
  public final static String IDAT_CSV_FILENAME = "IDAT_CSV_FILENAME";
  public final static String IDAT_CSV_FIRST_NAME_HEADER = "IDAT_CSV_FIRST_NAME_HEADER";
  public final static String IDAT_CSV_LAST_NAME_HEADER = "IDAT_CSV_LAST_NAME_HEADER";
  public final static String IDAT_CSV_PREVIOUS_NAMES_HEADER = "IDAT_CSV_PREVIOUS_NAMES_HEADER";
  public final static String IDAT_CSV_BIRTHDAY_HEADER = "IDAT_CSV_BIRTHDAY_HEADER";
  public final static String IDAT_CSV_CITIZENSHIP_HEADER = "IDAT_CSV_CITIZENSHIP_HEADER";
  public final static String IDAT_CSV_GENDER_HEADER = "IDAT_CSV_GENDER_HEADER";
  public final static String NEW_FILES_DIRECTORY = "NEW_FILES_DIRECTORY";
  public final static String TEMPORAL_DIRECTORY = "TEMPORAL_DIRECTORY";
  public final static String PERSIST_DIRECTORY = "PERSIST_DIRECTORY";
  public final static String FHIR_BUNDLE_CSV_FILENAME = "FHIR_BUNDLE_CSV_FILENAME";


  // Spring Values
  public final static String HEAD_SV = "${";
  public final static String BOTTOM_SV = "}";
  public final static String NEW_FILES_DIRECTORY_SV = HEAD_SV + NEW_FILES_DIRECTORY + ":#{'./mtba-files/input'}" + BOTTOM_SV;
  public final static String TEMPORAL_DIRECTORY_SV = HEAD_SV + TEMPORAL_DIRECTORY + ":#{'./mtba-files/temp'}" + BOTTOM_SV;
  public final static String PERSIST_DIRECTORY_SV = HEAD_SV + PERSIST_DIRECTORY + ":#{'./mtba-files/persist'}" + BOTTOM_SV;
  public final static String ID_MANAGER_URL_SV = HEAD_SV + ID_MANAGER_URL + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_API_KEY_SV = HEAD_SV + ID_MANAGER_API_KEY + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE_SV = HEAD_SV + ID_MANAGER_PSEUDONYM_ID_TYPE + ":#{null}" + BOTTOM_SV;
  public final static String LOCAL_ID_CSV_FILENAME_SV = HEAD_SV + LOCAL_ID_CSV_FILENAME + ":#{null}" + BOTTOM_SV;
  public final static String LOCAL_ID_CSV_LOCAL_ID_HEADER_SV = HEAD_SV + LOCAL_ID_CSV_LOCAL_ID_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_FILENAME_SV = HEAD_SV + IDAT_CSV_FILENAME + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_FIRST_NAME_HEADER_SV = HEAD_SV + IDAT_CSV_FIRST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_LAST_NAME_HEADER_SV = HEAD_SV + IDAT_CSV_LAST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_PREVIOUS_NAMES_HEADER_SV = HEAD_SV + IDAT_CSV_PREVIOUS_NAMES_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_BIRTHDAY_HEADER_SV = HEAD_SV + IDAT_CSV_BIRTHDAY_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_CITIZENSHIP_HEADER_SV = HEAD_SV + IDAT_CSV_CITIZENSHIP_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDAT_CSV_GENDER_HEADER_SV = HEAD_SV + IDAT_CSV_GENDER_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String FHIR_BUNDLE_CSV_FILENAME_SV = HEAD_SV + FHIR_BUNDLE_CSV_FILENAME + ":#{'data_mutations_extended.txt'}" + BOTTOM_SV;

  public final static String ID_MANAGER_PAGE_SIZE_SV = HEAD_SV + ID_MANAGER_PAGE_SIZE + ":#{'10'}" + BOTTOM_SV;


  // Camunda BP Constants
  public final static String PATHS_BUNDLE = "pathsBundle";
  public final static String IS_PATHS_BUNDLE_EMPTY = "isPathsBundleEmpty";

}
