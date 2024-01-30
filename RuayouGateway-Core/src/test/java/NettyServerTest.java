import com.ruayou.core.netty.NettyHttpServer;
import com.ruayou.common.config.NettyServerConfig;
import com.ruayou.core.netty.processor.HttpServerCoreProcessor;

/**
 * @Author：ruayou
 * @Date：2024/1/26 21:22
 * @Filename：NettyServerTest
 */

public class NettyServerTest {

    public void startServer(){
//        NettyHttpServer server = new NettyHttpServer(new NettyServerConfig());
//        server.init();
//        server.start();
//        System.out.println("ss");
    }

    public static void main(String[] args) {
        NettyHttpServer server = new NettyHttpServer(new NettyServerConfig(),new HttpServerCoreProcessor());
        server.init();
        server.start();
    }
}
