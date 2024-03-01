
import com.ruayou.common.config.FilterRule;
import com.ruayou.common.config.FilterRules;
import com.ruayou.common.config.PatternPathConfig;
import com.ruayou.common.utils.PathUtils;
import com.ruayou.common.utils.YamlUtils;
import com.ruayou.common.config.GlobalConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        ArrayList<String> filters = new ArrayList<>();
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

        FilterRules rules=FilterRules.getGlobalRules();
       // rules.addRule(rules.getDefaultFilterRule());
        rules.addRule(filterRule);
        rules.addRule(filterRule);
        System.out.println(YamlUtils.toYaml(rules));


    }

}
