package de.samply.pseudonymisation;

import java.util.List;

public record IdManagerResponse (
  List<PatientId> ids
) {}
