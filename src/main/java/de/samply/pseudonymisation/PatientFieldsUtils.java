package de.samply.pseudonymisation;

import de.samply.spring.MtbaConst;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PatientFieldsUtils {

  private String idManagerGenderMale;
  private String idManagerGenderFemale;
  private String idManagerGenderOther;
  private String idManagerGenderUnknown;

  private Set<String> sourceGenderMale ;
  private Set<String> sourceGenderFemale;
  private Set<String> sourceGenderOther;
  private Set<String> sourceGenderUnknown;

  private DateTimeFormatter idManagerBirthdayDateFormat;
  private DateTimeFormatter sourceBirthdayDateFormat;

  public PatientFieldsUtils(
      @Value(MtbaConst.ID_MANAGER_GENDER_MALE_SV) String idManagerGenderMale,
      @Value(MtbaConst.ID_MANAGER_GENDER_FEMALE_SV) String idManagerGenderFemale,
      @Value(MtbaConst.ID_MANAGER_GENDER_OTHER_SV) String idManagerGenderOther,
      @Value(MtbaConst.ID_MANAGER_GENDER_UNKNOWN_SV) String idManagerGenderUnknown,
      @Value(MtbaConst.SOURCE_GENDER_MALE_SV) String sourceGenderMale,
      @Value(MtbaConst.SOURCE_GENDER_FEMALE_SV) String sourceGenderFemale,
      @Value(MtbaConst.SOURCE_GENDER_OTHER_SV) String sourceGenderOther,
      @Value(MtbaConst.SOURCE_GENDER_UNKNOWN_SV) String sourceGenderUnknown,
      @Value(MtbaConst.ID_MANAGER_BIRTHDAY_DATE_FORMAT_SV) String idManagerBirthdayDateFormat,
      @Value(MtbaConst.SOURCE_BIRTHDAY_DATE_FORMAT_SV) String sourceBirthdayDateFormat) {
    this.idManagerGenderMale = idManagerGenderMale;
    this.idManagerGenderFemale = idManagerGenderFemale;
    this.idManagerGenderOther = idManagerGenderOther;
    this.idManagerGenderUnknown = idManagerGenderUnknown;

    this.sourceGenderMale = splitAndConvertToSet(sourceGenderMale);
    this.sourceGenderFemale = splitAndConvertToSet(sourceGenderFemale);
    this.sourceGenderOther = splitAndConvertToSet(sourceGenderOther);
    this.sourceGenderUnknown = splitAndConvertToSet(sourceGenderUnknown);

    this.idManagerBirthdayDateFormat = DateTimeFormatter.ofPattern(idManagerBirthdayDateFormat);
    this.sourceBirthdayDateFormat = DateTimeFormatter.ofPattern(sourceBirthdayDateFormat);
  }

  private Set<String> splitAndConvertToSet (String elements){
    Set<String> result = new HashSet<>();
    if (elements != null){
      String[] split = elements.trim().split(MtbaConst.SOURCE_GENDER_DELIMITER);
      Arrays.stream(split).forEach(element -> result.add(element.toLowerCase()));
    }
    return result;
  }

  public String covertGenderToIdManager(String sourceGender) {
    sourceGender = sourceGender.toLowerCase();
    if (sourceGenderMale.contains(sourceGender)){
      return idManagerGenderMale;
    } else if (sourceGenderFemale.contains(sourceGender)){
      return idManagerGenderFemale;
    } else if (sourceGenderOther.contains(sourceGender)){
      return idManagerGenderOther;
    } else if (sourceGenderUnknown.contains(sourceGender)){
      return idManagerGenderUnknown;
    } else{
      return idManagerGenderUnknown;
    }
  }

  public String convertBirthdayToIdManager(String sourceBirthday) {
    TemporalAccessor date = sourceBirthdayDateFormat.parse(sourceBirthday);
    return idManagerBirthdayDateFormat.format(date);
  }


}
