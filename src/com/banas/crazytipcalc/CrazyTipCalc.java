package com.banas.crazytipcalc;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class CrazyTipCalc extends Activity {
	
	// muutujad, mis salvestavad väärtused juhuks, kui inimene peaks app'i kinni panema
	// ja uuesti tööle panema
	
	private static final String TOTAL_BILL = "TOTAL_BILL";
	private static final String CURRENT_TIP = "CURRENT_TIP";
	private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";
	
	
	private double billBeforeTip; // arve enne jootraha juurdeliitmist
	private double tipAmount; // jootraha suurus
	private double finalBill; // arve + jootraha
	
	private int[] checkListValues = new int[12];
	
	
	// tulevad "activity_crazy_tip_calc.xml" failist - ID-d
	CheckBox friendlyCheckBox;
	CheckBox specialsCheckBox;
	CheckBox opinionCheckBox;
	
	RadioGroup availableRadioGroup;
	RadioButton availableBadRadio;
	RadioButton availableOkRadio;
	RadioButton availableGoodRadio;
	
	Spinner problemsSpinner;
	
	Button startChronometerButton;
	Button pauseChronometerButton;
	Button resetChronometerButton;
	
	Chronometer timeWaitingChronometer;
	
	// kaua klient oma praadi ootas; reaalselt läheb aega ikka minutites, aga kuna see on 
	// demonsteerimiseks, siis pole aega 10 min kelnerit oodata
	long secondsYouWaited = 0; 
	
	TextView timeWaitingTextView;
	
	
	EditText billBeforeTipET; // "ET" - editable text   billEditText
	EditText tipAmountET;
	EditText finalBillET;
	
	SeekBar tipSeekBar;
	
	// "onCreate"-i võib vaadelda justkui meie tavapärast "algus()"-t, mis meil oli Javascript'is
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crazy_tip_calc);
		
		// kas app alles pandi tööle või seda "restore"-itakse
		// midagi salvestatud pole ("null"), tähendab, et app pannakse värskelt alles tööle
		if(savedInstanceState == null){
			billBeforeTip = 0.0;
			tipAmount = .15; // 15% pannakse arvele juurde, et jootraha maksta
			finalBill = 0.0;
		
		} else {
			// tulen tagasi mõnest muust olekust. Nt tegutsesin teise app'iga ja nüüd tulin siia
			// app'i tagasi. Taastan väärtused:
			
			billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
			tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
			finalBill = savedInstanceState.getDouble(TOTAL_BILL);			
		}
		
		billBeforeTipET = (EditText) findViewById(R.id.billEditText);
		tipAmountET = (EditText) findViewById(R.id.tipEditText);
		finalBillET = (EditText) findViewById(R.id.finalBillEditText);
		tipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);
		
		
		tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);
		
		
		// kui muutujad muutuvad
		billBeforeTipET.addTextChangedListener(billBeforeTipListener);
		
		
		friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
		specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
		opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);
		
		setUpIntroCheckBoxes(); // funktsiooni väljakutsumine
		
		availableRadioGroup = (RadioGroup) findViewById(R.id.availableRadioGroup);		
		availableBadRadio = (RadioButton) findViewById(R.id.availableBadRadio);
		availableOkRadio =  (RadioButton) findViewById(R.id.availableOkRadio);
		availableGoodRadio =  (RadioButton) findViewById(R.id.availableGoodRadio);
		
		addChangeListenerToRadios();
		
		problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner);
		problemsSpinner.setPrompt("Problem solving");
		
		addItemSelectedListenerToSpinner();	
		
		startChronometerButton = (Button) findViewById(R.id.startChronometerButton);
		pauseChronometerButton = (Button) findViewById(R.id.pauseChronometerButton);
		resetChronometerButton = (Button) findViewById(R.id.resetChronometerButton);
		
		setButtonClickListeners();
		
		timeWaitingTextView = (TextView) findViewById(R.id.timeWaitingTextView);
		timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWaitingChronometer);
		
		
	}
		// Called when the bill before tip amount is changed
		private TextWatcher billBeforeTipListener = new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
				// "try" - hakka otsima
				try{
					// "s" on see number, mis muudetakse 0.0 asemel
					billBeforeTip = Double.parseDouble(arg0.toString()); 
				}
				
				// catch - püüdis kinni
				catch(NumberFormatException e){
					billBeforeTip = 0.0;
				}
				
				updateTipAndFinalBill();
				
			}
			
		};
			
		
	private void updateTipAndFinalBill(){
		// tipAmountET = (EditText) findViewById(R.id.tipEditText);
		double tipAmount = Double.parseDouble(tipAmountET.getText().toString());
		
		// "billBeforeTip"-i sai "billBeforeTipListener"-ist
		double finalBill = billBeforeTip + (billBeforeTip * tipAmount);
		
		finalBillET.setText(String.format("%.02f", finalBill));
	}
	
	// kutsutakse siis nt kui telefoni pööratakse või kui minnakse sellest app'ist hetkeks
	// välja, et kasutada mõnda muud app'i
	
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);		
		outState.putDouble(TOTAL_BILL, finalBill);
		outState.putDouble(CURRENT_TIP, tipAmount);
		outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
	}
	
	
	private OnSeekBarChangeListener tipSeekBarListener = new OnSeekBarChangeListener(){

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			// TODO Auto-generated method stub
			tipAmount = (tipSeekBar.getProgress()) * .01; // et saaks 10% võrra üles-alla
			
			tipAmountET.setText(String.format("%.02f", tipAmount));
			updateTipAndFinalBill();
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	private void setUpIntroCheckBoxes(){
		friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// kui on check'itud, siis väärtus "4", muidu "0"
				checkListValues[0] = (friendlyCheckBox.isChecked())?4:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
				
			}
			
		});
		
		
		specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkListValues[1] = (specialsCheckBox.isChecked())?1:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
				
			}
			
		});
		
		/* "opinion" - kui teenindaja kommenteerib/soovitab mingit sööki, siis antakse talle
		 jootrahalisa		  
		 */	
		opinionCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				checkListValues[2] = (opinionCheckBox.isChecked())?2:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();
				
			}
			
		});			
	}
	
	private void setTipFromWaitressCheckList(){
		int checklistTotal = 0;
		
		for(int item : checkListValues){
			checklistTotal += item;
		}
		
		tipAmountET.setText(String.format("%.02f", checklistTotal * .01));
	}
	
	private void addChangeListenerToRadios(){
		availableRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// kui oli halb ettekandja, siis lahutatakse -1 (10% vist?) jootrahast
				checkListValues[3] = (availableBadRadio.isChecked())?-1:0;
				checkListValues[4] = (availableOkRadio.isChecked())?2:0; // "OK" ettekandja 
				checkListValues[5] = (availableGoodRadio.isChecked())?4:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();				
			}
			
		});
		
	}
	
	private void addItemSelectedListenerToSpinner(){
		problemsSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// kui ettekandja oli halb (availability?)
				checkListValues[6] = (problemsSpinner.getSelectedItem().equals("Bad"))?-1:0;
				checkListValues[7] = (problemsSpinner.getSelectedItem().equals("OK"))?3:0; 
				checkListValues[8] = (problemsSpinner.getSelectedItem().equals("Good"))?6:0;
				
				setTipFromWaitressCheckList();
				
				updateTipAndFinalBill();				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
			
		});
	}
	
	private void setButtonClickListeners(){
		startChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// hoiab millisekundite arvu - kui inimene vajutab "Pause" nupu peale, siis
				// hakkab tuleb millisekundeid vastavalt juurde
				// (või salvestab vahemiku "stardist-pausini")?
				// "0" sellepärast, et algul kui inimene "Start" vajutab, peabki null olema
				int stoppedMilliseconds = 0;
				
				// print(timeWaitinChronometer)
				String chronoText = timeWaitingChronometer.getText().toString();
				String array[] = chronoText.split(":");
				
				// tehakse kindlaks minutid ja sekundid
				if(array.length == 2){
					// sekundid... array[1] - sekundid
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 + 
							Integer.parseInt(array[1]) * 1000;					
				  // minutid	
				} else if(array.length == 3){
					stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 + 
							Integer.parseInt(array[1]) * 60 * 1000
							+ Integer.parseInt(array[2]) * 1000;					
				}
				
				// (aeg, mis on möödunud sellest kui "Start" nuppu vajutati)
				// 							-
				// (millal "Pause" nuppu vajutati)
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				
				// aeg kaua teenindajal läks aega laua juurde tulemisega, et tellimust küsida
				// või kaua läks joogi kallamisega (? :D) vms
				secondsYouWaited = Long.parseLong(array[1]);
				
				updateTipBasedOnTimeWaited(secondsYouWaited);
				
				timeWaitingChronometer.start();
				
			}
			
		});
		
		pauseChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				timeWaitingChronometer.stop();
				
			}
			
		});
		
		resetChronometerButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());
				
				secondsYouWaited = 0;
				
			}
			
		});			
		
		
	}
	
	private void updateTipBasedOnTimeWaited(long secondsYouWaited){
		// ootasid teenindajat üle 10 sekundi, siis -2 ühikut jootrahast maha, muidu 2 juurde
		checkListValues[9] = (secondsYouWaited > 10)?-2:2;
		
		setTipFromWaitressCheckList();
		
		updateTipAndFinalBill();		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crazy_tip_calc, menu);
		return true;
	}

}
