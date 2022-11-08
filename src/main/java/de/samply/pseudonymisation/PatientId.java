package de.samply.pseudonymisation;

public record PatientId(
    String idType,
    String idString,
    Boolean tentative,
    String uri
) {}
