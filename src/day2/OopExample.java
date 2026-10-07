package day2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import static java.lang.IO.println;

class OopExample {
    void main() {
        var countryForKey = new CountryForKey();

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverChannel.bind(new InetSocketAddress(8910));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            println("Binary echo service successfully running on port 8910.");

            var server = new Server(selector, countryForKey);
            server.run();
        } catch (IOException e) {
            println("Failed to run binary echo service on port 8910: " + e.getMessage());
        }
    }
}
