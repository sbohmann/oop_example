package day0;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static java.lang.IO.println;

class OopExample {
    void main() {
        String[] lookupTable = new String[256];
        for (int i = 0; i < 256; i++) {
            lookupTable[i] = "Value_" + i;
        }

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverChannel.bind(new InetSocketAddress(8910));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            println("Binary echo service successfully running on port 8910.");

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
                                String mappedString = lookupTable[Byte.toUnsignedInt(b)];
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
        } catch (IOException e) {
            println("Failed to run binary echo service on port 8910: " + e.getMessage());
        }
    }
}