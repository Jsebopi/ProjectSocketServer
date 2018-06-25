package controller;

import java.awt.EventQueue;

import view.VentanaS;

public class Controller {
	private VentanaS ventanas;

	public Controller() {
		// TODO Auto-generated constructor stub
		ventanas = new VentanaS();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ventanas.setVisible(true);
			}
		});
	}
}
