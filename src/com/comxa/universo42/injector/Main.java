package com.comxa.universo42.injector;

import java.awt.EventQueue;

import com.comxa.universo42.injector.view.MainWindow;

/*
 * Objetivo: forcecer um proxy HTTP capaz de modificar requisi��es de sa�da. 
 * Entrada: um pr�ximo servidor proxy qual a requisi��o de sa�da ser� encaminhada;
 * 		    uma m�scara qual as requisi��es de sa�da ser�o formadas;
 *          um endere�o e porta qual ser� usado para escuta. 
 * Sa�da: requisi��es HTTP no formato pr� definido.
 */
public class Main {
	/*
	 * Respons�vel por iniciar a interface gr�fica.
	 */
    public static void main(String[] args) throws Exception {
    	
    	EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}
