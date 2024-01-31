
import com.ruayou.common.config.PatternPathConfig;
import com.ruayou.common.utils.PathUtils;
import com.ruayou.common.utils.YamlUtils;
import com.ruayou.common.config.GlobalConfig;
import org.junit.Test;

import java.util.HashMap;

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

}
