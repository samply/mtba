package de.samply.pseudonymisation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;


@XmlRootElement
@JsonInclude(Include.NON_NULL)
public record Patient(
    PatientFields fields,
    List<String> idTypes,
    @JsonProperty("locallyUniqueId") String locallyUniqueId
) {}
