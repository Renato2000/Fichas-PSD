import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws IOException{
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(12345));

        Receivers receivers = new Receivers();

        while(true) {
            SocketChannel s = ss.accept();
            receivers.addChannel(s);

            Thread worker = new Thread(new Worker(s, receivers));
            worker.start();
        }
    }
}

class Worker implements Runnable {
    private final SocketChannel s;
    private final Receivers receivers;
    
    public Worker(SocketChannel s, Receivers receivers) {
        this.s = s;
        this.receivers = receivers;
    }

    public void run() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(100);

            while(true) {
                if(s.read(buf) <= 0) {
                    receivers.removeChannel(s);
                    s.close();
                    break;
                }

                buf.flip();
    
                for(SocketChannel r : receivers.getChannels()) {
                    if(!r.equals(s)) r.write(buf.duplicate());
                }

                buf.clear();
            }
            
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

class Receivers {
    private final Map<SocketChannel, Queue<String>> receivers;

    public Receivers() {
        receivers = new HashMap<>();
    }

    public Queue<String> getMessages(SocketChannel ss) {
        return receivers.get(ss);
    }

    public Set<SocketChannel> getChannels() {
        return this.receivers.keySet();
    }
    
    public void addChannel(SocketChannel ss) {
        this.receivers.put(ss, new LinkedList<>());
    }

    public void removeChannel(SocketChannel ss) {
        this.receivers.remove(ss);
    }
}

/* TO DO
Adicionar o bot e clientes com delay
*/