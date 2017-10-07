
/*
 * Copyright 2017-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.hub.app;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//#################### Imported those ##################################

// In order to use reference and deal with the core service
// For step # 1
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.CoreService;
import org.onosproject.net.packet.PacketService;
import org.onosproject.core.ApplicationId;


// In order to register at for the pkt-in event
// For step # 2
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketContext;


// In order you install matching rules (traffic selector)
// For step # 3
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.TrafficSelector;

// In order to get access to packet header
// For step # 4
import org.onlab.packet.Ethernet;
import org.onosproject.net.packet.PacketPriority;
import java.util.Optional;


//For step #5
import org.onosproject.net.PortNumber;


//#####################################################################


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //  1#-------> You need to refer to the interface class in order register your component
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    //  In order to add/delete matching rules of the selector
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;

    //  PacketProcessor pktprocess; // To process incoming packets and use methods
    //  of PacketProcess such as addProcessor and removeProcessor
    PacketProcessor pktprocess = new Hub();
    private ApplicationId appId;
//#############################


    @Activate
    protected void activate() {
//   2#-------> You need to register your component at the core
        appId = coreService.registerApplication("org.hub.app");

//   3#-------> This is to add a listener for the pkt-in event with priority and process incoming pkts
        packetService.addProcessor(pktprocess, PacketProcessor.director(1));


//   4#-------> This is to add matching rules on incoming packets
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
//    For example, here we just want ARP and IPv4 packets to be forwarded to this app
        selector.matchEthType(Ethernet.TYPE_ARP);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE, appId, Optional.empty());
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE, appId, Optional.empty());




        log.info("Started");
    }
    //  5#-------> Override the packeProcessor class in order to change whatever methods you like
    private class Hub implements PacketProcessor {
        @Override
        public void process(PacketContext pktIn) {
            pktIn.treatmentBuilder().setOutput(PortNumber.FLOOD);
            pktIn.send();
        }
    }
    @Deactivate
    protected void deactivate() {

//    6#-------> You should add this removal to delete the listener
        // and delete the rules once the you deactivate the app
        log.info("Stopped");
        packetService.removeProcessor(pktprocess);
        withdrawIntercepts();

    }
    //   7#---------> You need to cancel the all selectors
    private void withdrawIntercepts() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.cancelPackets(selector.build(), PacketPriority.REACTIVE, appId);
        selector.matchEthType(Ethernet.TYPE_ARP);
        packetService.cancelPackets(selector.build(), PacketPriority.REACTIVE, appId);
    }



}