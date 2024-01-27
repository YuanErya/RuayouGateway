package com.ruayou.core;

/**
 * @Author：ruayou
 * @Date：2024/1/27 9:17
 * @Filename：ContainerCompoent
 */
public abstract  class ContainerComponent {
    /**
     * 用于向运行容器中添加组件
     * @param component
     */
    public void registerComponent(LifeCycle component){
        ServerContainer.addComponent(component);
    }
}
