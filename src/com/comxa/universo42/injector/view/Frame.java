package com.comxa.universo42.injector.view;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Frame extends JFrame {    
    public Frame() {
        //Toolkit kit = Toolkit.getDefaultToolkit();
        //Dimension dim = kit.getScreenSize();
                
        //setSize((int)(dim.getWidth() / 2), (int)(dim.getHeight() / 2));
        setLocationByPlatform(true);
    }
    
    public Frame(String titulo, String iconeResoucePatch) {
        this();
        setTitle(titulo);
        setResourceIconImage(iconeResoucePatch);
    }
    
    public void setResourceIconImage(String path) {
        setIconImage(new ImageIcon(getClass().getClassLoader().getResource(path)).getImage());
    }
}
