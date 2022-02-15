import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel ss = SocketChannel.open();
        SocketAddress socketAddr = new InetSocketAddress("localhost", 12345);
        ss.connect(socketAddr);
        
        Scanner sc = new Scanner(System.in);
        String str = "";
        
        ByteBuffer buf = ByteBuffer.allocate(100);

        Thread thread = new Thread(new Listenner(ss));
        thread.start();

        while(!str.equals("exit")) {
            System.out.print("> ");
            str = sc.nextLine();
            
            try {            
                buf.put(str.getBytes());
                buf.flip();
                ss.write(buf);
                buf.clear();
                
            } catch(IOException e) {
                e.printStackTrace();
            }            
        }

        sc.close();
    }        
}

class Listenner implements Runnable {
    private final SocketChannel ss;
    private final ByteBuffer buf;

    public Listenner(SocketChannel ss) {
        this.ss = ss;
        this.buf = ByteBuffer.allocate(100);
    }

    public void run() {
        try {
            while(true) {
                if(ss.read(buf) <= 0) break;                    

                System.out.println(new String(buf.array()));

                buf.clear();
            }
        } catch (IOException e) { 
            e.printStackTrace();
        }
    }
}
