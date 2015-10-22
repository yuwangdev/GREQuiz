package com.example.grevol;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.tv.TvView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class QuizPage extends Activity {

	int amountYes;
	int amountNo;
	int totalMarked;
	int totalTest;
	int knownPercentage;

	TextView tvWord;
	TextView tvSolution;
	Button btSeeAnswer;
	Button btYes;
	Button btNo;
	Button btMark;

	HashMap<String, String> wordMarked = new HashMap<String, String>();

	public String randomWord() throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("GRE.txt")));
		String str;

		List<String> list = new ArrayList<String>();
		while ((str = reader.readLine()) != null) {
			list.add(str);
		}

		String[] stringArr = list.toArray(new String[0]);

		Random randomno = new Random();
		int randomInt = randomno.nextInt(list.size());
		String randomWord = list.get(randomInt);
		return randomWord;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		amountYes = 0;
		amountNo = 0;
		totalMarked = 0;
		totalTest = 0;
		knownPercentage = 0;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_page);
		tvWord = (TextView) findViewById(R.id.textview_word);
		btMark = (Button) findViewById(R.id.button_mark);
		btMark.setVisibility(View.INVISIBLE);
		btYes = (Button) findViewById(R.id.button_yes);
		btYes.setVisibility(View.INVISIBLE);
		btNo = (Button) findViewById(R.id.button_no);
		btNo.setVisibility(View.INVISIBLE);
		tvSolution = (TextView) findViewById(R.id.textView_answer);
		btSeeAnswer = (Button) findViewById(R.id.button_seeAnswer);

		tvSolution.setText("");

		try {
			tvWord.setText(randomWord());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void seeAnswer(View view) {

		class doBgTranslate extends AsyncTask<Void, Void, Void> {

			String word = (String) tvWord.getText();
			String answer = "";

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				Translate.setClientId("yuwang_greQuiz"); // Change this
				Translate.setClientSecret("P8gnZdOA/GhDhYiQz74FSPjj0xdqKn5O2BYu61OZWqI="); // change
				try {
					answer = Translate.execute(word, Language.ENGLISH, Language.CHINESE_SIMPLIFIED);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					answer = "没网了 TT";
				}
				return null;

			}

			protected void onPostExecute(Void result) {
				((TextView) findViewById(R.id.textView_answer)).setText(answer);
				super.onPostExecute(result);
			}

		}

		new doBgTranslate().execute();
		btMark.setVisibility(View.VISIBLE);
		btYes.setVisibility(View.VISIBLE);
		btNo.setVisibility(View.VISIBLE);
		totalTest = totalTest + 1;
		btSeeAnswer.setVisibility(View.GONE);
	}

	public void markWord(View view) {

		String word = (String) tvWord.getText();
		String answer = (String) tvSolution.getText();
		wordMarked.put(word, answer);
		totalMarked = totalMarked + 1;
		String msg = "Marked: " + totalMarked;
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		btMark.setVisibility(View.INVISIBLE);

	}

	public void monitorPercentage() {
		double amountYes_double = (double) amountYes;
		double amountNo_double = (double) amountNo;
		double total_double = (double) totalTest;
		double knownPercentage_d = amountYes_double / total_double * 100;
		knownPercentage = (int) knownPercentage_d;

	}

	public void noKnow(View view) {

		amountNo = amountNo + 1;
		tvSolution.setText("");
		monitorPercentage();
		String msg = "Your score: " + knownPercentage;
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

		try {
			tvWord.setText(randomWord());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btMark.setVisibility(View.INVISIBLE);
		btYes.setVisibility(View.INVISIBLE);
		btNo.setVisibility(View.INVISIBLE);
		btSeeAnswer.setVisibility(View.VISIBLE);
	}

	public void yesKnow(View view) {

		amountYes = amountYes + 1;
		monitorPercentage();
		String msg = "Your score: " + knownPercentage;
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		tvSolution.setText("");
		try {
			tvWord.setText(randomWord());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btMark.setVisibility(View.INVISIBLE);
		btYes.setVisibility(View.INVISIBLE);
		btNo.setVisibility(View.INVISIBLE);
		btSeeAnswer.setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.quiz_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.clear:
			clear();
			break;

		case R.id.stats:
			showStats();
			break;

		case R.id.marked:
			goMarked();
			break;

		case R.id.clearMarked:
			clearMarkedWord();
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	private void goMarked() {
		// TODO Auto-generated method stub
		int i = 0;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Marked Words:");
		String[] w_string_array = new String[wordMarked.size()];

		Iterator<String> keySetIterator = wordMarked.keySet().iterator();

		while (keySetIterator.hasNext()) {
			String key = keySetIterator.next();
			String value = wordMarked.get(key);
			String entry = key + " : " + value;
			w_string_array[i] = entry;
			i = i + 1;
		}

		ListView modeList = new ListView(this);

		// String[] w_string_array = (String[]) wordMarked.keySet().toArray(new
		// String[wordMarked.size()]);

		ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, w_string_array);
		modeList.setAdapter(modeAdapter);

		builder.setView(modeList);
		final Dialog dialog = builder.create();

		dialog.show();
	}

	private void showStats() {
		// TODO Auto-generated method stub
		String stats = "Know: " + amountYes + "; Unknown: " + amountNo + "; Total: " + totalTest + " Marked: "
				+ totalMarked;
		Toast.makeText(getApplicationContext(), stats, Toast.LENGTH_LONG).show();

	}

	private void clear() {
		// TODO Auto-generated method stub
		amountYes = 0;
		amountNo = 0;
		totalTest = 0;
		Toast.makeText(getApplicationContext(), "A new round begins!", Toast.LENGTH_SHORT).show();

	}

	private void clearMarkedWord() {
		if (!wordMarked.isEmpty()) {
			wordMarked.clear();
		}
		totalMarked = 0;
		Toast.makeText(getApplicationContext(), "Marked Word has been cleared!", Toast.LENGTH_SHORT).show();

	}

}
