import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Reactor reactor = new Reactor(8888);
        reactor.run();
    }
}
