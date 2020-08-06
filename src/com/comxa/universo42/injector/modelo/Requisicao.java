package com.comxa.universo42.injector.modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Requisicao {
    private static final String NETDATA = "[netData]";     //Ex.: CONNECT 188.100.100.123:443 HTTP/1.0
    private static final String HOST = "[host]";           //Ex.: 188.100.100.123
    private static final String PORT = "[port]";           //Ex.: 443
    private static final String HOST_PORT = "[host_port]"; //Ex.: 188.100.100.123:443
    private static final String PROTOCOL = "[protocol]";   //Ex.: HTTP/1.0
    private static final String NEW_LINE = "[crlf]";       //Ex.: \r\n

    private String metodo;
    private String host;
    private String port;
    private String hostPort;
    private String protocolo;
    private Map<String, String> headers;
    
    private String payload;
    private String strRequisicao;

    public Requisicao() {
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    public String getHeaderVal(String header) {
        String val = null;
        
        for (String h : this.headers.keySet()) {
            if (h.equalsIgnoreCase(header)) {
                val = this.headers.get(h);
                break;
            }
        }
        
        return val;
    }

    public String getStrRequisicao() {
        if (this.payload != null) {
            parsePayload(this.payload);
        }

        return this.strRequisicao;
    }

    public void parsePayload(String payload) {
        if (this.metodo != null && this.host != null && this.port != null && this.protocolo != null)
            payload = payload.replace(NETDATA, String.format("%s %s %s", this.metodo, this.hostPort, this.protocolo));
       
        if (this.host != null)
            payload = payload.replace(HOST, this.host);
       
        if (this.port != null)
            payload = payload.replace(PORT, this.port);
       
        if (this.host != null && this.port != null)
            payload = payload.replace(HOST_PORT, getHostWithPort());
       
        if (this.protocolo != null)
            payload = payload.replace(PROTOCOL, this.protocolo);
        
        payload = payload.replace(NEW_LINE, "\r\n");

        this.strRequisicao = payload;
    }

    public void parseRequisicaoStr(String strReq) {
        if (strReq.length() == 0)
            return;

        Scanner scanner = new Scanner(strReq);
        this.metodo = scanner.next();
        this.hostPort = scanner.next();
        String[] hostAndPort = getHostAndPort();
        this.host = hostAndPort[0];
        this.port = hostAndPort[1];
        this.protocolo = scanner.next();
        this.headers = new HashMap<String, String>();
        String headerKey, headerValue;

        while (scanner.hasNext()) {
            headerKey = scanner.next();
            headerKey = headerKey.substring(0, headerKey.length() - 1);

            if (scanner.hasNextLine())
                headerValue = scanner.nextLine().substring(1);
            else
                break;
            this.headers.put(headerKey, headerValue);
        }

        this.strRequisicao = strReq;

        scanner.close();
    }

    public String makeRequisicao() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("%s %s %s\r\n", this.metodo, this.hostPort, this.protocolo));

        for (String headerKey : this.headers.keySet()) {
            builder.append(String.format("%s: %s\r\n", headerKey, this.headers.get(headerKey)));
        }
        builder.append("\r\n");

        this.strRequisicao = builder.toString();

        return this.strRequisicao;
    }

    
    private String[] getHostAndPort() {
        String[] ret = new String[2];

        if (hostPort.length() > 7 && hostPort.substring(0, 4).equals("http")) {
            ret[0] = hostPort.substring(hostPort.indexOf('/') + 2);
        } else {
            ret[0] = hostPort;
        }

        if (ret[0].contains(":")) {
            String str = ret[0];
            ret[0] = ret[0].substring(0, ret[0].indexOf(':'));
            ret[1] = str.substring(str.indexOf(':') + 1);
            if (ret[1].contains("/")) {
                ret[1] = ret[1].substring(0, ret[1].indexOf('/'));
            }
        }

        return ret;
    }

    private String getHostWithPort() {
        if (this.port != null) {
            return String.format("%s:%s", this.host, this.port);
        }

        return this.host;
    }
}