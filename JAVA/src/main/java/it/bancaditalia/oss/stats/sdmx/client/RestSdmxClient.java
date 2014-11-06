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
package it.bancaditalia.oss.stats.sdmx.client;

import it.bancaditalia.oss.stats.sdmx.api.DSDIdentifier;
import it.bancaditalia.oss.stats.sdmx.api.DataFlowStructure;
import it.bancaditalia.oss.stats.sdmx.api.Dataflow;
import it.bancaditalia.oss.stats.sdmx.api.GenericSDMXClient;
import it.bancaditalia.oss.stats.sdmx.api.PortableTimeSeries;
import it.bancaditalia.oss.stats.sdmx.parser.v21.CodelistParser;
import it.bancaditalia.oss.stats.sdmx.parser.v21.CompactDataParser;
import it.bancaditalia.oss.stats.sdmx.parser.v21.DataStructureParser;
import it.bancaditalia.oss.stats.sdmx.parser.v21.DataflowParser;
import it.bancaditalia.oss.stats.sdmx.parser.v21.RestQueryBuilder;
import it.bancaditalia.oss.stats.util.Configuration;
import it.bancaditalia.oss.stats.util.SdmxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Attilio Mattiocco
 *
 */
public class RestSdmxClient implements GenericSDMXClient{
		
	protected boolean dotStat = false;
	protected URL wsEndpoint = null;
	protected String name = null;
	protected boolean needsCredentials = false;
	protected boolean containsCredentials = false;
	protected String user = null;
	protected String pw = null;
	
	private static final String sourceClass = RestSdmxClient.class.getSimpleName();
	protected static Logger logger = Configuration.getSdmxLogger();
	
	public RestSdmxClient(String name, URL endpoint, boolean needsCredentials, boolean dotStat){
		this.wsEndpoint = endpoint;
		this.name = name;
		this.needsCredentials=needsCredentials;
		this.dotStat=dotStat;
	}

	@Override
	public Map<String, Dataflow> getDataflows() throws SdmxException {
		String query=null;
		String xml = null;
		Map<String, Dataflow> result = null;
		query = buildFlowQuery(wsEndpoint, SdmxClientHandler.ALL_AGENCIES, "all", SdmxClientHandler.LATEST_VERSION);
		xml = runQuery(query, null);
		if(xml!=null && !xml.isEmpty()){
			logger.finest(xml);
			try {
				List<Dataflow> flows = DataflowParser.parse(xml);
				if(flows.size() > 0){
					result = new HashMap<String, Dataflow>();
					for (Iterator<Dataflow> iterator = flows.iterator(); iterator.hasNext();) {
						Dataflow dataflow = (Dataflow) iterator.next();
						result.put(dataflow.getId(), dataflow);
					}
				}
				else{
					throw new SdmxException("The query returned zero dataflows");
				}
				
			} catch (Exception e) {
				logger.severe("Exception caught parsing results from call to provider " + name);
				logger.log(Level.FINER, "Exception: ", e);
				throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
			}
		}
		else{
			throw new SdmxException("The query returned an empty result");
		}
		return result;
	}

	@Override
	public Dataflow getDataflow(String dataflow, String agency, String version) throws SdmxException {
		String query=null;
		String xml = null;
		Dataflow result = null;
		query = buildFlowQuery(wsEndpoint, dataflow, agency, version);
		xml = runQuery(query, null);
		if(xml!=null && !xml.isEmpty()){
			logger.finest(xml);
			try {
				List<Dataflow> flows = DataflowParser.parse(xml);
				if(flows.size() >= 1){
					result = flows.get(0);
				}
				else{
					throw new SdmxException("The query returned zero dataflows");
				}
			} catch (Exception e) {
				logger.severe("Exception caught parsing results from call to provider " + name);
				logger.log(Level.FINER, "Exception: ", e);
				throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
			}
	
		}
		else{
			throw new SdmxException("The query returned an empty result");
		}
		return result;
	}

	@Override
	public DataFlowStructure getDataFlowStructure(DSDIdentifier dsd) throws SdmxException {
		String query=null;
		String xml = null;
		DataFlowStructure str = null;
		if(dsd!=null){
			query = buildDSDQuery(wsEndpoint, dsd.getId(), dsd.getAgency(), dsd.getVersion());
			
			xml = runQuery(query, null);
			if(xml!=null && !xml.isEmpty()){
				logger.finest(xml);
				try {
					str = DataStructureParser.parse(xml).get(0);
				} catch (Exception e) {
					logger.severe("Exception caught parsing results from call to provider " + name);
					logger.log(Level.FINER, "Exception: ", e);
					throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
				}
			}
			else{
				throw new SdmxException("The query returned an empty result");
			}
		}
		else{
			throw new SdmxException("Null dsd in input");
		}
		return str;
	
	}

	@Override
	public Map<String,String> getCodes(String provider, String codeList, String agency, String version) throws SdmxException {
		String query=null;
		String xml = null;
		Map<String, String> result = null;
		query = buildCodelistQuery(wsEndpoint, codeList, agency, version);
		xml = runQuery(query, null);
		if(xml!=null && !xml.isEmpty()){
			logger.finest(xml);
			try {
				result = CodelistParser.parse(xml);
			} catch (Exception e) {
				logger.severe("Exception caught parsing results from call to provider " + name);
				logger.log(Level.FINER, "Exception: ", e);
				throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
			}
			
		}
		else{
			throw new SdmxException("The query returned an empty result");
		}
		return result;
	}

