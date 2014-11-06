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
package it.bankitalia.reri.sia.sdmx.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import it.bankitalia.reri.sia.sdmx.api.DSDIdentifier;
import it.bankitalia.reri.sia.sdmx.api.Dimension;
import it.bankitalia.reri.sia.sdmx.api.PortableTimeSeries;
import it.bankitalia.reri.sia.sdmx.client.SdmxClientHandler;
import it.bankitalia.reri.sia.util.SdmxException;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;


public class ESTATTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
	}
	
	@Test
	public void testGetDSDIdentifier() throws SdmxException {
		DSDIdentifier dsd = SdmxClientHandler.getDSDIdentifier("EUROSTAT", "prc_hicp_midx");
		assertNotNull("Null key family for prc_hicp_midx", dsd);
		assertEquals("Wrong Key Family", "DSD_prc_hicp_midx", dsd.getId());
	}

	@Test
	public void testGetFlows() throws SdmxException {
		Map<String, String> f = SdmxClientHandler.getFlows("EUROSTAT", "prc_hicp_midx");
		assertNotNull("Null getFlows result", f);
		String descr = f.get("prc_hicp_midx");
		assertEquals("Wrong description for prc_hicp_midx", "ESTAT,prc_hicp_midx,1.0 ; HICP (2005 = 100) - monthly data (index)", descr);
	}

	@Test
	public void testGetDimensionsAndCodes() throws SdmxException {
		Map<String, String> codes = SdmxClientHandler.getCodes("EUROSTAT", "prc_hicp_midx", "FREQ");
		assertNotNull("Null getCodes result", codes);
		assertEquals("Wrong code for FREQ annual", codes.get("A"), "Annual");
		List<Dimension> dim = SdmxClientHandler.getDimensions("EUROSTAT", "prc_hicp_midx");
		assertNotNull("Null getDimensions result", dim);
		String result = "[Dimension [id=FREQ, position=1, codelist=Codelist [id=ESTAT/CL_FREQ/1.0, codes={A=Annual, W=Weekly, H=Semi-annual, Q=Quarterly, D=Daily, M=Monthly}]]";
		assertEquals("Wrong dimensions for prc_hicp_midx", result,dim.toString().substring(0, result.length()));
	}


	@Test
	public void testGetCodes() throws SdmxException {
	}

	@Test
	public void testGetTimeSeries() throws SdmxException {
		List<PortableTimeSeries> res = SdmxClientHandler.getTimeSeries("EUROSTAT","prc_hicp_midx/..CP00.EU+DE+FR", null, "2013-08");
		assertNotNull("Null time series result", res);
		
		//warning: they depend on eventual order
		String monthly = res.get(0).getName();
		assertEquals("Wrong name for first time series", "prc_hicp_midx.M.I2005.CP00.DE", monthly);
		String start = res.get(0).getTimeSlots().get(0);
		assertEquals("Wrong start date for time series", "2013-08", start);
		//System.out.println(res);
	}

}
