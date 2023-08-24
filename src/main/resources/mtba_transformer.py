from xml.etree import ElementTree as ET
import csv
import sys
import uuid
from hashlib import sha256

Inputfile = sys.argv[1]
Outputfile = sys.argv[2]
delimiter = "\t"

def convert_row(row):
    patient_id=row[headers.index(blaze_id_column_name)]
    #diagnosis_id="TODO"
    #specimen_id="TODO"
    #time_id="TODO"
    mutation_dktk="M"
    
    identifier=sha256(''.join(row).encode('utf-8')).hexdigest()[0:15]
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
               generateFHIR('48018-6', getMandatoryColumn(row, genename_column_name))+
               generateFHIR('48005-3', getOptionalColumn(row, aminoacidchange_column_name))+
               generateFHIR('81290-9', getOptionalColumn(row, DNAchange_column_name))+
               #generateFHIR('81248-7', getOptionalColumn(row, SeqRef_NCBI_column))+
               generateFHIR('81249-5', getOptionalColumn(row, EnsemblID_column_name))
               )
    request=f'</Observation></resource><request><method value="PUT" /><url value="Observation/{identifier}" /></request>'
    return entry + resource + request + '</entry>'

def generateFHIR(loinc, value):
    result=''
    if value !='':
        if loinc =='48018-6':
            result=generateFHIR_component_codeable(loinc, value)
        else:
            result=generateFHIR_component_string(loinc, value)
    return result

def generateFHIR_component_codeable(loinc, value):
    result = (f'<component><code><coding><system value="http://loinc.org" /><code value="{loinc}"/></coding></code>'+
              f'<valueCodeableConcept><coding><system value="http://www.genenames.org" /><code value="{value}" />' +
              '</coding></valueCodeableConcept></component>')
    return result

def generateFHIR_component_string(loinc, value):
    result = (f'<component><code><coding><system value="http://loinc.org" /><code value="{loinc}"/></coding></code>'+
              f'<valueString value="{value}" /></component>')
    return result

def getOptionalColumn(row, column_name):
    try:
        return row[headers.index(column_name)]
    except IndexError:
        print(f'WARN: missing value in optional column {column_name}')
        return ''
    
def getMandatoryColumn(row, column_name):
    result=''
    try:
        result = row[headers.index(column_name)]
        if result !='':
            return result
        else:
            raise Exception('ERROR: empty value in mandatory column ' + column_name)
    except IndexError:
        raise Exception('ERROR: missing value in mandatory column ' + column_name)


with open(Inputfile, 'r') as f:
    r = csv.reader(f, delimiter=delimiter)
    headers = next(r)

    ###load columns - TODO replace with config file
    blaze_id_column_name = "Tumor_Sample_Barcode"
    genename_column_name = "Hugo_Symbol"
    aminoacidchange_column_name = "Amino_Acid_Change"
    DNAchange_column_name = "TxChange"
    SeqRef_NCBI_column_name = "SeqRef_NCBI"
    EnsemblID_column_name = "ENSEMBL_Gene_Id"

    bundle_id = uuid.uuid1()
    xml = f'<Bundle xmlns="http://hl7.org/fhir">\n<id value="{bundle_id}"/>\n<type value="transaction"/>\n'
    counter=1
    for row in r:
        counter+=1
        if(len(row) != len(headers)):
            raise Exception('ERROR: number of row elemnts ' + str(len(row)) + ' differs from number of headers ' + str(len(headers)) + ' in line ' + str(counter))
        else:
            xml += convert_row(row) + '\n'
    xml += '</Bundle>'
    # print(xml)

    tree = ET.XML(xml)
    with open(Outputfile, "w") as f:
        f.write(xml)