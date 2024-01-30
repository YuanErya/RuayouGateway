import com.ruayou.common.utils.YamlUtils;
import com.ruayou.common.config.GlobalConfig;
import org.junit.Test;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:33
 * @Filename：YamlTest
 */
public class YamlTest {
    @Test
    public void testYaml(){
        String yaml = YamlUtils.toYaml(new GlobalConfig());
        long l = System.currentTimeMillis();
        GlobalConfig config = YamlUtils.parseYaml(
                "httpClientConfig:\n" +
                "  httpConnectTimeout: 30000\n" +
                "  httpConnectionsPerHost: 8000\n" +
                "  httpMaxConnections: 10000\n" +
                "  httpMaxRequestRetry: 2\n" +
                "  httpPooledConnectionIdleTimeout: 60000\n" +
                "  httpRequestTimeout: 30000\n" +
                "nacosConfig:\n" +
                "  applicationName: ruayou-gateway\n" +
                "  env: DEFAULT_GROUP\n" +
                "  registryAddress: 127.0.0.1:8848\n" +
                "nettyServerConfig:\n" +
                "  eventLoopGroupWorkerNum: 1\n" +
                "  maxContentLength: 67108864\n" +
                "  port: 8999", GlobalConfig.class);
        System.out.println(System.currentTimeMillis()-l);
        System.out.println(config);
    }



}
