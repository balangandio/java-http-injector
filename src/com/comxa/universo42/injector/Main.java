package com.comxa.universo42.injector;

import java.awt.EventQueue;

import com.comxa.universo42.injector.view.MainWindow;

/*
 * Objetivo: forcecer um proxy HTTP capaz de modificar requisições de saída. 
 * Entrada: um próximo servidor proxy qual a requisição de saída será encaminhada;
 * 		    uma máscara qual as requisições de saída serão formadas;
 *          um endereço e porta qual será usado para escuta. 
 * Saída: requisições HTTP no formato pré definido.
 */
public class Main {
	/*
	 * Responsável por iniciar a interface gráfica.
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
