package de.syscy.warpsystem.utils;

public interface Manager {
	boolean load();

	void save(boolean saver);

	void destroy();
}