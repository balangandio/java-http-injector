package com.comxa.universo42.injector.controle;

import com.comxa.universo42.embaralhador.Embaralhador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.comxa.universo42.injector.modelo.Config;

public class FileConfig extends Config {
	
	private String filePath;
    
    public FileConfig() {
        super();
        this.filePath = FILE_PATCH;
    }
    
    public void save() throws FileNotFoundException, IOException {
        Properties props = new Properties();

        props.setProperty(LISTEN, (getListenAddr() == null) ? "" : getListenAddr());
        props.setProperty(RPROXY, (getrProxy() == null) ? "" : getrProxy());
        props.setProperty(PAYLOAD, (getPayload() == null) ? "" : getPayload());
        props.setProperty(SSH_HOST, (getSshHost() == null) ? "" : getSshHost());
        props.setProperty(SSH_PORT, String.valueOf(getSshPort()));
        props.setProperty(SSH_USER, (getSshUser() == null) ? "" : getSshUser());
        props.setProperty(SSH_PASS, (getSshPass() == null) ? "" : getSshPass());
        props.setProperty(SOCKS_PORT, String.valueOf(getSocksPort()));
        props.setProperty(USAR_SSH, (isUseSSH()) ? "1" : "0");
        props.setProperty(LOCK_SSH, (isSshLocked()) ? "1" : "0");
        props.setProperty(LOCK_PAYLOAD, (isPayloadLocked()) ? "1" : "0");
        props.setProperty(LOCK_RPROXY, (isRproxyLocked()) ? "1" : "0");
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        props.store(baos, null);
        
        FileOutputStream file = new FileOutputStream(this.filePath);
        file.write(Embaralhador.embaralhar(baos.toByteArray()));
        file.close();
    }
    
    public void load() throws FileNotFoundException, IOException {
        Properties props = new Properties(); 
        
        FileInputStream file = new FileInputStream(this.filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeStream(file, baos, 1024 * 8);
        file.close();
        
        props.load(new ByteArrayInputStream(Embaralhador.desembaralhar(baos.toByteArray())));
        
        
        setListenAddr(props.getProperty(LISTEN, DEFAULT_LISTEN));
        setrProxy(props.getProperty(RPROXY, DEFAULT_RPROXY));
        setPayload(props.getProperty(PAYLOAD, DEFAULT_PAYLOAD));
        setSshHost(props.getProperty(SSH_HOST, DEFAULT_SSH_HOST));
        
        try {
            setSshPort(Integer.valueOf(props.getProperty(SSH_PORT, String.valueOf(DEFAULT_SSH_PORT))));
        } catch(NumberFormatException e) {
            setSshPort(DEFAULT_SSH_PORT);
        }
        
        setSshUser(props.getProperty(SSH_USER, DEFAULT_SSH_USER));
        setSshPass(props.getProperty(SSH_PASS, DEFAULT_SSH_PASS));
        
        try {
            setSocksPort(Integer.valueOf(props.getProperty(SOCKS_PORT, String.valueOf(DEFAULT_SOCKS_PORT))));
        } catch(NumberFormatException e) {
            setSocksPort(DEFAULT_SOCKS_PORT);
        }
        
        setUseSSH(isTrue(props.getProperty(USAR_SSH), DEFAULT_USAR_SSH));
        setSshLocked(isTrue(props.getProperty(LOCK_SSH), DEFAULT_LOCK_SSH));
        setRproxyLocked(isTrue(props.getProperty(LOCK_RPROXY), DEFAULT_LOCK_RPROXY));
        setPayloadLocked(isTrue(props.getProperty(LOCK_PAYLOAD), DEFAULT_LOCK_PAYLOAD));
    }
    
    public void setFilePath(String filePath) {
    	this.filePath = filePath;
    }
    
    
    private boolean isTrue(String str, boolean defaultValue) {
    	if (str == null || str.length() == 0)
    		return defaultValue;
    	
    	return str.charAt(0) == '1';
    }
    
	private void writeStream(InputStream in, OutputStream out, int tamBuffer) throws IOException {
		byte []buffer = new byte[tamBuffer];
		int len;
		
		while ((len = in.read(buffer)) != -1)
			out.write(buffer, 0 , len);
	}
}
