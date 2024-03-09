package com.ruayou.common.constant;

/**
 * @Author：ruayou
 * @Date：2024/2/2 22:26
 * @Filename：FilterConst
 */
public interface FilterConst {
    String MONITOR_FILTER_ID = "monitor_filter";

    String MONITOR_FILTER_NAME = "monitor_filter";

    int MONITOR_FILTER_ORDER = -1;


    String LIMIT_FILTER_ID = "limit_filter";
    String LIMIT_FILTER_NAME = "limit_filter";
    int LIMIT_FILTER_ORDER = 1;

    String LIMIT_FILTER_TYPE_IP = "ip";
    String LIMIT_FILTER_TYPE_PROVINCE = "province";


    String MOCK_FILTER_ID = "mock_filter";
    String MOCK_FILTER_NAME = "mock_filter";
    int MOCK_FILTER_ORDER = 2;

    String AUTH_FILTER_ID = "auth_filter";

    String AUTH_FILTER_NAME = "auth_filter";
    int AUTH_FILTER_ORDER = 3;

    String GRAY_FILTER_ID = "gray_filter";
    String GRAY_FILTER_NAME = "gray_filter";
    int GRAY_FILTER_ORDER = 4;


    String FLOW_CTL_FILTER_ID = "flow_ctl_filter";
    String FLOW_CTL_FILTER_NAME = "flow_ctl_filter";
    int FLOW_CTL_FILTER_ORDER = 50;
    String FLOW_CTL_FILTER_MODE_LOCAL = "local";
    String FLOW_CTL_FILTER_MODE_CLOUD = "cloud";

    String FLOW_CTL_TYPE_PATH = "path";
    String FLOW_CTL_TYPE_SERVICE = "service";


    String LOAD_BALANCE_FILTER_ID = "load_balance_filter";
    String LOAD_BALANCE_FILTER_NAME = "load_balance_filter";
    int LOAD_BALANCE_FILTER_ORDER = 100;

    String LOAD_BALANCE_KEY = "load_balance";
    String LOAD_BALANCE_STRATEGY_RANDOM = "Random";
    String LOAD_BALANCE_STRATEGY_POLLING = "Polling";
    String MONITOR_END_FILTER_ID = "monitor_end_filter";
    String MONITOR_END_FILTER_NAME = "monitor_end_filter";
    int MONITOR_END_FILTER_ORDER = Integer.MAX_VALUE - 1;

    String ROUTER_FILTER_ID = "router_filter";
    String ROUTER_FILTER_NAME = "router_filter";
    int ROUTER_FILTER_ORDER = Integer.MAX_VALUE;



}
