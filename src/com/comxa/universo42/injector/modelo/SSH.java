package com.comxa.universo42.injector.modelo;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.DynamicPortForwarder;
import com.trilead.ssh2.HTTPProxyData;
import java.io.IOException;

public class SSH implements Loggable, Runnable{
	public static final int TCP_TIME_OUT = 7000;
	public static final int KEX_TIME_OUT = 13000;
    
    private String host;
    private int port;
    private String user;
    private String pass;
    private int socksPort;
    
    private String proxyHost;
    private int proxyPort;
    
    private boolean isRunning;
    
    private Connection conexao;
    private DynamicPortForwarder dpf;
    
    public SSH() {}
    
    public SSH(Config config) {
    	setHost(config.getSshHost());
    	setPort(config.getSshPort());
    	setUser(config.getSshUser());
    	setPass(config.getSshPass());
    	setSocksPort(config.getSocksPort());
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getSocksPort() {
        return socksPort;
    }

    public void setSocksPort(int socksPort) {
        this.socksPort = socksPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        onLogReceived("<-> SSH: iniciando conexão.", LOG_LEVEL_INFO, null);
        
        this.conexao = new Connection(this.host, this.port);
        
        
        if (proxyHost != null && proxyHost.length() > 0)
            this.conexao.setProxyData(new HTTPProxyData(this.proxyHost, this.proxyPort));
                
        try {
            this.conexao.connect(null, TCP_TIME_OUT, KEX_TIME_OUT);
        } catch (IOException e) {
            onLogReceived("<#> SSH: falha ao realizar conexão.", LOG_LEVEL_CRITICAL, e);
            return;
        }
        
        boolean success = false;
        try {
            success = this.conexao.authenticateWithPassword(this.user, this.pass);
        } catch (IOException e) {
            onLogReceived("<#> SSH: falha ao autenticar. " + e.getMessage(), LOG_LEVEL_CRITICAL, e);
            this.conexao.close();
            return;
        }
        
        if (!success) {
            onLogReceived("<-> SSH: falha ao autenticar credenciais.", LOG_LEVEL_ATENTION, null);
            this.conexao.close();
            return;
        }
        
        if (this.socksPort != 0) {
            try {
                this.dpf = this.conexao.createDynamicPortForwarder(this.socksPort);
            } catch (IOException e) {
                onLogReceived("<#> SSH: falha ao iniciar port forwarding. " + e.getMessage(), LOG_LEVEL_CRITICAL, e);
                this.conexao.close();
                return;
            }
            
            onLogReceived("<-> SSH: SOCKS Proxy lançado em: 0.0.0.0:" + this.socksPort, LOG_LEVEL_INFO, null);
        }
        
        onLogReceived("<-> SSH: conexão bem sucedida!", LOG_LEVEL_INFO, null);
        
        this.isRunning = true;
    }
    
    public void close() {
        if (isRunning()) {
            onLogReceived("<-> SSH: stopping.", LOG_LEVEL_INFO, null);
            
            try {
                this.dpf.close();
            } catch (IOException ex) {
                onLogReceived("<#> SSH: falha ao fechar port forwarding.", LOG_LEVEL_ATENTION, ex);
            }
            this.conexao.close();
        }
    }
    
    @Override
    public void onLogReceived(String log, int level, Exception e) {}
}
