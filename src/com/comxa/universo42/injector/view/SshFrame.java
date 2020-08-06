package com.comxa.universo42.injector.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.comxa.universo42.injector.modelo.Config;

public class SshFrame extends Frame{
    public static final String TITULO_FRAME = "SSH Tunneling";
    public static final String FRAME_ICON_RESOURCE = "resources/ico.png";
    public static final String BOTAO_VOLTAR_LABEL = "Voltar";
    public static final String CAIXA_SELECAO_LABEL = "Usar SSH Tunneling";
    public static final String HOSTADDR_LABEL = "Host:";
    public static final int COLUNAS_HOSTADDR = 15;
    public static final String HOSTPORT_LABEL = "Port:";
    public static final int COLUNAS_HOSTPORT = 15;
    public static final String USER_LABEL = "User:";
    public static final int COLUNAS_USER = 15;
    public static final String PASS_LABEL = "Pass:";
    public static final int COLUNAS_PASS = 15;
    public static final String SOCKS_LABEL = "SOCKS Port:";
    public static final int COLUNAS_SOCKS = 15;
    
            
    private JCheckBox caixaSelecao = new JCheckBox(getActionCaixaSelecao());
    private JPanel painelCentral = new JPanel();
    private JLabel hostAddrLabel = new JLabel(HOSTADDR_LABEL);
    private JTextField hostAddr = new JTextField(COLUNAS_HOSTADDR);
    private JLabel hostPortLabel = new JLabel(HOSTPORT_LABEL);
    private JTextField hostPort = new JTextField(COLUNAS_HOSTPORT);
    private JLabel userLabel = new JLabel(USER_LABEL);
    private JTextField user = new JTextField(COLUNAS_USER);
    private JLabel passLabel = new JLabel(PASS_LABEL);
    private JTextField pass = new JTextField(COLUNAS_PASS);
    private JLabel socksLabel = new JLabel(SOCKS_LABEL);
    private JTextField socks = new JTextField(COLUNAS_SOCKS);
    private JButton botaoVoltar = new JButton(getActionBotaoVoltar());
    
    private JFrame backFrame;
    private Config config;
    
    public SshFrame(JFrame backFrame, Config config) {
        super(TITULO_FRAME, FRAME_ICON_RESOURCE);
        
        this.backFrame = backFrame;
        this.config = config;
        
        initComponentes();
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(getOnClosingFrame());
        
        
        JPanel painelNorte = new JPanel();
        painelNorte.setLayout(new FlowLayout());
        painelNorte.add(this.caixaSelecao);
        add(painelNorte, BorderLayout.NORTH);
        
        painelCentral.setLayout(new GridLayout(5, 2));
        painelCentral.add(this.hostAddrLabel);
        painelCentral.add(this.hostAddr);
        painelCentral.add(this.hostPortLabel);
        painelCentral.add(this.hostPort);
        painelCentral.add(this.userLabel);
        painelCentral.add(this.user);
        painelCentral.add(this.passLabel);
        painelCentral.add(this.pass);
        painelCentral.add(this.socksLabel);
        painelCentral.add(this.socks);
        add(painelCentral, BorderLayout.CENTER);
        
        
        JPanel painelSul = new JPanel();
        painelSul.setLayout(new FlowLayout());
        painelSul.add(this.botaoVoltar);
        add(painelSul, BorderLayout.SOUTH);
        
        pack(); 
        if (this.config.isUseSSH()) {
            this.caixaSelecao.setSelected(true);
        }else{
            painelCentral.setVisible(false);
        }
        
        setLocation(backFrame.getX() + backFrame.getWidth()/2 - getWidth()/2, backFrame.getY() + backFrame.getHeight()/2 - getHeight()/2);
        setVisible(true);
    }
    
    private void initComponentes() {
        this.hostAddr.setBackground(null);
        this.hostPort.setBackground(null);
        this.user.setBackground(null);
        this.pass.setBackground(null);
        this.socks.setBackground(null);
        
        load();
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
    
    private Action getActionCaixaSelecao() {
        return new Action(CAIXA_SELECAO_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
                config.setUseSSH(caixaSelecao.isSelected());
                painelCentral.setVisible(caixaSelecao.isSelected());
            }            
        };
    }
    
    private void load() {
    	this.caixaSelecao.setSelected(this.config.isUseSSH());
    	
    	if (this.config.isSshLocked()) {
    		this.hostAddr.setText("Locked");
    		this.hostAddr.setEnabled(false);
    		this.hostPort.setText("Locked");
    		this.hostPort.setEnabled(false);
    		this.user.setText("Locked");
    		this.user.setEnabled(false);
    		this.pass.setText("Locked");
    		this.pass.setEnabled(false);
    	}else{
            this.hostAddr.setText(this.config.getSshHost());
            this.hostPort.setText(String.valueOf(this.config.getSshPort()));
            this.user.setText(this.config.getSshUser());
            this.pass.setText(this.config.getSshPass());	
    	}
    	
        this.socks.setText(String.valueOf(this.config.getSocksPort()));
    }
    
    public void save() {
    	if (!this.config.isSshLocked()) {
    		this.config.setSshHost(this.hostAddr.getText());
        	this.config.setSshUser(this.user.getText());
        	this.config.setSshPass(this.pass.getText());
	        
	        try {
	            this.config.setSshPort(Integer.valueOf(this.hostPort.getText()));
	        } catch(NumberFormatException e) {
	            this.config.setSshPort(Config.DEFAULT_SSH_PORT);
	        }
    	}
    	
        try {
            this.config.setSocksPort(Integer.valueOf(this.socks.getText()));
        } catch(NumberFormatException e) {
            this.config.setSocksPort(Config.DEFAULT_SOCKS_PORT);
        }
        
        this.config.setUseSSH(this.caixaSelecao.isSelected());
    }
    
    private void close() {
        save();
        
        setVisible(false);
        dispose();
        backFrame.setVisible(true);
    }
}
