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

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/*.xml"})
public class HL7RouteTest {

  @Autowired(required=true)
  private CamelContext camelCtx;
  
  @Produce(uri="mina2:tcp://localhost:8888?sync=true&disconnectOnNoReply=true&timeout=5000&codec=#hl7codec")
  private ProducerTemplate hl7TcpProducer;
  
  @Produce(uri="file:///tmp/?fileName=camel-test.hl7")
  private ProducerTemplate hl7FileProducer;

  @Produce(uri="direct:hl7Direct")
  private ProducerTemplate hl7DirectProducer;
  
  @EndpointInject(uri="mock:hl7DirectResponse")
  private MockEndpoint hl7DirectResponseMockEP;
  
  private String createValidHl7Message() {
    StringBuilder hl7Message = new StringBuilder();
    hl7Message.append("MSH|^~\\&|FUSEDEMO|ORG|TEST|JBOSS|20061019172719||ADT^A01^ADT_A01|MSGID12349876|P|2.4").append("\r");
    hl7Message.append("PID|||20301||Durden^Tyler^^^Mr.||19700312|M|||88 Punchward Dr.^^Los Angeles^CA^11221^USA|||||||").append("\r");
    hl7Message.append("PV1||O|OP^^||||4652^Paulson^Robert|||OP|||||||||9|||||||||||||||||||||||||20061019172717|20061019172718").append("\r");
    return hl7Message.toString();
  }
  
  @Test
  public void testHl7TcpRouteValidMessage() throws Exception {
    hl7DirectResponseMockEP.expectedMessageCount(1);
    // TODO Add more validation criteria
    
    hl7TcpProducer.sendBody(createValidHl7Message());
    
    MockEndpoint.assertIsSatisfied(hl7DirectResponseMockEP);
    hl7DirectResponseMockEP.reset();
  }
  
  @Test
  public void testHl7FileRouteValidMessage() throws Exception {
    hl7DirectResponseMockEP.expectedMessageCount(1);
    // TODO Add more validation criteria
    
    hl7FileProducer.sendBody(createValidHl7Message());
    
    MockEndpoint.assertIsSatisfied(hl7DirectResponseMockEP);
    hl7DirectResponseMockEP.reset();
  }
  
  @Test
  public void testHl7DirectRouteValidMessageWithTransform() throws Exception {
    hl7DirectResponseMockEP.expectedMessageCount(1);
    // TODO Add more validation criteria
    
    hl7DirectProducer.sendBody(createValidHl7Message());
    
    MockEndpoint.assertIsSatisfied(hl7DirectResponseMockEP);
    hl7DirectResponseMockEP.reset();
  }
  
  @Test
  public void testHl7DirectRouteValidMessageNoTransform() throws Exception {
    hl7DirectResponseMockEP.expectedMessageCount(1);
    // TODO Add more validation criteria
    
    hl7DirectProducer.sendBody(createValidHl7Message().replaceAll("\\Q|FUSEDEMO|\\E", "|NOTRANS|"));
    
    MockEndpoint.assertIsSatisfied(hl7DirectResponseMockEP);
    hl7DirectResponseMockEP.reset();
  }
}
