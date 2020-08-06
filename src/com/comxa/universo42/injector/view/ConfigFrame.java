package com.comxa.universo42.injector.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.comxa.universo42.injector.controle.FileConfig;
import com.comxa.universo42.injector.modelo.Config;

public class ConfigFrame extends Frame {
    private static final String TITULO_FRAME = "Config";
    private static final String FRAME_ICON_RESOURCE = "resources/ico.png";
    private static final String BOTAO_VOLTAR_LABEL = "Voltar";
    private static final String BOTAO_RESET_LABEL = "Reset";
    private static final String BOTAO_RESET_TIP = "Limpar config";
	private static final String BOTAO_IMPORTAR_LABEL = "Importar";
	private static final String BOTAO_EXPORTAR_LABEL = "Exportar";
    private static final int COLUNAS_TEXTO = 45;
    private static final int LINHAS_TEXTO = 20;
    
    private JButton botaoVoltar = new JButton(getActionBotaoVoltar());
    private JButton botaoReset = new JButton(getActionBotaoReset());
    private JButton botaoImportar = new JButton(getActionBotaoImportar());
    private JButton botaoExportar = new JButton(getActionBotaoExportar());
    
    private JTextArea texto = new JTextArea(LINHAS_TEXTO, COLUNAS_TEXTO);
    private JScrollPane scrollPaneTexto = new JScrollPane(texto);
    
    private JFrame backFrame;
    private MainWindow main;
    private FileConfig config;
    private JFileChooser fileChooser = null;
     
    
    public ConfigFrame(MainWindow main, JFrame backFrame, FileConfig config) {
        super(TITULO_FRAME, FRAME_ICON_RESOURCE);
        this.config = config;
        this.backFrame = backFrame;
        this.main = main;
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(getOnClosingFrame());
        
        initTexto();
        add(this.scrollPaneTexto, BorderLayout.CENTER);
        
        
        JPanel p = new JPanel(new FlowLayout());
        p.add(this.botaoVoltar);
        p.add(this.botaoReset);
        p.add(this.botaoImportar);
        p.add(this.botaoExportar);
        add(p, BorderLayout.SOUTH);
        
        
        pack();
        setLocation(backFrame.getX() + backFrame.getWidth()/2 - getWidth()/2, backFrame.getY() + backFrame.getHeight()/2 - getHeight()/2);
        setVisible(true);
    }
    
    private WindowAdapter getOnClosingFrame() {
        return new WindowAdapter() {                   
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        };
    }
    
    private Action getActionBotaoVoltar() {
        return new Action(BOTAO_VOLTAR_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	close();
            }
        };
    }
    
    private Action getActionBotaoReset() {
        return new Action(BOTAO_RESET_LABEL, BOTAO_RESET_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	main.resetConfig();
            	main.loadConfig();
            	close();
            }
        };
    }

    private Action getActionBotaoExportar() {
        return new Action(BOTAO_EXPORTAR_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	setVisible(false);
                new ConfigExportFrame(ConfigFrame.this, config);
            }
        };
    }
    
    private Action getActionBotaoImportar() {
        return new Action(BOTAO_IMPORTAR_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	if (fileChooser == null)
            		fileChooser = new JFileChooser();
            	
            	fileChooser.showOpenDialog(ConfigFrame.this);
                
                File file = fileChooser.getSelectedFile();
                
                if (file == null)
                	return;
            	
                if (!file.exists()) {
                	JOptionPane.showMessageDialog(ConfigFrame.this,
                			"Arquivo selecionado não mais existe", 
                			"Arquivo não encontrado!",
                			JOptionPane.OK_OPTION );
                }else{
                	config.setFilePath(file.getPath());
                	
                	try {
                		config.load();
	                	config.setFilePath(Config.FILE_PATCH);
                		config.save();
					} catch (IOException e) {
	                	config.setFilePath(Config.FILE_PATCH);
	                	JOptionPane.showMessageDialog(ConfigFrame.this,
	                			"Houve um erro ao carregar o arquivo. " + e.getMessage(), 
	                			"Error",
	                			JOptionPane.OK_OPTION );
	                	return;
					}
                	config.setFilePath(Config.FILE_PATCH);
                	JOptionPane.showMessageDialog(ConfigFrame.this,
                			"Arquivo importado: " + file.getPath(), 
                			"Done!",
                			JOptionPane.INFORMATION_MESSAGE);

                	main.loadConfig();
                	close();
                }
            }
        };
    }
    
    private void initTexto() {
    	this.texto.setEditable(false);
        texto.setBackground(null);
        
        StringBuilder builder = new StringBuilder();
        builder.append("--->Listening\n");
        builder.append("--->" + this.config.getListenAddr() + "\n");
        builder.append("--->Payload\n");
        builder.append("--->" + ((this.config.isPayloadLocked()) ? "Locked" : this.config.getPayload()) + "\n");
        builder.append("--->Remote Proxy\n");
        builder.append("--->" + ((this.config.isRproxyLocked()) ? "Locked" : this.config.getrProxy()) + "\n\n");
        
        if (this.config.isUseSSH()) {
        	if (!this.config.isSshLocked()) {
			    builder.append("--->SSH Host\n");
			    builder.append("--->" + this.config.getSshHost()+ "\n");
			    builder.append("--->SSH Port\n");
			    builder.append("--->" + this.config.getSshPort() + "\n");
			    builder.append("--->SSH User\n");
			    builder.append("--->" + this.config.getSshUser() + "\n");
			    builder.append("--->SSH Pass\n");
			    builder.append("--->" + this.config.getSshPass() + "\n");
		    }
		    builder.append("--->Socks Port\n");
		    builder.append("--->" + this.config.getSocksPort());
        }
        
        this.texto.setText(builder.toString());
    }
    
    private void close() {
        this.setVisible(false);
        dispose();
        backFrame.setVisible(true);
    }
}
