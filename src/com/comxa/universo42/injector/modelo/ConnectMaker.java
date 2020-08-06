package com.comxa.universo42.injector.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class ConnectMaker implements Loggable {
    private int id;

    private int tamBufferEnvio = 4096;
    private int tamBufferRecepcao = 4096;
    private Host destino;
    private Host cliente;
    
    private boolean isThread1Closed;
    private boolean isThread2Closed;
    private boolean isStopped;

    public ConnectMaker() {}

    public ConnectMaker(Host destino, Host cliente) throws IOException {
        this.cliente = cliente;
        this.destino = destino;
    }
  
    public int getTamBufferEnvio() {
        return tamBufferEnvio;
    }

    public void setTamBufferEnvio(int tamBufferEnvio) {
        this.tamBufferEnvio = tamBufferEnvio;
    }

    public int getTamBufferRecepcao() {
        return tamBufferRecepcao;
    }

    public void setTamBufferRecepcao(int tamBufferRecepcao) {
        this.tamBufferRecepcao = tamBufferRecepcao;
    }

    public Host getDestino() {
        return destino;
    }

    public void setDestino(Host destino) {
        this.destino = destino;
    }

    public Host getCliente() {
        return cliente;
    }

    public void setCliente(Host cliente) {
        this.cliente = cliente;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void stop() {
    	isStopped = true;
    	close();
    }
    
    private void close() {
    	try {
	    	try {
	    		this.destino.close();
	    	} finally {
	    		this.cliente.close();
	    	}
    	} catch(IOException e) {}
    }

    
    public void run() {
        //Sender thread
        run(this.cliente, this.destino, this.tamBufferEnvio);
        //Receiver thread
        run(this.destino, this.cliente, this.tamBufferRecepcao);
    }

    private void run(final Host hostIn, final Host hostOut, final int tamBuffer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = hostIn.getIn();
                    OutputStream out = hostOut.getOut();
                    Socket inSocket = hostIn.getSocket();
                    Socket outSocket = hostOut.getSocket();
                    
                    byte[] buffer = new byte[tamBuffer];
                    int len = in.read(buffer);

                    while (len != -1 && !inSocket.isOutputShutdown()) {
                        out.write(buffer, 0, len);
                        out.flush();
                        len = in.read(buffer);
                    }

                    if (len == -1 || inSocket.isOutputShutdown()) {
                        if (!isThread1Closed)
                            isThread1Closed = true;
                        else
                            isThread2Closed = true;
                        
                        inSocket.shutdownInput();
                        outSocket.shutdownOutput();
                    }
                } catch (IOException e) {
                	if (!isStopped)
                		onLogReceived("<#> Thread "+getId()+": erro na transferência de dados. " + e.getMessage(), LOG_LEVEL_CRITICAL, e);
                } finally {
                    if (isThread2Closed) {
                        close();
                        onLogReceived("<-> Thread "+getId()+": conexão encerrada.", LOG_LEVEL_INFO, null);
                    }
                    onConnectionClosed();
                }
            }
        }).start();
    }

    @Override
    public void onLogReceived(String log, int level, Exception e) {}
    
    public void onConnectionClosed() {}
    
}

