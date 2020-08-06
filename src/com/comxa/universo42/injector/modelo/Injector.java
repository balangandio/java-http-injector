package com.comxa.universo42.injector.modelo;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Injector implements Loggable, Runnable {
    public final static int TIME_OUT_SERVER_LISTENNING = 1000;

    private String listeningAddr;
    private int listeningPort;

    private String proxyAddr;
    private int proxyPort;

    private String payload;

    private boolean isRunning;
    private List<RequisicaoInject> connections = new LinkedList<RequisicaoInject>();
    
    public Injector(Config config) throws IllegalArgumentException {
    	this(config.getListenHost(), config.getListenPort(), config.getProxyHost(), config.getProxyPort());
    }

    public Injector(String listeningAddr, int listeningPort, String proxyAddr, int proxyPort) throws IllegalArgumentException {
        setListeningAddr(listeningAddr);
        setListeningPort(listeningPort);
        setProxyAddr(proxyAddr);
        setProxyPort(proxyPort);
    }

    public String getListeningAddr() {
        return listeningAddr;
    }

    public void setListeningAddr(String listeningAddr) throws IllegalArgumentException {
    	if (listeningAddr == null || listeningAddr.length() == 0)
    		throw new IllegalArgumentException("<#> Endereço listening vazio!");
    	
        this.listeningAddr = listeningAddr;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void setListeningPort(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public String getProxyAddr() {
        return proxyAddr;
    }

    public void setProxyAddr(String proxyAddr) throws IllegalArgumentException {
    	if (proxyAddr == null || proxyAddr.length() == 0)
    		throw new IllegalArgumentException("<#> Remote proxy vazio!");
    	
        this.proxyAddr = proxyAddr;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void stop() {
        this.isRunning = false;
        for (RequisicaoInject ri : connections)
			ri.stop();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = serverFactory(this.listeningAddr, this.listeningPort);
            serverSocket.setSoTimeout(TIME_OUT_SERVER_LISTENNING);

            this.isRunning = true;

            onLogReceived("<-> Binding em " + this.listeningAddr + ":" + this.listeningPort, LOG_LEVEL_INFO, null);
            
            while (true) {
                try {
                    Socket acceptCliente = serverSocket.accept();
                    onLogReceived("<-> Requisição recebida.", LOG_LEVEL_INFO, null);
          
                    Host hostProxy = new Host(this.proxyAddr, this.proxyPort);
                    Host hostCliente = new Host(acceptCliente);
                    
                    RequisicaoInject reqInject = new RequisicaoInject(hostProxy, hostCliente, acceptCliente.getPort()) {
                        @Override
                        public void onLogReceived(String log, int level, Exception e) {
                            Injector.this.onLogReceived(log, level, e);
                        }
                        
                        @Override
                        public void onConnectionClosed() {
                        	Injector.this.onConnectionClosed(this);
                        }
                    };
                    reqInject.setPayload(payload);
                    
                    connections.add(reqInject);
                    new Thread(reqInject).start();

                } catch (SocketTimeoutException e) {
                    if (!isRunning())
                        break;
                }
            }
        } finally {
            this.isRunning = false;
            if (serverSocket != null)
                serverSocket.close();
            onLogReceived("<-> Server listening interrompido.", LOG_LEVEL_INFO, null);
        }
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            onLogReceived("<#> Erro no servidor. " + e.getMessage(), LOG_LEVEL_CRITICAL, e);
        }
    }

    private ServerSocket serverFactory(String addr, int port) throws IOException {
        ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(addr));

        return server;
    }

    public void onLogReceived(String log, int level, Exception e) {}
    
    public synchronized void onConnectionClosed(RequisicaoInject obj) {
    	connections.remove(obj);
    }
}
