
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
package org.remi.app;

import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//#################### Imported those ##################################


//#####################################################################


/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;


    private DeviceListener deviceListener;
    private ApplicationId appId;
//#####################################################################


//#####################################################################s
    @Activate
    protected void activate() {

        appId = coreService.registerApplication("org.remi.app");
        Iterable<Device> devices = deviceService.getDevices();
        for(Device d : devices) {
            deviceListener = new InnerDeviceListener(deviceService,d.id());
            deviceService.addListener(deviceListener);
        }

        log.info("!!!!!!!!!!!!!!Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
        //deviceService.removeListener(deviceListener);
    }

}