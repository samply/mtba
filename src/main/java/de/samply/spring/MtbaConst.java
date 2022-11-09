package de.samply.spring;

public class MtbaConst {

  // ID Manager Constants
  public final static String ID_MANAGER_GET_IDS_PATH = "/paths/translator/getIds";
  public final static String ID_MANAGER_API_KEY_HEADER = "apiKey";

  // Environment Variables
  public final static String CSV_REMOVE_IDAT = "CSV_REMOVE_IDAT";
  public final static String ID_MANAGER_API_KEY = "ID_MANAGER_API_KEY";
  public final static String ID_MANAGER_URL = "ID_MANAGER_URL";
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE = "ID_MANAGER_PSEUDONYM_ID_TYPE";
  public final static String ID_MANAGER_PAGE_SIZE = "ID_MANAGER_PAGE_SIZE";
  public final static String IDENTITY_CSV_LOCAL_ID_HEADER = "IDENTITY_CSV_LOCAL_ID_HEADER";
  public final static String IDENTITY_CSV_FILENAME = "IDENTITY_CSV_FILENAME";
  public final static String IDENTITY_CSV_FIRST_NAME_HEADER = "IDENTITY_CSV_FIRST_NAME_HEADER";
  public final static String IDENTITY_CSV_LAST_NAME_HEADER = "IDENTITY_CSV_LAST_NAME_HEADER";
  public final static String IDENTITY_CSV_PREVIOUS_NAMES_HEADER = "IDENTITY_CSV_PREVIOUS_NAMES_HEADER";
  public final static String IDENTITY_CSV_BIRTHDAY_HEADER = "IDENTITY_CSV_BIRTHDAY_HEADER";
  public final static String IDENTITY_CSV_CITIZENSHIP_HEADER = "IDENTITY_CSV_CITIZENSHIP_HEADER";
  public final static String IDENTITY_CSV_GENDER_HEADER = "IDENTITY_CSV_GENDER_HEADER";
  public final static String NEW_FILES_DIRECTORY = "NEW_FILES_DIRECTORY";
  public final static String TEMPORAL_DIRECTORY = "TEMPORAL_DIRECTORY";
  public final static String PERSIST_DIRECTORY = "PERSIST_DIRECTORY";
  public final static String FHIR_BUNDLE_CSV_FILENAME = "FHIR_BUNDLE_CSV_FILENAME";
  public final static String FHIR_BUNDLE_CSV_SCRIPT_INTERPRETER = "FHIR_BUNDLE_CSV_SCRIPT_INTERPRETER";
  public final static String ID_MANAGER_GENDER_MALE = "ID_MANAGER_GENDER_MALE";
  public final static String ID_MANAGER_GENDER_FEMALE = "ID_MANAGER_GENDER_FEMALE";
  public final static String ID_MANAGER_GENDER_OTHER = "ID_MANAGER_GENDER_OTHER";
  public final static String ID_MANAGER_GENDER_UNKNOWN = "ID_MANAGER_GENDER_UNKNOWN";
  public final static String SOURCE_GENDER_MALE = "SOURCE_GENDER_MALE";
  public final static String SOURCE_GENDER_FEMALE = "SOURCE_GENDER_FEMALE";
  public final static String SOURCE_GENDER_OTHER = "SOURCE_GENDER_OTHER";
  public final static String SOURCE_GENDER_UNKNOWN = "SOURCE_GENDER_UNKNOWN";
  public final static String ID_MANAGER_BIRTHDAY_DATE_FORMAT = "ID_MANAGER_BIRTHDAY_DATE_FORMAT";
  public final static String SOURCE_BIRTHDAY_DATE_FORMAT = "SOURCE_BIRTHDAY_DATE_FORMAT";

