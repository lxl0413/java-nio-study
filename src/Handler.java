import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable{

    final SocketChannel socket;
    final Selector selector;
    final SelectionKey selectionKey;

    ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
    ByteBuffer outputBuffer = ByteBuffer.allocate(1024);

    public Handler(SocketChannel socket, Selector selector)
            throws IOException {
        this.socket = socket;
        this.selector = selector;
        this.socket.configureBlocking(false);   //非阻塞式

        // 监听读就绪事件
        this.selectionKey = socket.register(selector, SelectionKey.OP_READ);

        selectionKey.attach(this);
    }

    @Override
    public void run() {
        if (selectionKey.isReadable()) {
            read();
        } else {
            write();
        }
    }

    public void read() {
        try {
            while (socket.read(inputBuffer) > 0) {
                inputBuffer.flip();
            }
            while (inputBuffer.hasRemaining()) {
                System.out.print((char) inputBuffer.get());
            }
            inputBuffer.clear();

            // 监听写事件
            selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() {
        try {
            outputBuffer.put("TEST-DATA-123".getBytes());
            outputBuffer.flip();
            while (outputBuffer.hasRemaining()) {
                socket.write(outputBuffer);
            }
            outputBuffer.clear();

            // 注销监听写事件
            selectionKey.interestOps(selectionKey.interestOps() &~SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
