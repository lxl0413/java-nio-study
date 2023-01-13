import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
    final ServerSocketChannel serverSocket;
    final Selector selector;

    public Acceptor(ServerSocketChannel serverSocket, Selector selector) {
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            // 拿到accept的客户端连接
            SocketChannel clientSocket = serverSocket.accept();

            if (clientSocket != null) {
                new Handler(clientSocket, selector);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
