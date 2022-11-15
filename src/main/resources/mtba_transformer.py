from xml.etree import ElementTree as ET
import csv
import re
import sys
import uuid
from hashlib import sha256

Inputfile = sys.argv[1]
Outputfile = sys.argv[2]

def convert_row(headers, row):
    columns=re.split(r'\t+', row[0])
    patient_id=f"{columns[-1]}"
    #diagnosis_id="TODO"
    #specimen_id="TODO"
    #time_id="TODO"
    mutation_dktk="M"
    mutation_mtba=f"{columns[-2]}"
    identifier=sha256(''.join(columns).encode('utf-8')).hexdigest()[0:15]
    entry = '<entry>\n' + f'<fullUrl value="http://example.com/Observation/{identifier}" /><resource><Observation xmlns="http://hl7.org/fhir">'
    resource= (f'<id value="{identifier}" />' +
               '<meta><profile value="http://dktk.dkfz.de/fhir/StructureDefinition/onco-core-Observation-GenetischeVariante" /></meta>' +
               '<status value="final" />' +
               '<code><coding><system value="http://loinc.org" /><code value="69548-6" /></coding></code>' +
               f'<subject><reference value="Patient/{patient_id}" /></subject>' +
               #f'<focus><reference value="Condition/{diagnosis_id}"/></focus>' +
               #f'<specimen><reference value="Condition/{specimen_id}"/></specimen>' +
               #f'<effectiveDateTime value="{time_id}" />' +
               '<valueCodeableConcept><coding><system value="http://dktk.dkfz.de/fhir/onco/core/CodeSystem/GenetischeVarianteCS" />' +
               f'<code value="{mutation_dktk}" /></coding></valueCodeableConcept>' +
               '<component><code><coding><system value="http://loinc.org" /><code value="21899-0"/></coding></code>' +
               f'<valueCodeableConcept><coding><system value="http://www.genenames.org" /><code value="{mutation_mtba}" />' +
               '</coding></valueCodeableConcept></component>')

    request=f'</Observation></resource><request><method value="PUT" /><url value="Observation/{identifier}" /></request>'
    return entry + resource + request + '</entry>'



with open(Inputfile, 'r') as f:
    r = csv.reader(f)
    headers = next(r)
    bundle_id=uuid.uuid1()
    xml = f'<Bundle xmlns="http://hl7.org/fhir">\n<id value="{bundle_id}"/>\n<type value="transaction"/>\n'
    for row in r:
        xml += convert_row(headers, row) + '\n'
    xml += '</Bundle>'
    # print(xml)

    tree = ET.XML(xml)
    with open(Outputfile, "w") as f:
        f.write(xml)
