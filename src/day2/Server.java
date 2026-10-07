package day2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

class Server {
    private final Selector selector;
    private final CountryForKey countryForKey;

    Server(Selector selector, CountryForKey countryForKey) {
        this.selector = selector;
        this.countryForKey = countryForKey;
    }

    void run() throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(4096);

        while (true) {
            selector.select();
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    if (client != null) {
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                } else if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
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
        }
    }
}
