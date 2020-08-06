package com.comxa.universo42.injector.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class AboutFrame extends Frame {
    public static final String TITULO_FRAME = "About";
    public static final String FRAME_ICON_RESOURCE = "resources/ico.png";
    public static final String BOTAO_VOLTAR_LABEL = "Voltar";
    public static final int COLUNAS_TEXTO = 45;
    public static final int LINHAS_TEXTO = 34;
    
    private final JTextArea texto = new JTextArea(LINHAS_TEXTO, COLUNAS_TEXTO);
    private final JScrollPane scrollPaneTexto = new JScrollPane(texto);
    private final JButton botaoVoltar = new JButton(getActionBotaoVoltar());
    
    private final JFrame backFrame;
    
    public AboutFrame(JFrame backFrame) {
        super(TITULO_FRAME, FRAME_ICON_RESOURCE);
        
        this.backFrame = backFrame;
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(getOnClosingFrame());
        
        setTexto();
        texto.setBackground(null);
        add(this.scrollPaneTexto, BorderLayout.CENTER);
        add(this.botaoVoltar, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    public AboutFrame(JFrame backFrame, int xPosition, int yPositon) {
    	this(backFrame);
    	setLocation(xPosition, yPositon);
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
    
    private void close() {
        this.setVisible(false);
        dispose();
        backFrame.setLocation(this.getX(), this.getY());
        backFrame.setVisible(true);
    }
    
    private void setTexto() {
        this.texto.setEditable(false);
        this.texto.setText("\tEsta ferramenta tem como fun��o modificar o formato de requisi��es\n"+
        "HTTP de uma aplica��o. Seu funcionamento se baseia em receber uma requisi��o\n"+
        "como um proxy HTTP e alterar seu formato e as reenviar a um pr�ximo servidor proxy.\n\n" +
        "	Listening\n" +
        "	Endere�o + porta de escuta que receber� as requisi��es.\n" +
        "	Valor padr�o: 127.0.0.1:8989\n" +
        "	Exemplos de uso: 0.0.0.0:4242; 192.168.1.1:12345\n" +
        "\n" +
        "	Remote Proxy\n" +
        "	Endere�o + porta do pr�ximo servidor proxy a ser comunicado.\n" +
        "	Exemplos de uso: 24.157.37.61:8080\n" +
        "\n" +
        "	Payload\n" +
        "	Formato das requisi��es de sa�da.\n" +
        "	Se nulo, as requisi��es reais ser�o repassadas sem altera��o.\n" +
        "	Exemplo de uso: [netData][crlf]Host: host.net[crlf][crlf]\n" +
        "	O campo de payload interpreta as seguintes macros:\n" +
        "\n" +
        "*Levando em considera��o a requisi��o: CONNECT 1.23.45.6:443 HTTP/1.0\n" +
        "\n" +
        "-[netData] : cabe�alho de requisi��o completo. Todo o texto do exemplo.\n" +
        "-[host_port] : host e porta informados na requisi��o. No exemplo: 1.23.45.6:443\n" +
        "-[host] : host da requisi��o. No Exemplo: 1.23.45.6\n" +
        "-[port] : porta da requisi��o. No Exemplo: 443\n" +
        "-[protocol] : protocolo HTTP utilizado. No exemplo: HTTP/1.0\n" +
        "-[split] : divide o envio da requisi��o em duas partes.\n" +
        "-[crlf] : conjunto de caracteres especiais \\r\\n\n" +
        "\n" +
        "*[crlf] marca um salto de linha. Dois [crlf] seguidos marcam o fim de uma requisi��o.\n" +
        "*O caracter ENTER n�o � interpretado no campo Payload.\n" +
        "*Qualquer texto n�o interpretativo ser� escrito puramente na sa�da.\n\n" + 
        "Version date: 29/04/2016 - Transversal");
    }
}
