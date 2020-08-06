package com.comxa.universo42.injector.view;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public abstract class Action extends AbstractAction{
    
    public Action(String nome, String dica, String iconeResourcePath) {
        if (nome != null)
            putValue(javax.swing.Action.NAME, nome);
        if (iconeResourcePath != null)
            putValue(javax.swing.Action.SMALL_ICON, new ImageIcon(iconeResourcePath));
        if (dica != null)
            putValue(javax.swing.Action.SHORT_DESCRIPTION, dica);
    }
    
    @Override
    public abstract void actionPerformed(ActionEvent event);
}
