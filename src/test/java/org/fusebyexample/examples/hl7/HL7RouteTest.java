/*
 * Copyright (C) Red Hat, Inc.
 * http://www.redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusebyexample.examples.hl7;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v22.message.ACK;
import ca.uhn.hl7v2.parser.Parser;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/*.xml"})
public class HL7RouteTest {

  @Autowired(required = true)
  private CamelContext camelCtx;

  @Produce(uri = "netty4:tcp://localhost:8888?sync=true&decoder=#hl7decoder&encoder=#hl7encoder")
  private ProducerTemplate hl7TcpProducer;

  @Produce(uri = "file:///tmp/hl7-example/?fileName=camel-test.hl7")
  private ProducerTemplate hl7FileProducer;

  @EndpointInject(uri = "mock:hl7MockBackend")
  private MockEndpoint hl7MockBackend;

  private HapiContext hapiContext;
  private Parser hapiParser;

  private String validHl7Message;
  private String invalidHl7Message;

  private HapiContext hapiContext() {
    if (hapiContext == null) {
      hapiContext = new DefaultHapiContext();
    }
    return hapiContext;
  }

  private Parser hapiParser() {
    if (hapiParser == null) {
      hapiParser = hapiContext().getGenericParser();
    }
    return hapiParser;
  }

  private String validHl7Message() {
    if (validHl7Message == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038||ADT^A01||P|2.2\r");
      sb.append("PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r");
      sb.append("NK1|0222555|NOTREAL^JAMES^R|FA|11111^OTHER ST.^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r");
      sb.append("PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r");
      sb.append("PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r");
      sb.append("AL1||SEV|001^POLLEN\r");
      sb.append("GT1||0222PL|NOTREAL^BOB^B||1111^OTHER ST.^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|1111^OTHER ST.^CITY^ST^99999|(111)222-3333\r");
      sb.append("IN1||022254P|4558PD|BLUE CROSS|1111^OTHER ST.^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554");
      validHl7Message = sb.toString();
    }
    return validHl7Message;
  }

  private String invalidHl7Message() {
    if (invalidHl7Message == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("MSH|^~\\&|HIS|RIH|EKG|EKG|199904140038xxxxxxxxxx||ADT^A01||P|2.2\r");
      sb.append("PID|0001|00009874|00001122|A00977|SMITH^JOHN^M|MOM|19581119|F|NOTREAL^LINDA^M|C|564 SPRING ST^^NEEDHAM^MA^02494^US|0002|(818)565-1551|(425)828-3344|E|S|C|0000444444|252-00-4414||||SA|||SA||||NONE|V1|0001|I|D.ER^50A^M110^01|ER|P00055|11B^M011^02|070615^BATMAN^GEORGE^L|555888^NOTREAL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^NOTREAL^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|199904101200||||5555112333|||666097^NOTREAL^MANNY^P\r");
      sb.append("NK1|0222555|NOTREAL^JAMES^R|FA|1111^OTHER ST.^CITY^ST^55566|(222)111-3333|(888)999-0000|||||||ORGANIZATION\r");
      sb.append("PV1|0001|I|D.ER^1F^M950^01|ER|P000998|11B^M011^02|070615^BATMAN^GEORGE^L|555888^OKNEL^BOB^K^DR^MD|777889^NOTREAL^SAM^T^DR^MD^PHD|ER|D.WT^1A^M010^01|||ER|AMB|02|070615^VOICE^BILL^L|ER|000001916994|D||||||||||||||||GDD|WA|NORM|02|O|02|E.IN^02D^M090^01|E.IN^01D^M080^01|199904072124|199904101200|||||5555112333|||666097^DNOTREAL^MANNY^P\r");
      sb.append("PV2|||0112^TESTING|55555^PATIENT IS NORMAL|NONE|||19990225|19990226|1|1|TESTING|555888^NOTREAL^BOB^K^DR^MD||||||||||PROD^003^099|02|ER||NONE|19990225|19990223|19990316|NONE\r");
      sb.append("AL1||SEV|001^POLLEN\r");
      sb.append("GT1||0222PL|NOTREAL^BOB^B||1111^OTHER ST.^CITY^ST^77787|(444)999-3333|(222)777-5555||||MO|111-33-5555||||NOTREAL GILL N|1111^OTHER ST.^CITY^ST^99999|(111)222-3333\r");
      sb.append("IN1||022254P|4558PD|BLUE CROSS|1111^OTHER ST.^CITY^ST^00990||(333)333-6666||221K|LENIX|||19980515|19990515|||PATIENT01 TEST D||||||||||||||||||02LL|022LP554");
      invalidHl7Message = sb.toString();
    }
    return invalidHl7Message;
  }

  @Before
  public void initTest() {
    hl7MockBackend.reset();
  }

  @Test
  public void testHl7TcpRouteValidMessage() throws Exception {
    String ack = hl7TcpProducer.requestBody((Object) validHl7Message(), String.class);
    Message hapiAck = hapiParser().parse(ack);
    assertTrue("The result was not a HAPI ACK.", hapiAck instanceof ACK);
    assertTrue("The result was not a successful HAPI ACK.", ((ACK) hapiAck).getMSA().getAcknowledgementCode().getValue().equals("AA"));

    hl7MockBackend.expectedMessageCount(1);
    // TODO Add more validation criteria
    MockEndpoint.assertIsSatisfied(hl7MockBackend);
  }

  @Test
  public void testHl7TcpRouteInvalidMessage() throws Exception {
    String ack = hl7TcpProducer.requestBody((Object) invalidHl7Message(), String.class);
    Message hapiAck = hapiParser().parse(ack);
    assertTrue("The result was not a HAPI ACK.", hapiAck instanceof ACK);
    assertTrue("The result was not an error HAPI ACK.", ((ACK) hapiAck).getMSA().getAcknowledgementCode().getValue().equals("AE"));

    hl7MockBackend.expectedMessageCount(0);
    // TODO Add more validation criteria
    MockEndpoint.assertIsSatisfied(hl7MockBackend);
  }

  @Test
  public void testHl7FileRouteValidMessage() throws Exception {
    hl7FileProducer.sendBody(validHl7Message());

    hl7MockBackend.expectedMessageCount(1);
    // TODO Add more validation criteria
    MockEndpoint.assertIsSatisfied(hl7MockBackend);
  }

  @Test
  public void testHl7FileRouteInvalidMessage() throws Exception {
    hl7FileProducer.sendBody(invalidHl7Message());

    hl7MockBackend.expectedMessageCount(0);
    // TODO Add more validation criteria
    MockEndpoint.assertIsSatisfied(hl7MockBackend);
  }
}
