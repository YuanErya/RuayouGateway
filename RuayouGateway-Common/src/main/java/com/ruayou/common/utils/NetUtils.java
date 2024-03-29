package com.ruayou.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author：ruayou
 * @Date：2024/1/29 17:14
 * @Filename：NetUtils
 */
public class NetUtils {

    public static String getLocalIp() {
        return getLocalIp("*>10>192>172>127");
    }

    public static String getLocalIp(String ipPreference) {
        if (ipPreference == null) {
            ipPreference = "*>10>192>172>127";
        }
        String[] prefix = ipPreference.split("[> ]+");
        try {
            Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            String matchedIp = null;
            int matchedIdx = -1;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                //	跳过虚拟网卡
                if(ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                String displayName = ni.getDisplayName().toLowerCase();
                if(displayName.startsWith("v")||displayName.contains("virtual")||displayName.contains("docker"))continue;
                Enumeration<InetAddress> en = ni.getInetAddresses();
                // 	跳过虚拟网卡
                while (en.hasMoreElements()) {
                    InetAddress addr = en.nextElement();
                    if(addr.isLoopbackAddress() ||
                            !addr.isSiteLocalAddress() ||
                            addr.isAnyLocalAddress()) {
                        continue;
                    }
                    String ip = addr.getHostAddress();
                    Matcher matcher = pattern.matcher(ip);
                    if (matcher.matches()) {
                        int idx = matchedIndex(ip, prefix);
                        if (idx == -1) {
                            continue;
                        }
                        if (matchedIdx == -1) {
                            matchedIdx = idx;
                            matchedIp = ip;
                        } else {
                            if (matchedIdx > idx) {
                                matchedIdx = idx;
                                matchedIp = ip;
                            }
                        }
                    }
                }
            }
            if (matchedIp != null)
                return matchedIp;
            return "127.0.0.1";
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private static int matchedIndex(String ip, String[] prefix) {
        for (int i = 0; i < prefix.length; i++) {
            String p = prefix[i];
            if ("*".equals(p)) { // *, assumed to be IP
                if (ip.startsWith("127.") ||
                        ip.startsWith("10.") ||
                        ip.startsWith("172.") ||
                        ip.startsWith("192.")) {
                    continue;
                }
                return i;
            } else {
                if (ip.startsWith(p)) {
                    return i;
                }
            }
        }

        return -1;
    }



}
