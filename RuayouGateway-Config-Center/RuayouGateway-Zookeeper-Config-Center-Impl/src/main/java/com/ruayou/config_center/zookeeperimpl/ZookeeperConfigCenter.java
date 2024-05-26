package com.ruayou.config_center.zookeeperimpl;

import com.ruayou.configcenter.api.ConfigCenter;
import com.ruayou.configcenter.api.ConfigChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import static com.ruayou.common.constant.CommonConst.PATH_SEPARATOR;

/**
 * @Author：ruayou
 * @Date：2024/5/26 13:02
 * @Filename：ZookeeperConfigCenter
 */
@Slf4j
public class ZookeeperConfigCenter implements ConfigCenter {
    private static String CONFIG_BASE = "/config";
    private String serverAddr;
    private String env;
    private CuratorFramework client;

    @Override
    public void init(String serverAddr, String env) {
        this.serverAddr = serverAddr;
        this.env = env;
        this.client = CuratorFrameworkFactory.builder()
                .connectString(serverAddr)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                //重试策略
                .sessionTimeoutMs(5 * 1000)
                .namespace(env)
                //相当于本次连接的所有操作路径都有一个前缀
                .build();
        client.start();
        try {
            //添加Service的根节点
            if (client.checkExists().forPath(CONFIG_BASE) == null) {
                client.create().withMode(CreateMode.CONTAINER).forPath(CONFIG_BASE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void subscribeConfigChange(String dataId, ConfigChangeListener listener) {
        try {
            String configPath = CONFIG_BASE + PATH_SEPARATOR + dataId;
            String config = new String(client.getData().forPath(configPath));
            listener.onConfigChange(config);

            //注册监听器
            NodeCache nodeCache = new NodeCache(client, configPath);
            nodeCache.getListenable().addListener(()->{
                String configStr = new String(nodeCache.getCurrentData().getData());
                log.debug("config from zookeeper: {}", configStr);
                listener.onConfigChange(configStr);
            });
            nodeCache.start();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
