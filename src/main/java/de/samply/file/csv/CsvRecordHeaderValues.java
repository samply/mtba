package de.samply.file.csv;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CsvRecordHeaderValues {

    private final Logger logger = LoggerFactory.getLogger(CsvRecordHeaderValues.class);
    private Map<String, String> headerValueMap = new HashMap<>();

    public CsvRecordHeaderValues() {
    }

    public CsvRecordHeaderValues(Map<String, String> headerValuesMap) {
        this.headerValueMap = headerValuesMap;
    }

    /**
     * Values of a csv record for specified headers.
     *
     * @param headers   headers to be considered in csv record.
     * @param csvRecord csv record where the information is extracted.
     */
    public CsvRecordHeaderValues(Set<String> headers, CSVRecord csvRecord) {

        if (csvRecord != null) {
            if (headers.size() > 0) {
                Map<String, String> csvRecordMap = csvRecord.toMap();
                for (String header : headers) {
                    String value = csvRecordMap.get(header);
                    if (value != null) {
                        this.headerValueMap.put(header, value);
                    }
                }
            } else {
                headerValueMap = csvRecord.toMap();
            }
        }

    }

    public Map<String, String> getHeaderValueMap() {
        return headerValueMap;
    }

    public String getValue(String header) {
        return getHeaderValueMap().get(header);
    }

    /**
     * Merges current csv record header value with input.
     *
     * @param csvRecordHeaderValues csv record header values.
     */
    public void merge(CsvRecordHeaderValues csvRecordHeaderValues) {
        for (Entry<String, String> entry : csvRecordHeaderValues.getHeaderValueMap().entrySet()) {
            headerValueMap.put(entry.getKey(), entry.getValue());
        }
    }

}
