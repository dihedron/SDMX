/* Copyright 2010,2014 Bank Of Italy
*
* Licensed under the EUPL, Version 1.1 or - as soon they
* will be approved by the European Commission - subsequent
* versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the
* Licence.
* You may obtain a copy of the Licence at:
*
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in
* writing, software distributed under the Licence is
* distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing
* permissions and limitations under the Licence.
*/
package it.bankitalia.reri.sia.sdmx.parser.v20;

import it.bankitalia.reri.sia.sdmx.api.PortableTimeSeries;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author Attilio Mattiocco
 *
 */
public class GenericDataParser {

	private static final String SERIES = "Series";
	private static final String SERIES_KEY = "SeriesKey";
	private static final String VALUE = "value";
	private static final String CONCEPT = "concept";
	private static final String OBS = "Obs";
	private static final String OBS_TIME = "Time";
	private static final String OBS_VALUE = "ObsValue";
	private static final String ATTRIBUTES = "Attributes";

	public static List<PortableTimeSeries> parse(String xmlBuffer, String dataflow) throws XMLStreamException, UnsupportedEncodingException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new ByteArrayInputStream(xmlBuffer.getBytes("UTF-8"));
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

		List<PortableTimeSeries> tsList = new ArrayList<PortableTimeSeries>();
		PortableTimeSeries ts = null;

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();

				if (startElement.getName().getLocalPart() == (SERIES)) {
					ts = new PortableTimeSeries();
					ts.setDataflow(dataflow);
				}

				if (startElement.getName().getLocalPart() == (SERIES_KEY)) {
					setSeriesKey(ts, eventReader);
				}

				if (startElement.getName().getLocalPart() == (ATTRIBUTES)) {
					setSeriesAttributes(ts, eventReader);
				}

				if (startElement.getName().getLocalPart() == (OBS)) {
					setSeriesSingleObs(ts, eventReader);
				}
				
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == (SERIES)) {
					tsList.add(ts);
				}
			}
		}
		return tsList;
	}

	private static void setSeriesKey(PortableTimeSeries ts, XMLEventReader eventReader) throws XMLStreamException {
		String id = null;
		String val = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equalsIgnoreCase(VALUE)) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equalsIgnoreCase(CONCEPT)) {
							id=attribute.getValue();
						}
						else if (attribute.getName().toString().equalsIgnoreCase(VALUE)) {
							val=attribute.getValue();
						}
					}
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equalsIgnoreCase(VALUE)) {
					ts.addDimension(id+'='+val);
//					if(!key.equalsIgnoreCase("")){
//						key+=".";
//					}
//					key+=val;
					if(id.equalsIgnoreCase("FREQ") || id.equalsIgnoreCase("FREQUENCY")){
						ts.setFrequency(val);
					}
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == (SERIES_KEY)) {
					//ts.setName(key);
					break;
				}
			}
		}
	}
	
	private static void setSeriesAttributes(PortableTimeSeries ts, XMLEventReader eventReader) throws XMLStreamException {
		String id = null;
		String val = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equalsIgnoreCase(VALUE)) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equalsIgnoreCase(CONCEPT)) {
							id=attribute.getValue();
						}
						else if (attribute.getName().toString().equalsIgnoreCase(VALUE)) {
							val=attribute.getValue();
						}
					}
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equalsIgnoreCase(VALUE)) {
					ts.addAttribute(id+'='+val);
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == (ATTRIBUTES)) {
					break;
				}
			}
		}
	}

	private static void setSeriesSingleObs(PortableTimeSeries ts, XMLEventReader eventReader) throws XMLStreamException {
		String time = null;
		String val = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart() == (OBS_TIME)) {
					time = eventReader.getElementText();
				}
				if (startElement.getName().getLocalPart() == (OBS_VALUE)) {
					val = startElement.getAttributeByName(new QName(VALUE)).getValue();
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == (OBS)) {
					ts.addObservation(new Double(val), time, "");
					break;
				}
			}
		}
	}
} 
