package de.samply.spring;

import java.nio.charset.Charset;

public class MtbaConst {

  // Other constants
  public static final String DEFAULT_CSV_DELIMITER = "\t";
  public static final String DEFAULT_END_OF_LINE = System.lineSeparator();
  public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

  // cBioPortal Constands
  public final static String CBIOPORTAL_API_ROOT_PATH = "/api";

  // FHIR Converter Constants
  public final static String SOURCE_GENDER_DELIMITER = ",";
  public final static String MTBA_TRANSFORMER_SCRIPT_FILENAME = "mtba_transformer.py";

  // Blaze Store Constants
  public final static String BLAZE_STORE_API_PATH = "/fhir";
  public final static String BLAZE_STORE_GET_PATIENT_THROUGH_PSEUDONYM_PATH = "/Patient?identifier=";

  // ID Manager Constants
  public final static String ID_MANAGER_GET_IDS_PATH = "/paths/translator/getIds";
  public final static String ID_MANAGER_API_KEY_HEADER = "apiKey";

  // Environment Variables
  public final static String CSV_REMOVE_IDAT = "CSV_REMOVE_IDAT";
  public final static String ID_MANAGER_API_KEY = "ID_MANAGER_API_KEY";
  public final static String ID_MANAGER_URL = "ID_MANAGER_URL";
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE = "ID_MANAGER_PSEUDONYM_ID_TYPE";
  public final static String ID_MANAGER_PAGE_SIZE = "ID_MANAGER_PAGE_SIZE";
  public final static String PATIENT_CSV_LOCAL_ID_HEADER = "PATIENT_CSV_LOCAL_ID_HEADER";
  public final static String PATIENT_CSV_FILENAME = "PATIENT_CSV_FILENAME";
  public final static String PATIENT_CSV_PATIENT_ID_HEADER = "PATIENT_CSV_PATIENT_ID_HEADER";
  public final static String PATIENT_CSV_FIRST_NAME_HEADER = "PATIENT_CSV_FIRST_NAME_HEADER";
  public final static String PATIENT_CSV_LAST_NAME_HEADER = "PATIENT_CSV_LAST_NAME_HEADER";
  public final static String PATIENT_CSV_PREVIOUS_NAMES_HEADER = "PATIENT_CSV_PREVIOUS_NAMES_HEADER";
  public final static String PATIENT_CSV_BIRTHDAY_HEADER = "PATIENT_CSV_BIRTHDAY_HEADER";
  public final static String PATIENT_CSV_CITIZENSHIP_HEADER = "PATIENT_CSV_CITIZENSHIP_HEADER";
  public final static String PATIENT_CSV_GENDER_HEADER = "PATIENT_CSV_GENDER_HEADER";
  public final static String NEW_FILES_DIRECTORY = "NEW_FILES_DIRECTORY";
  public final static String TEMPORAL_DIRECTORY = "TEMPORAL_DIRECTORY";
  public final static String PERSIST_DIRECTORY = "PERSIST_DIRECTORY";
  public final static String MUTATIONS_CSV_FILENAME = "MUTATIONS_CSV_FILENAME";
  public final static String MUTATIONS_CSV_SAMPLE_ID_HEADER = "MUTATIONS_CSV_SAMPLE_ID_HEADER";
  public final static String MUTATIONS_CSV_SCRIPT_INTERPRETER = "MUTATIONS_CSV_SCRIPT_INTERPRETER";
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
  public final static String SAMPLE_CSV_FILENAME = "SAMPLE_CSV_FILENAME";
  public final static String SAMPLE_CSV_SAMPLE_ID_HEADER = "SAMPLE_CSV_SAMPLE_ID_HEADER";
  public final static String SAMPLE_CSV_PATIENT_ID_HEADER = "SAMPLE_CSV_PATIENT_ID_HEADER";
  public final static String BLAZE_STORE_URL = "BLAZE_STORE_URL";
  public final static String BLAZE_ID_HEADER = "BLAZE_ID_HEADER";
  public final static String CBIOPORTAL_URL = "CBIOPORTAL_URL";
  public final static String FILE_CHARSET = "FILE_CHARSET";
  public final static String FILE_END_OF_LINE = "FILE_END_OF_LINE";
  public final static String CSV_DELIMITER = "CSV_DELIMITER";

