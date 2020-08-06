package com.comxa.universo42.injector.modelo;

public interface Loggable {
	public final static int LOG_LEVEL_DEBUG = 2;
	public final static int LOG_LEVEL_INFO = 1;
	public final static int LOG_LEVEL_ATENTION = 0;
	public final static int LOG_LEVEL_CRITICAL = -1;
	
	public void onLogReceived(String log, int level, Exception e);
}
