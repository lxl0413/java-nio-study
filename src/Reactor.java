import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public class Reactor {
    final ServerSocketChannel serverSocket;
    final Selector selector;

    public Reactor(int port) throws IOException {
        // 创建socketChannel
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);   // 非阻塞式

        // 绑定端口
        serverSocket.bind(new InetSocketAddress(port));

        // 创建Selector
        selector = Selector.open();

        // 向Selector注册Channel，通过Selector监听Accept事件，接收客户端连接
        SelectionKey selectionKey = serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        // 将acceptor绑定到selectionKey上
        selectionKey.attach(new Acceptor(serverSocket, selector));
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                // 阻塞到有事件就绪
                selector.select();

                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey selectionKey : selected) {
                    // 分发事件
                    dispatch(selectionKey);
                }

                // 处理完后清除事件
                selected.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable r = (Runnable) selectionKey.attachment();
        if (r != null) {
            r.run();
        }
    }
}