  // Spring Values (SV)
  public final static String HEAD_SV = "${";
  public final static String BOTTOM_SV = "}";
  public final static String NEW_FILES_DIRECTORY_SV =
      HEAD_SV + NEW_FILES_DIRECTORY + ":#{'./mtba-files/input'}" + BOTTOM_SV;
  public final static String TEMPORAL_DIRECTORY_SV =
      HEAD_SV + TEMPORAL_DIRECTORY + ":#{'./mtba-files/temp'}" + BOTTOM_SV;
  public final static String PERSIST_DIRECTORY_SV =
      HEAD_SV + PERSIST_DIRECTORY + ":#{'./mtba-files/persist'}" + BOTTOM_SV;
  public final static String ID_MANAGER_URL_SV = HEAD_SV + ID_MANAGER_URL + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_API_KEY_SV =
      HEAD_SV + ID_MANAGER_API_KEY + ":#{null}" + BOTTOM_SV;
  public final static String ID_MANAGER_PSEUDONYM_ID_TYPE_SV =
      HEAD_SV + ID_MANAGER_PSEUDONYM_ID_TYPE + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_FILENAME_SV =
      HEAD_SV + PATIENT_CSV_FILENAME + ":#{'data_clinical_patient.txt'}" + BOTTOM_SV;
  public final static String PATIENT_CSV_PATIENT_ID_HEADER_SV =
      HEAD_SV + PATIENT_CSV_PATIENT_ID_HEADER + ":#{'PATIENT_ID'}" + BOTTOM_SV;
  public final static String PATIENT_CSV_LOCAL_ID_HEADER_SV =
      HEAD_SV + PATIENT_CSV_LOCAL_ID_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_FIRST_NAME_HEADER_SV =
      HEAD_SV + PATIENT_CSV_FIRST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_LAST_NAME_HEADER_SV =
      HEAD_SV + PATIENT_CSV_LAST_NAME_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_PREVIOUS_NAMES_HEADER_SV =
      HEAD_SV + PATIENT_CSV_PREVIOUS_NAMES_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_BIRTHDAY_HEADER_SV =
      HEAD_SV + PATIENT_CSV_BIRTHDAY_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_CITIZENSHIP_HEADER_SV =
      HEAD_SV + PATIENT_CSV_CITIZENSHIP_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String PATIENT_CSV_GENDER_HEADER_SV =
      HEAD_SV + PATIENT_CSV_GENDER_HEADER + ":#{null}" + BOTTOM_SV;
  public final static String MUTATIONS_CSV_FILENAME_SV =
      HEAD_SV + MUTATIONS_CSV_FILENAME + ":#{'data_mutations_extended.txt'}" + BOTTOM_SV;
  public final static String MUTATIONS_CSV_SAMPLE_ID_HEADER_SV =
      HEAD_SV + MUTATIONS_CSV_SAMPLE_ID_HEADER + ":#{'Tumor_Sample_Barcode'}" + BOTTOM_SV;
  public final static String MUTATIONS_CSV_SCRIPT_INTERPRETER_SV =
      HEAD_SV + MUTATIONS_CSV_SCRIPT_INTERPRETER + ":#{'python'}" + BOTTOM_SV;
  public final static String ID_MANAGER_PAGE_SIZE_SV =
      HEAD_SV + ID_MANAGER_PAGE_SIZE + ":#{'10'}" + BOTTOM_SV;
  public final static String CSV_REMOVE_IDAT_SV =
      HEAD_SV + CSV_REMOVE_IDAT + ":#{'true'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_MALE_SV =
      HEAD_SV + ID_MANAGER_GENDER_MALE + ":#{'M'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_FEMALE_SV =
      HEAD_SV + ID_MANAGER_GENDER_FEMALE + ":#{'W'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_OTHER_SV =
      HEAD_SV + ID_MANAGER_GENDER_OTHER + ":#{'S'}" + BOTTOM_SV;
  public final static String ID_MANAGER_GENDER_UNKNOWN_SV =
      HEAD_SV + ID_MANAGER_GENDER_UNKNOWN + ":#{'U'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_MALE_SV =
      HEAD_SV + SOURCE_GENDER_MALE + ":#{'male,M,männlich'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_FEMALE_SV =
      HEAD_SV + SOURCE_GENDER_FEMALE + ":#{'female,F,W,weiblich'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_OTHER_SV =
      HEAD_SV + SOURCE_GENDER_OTHER + ":#{'other,O,S,sonstig,intersexuell'}" + BOTTOM_SV;
  public final static String SOURCE_GENDER_UNKNOWN_SV =
      HEAD_SV + SOURCE_GENDER_UNKNOWN + ":#{'unknown,U,unbekannt,X'}" + BOTTOM_SV;
  public final static String ID_MANAGER_BIRTHDAY_DATE_FORMAT_SV =
      HEAD_SV + ID_MANAGER_BIRTHDAY_DATE_FORMAT + ":#{'dd.MM.yyyy'}" + BOTTOM_SV;
  public final static String SOURCE_BIRTHDAY_DATE_FORMAT_SV =
      HEAD_SV + SOURCE_BIRTHDAY_DATE_FORMAT + ":#{'dd.MM.yyyy'}" + BOTTOM_SV;
  public final static String SAMPLE_CSV_FILENAME_SV =
      HEAD_SV + SAMPLE_CSV_FILENAME + ":#{'data_clinical_sample.txt'}" + BOTTOM_SV;
  public final static String SAMPLE_CSV_SAMPLE_ID_HEADER_SV =
      HEAD_SV + SAMPLE_CSV_SAMPLE_ID_HEADER + ":#{'SAMPLE_ID'}" + BOTTOM_SV;
  public final static String SAMPLE_CSV_PATIENT_ID_HEADER_SV =
      HEAD_SV + SAMPLE_CSV_PATIENT_ID_HEADER + ":#{'PATIENT_ID'}" + BOTTOM_SV;
  public final static String BLAZE_STORE_URL_SV =
      HEAD_SV + BLAZE_STORE_URL + ":#{null}" + BOTTOM_SV;
  public final static String BLAZE_ID_HEADER_SV =
      HEAD_SV + BLAZE_ID_HEADER + ":#{'BLAZE_ID'}" + BOTTOM_SV;
  public final static String CBIOPORTAL_URL_SV = HEAD_SV + CBIOPORTAL_URL + ":#{null}" + BOTTOM_SV;
  public final static String FILE_CHARSET_SV = HEAD_SV + FILE_CHARSET + ":#{'null'}" + BOTTOM_SV;
  public final static String FILE_END_OF_LINE_SV =
      HEAD_SV + FILE_END_OF_LINE + ":#{'null'}" + BOTTOM_SV;
  public final static String CSV_DELIMITER_SV = HEAD_SV + CSV_DELIMITER + ":#{'null'}" + BOTTOM_SV;

  // Camunda BP Constants
  public final static String PATHS_BUNDLE = "pathsBundle";
  public final static String IS_PATHS_BUNDLE_EMPTY = "isPathsBundleEmpty";


}
