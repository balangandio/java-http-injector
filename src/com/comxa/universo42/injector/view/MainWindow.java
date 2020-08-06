package com.comxa.universo42.injector.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import com.comxa.universo42.injector.controle.FileConfig;
import com.comxa.universo42.injector.modelo.Injector;
import com.comxa.universo42.injector.modelo.SSH;

public class MainWindow {
	private static final int MAX_QTD_LOG = 100;
	private static final String FRAME_ICON_RESOURCE = "resources/ico.png";
	private static final String FRAME_TITLE = "Injector";
	private static final String PAYLOAD_LABEL = "Payload:";
	private static final String RPROXY_LABEL = "Remote Proxy:";
	private static final String LISTENADDR_LABEL = "Listening:";
	private static final String BOTAO_START_LABEL = "Start";
	private static final String BOTAO_SSH_LABEL = "SSH";
    private static final String BOTAO_SSH_TIP = "Configurar SSH tunneling"; 
    private static final String BOTAO_STOP_LABEL = "Stop";
    private static final String BOTAO_CLEAR_LABEL = "Clear"; 
    private static final String BOTAO_CLEAR_TIP = "Limpar log"; 
    private static final String BOTAO_SAVE_LABEL = "Save";
    private static final String BOTAO_SAVE_TIP = "Salvar config";
    private static final String BOTAO_CONFIG_LABEL = "Config";
    private static final String BOTAO_CONFIG_TIP = "Exportar/Importar";
    private static final String BOTAO_ABOUT_LABEL = "About";
    private static final String BOTAO_ABOUT_TIP = "Help";
    private static final int COLUNAS_RPROXY = 15;
    private static final int COLUNAS_LISTENADDR = 15;
    private static final int COLUNAS_PAYLOAD = 20;
    private static final int LINHAS_PAYLOAD = 6;
    private static final int COLUNAS_LOGAREA = 35;
    private static final int LINHAS_LOGAREA = 13;
    
    
    private final Frame frame;
    private final JLabel listenAddrLabel = new JLabel(LISTENADDR_LABEL);
    private final JTextField listenAddr = new JTextField(COLUNAS_LISTENADDR);
    private final JLabel payloadLabel = new JLabel(PAYLOAD_LABEL);
    private final JTextArea payload = new JTextArea(LINHAS_PAYLOAD, COLUNAS_PAYLOAD);
    private final JLabel rProxyLabel = new JLabel(RPROXY_LABEL);
    private final JTextField rProxy = new JTextField(COLUNAS_RPROXY);
    private final JTextArea logArea = new JTextArea(LINHAS_LOGAREA, COLUNAS_LOGAREA);
    private final JScrollPane scrollPaneLogArea = new JScrollPane(logArea);
    private final JButton botaoStart = new JButton(getActionBotaoStart()); 
    private final JButton botaoSSH = new JButton(getActionBotaoSSH());
    private final JButton botaoClear = new JButton(getActionBotaoClear());
    private final JButton botaoSave = new JButton(getActionBotaoSave());
    private final JButton botaoConfig = new JButton(getActionBotaoConfig());
    private final JButton botaoAbout = new JButton(getActionBotaoAbout());
    
    
    private FileConfig config = new FileConfig();
    private SSH ssh;
    private Injector injector;
    private int qtdLog;
    
