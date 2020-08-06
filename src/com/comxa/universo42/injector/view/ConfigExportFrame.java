package com.comxa.universo42.injector.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.comxa.universo42.injector.controle.FileConfig;
import com.comxa.universo42.injector.modelo.Config;

public class ConfigExportFrame extends Frame {
    private static final String TITULO_FRAME = "Exportar";
    private static final String FRAME_ICON_RESOURCE = "resources/ico.png";
    private static final String BOTAO_VOLTAR_LABEL = "Voltar";
	private static final String BOTAO_ARQ_LABEL = "Arquivo";
	private static final String BOX_ALL_LABEL = "Lock All";
	private static final String BOX_SSH_LABEL = "Lock SSH";
	private static final String BOX_PAYLOAD_LABEL = "Lock Payload";
	private static final String BOX_RPROXY_LABEL = "Lock Remote Proxy";
    
	private JCheckBox boxAll = new JCheckBox(getActionBoxAll());
	private JCheckBox boxSsh = new JCheckBox();
	private JCheckBox boxPayload = new JCheckBox();
	private JCheckBox boxRproxy = new JCheckBox();
	
    private JButton botaoVoltar = new JButton(getActionBotaoVoltar());
    private JButton botaoArq = new JButton(getActionBotaoArq());
    
    private JFrame backFrame;
    private FileConfig config;
    private JFileChooser fileChooser;
    
    public ConfigExportFrame(JFrame backFrame, FileConfig config) {
        super(TITULO_FRAME, FRAME_ICON_RESOURCE);
        this.config = config;
        this.backFrame = backFrame;
        setSize(320, 150);
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(getOnClosingFrame());
        
        JPanel p = new JPanel(new GridLayout(4, 2));
        p.add(this.boxAll);
        p.add(new JLabel(BOX_ALL_LABEL));
        p.add(this.boxSsh);
        p.add(new JLabel(BOX_SSH_LABEL));
        p.add(this.boxPayload);
        p.add(new JLabel(BOX_PAYLOAD_LABEL));
        p.add(this.boxRproxy);
        p.add(new JLabel(BOX_RPROXY_LABEL));
        add(p, BorderLayout.CENTER);

        p = new JPanel(new FlowLayout());
        p.add(this.botaoVoltar);
        p.add(this.botaoArq);
        add(p, BorderLayout.SOUTH);
        
        initCheckBox();
        
        //pack();
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

    private Action getActionBotaoArq() {
        return new Action(BOTAO_ARQ_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	if (fileChooser == null)
            		fileChooser = new JFileChooser();
            	
            	fileChooser.showOpenDialog(ConfigExportFrame.this);
                
                File file = fileChooser.getSelectedFile();
                
                if (file == null)
                	return;
                
                if (file.exists()) {
                	JOptionPane.showMessageDialog(ConfigExportFrame.this,
                			"Informe o nome de um arquivo a ser criado", 
                			"Arquivo já existente!",
                			JOptionPane.OK_OPTION );
                }else{
                	config.setFilePath(file.getPath());
                	
                	try {
                		config.setSshLocked(boxSsh.isSelected());
                		config.setPayloadLocked(boxPayload.isSelected());
                		config.setRproxyLocked(boxRproxy.isSelected());
                		
						config.save();
					} catch (IOException e) {
	                	config.setFilePath(Config.FILE_PATCH);
	                	JOptionPane.showMessageDialog(ConfigExportFrame.this,
	                			"Houve um erro ao gravar o arquivo. " + e.getMessage(), 
	                			"Error",
	                			JOptionPane.OK_OPTION );
	                	return;
					}
                	config.setFilePath(Config.FILE_PATCH);
                	JOptionPane.showMessageDialog(ConfigExportFrame.this,
                			"Arquivo criado: " + file.getPath(), 
                			"Done!",
                			JOptionPane.INFORMATION_MESSAGE);
                	close();
                }
            	
            }
        };
    }

    private Action getActionBoxAll() {
        return new Action(null, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	boolean enable = true;
            	boolean selected = false;
            	
            	if (boxAll.isSelected()) {
            		enable = false;
            		selected = true;
            	}
            	
		        boxSsh.setSelected(selected);
		        boxSsh.setEnabled(enable);
		        boxPayload.setSelected(selected);
		        boxPayload.setEnabled(enable);
		        boxRproxy.setSelected(selected);
		        boxRproxy.setEnabled(enable);
            }            
        };
    }
    
    private void initCheckBox() {
        if (this.config.isPayloadLocked() || this.config.isRproxyLocked() || this.config.isSshLocked()) {
        	this.boxAll.setEnabled(false);
        	
        	if (this.config.isPayloadLocked()) {
        		this.boxPayload.setSelected(true);
        		this.boxPayload.setEnabled(false);
        	}
        	if (this.config.isRproxyLocked()) {
        		this.boxRproxy.setSelected(true);
        		this.boxRproxy.setEnabled(false);
        	}
        	if (this.config.isSshLocked()) {
        		this.boxSsh.setSelected(true);
        		this.boxSsh.setEnabled(false);
        	}
        }
    }
    
    private void close() {
        this.setVisible(false);
        dispose();
        backFrame.setVisible(true);
    }
}
