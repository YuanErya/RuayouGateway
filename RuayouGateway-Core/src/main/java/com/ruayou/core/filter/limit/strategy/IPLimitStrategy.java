package com.ruayou.core.filter.limit.strategy;

import com.ruayou.core.filter.filter_rule.FilterRule;
import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @Author：ruayou
 * @Date：2024/3/8 23:30
 * @Filename：IPLimitStrategy
 */
@Slf4j
public class IPLimitStrategy implements LimitStrategy {
    @Override
    public Boolean tryPass(String clientIp, FilterRule.LimitConfig config) {
        List<String> limitRule = config.getLimitRule();
        for (String black : limitRule) {
            if (black.contains("-")) {
                //IP段
                String[] rangeParts = black.split("-");
                String startIP = rangeParts[0];
                String endIP = rangeParts[1];
                if (isIPInRange(clientIp,startIP,endIP)) {
                    return false;
                }
            }else{
                //ip
                if (clientIp.equals(black)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isIPInRange(String ipAddress, String startIP, String endIP) {
        try {
            InetAddress start = InetAddress.getByName(startIP);
            InetAddress end = InetAddress.getByName(endIP);
            InetAddress target = InetAddress.getByName(ipAddress);
            long startLong = ipToLong(start);
            long endLong = ipToLong(end);
            long targetLong = ipToLong(target);
            return targetLong >= startLong && targetLong <= endLong;
        } catch (UnknownHostException e) {
            log.error("ip黑名单配置异常");
        }
        return false;
    }

    private static long ipToLong(InetAddress ipAddress) {
        byte[] ipAddressBytes = ipAddress.getAddress();
        long result = 0;
        for (byte octet : ipAddressBytes) {
            result <<= 8;
            result |= octet & 0xFF;
        }
        return result;
    }
}
