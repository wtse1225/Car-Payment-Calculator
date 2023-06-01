package application;
import java.net.URL;
import java.util.ResourceBundle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class SampleController implements Initializable {
	// Currency format setup
	private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance();
	
	// Object declarations
	@FXML
    private TextField price;
	
    @FXML
    private TextField downPayment;   
    
    @FXML
    private TextField rate;  
    
    @FXML
    private Slider lengthSlider;
    
    @FXML
    private Number months = 48;
    
    @FXML
    private Label length;
    
    @FXML
    private TextField paymentAmount;	
    
	// ChoiceBox items
    @FXML
    private ChoiceBox<String> ageChoiceBox, typeChoiceBox, frequency;
    
    private void setChoiceBoxItems() { 	    	
    	String[] typeItems = {"Car", "SUV", "Truck"};
        typeChoiceBox.getItems().addAll(typeItems);
        typeChoiceBox.setValue("Car");

        String[] ageItems = {"New", "Used"};
        ageChoiceBox.getItems().addAll(ageItems);
        ageChoiceBox.setValue("New");
        
        String[] periodItems = {"Weekly", "Bi-weekly", "Monthly"};
        frequency.getItems().addAll(periodItems);
        frequency.setValue("Monthly");
    }
    
    
    // Calculation button
    @FXML
    void calculatePaymentBtn(ActionEvent event) {    	
    	try {
    		// Formula: monthlyPayment = (loanAmount * monthlyInterest * (1 + monthlyInterest)^loanTerms) / ((1 + monthlyInterest)^loanTerms - 1)
    		// Extract data from text fields
    		BigDecimal carPrice = new BigDecimal(price.getText());
    		BigDecimal downPay = new BigDecimal(downPayment.getText());
    		BigDecimal apr = new BigDecimal(rate.getText().replace("%", "")).divide(BigDecimal.valueOf(100));    		
    		
    		BigDecimal loanAmount = carPrice.subtract(downPay);
    		BigDecimal monthlyInterest = apr.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
    		int loanTerms = months.intValue();
    		
    		// Calculate monthly payment (BigDecimal does not support typical operators, needs to break down all the parts, requires a lot of extra work)
    		BigDecimal power = BigDecimal.ONE.add(monthlyInterest).pow(loanTerms);
    		BigDecimal denominator = power.subtract(BigDecimal.ONE);
    		BigDecimal numerator = loanAmount.multiply(monthlyInterest).multiply(power);
    		BigDecimal monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    		
    		// Calculate payment based on selected frequency
    		String selectedFrequency = frequency.getValue();
    		
    		if (selectedFrequency.equals("Weekly")){
    			monthlyPayment = monthlyPayment.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);
    		} else if (selectedFrequency.equals("Bi-weekly")) {
    			monthlyPayment = monthlyPayment.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    		}
    		
            // Set the monthly payment amount in the paymentAmount text field
            paymentAmount.setText(CURRENCY.format(monthlyPayment));
    	} catch (NumberFormatException ex) {
    		price.setText("Please Enter Proper Number");
    		price.selectAll();
    		price.requestFocus();
    	}
    }
    
    
 // Calculation button
    @FXML
    void clearBtn(ActionEvent event) {
    	price.clear();
    	downPayment.clear();
    	rate.setText("6.99");
    	lengthSlider.setValue(48);
        typeChoiceBox.setValue("Car");
        ageChoiceBox.setValue("New");
        frequency.setValue("Monthly");
        paymentAmount.clear();
    }
    
    
    // Startup initialization
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	// ChoiceBox initialization
    	setChoiceBoxItems();
    	
    	// Currency roundup
    	CURRENCY.setRoundingMode(RoundingMode.HALF_UP);
    	
    	// Slider change listener
    	lengthSlider.valueProperty().addListener(new ChangeListener<Number>() {
    		@Override
    		public void changed(ObservableValue<? extends Number> objectValue, Number oldValue, Number newValue) {
				months = newValue.intValue();
				length.setText(String.valueOf(months)+ " Months");
			}
    	});
	} 

}
