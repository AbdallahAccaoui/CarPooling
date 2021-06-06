package application;


import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class MainController {

	@FXML
	private CheckBox NS;
	
	@FXML
	private CheckBox NP;
	
	@FXML
	private CheckBox NK;
	
	@FXML
	private CheckBox QR;
	
	@FXML
	private Label msg;
	
	public void checkEvent(ActionEvent event){
		String message="";
		if(NS.isSelected()){
			message += NS.getText() +", ";
		}
		if(NP.isSelected()){
			message += NP.getText() +", ";
		}
		if(NK.isSelected()){
			message += NK.getText() +", ";
		}
		if(QR.isSelected()){
			message += QR.getText() +", ";
		}
		msg.setText(message);
		
	}
}
