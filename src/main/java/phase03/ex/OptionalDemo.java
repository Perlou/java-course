package phase03.ex;

import java.util.Map;
import java.util.Optional;

public class OptionalDemo {
    public static void main(String[] args) {
        // Optional<String> optName = Optional.ofNullable(name);
        Map<String, String> config = Map.of("timeout", "30", "host", "localhost");

        int timeout = Optional.ofNullable(config.get("timeout"))
                .map(Integer::parseInt)
                .orElse(60);
        int port = Optional.ofNullable(config.get("port"))
                .map(Integer::parseInt)
                .orElse(8080);

        System.out.println("timeout: " + timeout);
        System.out.println("port (默认): " + port);
    }
}
