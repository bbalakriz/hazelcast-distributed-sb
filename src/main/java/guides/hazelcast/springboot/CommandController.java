package guides.hazelcast.springboot;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;

@RestController
public class CommandController {

    HazelcastInstance client;

    @PostConstruct
    public void init() {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("hz-hazelcast");
        client = HazelcastClient.newHazelcastClient(config);
    }

    private ConcurrentMap<String, String> retrieveMap() {
        return client.getMap("map");
    }

    @PostMapping("/put")
    public CommandResponse put(@RequestParam(value = "key") String key,
            @RequestParam(value = "value", required = false) String value) {
        if (value == null) {
            value = "sample";
        }
        retrieveMap().put(key, value);
        return new CommandResponse(value);
    }

    @GetMapping("/get")
    public CommandResponse get(@RequestParam(value = "key") String key) {
        String value = retrieveMap().get(key);
        return new CommandResponse(value);
    }
}
