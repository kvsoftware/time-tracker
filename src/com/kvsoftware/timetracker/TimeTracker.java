package com.kvsoftware.timetracker;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class TimeTracker {

	// UI
	private JFrame frame;
	private JButton startButton;
	private JButton stopButton;
	private JCheckBox breakCheckBox;

	// Timer
	private Timer taskTimer;
	private Timer breakTimer;

	// Input
	private String task = "";

	// Debug
	private boolean isDebug = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					TimeTracker window = new TimeTracker();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TimeTracker() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 290, 205);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setAlwaysOnTop(true);
		frame.setResizable(false);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				stopTimer();
				System.exit(0);
			}
		});

		JLabel titleLabel = new JLabel("Time Tracker 0.0.1");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(20, 18, 250, 30);
		frame.getContentPane().add(titleLabel);

		startButton = new JButton("Start");
		startButton.setBounds(20, 55, 250, 30);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				breakCheckBox.setEnabled(false);
				startTimer();

			}
		});
		frame.getContentPane().add(startButton);

		stopButton = new JButton("Stop");
		stopButton.setBounds(20, 96, 250, 30);
		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				breakCheckBox.setEnabled(true);
				stopTimer();
			}
		});
		frame.getContentPane().add(stopButton);

		stopButton.setEnabled(false);

		breakCheckBox = new JCheckBox("Remind every hour to take a break", true);
		breakCheckBox.setBounds(20, 138, 250, 30);
		frame.getContentPane().add(breakCheckBox);
	}

	private void startTimer() {
		stopTimer();
		taskTimer = new Timer();
		taskTimer.schedule(new AddTask(), 0, 1000);

		if (breakCheckBox.isSelected()) {
			breakTimer = new Timer();
			breakTimer.schedule(new BreakTask(), 3600000, 3600000);
		}
	}

	private void stopTimer() {
		if (taskTimer != null) {
			taskTimer.cancel();
		}
		if (breakTimer != null) {
			breakTimer.cancel();
		}
	}

	private void writeFile(String message) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(dateToString("yyyy-MM-dd") + ".txt", true);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(message); // New line
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} // Set true for append mode
	}

	private String dateToString(String format) {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	class AddTask extends TimerTask {
		@Override
		public void run() {
			Calendar calendar = Calendar.getInstance();
			if (isDebug) {
				if (calendar.get(Calendar.SECOND) == 0) {
					showAddTaskDialog();
				}
			} else {
				if (calendar.get(Calendar.SECOND) == 0
						&& (calendar.get(Calendar.MINUTE) == 0 || calendar.get(Calendar.MINUTE) == 15
								|| calendar.get(Calendar.MINUTE) == 30 || calendar.get(Calendar.MINUTE) == 45)) {
					showAddTaskDialog();
				}
			}
		}

		private void showAddTaskDialog() {
			String tempTask = JOptionPane.showInputDialog(frame, "What task are you working ?", task);
			if (tempTask != null) {
				writeFile(dateToString("HHmm") + ":" + tempTask);
				task = tempTask;
			}
		}
	}

	class BreakTask extends TimerTask {
		@Override
		public void run() {
			JOptionPane.showMessageDialog(frame, "Please take a break !", "warning", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
