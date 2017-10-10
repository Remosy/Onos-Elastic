package org.remi.app;

import org.elasticsearch.common.settings.Settings;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

//Import Jest
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestClient;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;

//Import ElasticSearch
import org.elasticsearch.client.Client;

public class InnerDeviceListener implements DeviceListener {
    private final Logger log = LoggerFactory.getLogger(getClass());
    DeviceService deviceService;
    private DeviceId deviceId;
    //JestClientFactory factory;

    public InnerDeviceListener(DeviceService deviceService, DeviceId deviceId){
        this.deviceService = deviceService;
        this.deviceId = deviceId;
        //this.factory = new JestClientFactory();
    }

    @Override
    public void event(DeviceEvent deviceEvent) {
        if(deviceEvent.type()== DeviceEvent.Type.DEVICE_ADDED||
                deviceEvent.type()== DeviceEvent.Type.DEVICE_AVAILABILITY_CHANGED||
                deviceEvent.type()== DeviceEvent.Type.DEVICE_UPDATED){
            log.info("!!!!!//DEVICE_ADDED//!!!!!!!");
            if (deviceService.isAvailable(deviceEvent.subject().id())) {
                log.info("Device connected {}", deviceEvent.subject().id());

                if (deviceEvent.subject().id().equals(deviceId)) {
                    log.info("!!!!!//DEVICE_AVAILABILITY_CHANGED//!!!!!!!");
                }
            }
        }else if(deviceEvent.type()== DeviceEvent.Type.PORT_UPDATED||
                deviceEvent.type()==DeviceEvent.Type.PORT_STATS_UPDATED){
            log.info("!!!!!//PORT_UPDATED//!!!!!!!");
            List<PortStatistics> portStatisticsList = deviceService.getPortStatistics(deviceId);
            for (PortStatistics portstat : portStatisticsList) {
                try {
                    TransferData(portstat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void TransferData(PortStatistics portstat) throws IOException {
        int port_number = portstat.port();
        long packetsRxDropped = portstat.packetsRxDropped();
        long packets_TxDropped = portstat.packetsTxDropped();
        long packetsReceived = portstat.packetsReceived();
        long packetsSent = portstat.packetsSent();
        long bytesReceived = portstat.bytesReceived();
        long bytesSent = portstat.bytesSent();
        long durationSec = portstat.durationSec();
        /*factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true).build());
        JestClient client = factory.getObject();
        String settings = "\"settings\" : {\n" +
                "        \"number_of_shards\" : 5,\n" +
                "        \"number_of_replicas\" : 1\n" +
                "    }\n";
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("number_of_shards",5);
        settingsBuilder.put("number_of_replicas",1);
        client.execute(new CreateIndex.Builder("articles").settings(Settings.builder().loadFromSource(settings).build().getAsMap()).build());*/
        log.info("@@@@@"+"port number: "+port_number+"\n packet dropped: "+packetsRxDropped+"\n packet sent: " + packetsSent+"\n byte sent: " +bytesSent+"\n over");
    }

}
