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
package it.bankitalia.reri.sia.sdmx.client;

import it.bankitalia.reri.sia.util.SdmxException;



public class OecdClientTest {
	public static void main(String[] args) throws SdmxException{
//		System.err.println(SdmxClientHandler.getFlows("OECD", null));
//		System.err.println(SdmxClientHandler.getDSDIdentifier("OECD", "REFSERIES"));
//		System.err.println(SdmxClientHandler.getDimensions("OECD", "REFSERIES"));
//		System.err.println(SdmxClientHandler.getDataFlowStructure("OECD", "REFSERIES"));
		//System.err.println(SdmxClientHandler.getTimeSeries("OECD", "QNA.ITA.B1_GE.CARSA.Q", "2000", "2010"));
		System.err.println(SdmxClientHandler.getTimeSeries("OECD", "KEI.*.*.*.*", "2000", "2010"));
		//		System.err.println(SdmxClientHandler.getTimeSeries("OECD", "G20_PRICES.CAN.CPALTT01.IXOB.M", "2000", "2010"));

//		System.err.println(SdmxClientHandler.getCodes("OECD", "QNA", "SUBJECT"));

	}
}
