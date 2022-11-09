from xml.etree import ElementTree as ET
import csv
import re

filename = 'C:/projects/mtba/mtba-files/temp/tempDir15846579076490716024/data_mutations_extended.txt'

def convert_row(headers, row):
    columns=re.split(r'\t+', row[0])
    patient_id=f"{columns[-1]}"
    diagnosis_id="TODO"
    specimen_id="TODO"
    mutation_dktk="M"
    mutation_mtba=f"{columns[0]}"
    identifier="asdf"
    entry = '<entry>\n' + f'<fullUrl value="http://example.com/Observation/{identifier}" /><resource><Observation xmlns="http://hl7.org/fhir">'
    resource= (f'<id value="{identifier}" />' +
        '<meta><profile value="http://dktk.dkfz.de/fhir/StructureDefinition/onco-core-Observation-GenetischeVariante" /></meta>' +
        '<status value="final" />' + 
        '<code><coding><system value="http://loinc.org" /><code value="69548-6" /></coding></code>' +
        f'<subject><reference value="Patient/{patient_id}" /></subject>' +
        f'<focus><reference value="Condition/{diagnosis_id}"/></focus>' +
        f'<specimen><reference value="Condition/{specimen_id}"/></specimen>' +
#        '<effectiveDateTime value="{time_TODO}" />' +
        '<valueCodeableConcept><coding><system value="http://dktk.dkfz.de/fhir/onco/core/CodeSystem/GenetischeVarianteCS" />' +
            f'<code value="{mutation_dktk}" /></coding></valueCodeableConcept>' +
        '<component><code><coding><system value="http://loinc.org" /><code value="21899-0"/></coding></code>' +
            f'<valueCodeableConcept><coding><system value="http://www.genenames.org" /><code value="{mutation_mtba}" />' +
            '</coding></valueCodeableConcept></component>')
    
    request=f'</Observation></resource><request><method value="PUT" /><url value="Observation/{identifier}" /></request>'
    return entry + resource + request + '</entry>'



with open(filename, 'r') as f:
    r = csv.reader(f)
    headers = next(r)
    test="a21sd"
    xml = f'<Bundle xmlns="http://hl7.org/fhir">\n<id value="{test}"/>\n<type value="transaction"/>\n'
    for row in r:
        xml += convert_row(headers, row) + '\n'
    xml += '</Bundle>'
    print(xml)
    
    tree = ET.XML(xml)
    with open("C:/projects/mtba/mtba-files/temp/tempDir15846579076490716024/result.xml", "w") as f:
        f.write(xml)