    /*
     * Constroí a tela principal do programa.
     */
    public MainWindow() {
        initComponentes();
        
        this.frame = new Frame(FRAME_TITLE, FRAME_ICON_RESOURCE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(getOnClosingFrame());
        
        JPanel painelTopo = new JPanel();
        painelTopo.setLayout(new GridLayout(2, 2));
        painelTopo.add(this.listenAddrLabel);
        painelTopo.add(this.listenAddr);
        painelTopo.add(this.rProxyLabel);
        painelTopo.add(this.rProxy);
        
        JPanel painelPayloadLabel = new JPanel();
        painelPayloadLabel.setLayout(new FlowLayout());
        painelPayloadLabel.add(this.payloadLabel);
        
        JPanel painelPayload = new JPanel();
        painelPayload.setLayout(new FlowLayout());
        painelPayload.add(this.payload);
        
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BorderLayout());
        painelCentro.add(painelPayloadLabel, BorderLayout.NORTH);
        painelCentro.add(painelPayload, BorderLayout.CENTER);

        JPanel painelBorda = new JPanel();
        painelBorda.setLayout(new FlowLayout());
        painelBorda.setBorder(BorderFactory.createEtchedBorder());
        painelBorda.add(painelTopo);
        painelBorda.add(painelCentro);
        
        this.frame.add(painelBorda, BorderLayout.CENTER);
        
        
        JPanel painelLogArea = new JPanel();
        painelLogArea.setLayout(new FlowLayout());
        painelLogArea.add(this.scrollPaneLogArea);
        
        JPanel painelBotao = new JPanel();
        painelBotao.setLayout(new FlowLayout());
        painelBotao.add(this.botaoStart);
        painelBotao.add(this.botaoSSH);
        painelBotao.add(this.botaoClear);
        painelBotao.add(this.botaoSave);
        painelBotao.add(this.botaoConfig);
        painelBotao.add(this.botaoAbout);
        
        JPanel painelFundo = new JPanel();
        painelFundo.setLayout(new BorderLayout());
        painelFundo.add(painelLogArea, BorderLayout.CENTER);
        painelFundo.add(painelBotao, BorderLayout.SOUTH);
        
        this.frame.add(painelFundo, BorderLayout.SOUTH);

        this.frame.pack();
        this.frame.setVisible(true);
        loadConfig();
    }
    
    /*
     * Pré-configura os componentes gráficos da tela principal. 
     */
    private void initComponentes() {
        DefaultCaret caret = (DefaultCaret)this.logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        this.listenAddr.setBackground(null);
        this.logArea.setBackground(null);
        this.rProxy.setBackground(null);
        this.payload.setBackground(null);
        this.payload.setBorder(BorderFactory.createEtchedBorder());
        
        this.payload.setLineWrap(true);
        this.payload.setWrapStyleWord(true);
        
        this.logArea.setLineWrap(true);
        this.logArea.setWrapStyleWord(true);
        this.logArea.setEditable(false);
    }

    /*
     * Recebe uma mensagem e adiciona a área de log do programa.
     */
    private synchronized void addLogLine(String str) {
    	String texto = "";
    	
    	if (qtdLog != MAX_QTD_LOG) {
    		qtdLog++;
	        texto = (this.logArea.getText().length() != 0) ? this.logArea.getText().concat("\n").concat(str) : str;
    	}else{
    		qtdLog = 0;
    	}
    	
    	this.logArea.setText(texto);
    }
    
    /*
     * Recupera os dados preenchidos nos componentes gráficos e os adiciona ao objeto Config.
     */
    private void makeConfig() {
        this.config.setListenAddr(this.listenAddr.getText());
        if (!this.config.isRproxyLocked())
        	this.config.setrProxy(this.rProxy.getText());
        
        if (!this.config.isPayloadLocked())
        	this.config.setPayload(this.payload.getText());
    }
        
    /*
     * Cria uma nova config com dados default.
     */
    public void resetConfig() {
    	this.config = new FileConfig();
    	try {
			this.config.save();
		} catch (IOException e) {
			addLogLine("<#> Erro ao resetar configuração. " + e.getMessage());
		}
    }
    
    /*
     * Carrega o objeto Config com dados e preenche os componentes gráficos com eles.
     */
    public void loadConfig() {
        try {
            this.config.load();
            
        } catch(FileNotFoundException e) {
            addLogLine("<-> Usando nenhum arquivo de configuração.");
        } catch(IOException e) {
            addLogLine("<#> Erro ao tentar carregar configuração!" + e.getMessage());
            return;
        }
        
        if (this.config.isPayloadLocked()) {
        	this.payload.setText("Locked");
        	this.payload.setEnabled(false);
        }else{
        	this.payload.setText(this.config.getPayload());
        	this.payload.setEnabled(true);
        }
        
        if (this.config.isRproxyLocked()) {
        	this.rProxy.setText("Locked");
        	this.rProxy.setEnabled(false);
        }else{
        	this.rProxy.setText(this.config.getrProxy());
        	this.rProxy.setEnabled(true);
        }

     	this.rProxy.setBackground(null);
        this.listenAddr.setText(this.config.getListenAddr());
    }
    
