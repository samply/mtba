package de.samply.blaze;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {

  private EntryResponse response;
  private String fullUrl;

  public EntryResponse getResponse() {
    return response;
  }

  public void setResponse(EntryResponse response) {
    this.response = response;
  }

  public String getFullUrl() {
    return fullUrl;
  }

  public void setFullUrl(String fullUrl) {
    this.fullUrl = fullUrl;
  }

}