	@Override
	public List<PortableTimeSeries> getTimeSeries(Dataflow dataflow, DataFlowStructure dsd, String resource, String startTime, String endTime) throws SdmxException {
		String query=null;
		String xml = null;
		List<PortableTimeSeries> ts = null;
		query = buildDataQuery(wsEndpoint, dataflow, resource, startTime, endTime);
			
		xml = runQuery(query, "application/vnd.sdmx.structurespecificdata+xml;version=2.1");
		if(xml!=null && !xml.isEmpty()){
			logger.finest(xml);
			try {
				ts = CompactDataParser.parse(xml, dsd, dataflow.getId());
				//ts = GenericDataParser.parse(xml);
			} catch (Exception e) {
				logger.severe("Exception caught parsing results from call to provider " + name);
				logger.log(Level.FINER, "Exception: ", e);
				throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
			}
		}
		else{
			throw new SdmxException("The query returned an empty result");
		}
			
		return ts;
	}

	@Override
	public boolean needsCredentials() {
		return needsCredentials;
	}

	@Override
	public void setCredentials(String user, String pw) {
		this.user=user;
		this.pw=pw;
		this.needsCredentials=false;
		this.containsCredentials=true;
	}
	
	protected String runQuery(String query, String acceptHeader) throws SdmxException{
		final String sourceMethod = "runQuery";
		logger.entering(sourceClass, sourceMethod);
		logger.info("Contacting web service with query: " + query);
	
		StringBuilder result = new StringBuilder(1000);
		try {
			URL url = new URL(query);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			
			handleHttpHeaders(conn, acceptHeader);
	
			int code = conn.getResponseCode();
			if (code == 200) {
				logger.fine("Connection opened. Code: " +code);
	
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;	
				while ((output = br.readLine()) != null) {
					result.append(output);
				}
				conn.disconnect();
				logger.fine("Connection closed.");
			}
			else{
				String msg = "Connection failed. HTTP error code : " + code + ", message: "+ conn.getResponseMessage() +"\n";
				switch (code) {
				case 400:
					msg += "SDMX meaning: there is a problem with the syntax of the query";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 403:
					msg += "SDMX meaning: The syntax of the query is OK but it has no meaning";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 404:
					msg += "SDMX meaning: No results matching the query.";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 406:
					msg += "SDMX meaning: Not a supported format.";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 500:
					msg += "SDMX meaning: Error on the provider side.";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 501:
					msg += "SDMX meaning: Feature not supported.";
					logger.severe(msg);
					throw new SdmxException(msg);
				case 503:
					msg += "SDMX meaning: Service temporarily unavailable. Please try again later..";
					logger.severe(msg);
					throw new SdmxException(msg);
				default:
					logger.severe(msg);
					throw new SdmxException(msg);
				}
			}
		}
		catch (IOException e) {
			logger.severe("Exception caught calling provider " + name);
			logger.log(Level.FINER, "Exception: ", e);
			throw new SdmxException("Exception. Class: " + e.getClass().getName() + " .Message: " + e.getMessage());
		}
		logger.exiting(sourceClass, sourceMethod);
		String res =  result.toString();
		return(res);
	}

	protected void handleHttpHeaders(HttpURLConnection conn, String acceptHeader){
		if(containsCredentials){
			logger.fine("Setting http authorization");		
			String auth = javax.xml.bind.DatatypeConverter.printBase64Binary((user + ":" + pw).getBytes());
			conn.setRequestProperty("Authorization", "Basic " + auth);
		}
		if(acceptHeader!=null){
			conn.setRequestProperty("Accept", acceptHeader);
		}
	}

	protected String buildDataQuery(URL endpoint, Dataflow dataflow, String resource, String startTime, String endTime) throws SdmxException{
		if( endpoint!=null && 
				dataflow!=null &&
				resource!=null && !resource.isEmpty()){

			String query = RestQueryBuilder.getDataQuery(endpoint, dataflow.getFullIdentifier(), resource, startTime, endTime);
			return query;
		}
		else{
			throw new RuntimeException("Invalid query parameters: dataflow=" + dataflow + " resource=" + resource + " endpoint=" + endpoint);
		}
	}
	
	protected String buildDSDQuery(URL endpoint, String dsd, String agency, String version) throws SdmxException{
		if( endpoint!=null  &&
				agency!=null && !agency.isEmpty() &&
				dsd!=null && !dsd.isEmpty()){

			String query = RestQueryBuilder.getStructureQuery(endpoint, dsd, agency,  version);
			return query;
		}
		else{
			throw new RuntimeException("Invalid query parameters: agency=" + agency + " dsd=" + dsd + " endpoint=" + endpoint);
		}
	}
	
	protected String buildFlowQuery(URL endpoint, String dataflow, String agency, String version) throws SdmxException{
		String query = RestQueryBuilder.getDataflowQuery(endpoint,dataflow, agency, version);
		return query;
	}
	
	protected String buildCodelistQuery(URL endpoint, String codeList, String agency, String version) throws SdmxException {
		String query = RestQueryBuilder.getCodelistQuery(endpoint, codeList, agency, version);
		return query;
	}


}
