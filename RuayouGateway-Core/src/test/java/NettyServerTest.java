import com.ruayou.core.netty.HttpServer;
import com.ruayou.core.netty.NettyServerConfig;
import org.junit.Test;

/**
 * @Author：ruayou
 * @Date：2024/1/26 21:22
 * @Filename：NettyServerTest
 */

public class NettyServerTest {

    public void startServer(){
        HttpServer server = new HttpServer(new NettyServerConfig());
        server.init();
        server.start();
        System.out.println("ss");
    }

    public static void main(String[] args) {
        HttpServer server = new HttpServer(new NettyServerConfig());
        server.init();
        server.start();
    }
}