  // Spring Values (SV)
  public final static String HEAD_SV = "${";
  public final static String BOTTOM_SV = "}";
  public final static String NEW_FILES_DIRECTORY_SV = HEAD_SV + NEW_FILES_DIRECTORY + ":#{'./mtba-files/input'}" + BOTTOM_SV;
  public final static String TEMPORAL_DIRECTORY_SV = HEAD_SV + TEMPORAL_DIRECTORY + ":#{'./mtba-files/temp'}" + BOTTOM_SV;
  public final static String PERSIST_DIRECTORY_SV = HEAD_SV + PERSIST_DIRECTORY + ":#{'./mtba-files/persist'}" + BOTTOM_SV;
  public final static String ID_MANAGER_URL_SV = HEAD_SV + ID_MANAGER_URL + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_API_KEY_SV = HEAD_SV + ID_MANAGER_API_KEY + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE_SV = HEAD_SV + ID_MANAGER_PSEUDONYM_ID_TYPE + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_FILENAME_SV = HEAD_SV + IDENTITY_CSV_FILENAME + ":#{'data_clinical_patient.txt'}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_LOCAL_ID_HEADER_SV = HEAD_SV + IDENTITY_CSV_LOCAL_ID_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_FIRST_NAME_HEADER_SV = HEAD_SV + IDENTITY_CSV_FIRST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_LAST_NAME_HEADER_SV = HEAD_SV + IDENTITY_CSV_LAST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_PREVIOUS_NAMES_HEADER_SV = HEAD_SV + IDENTITY_CSV_PREVIOUS_NAMES_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_BIRTHDAY_HEADER_SV = HEAD_SV + IDENTITY_CSV_BIRTHDAY_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_CITIZENSHIP_HEADER_SV = HEAD_SV + IDENTITY_CSV_CITIZENSHIP_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String IDENTITY_CSV_GENDER_HEADER_SV = HEAD_SV + IDENTITY_CSV_GENDER_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String FHIR_BUNDLE_CSV_FILENAME_SV = HEAD_SV + FHIR_BUNDLE_CSV_FILENAME + ":#{'data_mutations_extended.txt'}" + BOTTOM_SV;
  public final static String FHIR_BUNDLE_CSV_SCRIPT_INTERPRETER_SV = HEAD_SV + FHIR_BUNDLE_CSV_SCRIPT_INTERPRETER + ":#{'python'}" + BOTTOM_SV;
  public final static String ID_MANAGER_PAGE_SIZE_SV = HEAD_SV + ID_MANAGER_PAGE_SIZE + ":#{'10'}" + BOTTOM_SV;
  public final static String CSV_REMOVE_IDAT_SV = HEAD_SV + CSV_REMOVE_IDAT + ":#{'true'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_MALE_SV = HEAD_SV + ID_MANAGER_GENDER_MALE + ":#{'M'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_FEMALE_SV = HEAD_SV + ID_MANAGER_GENDER_FEMALE + ":#{'W'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_OTHER_SV = HEAD_SV + ID_MANAGER_GENDER_OTHER + ":#{'S'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_UNKNOWN_SV = HEAD_SV + ID_MANAGER_GENDER_UNKNOWN + ":#{'U'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_MALE_SV = HEAD_SV + SOURCE_GENDER_MALE + ":#{'male,M,männlich'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_FEMALE_SV = HEAD_SV + SOURCE_GENDER_FEMALE + ":#{'female,F,W,weiblich'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_OTHER_SV = HEAD_SV + SOURCE_GENDER_OTHER + ":#{'other,O,S,sonstig,intersexuell'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_UNKNOWN_SV = HEAD_SV + SOURCE_GENDER_UNKNOWN + ":#{'unknown,U,unbekannt,X'}" + BOTTOM_SV;
  public final static String ID_MANAGER_BIRTHDAY_DATE_FORMAT_SV = HEAD_SV + ID_MANAGER_BIRTHDAY_DATE_FORMAT + ":#{'dd.MM.yyyy'}" + BOTTOM_SV;
  public final static String SOURCE_BIRTHDAY_DATE_FORMAT_SV = HEAD_SV + SOURCE_BIRTHDAY_DATE_FORMAT + ":#{'dd.MM.yyyy'}" + BOTTOM_SV;

  // Camunda BP Constants
  public final static String PATHS_BUNDLE = "pathsBundle";
  public final static String IS_PATHS_BUNDLE_EMPTY = "isPathsBundleEmpty";

  // Other
  public final static String SOURCE_GENDER_DELIMITER = ",";

}
