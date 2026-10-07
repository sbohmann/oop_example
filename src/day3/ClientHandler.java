package day3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

class ClientHandler {
    private final CountryForKey countryForKey;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(4096);

    ClientHandler(CountryForKey countryForKey) {
        this.countryForKey = countryForKey;
    }

    void handle(SocketChannel client) throws IOException {
        readBuffer.clear();
        int bytesRead;
        try {
            bytesRead = client.read(readBuffer);
        } catch (IOException e) {
            bytesRead = -1;
        }

        if (bytesRead == -1) {
            client.close();
        } else {
            readBuffer.flip();
            while (readBuffer.hasRemaining()) {
                byte b = readBuffer.get();
                String mappedString = countryForKey.lookup(b);
                ByteBuffer writeBuffer = ByteBuffer.wrap(mappedString.getBytes(StandardCharsets.UTF_8));
                while (writeBuffer.hasRemaining()) {
                    client.write(writeBuffer);
                }
            }
        }
        readBuffer.clear();
    }
}
