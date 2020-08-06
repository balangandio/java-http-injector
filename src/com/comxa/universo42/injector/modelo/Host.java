package com.comxa.universo42.injector.modelo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Host {
	public static final String SPLIT_MACRO = "\\[split\\]";
	
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    
    private String addr;
    private int port;
    
    public Host(Socket socket) {
        this.socket = socket;
        this.addr = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
    }

    public Host(String addr, int port) {
        this.addr = addr;
        this.port = port;
    }
    
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getIn() throws IOException {
        connect();
        return in;
    }

    public OutputStream getOut() throws IOException {
        connect();
        return out;
    }
    
    public void connect() throws IOException {
        if (this.socket == null)
            this.socket = new Socket(this.addr, this.port);
        if (this.in == null)
            this.in = this.socket.getInputStream();
        if (this.out == null)
            this.out = this.socket.getOutputStream();
    }
    
    public void close() throws IOException {
        try {
            if (this.in != null)
                this.in.close();
            this.in = null;
            if (this.out != null)
                this.out.close();
            this.out = null;
        } finally {
            if (this.socket != null)
                this.socket.close();
            this.socket = null;
        }
    }
    
    
    public String getHttpHead() throws IOException {
        return getHttpHead(getIn());
    }
        
    public void writeStream(String str) throws IOException {
        getOut().write(str.getBytes());
    }
    
    public void writeStreamSplited(String str, String macro) throws IOException {
    	String []splits = str.split(macro);
    	
    	for (String s : splits)
    		if (s.length() > 0)
    			getOut().write(s.getBytes());
    }
    
    public void writeStream(InputStream input, int tamBuffer) throws IOException {
        writeStream(input, getOut(), tamBuffer);
    }
    
    public void writeStreamQtdBytes(InputStream input, int qtdBytes, int tamBuffer) throws IOException {
        writeStreamQtdBytes(input, getOut(), qtdBytes, tamBuffer);
    }
    
    
    private void writeStream(InputStream input, OutputStream out, int tamBuffer) throws IOException {
        byte[] buffer = new byte[tamBuffer];
        int len;
        
        while ((len = input.read(buffer)) != -1)
            out.write(buffer, 0, len);
    }
    
    private void writeStreamQtdBytes(InputStream input, OutputStream out, int qtdBytes, int tamBuffer) throws IOException {
        byte[] buffer = new byte[tamBuffer];
        int len, count = 0;
        
        if (count < qtdBytes && (len = input.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            count += len;
        }
    }    
    
    private String getHttpHead(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        String linha = "";
        
        while (!linha.equals("\r\n")) {
            linha = getLinha(in);

            if (linha == null)
                break;

            builder.append(linha);
        }
        
        return builder.toString();
    }
        
    private String getLinha(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        int b = 0;
        
        while (-1 != (b = in.read())) {
            builder.append((char)b);

            if (b == '\r') {
                b = in.read();

                if (b == -1)
                    break;

                builder.append((char)b);

                if (b == '\n')
                    break;
            }
        }

        return (b == -1) ? null : builder.toString();
    }


    @Override
    public String toString() {
        return this.addr + ":" + this.port;
    }
}
