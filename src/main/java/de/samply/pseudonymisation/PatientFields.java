package de.samply.pseudonymisation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class PatientFields {

  @JsonProperty("Vorname")
  String firstName;
  @JsonProperty("Nachname")
  String lastName;
  @JsonProperty("Fruehere_Namen")
  String previousNames;
  @JsonProperty("Geburtsdatum")
  String birthdate;
  @JsonProperty("Staatsangehoerigkeit")
  String citizenship;
  @JsonProperty("Geschlecht")
  String gender;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPreviousNames() {
    return previousNames;
  }

  public void setPreviousNames(String previousNames) {
    this.previousNames = previousNames;
  }

  public String getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(String birthdate) {
    this.birthdate = birthdate;
  }

  public String getCitizenship() {
    return citizenship;
  }

  public void setCitizenship(String citizenship) {
    this.citizenship = citizenship;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

}
