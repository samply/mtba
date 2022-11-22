from xml.etree import ElementTree as ET
import csv
import sys
import uuid
from hashlib import sha256

Inputfile = sys.argv[1]
Outputfile = sys.argv[2]

def convert_row(row):
    columns=row[0].split("\t")
    patient_id=columns[blaze_id_column]
    #diagnosis_id="TODO"
    #specimen_id="TODO"
    #time_id="TODO"
    mutation_dktk="M"
    genename=columns[genename_column]
    aminoacidchange=columns[aminoacidchange_column]
    DNAchange=columns[DNAchange_column]
    #SeqRef_NCBI=columns[SeqRef_NCBI_column]
    #SeqRef_NCBI='TODO'
    EnsemblID=columns[EnsemblID_column]
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
               generateFHIR_component_codeable('48018-6', genename)+
               generateFHIR_component_string('48005-3', aminoacidchange)+
               generateFHIR_component_string('81290-9', DNAchange)+
               #generateFHIR_component_string('81248-7', SeqRef_NCBI)+
               generateFHIR_component_string('81249-5', EnsemblID)
               )

    request=f'</Observation></resource><request><method value="PUT" /><url value="Observation/{identifier}" /></request>'
    return entry + resource + request + '</entry>'

def generateFHIR_component_codeable(loinc, value):
    result = (f'<component><code><coding><system value="http://loinc.org" /><code value="{loinc}"/></coding></code>'+
              f'<valueCodeableConcept><coding><system value="http://www.genenames.org" /><code value="{value}" />' +
              '</coding></valueCodeableConcept></component>')
    return result

def generateFHIR_component_string(loinc, value):
    result = (f'<component><code><coding><system value="http://loinc.org" /><code value="{loinc}"/></coding></code>'+
              f'<valueString value="{value}" /></component>')
    return result

with open(Inputfile, 'r') as f:
    r = csv.reader(f)
    headers = next(r)
    headers = headers[0].split('\t')

    ###load columns - TODO replace with config file
    blaze_id_column = headers.index("BLAZE_ID")
    genename_column = headers.index("Hugo_Symbol")
    aminoacidchange_column = headers.index("Amino_Acid_Change")
    DNAchange_column = headers.index("TxChange")
    #SeqRef_NCBI_column = headers.index("")
    EnsemblID_column = headers.index("ENSEMBL_Gene_Id")

    bundle_id = uuid.uuid1()
    xml = f'<Bundle xmlns="http://hl7.org/fhir">\n<id value="{bundle_id}"/>\n<type value="transaction"/>\n'
    for row in r:
        xml += convert_row(row) + '\n'
    xml += '</Bundle>'
    # print(xml)

    tree = ET.XML(xml)
    with open(Outputfile, "w") as f:
        f.write(xml)