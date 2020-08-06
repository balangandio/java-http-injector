package com.comxa.universo42.injector.modelo;

import java.io.IOException;
import java.net.UnknownHostException;

public class RequisicaoInject implements Runnable, Loggable {
    public static final int TAM_BUFFER_RECEPCAO = 4096;
    public static final int TAM_BUFFER_ENVIO = 4096;
    private static final String CONNECT_ESTABLISHED = "200";//"200 Connection established";
    public static final String CONTENT_LENGTH = "Content-Length";
    
    private int id;
    
    private Host hostDest;
    private Host hostCliente;
    private String payload;
    
    private ConnectMaker connect;
    private boolean isStopped;

    public RequisicaoInject() {}

    public RequisicaoInject(Host hostDest, Host hostCliente) {
        this.hostDest = hostDest;
        this.hostCliente = hostCliente;
    }
    
    public RequisicaoInject(Host hostDest, Host hostCliente, int id) {
        this(hostDest, hostCliente);
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public Host getHostDest() {
        return hostDest;
    }

    public void setHostDest(Host hostDest) {
        this.hostDest = hostDest;
    }

    public Host getHostCliente() {
        return this.hostCliente;
    }
    
    public void setHostCliente(Host hostCliente) {
        this.hostCliente = hostCliente;
    }
    
    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    public void stop() {
    	this.isStopped = true;
    	try {
    		close();
    	} catch(IOException e) {}
    }
    
    private void close() throws IOException {
    	if (connect == null) {
    		try {
    			if (this.hostDest != null)
    				this.hostDest.close();
    		}finally{
    			if (this.hostCliente != null)
    				this.hostCliente.close();
    		}
    	}else{
    		this.connect.stop();
    	}
    }
    	
    @Override
    public void run() {
        try {
            try {
                Requisicao reqCliente = getRequisicao(this.hostCliente);
                reqCliente.setPayload(this.payload);

                onLogReceived("<-> Thread "+this.id+": abrindo comunição com proxy.", LOG_LEVEL_INFO, null);
                this.hostDest.writeStreamSplited(reqCliente.getStrRequisicao(), Host.SPLIT_MACRO);
                
                int bodyLen;
                if ((bodyLen = getContentLength(reqCliente)) > 0)
                    this.hostDest.writeStreamQtdBytes(this.hostCliente.getIn(), bodyLen, TAM_BUFFER_ENVIO);

                String respostaDestino = this.hostDest.getHttpHead();
                String statusLine = respostaDestino.substring(0, respostaDestino.indexOf('\r'));

                onLogReceived("<-> Thread "+this.id+": Status line: " + statusLine, LOG_LEVEL_INFO, null);
                this.hostCliente.writeStream(respostaDestino);

                if (statusLine.contains(CONNECT_ESTABLISHED)) {
                    onLogReceived("<-> Thread "+this.id+": executando CONNECT...", LOG_LEVEL_INFO, null);

                    connect = new ConnectMaker(this.hostDest, this.hostCliente) {
                        @Override
                        public void onLogReceived(String log, int level, Exception e) {
                            RequisicaoInject.this.onLogReceived(log, level, e);
                        }
                        
                        @Override
                        public void onConnectionClosed() {
                        	RequisicaoInject.this.onConnectionClosed();
                        }
                    };
                    connect.setId(this.id);
                    connect.setTamBufferEnvio(TAM_BUFFER_ENVIO);
                    connect.setTamBufferRecepcao(TAM_BUFFER_RECEPCAO);
                    connect.run();
                }else{
                    if ((bodyLen = getContentLength(respostaDestino)) > 0)
                        this.hostCliente.writeStreamQtdBytes(this.hostDest.getIn(), bodyLen, TAM_BUFFER_RECEPCAO);
                }
            } finally {
                if (connect == null) {
                    close();
                    onLogReceived("<-> Thread "+this.id+": conexão encerrada.", LOG_LEVEL_INFO, null);
                    onConnectionClosed();
                }
            }

        } catch (UnknownHostException e) {
            onLogReceived("<#> Thread "+this.id+": erro ao resolver host destino.", LOG_LEVEL_CRITICAL, e);
        } catch (IOException e) {
        	if (!isStopped)
        		onLogReceived("<#> Thread "+this.id+": erro. " + e.getMessage(), LOG_LEVEL_CRITICAL, e);
        }
    }

    private Requisicao getRequisicao(Host host) throws IOException {
        Requisicao reqCliente;

        try {
            reqCliente = new Requisicao();
            reqCliente.parseRequisicaoStr(host.getHttpHead());
            reqCliente.setPayload(this.payload);
        } catch (IOException e) {
            throw new IOException("Ao receber requisição cliente: " + e.getMessage());
        }

        return reqCliente;
    }
    
    private int getContentLength(Requisicao req) {        
        String str = req.getHeaderVal(CONTENT_LENGTH);
        
        if (str == null)
            return -1;
        
        try {
            return Integer.valueOf(str);
        } catch(NumberFormatException e) {
            return -1;
        }
    }
    
    private int getContentLength(String str) {
        String cl = String.format("\r\n%s: ", CONTENT_LENGTH);
        
        int i = str.indexOf(cl);
        
        if (i == -1)
            return -1;
        
        int f = str.indexOf("\r\n", i+2);
        
        if (f == -1)
            return -1;
        
        try {
            return Integer.valueOf(str.substring(i + cl.length(), f));
        } catch(NumberFormatException e) {
            return -1;
        }
    }
    
    @Override
    public void onLogReceived(String log, int level, Exception e) {}
    
    public void onConnectionClosed() {}
}