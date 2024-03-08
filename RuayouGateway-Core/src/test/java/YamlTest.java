
import com.ruayou.common.config.FilterRule;
import com.ruayou.common.config.FilterRules;
import com.ruayou.common.config.PatternPathConfig;
import com.ruayou.common.utils.PathUtils;
import com.ruayou.common.utils.YamlUtils;
import com.ruayou.common.config.GlobalConfig;
import org.junit.Test;

import java.util.*;

/**
 * @Author：ruayou
 * @Date：2024/1/30 21:33
 * @Filename：YamlTest
 */
public class YamlTest {
    @Test
    public void testYaml(){
        HashMap<String, String> pattern = PatternPathConfig.getConfig().getPattern();
        pattern.put("/user/*","user");
        pattern.put("/user2/*","user2");
        pattern.put("/use3r/*","user3");
        System.out.println(YamlUtils.toYaml(PatternPathConfig.getConfig()));
    }

    @Test
    public void testConfig(){
        GlobalConfig config = new GlobalConfig();
        config.getNacosConfig().setEnv("prod");
        System.out.println(GlobalConfig.getConfig().equals(config));
    }
    @Test
    public void testPathMatch(){
        String p = "/users/*";
        String str = "/users/ers/ser";
        System.out.println(PathUtils.isMatch(str, p));
    }


    @Test
    public void testFilterRule(){
        FilterRule filterRule = new FilterRule();
        filterRule.setRuleId("1");
        Set<String> filters = new HashSet<>();
        filters.add("heys");
        filters.add("sad");
        filterRule.setFilters(filters);
        filterRule.setOrder(1);
        filterRule.setProtocol("http");
        Map<String,String> map=new HashMap<>();
        map.put("/user/*","user-service");
        map.put("/ping/*","ping-service");
        filterRule.setPatterns(map);
        FilterRule.RetryConfig retryConfig = new FilterRule.RetryConfig();
        retryConfig.setRetryCount(3);
        filterRule.setRetryConfig(retryConfig);
        //System.out.println(YamlUtils.toYaml(filterRule));
        List<FilterRule.FlowControlConfig> configs=new ArrayList<>();

        FilterRule.FlowControlConfig config = new FilterRule.FlowControlConfig();
        config.setOrder(1);
        config.setFlowRule(Map.of("key",2,"k2",3));
        config.setServiceIds(Set.of("asd","2asd"));
        configs.add(config);
        filterRule.setFlowControlConfigs(configs);
        FilterRule.MockConfig mockConfig=new FilterRule.MockConfig();
        mockConfig.setMockMap(Map.of("/km/asd/asd","nihao "));
        filterRule.setMockConfig(mockConfig);
        filterRule.setLoadBalanceConfig(new FilterRule.LoadBalanceConfig());
        FilterRules rules=FilterRules.getGlobalRules();
       // rules.addRule(rules.getDefaultFilterRule());
        rules.addRule(filterRule);
        rules.addRule(filterRule);
        System.out.println(YamlUtils.toYaml(rules));


    }

}
