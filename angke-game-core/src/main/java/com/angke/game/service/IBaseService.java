package com.angke.game.service;

/**
 * 基础服务
 * @author GeTOUO
 */
public interface IBaseService {
    public String getId();
    public void startup() throws Exception;
    public void shutdown() throws Exception;
}
