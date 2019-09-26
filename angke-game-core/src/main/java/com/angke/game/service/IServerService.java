package com.angke.game.service;

/**
 * 服务器启动服务
 * @author GeTOUO
 */
public interface IServerService {

    public String getServiceId();

    public boolean initialize();

    public boolean startService() throws Exception;
    public boolean stopService() throws Exception;

    public void release();

    public byte getState();
    public boolean isRunning();
}
