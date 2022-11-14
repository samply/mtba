package de.samply.pseudonymisation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PatientFieldsUtilsTest {

  private String idManagerMale = "M";
  private String idManagerFemale = "W";
  private String idManagerOther = "S";
  private String idManagerUnknown = "U";
  private String sourceMale = "male,M,männlich";
  private String sourceFemale = "female,F,W,weiblich";
  private String sourceOther = "other,O,S,sonstig,intersexuell";
  private String sourceUnknown = "unknown,U,unbekannt,X";
  private String idManagerDateFormat = "dd.MM.yyyy";
  private String sourceDateFormat = "yyyy-MM-dd";

  private PatientFieldsUtils patientFieldsUtils;

  @BeforeEach
  void setUp() {
    this.patientFieldsUtils = new PatientFieldsUtils(idManagerMale, idManagerFemale,
        idManagerOther, idManagerUnknown, sourceMale, sourceFemale, sourceOther, sourceUnknown,
        idManagerDateFormat, sourceDateFormat);
  }

  @Test
  void testGenderConverter() {

    assertEquals(idManagerMale, patientFieldsUtils.covertGenderToIdManager("m"));
    assertEquals(idManagerMale, patientFieldsUtils.covertGenderToIdManager("M"));
    assertEquals(idManagerMale, patientFieldsUtils.covertGenderToIdManager("male"));
    assertEquals(idManagerMale, patientFieldsUtils.covertGenderToIdManager("Male"));
    assertEquals(idManagerMale, patientFieldsUtils.covertGenderToIdManager("männlich"));

    assertEquals(idManagerFemale, patientFieldsUtils.covertGenderToIdManager("f"));
    assertEquals(idManagerFemale, patientFieldsUtils.covertGenderToIdManager("F"));
    assertEquals(idManagerFemale, patientFieldsUtils.covertGenderToIdManager("W"));
    assertEquals(idManagerFemale, patientFieldsUtils.covertGenderToIdManager("weiblich"));
    assertEquals(idManagerFemale, patientFieldsUtils.covertGenderToIdManager("female"));

    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("other"));
    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("O"));
    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("o"));
    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("S"));
    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("sonstig"));
    assertEquals(idManagerOther, patientFieldsUtils.covertGenderToIdManager("intersexuell"));

    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("unknown"));
    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("unbekannt"));
    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("U"));
    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("u"));
    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("X"));


    assertEquals(idManagerUnknown, patientFieldsUtils.covertGenderToIdManager("asfdkjh"));
  }

  @Test
  void testDateConverter() {
    assertEquals("17.11.1983", patientFieldsUtils.convertBirthdayToIdManager("1983-11-17"));
  }

}
