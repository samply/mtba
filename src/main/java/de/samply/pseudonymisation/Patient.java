package de.samply.pseudonymisation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public record Patient(
    PatientFields fields,
    List<String> idTypes,
    @JsonProperty("locallyUniqueId") String locallyUniqueId
) {}