    /*
     * Recupera os dados preenchidos nos componentes gráficos e os salvo no objeto Config.
     */
    private boolean saveConfig() {
        makeConfig();
        
        try {
            this.config.save();
        } catch(IOException e) {
            addLogLine("<#> Erro ao salvar configuração." + e.getMessage());
            return false;
        }
        
        addLogLine("<!> Configuração salva!");
        
        return true;
    }
    
        
    private WindowAdapter getOnClosingFrame() {
        return new WindowAdapter() {                   
            @Override
            public void windowClosing(WindowEvent e) {
                if (ssh != null && ssh.isRunning())
                    ssh.close();
                
                if (injector != null && injector.isRunning())
                    injector.stop();
                
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        };
    }
    
    private Action getActionBotaoStart() {
        return new Action(BOTAO_START_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	botaoStart.setEnabled(false);
                makeConfig();
                
                try {                
	                injector = new Injector(config) {
	                    @Override
	                    public void onLogReceived(String log, int level, Exception e) {
	                        addLogLine(log);
	                    }
	                };
                } catch(IllegalArgumentException e) {
                    addLogLine(e.getMessage());
                    return;
                }   
                
                
                injector.setPayload(config.getPayload());
                
                new Thread(injector).start();
                
                if (config.isUseSSH()) {
                    ssh = new SSH(config) {
                        public void onLogReceived(String log, int level, Exception e) {
                            addLogLine(log);
                        }   
                    };
                	ssh.setProxyHost(injector.getListeningAddr());
                    ssh.setProxyPort(injector.getListeningPort());
                    
                    while (!injector.isRunning()) {
                        try { Thread.sleep(300); } catch (InterruptedException e) {}
                    }
                    
                    new Thread(ssh).start();
                }
                
                botaoStart.setText(BOTAO_STOP_LABEL);
                botaoStart.setAction(getActionBotaoStop());
                botaoStart.setEnabled(true);
            }
        };
    }
    
    private Action getActionBotaoSSH() {
        return new Action(BOTAO_SSH_LABEL, BOTAO_SSH_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
                frame.setVisible(false);
                new SshFrame(frame, config);
            }
        };
    }
    
    private Action getActionBotaoStop() {
        return new Action(BOTAO_STOP_LABEL, null, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	if (ssh != null)
            		ssh.close();
                injector.stop();
                
                botaoStart.setText(BOTAO_START_LABEL);
                botaoStart.setAction(getActionBotaoStart());
            }
        };
    }

    private Action getActionBotaoSave() {
        return new Action(BOTAO_SAVE_LABEL, BOTAO_SAVE_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
               saveConfig();
            }
        };
    }    

    private Action getActionBotaoClear() {
        return new Action(BOTAO_CLEAR_LABEL, BOTAO_CLEAR_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
                logArea.setText(null);
                qtdLog = 0;
            }
        };
    }
    
    private Action getActionBotaoConfig() {
        return new Action(BOTAO_CONFIG_LABEL, BOTAO_CONFIG_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
            	makeConfig();
                frame.setVisible(false);
                new ConfigFrame(MainWindow.this, frame, config);
            }
        };
    }

    private Action getActionBotaoAbout() {
        return new Action(BOTAO_ABOUT_LABEL, BOTAO_ABOUT_TIP, null) {
            @Override
            public void actionPerformed(ActionEvent event) {
                frame.setVisible(false);
                new AboutFrame(frame, frame.getX(), frame.getY());
            }
        };
    }
}
