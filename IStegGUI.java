package isteg;

import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class IStegGUI {

	private final String VCODE = "2.0";
	
	private JFrame frame;
	private JTextField tfTopImage;
	private JTextField tfBottomFile;
	private JLabel lblTopImage;
	private JLabel lblBottomFile;
	private JButton btnTopImage;
	private JButton btnBottomFile;
	private JTextArea taMsg;
	private JTextField tfStegFile;
	private JRadioButton rdbtnlsb1;
	private JRadioButton rdbtnlsb2;
	private JRadioButton rdbtnHideAFile;
	private JRadioButton rdbtnHideAMessage;
	private JRadioButton rdbtnRevealFilemesssage;
	private JLabel lblMessage;
	private JLabel lblStegFile;
	private JButton btnStegFile;
	private JScrollPane scrollMsg;
	private JPasswordField passwordField;
	private JTextArea taLogs;
	private char defaultEchoChar;
	private int fcSaveDialogReturn;
	private StegDym mkSteg;
	private File fcCurDir;
	private JLabel lblSelectOne;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IStegGUI window = new IStegGUI();
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
	public IStegGUI() {
		mkSteg = new StegDym();
		fcCurDir=null;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String passwordTT, topImageTT, bottomFileTT, stegFileTT, lsb2TT,lsb1TT;
		passwordTT = "Enter password if you want to encrypt. Leave empty otherwise.";
		topImageTT = "The image in which your file/message will be hidden.";
		bottomFileTT = "The file you want to hide.";
		stegFileTT = "The steganographic image from which your file/message will be extracted.";
		lsb2TT = "Very low (undetectable) change in image quality. Holds more data than LSB-1.";
		lsb1TT = "Ultra low (undetectable) change in image quality. Holds less data than LSB-2.";
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("iSteg GUI");
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Choose one:");
		lblNewLabel.setBounds(10, 10, 93, 14);
		frame.getContentPane().add(lblNewLabel);
		
		rdbtnHideAFile = new JRadioButton("Hide a file");
		rdbtnHideAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleRadioClick(0);
			}
		});
		rdbtnHideAFile.setSelected(true);
		rdbtnHideAFile.setBounds(10, 26, 109, 23);
		frame.getContentPane().add(rdbtnHideAFile);
		
		rdbtnHideAMessage = new JRadioButton("Hide a message");
		rdbtnHideAMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRadioClick(1);
			}
		});
		rdbtnHideAMessage.setBounds(10, 52, 158, 23);
		frame.getContentPane().add(rdbtnHideAMessage);
		
		rdbtnRevealFilemesssage = new JRadioButton("Reveal file/messsage");
		rdbtnRevealFilemesssage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRadioClick(2);
			}
		});
		rdbtnRevealFilemesssage.setBounds(10, 78, 158, 23);
		frame.getContentPane().add(rdbtnRevealFilemesssage);
		
		tfTopImage = new JTextField();
		tfTopImage.setBounds(297, 27, 378, 20);
		tfTopImage.setToolTipText(topImageTT);
		frame.getContentPane().add(tfTopImage);
		tfTopImage.setColumns(10);
		
		btnTopImage = new JButton("Choose");
		btnTopImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] images = new String[] {".png",".jpg",".jpeg",".bmp",".gif",".tiff",".webp"};
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(fcCurDir);
				fc.setDialogTitle("Choose an Image");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new FilterTheFiles("Image Files",images));
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					tfTopImage.setText(fc.getSelectedFile().getAbsolutePath().toString());
					log("Top image selectrd: \""+fc.getSelectedFile().toString()+"\"");
				}
				fcCurDir = fc.getCurrentDirectory();
			}
		});
		btnTopImage.setBounds(685, 26, 89, 23);
		btnTopImage.setToolTipText(topImageTT);
		frame.getContentPane().add(btnTopImage);
		
		lblTopImage = new JLabel("Top Image:");
		lblTopImage.setBounds(224, 30, 63, 14);
		lblTopImage.setToolTipText(topImageTT);
		frame.getContentPane().add(lblTopImage);
		
		btnBottomFile = new JButton("Choose");
		btnBottomFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(fcCurDir);
				fc.setDialogTitle("Choose your file");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					tfBottomFile.setText(fc.getSelectedFile().getAbsolutePath().toString());
					log("Bottom file selectrd: \""+fc.getSelectedFile().toString()+"\"");
				}
				fcCurDir = fc.getCurrentDirectory();
			}
		});
		btnBottomFile.setBounds(685, 52, 89, 23);
		btnBottomFile.setToolTipText(bottomFileTT);
		frame.getContentPane().add(btnBottomFile);

		tfBottomFile = new JTextField();
		tfBottomFile.setBounds(297, 53, 378, 20);
		tfBottomFile.setToolTipText(bottomFileTT);
		frame.getContentPane().add(tfBottomFile);
		tfBottomFile.setColumns(10);

		lblBottomFile = new JLabel("Bottom File:");
		lblBottomFile.setBounds(224, 56, 73, 14);
		lblBottomFile.setToolTipText(bottomFileTT);
		frame.getContentPane().add(lblBottomFile);
		
		JButton btDoSteg = new JButton("Do it");
		btDoSteg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mkSteg.reset();
				if(rdbtnHideAFile.isSelected())
					hideFile();
				else if(rdbtnRevealFilemesssage.isSelected())
					reveal();
				else if(rdbtnHideAMessage.isSelected())
					hideMag();
				else
					log("Unexpected error.");
			}
		});
		btDoSteg.setBounds(685, 152, 89, 48);
		frame.getContentPane().add(btDoSteg);
		
		JCheckBox chckbxShowPassword = new JCheckBox("Show Password");
		chckbxShowPassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxShowPassword.isSelected())
					passwordField.setEchoChar((char)0);
				else
					passwordField.setEchoChar(defaultEchoChar);
			}
		});
		chckbxShowPassword.setBounds(488, 177, 182, 23);
		frame.getContentPane().add(chckbxShowPassword);
		
		JLabel lblPasswordoptional = new JLabel("Password (optional):");
		lblPasswordoptional.setBounds(355, 152, 133, 23);
		lblPasswordoptional.setToolTipText(passwordTT);
		frame.getContentPane().add(lblPasswordoptional);
		
		lblStegFile = new JLabel("Steg File:");
		lblStegFile.setToolTipText(stegFileTT);
		lblStegFile.setBounds(224, 82, 63, 14);
		frame.getContentPane().add(lblStegFile);
		
		tfStegFile = new JTextField();
		tfStegFile.setToolTipText(stegFileTT);
		tfStegFile.setBounds(297, 78, 378, 20);
		frame.getContentPane().add(tfStegFile);
		tfStegFile.setColumns(10);
		
		btnStegFile = new JButton("Choose");
		btnStegFile.setToolTipText(stegFileTT);
		btnStegFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(fcCurDir);
				fc.setDialogTitle("Choose a Steg Image");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new FilterTheFiles("PNG Files",new String[] {".png"}));
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					tfStegFile.setText(fc.getSelectedFile().getAbsolutePath().toString());
					log("Steg file selectrd: \""+fc.getSelectedFile().toString()+"\"");
				}
				fcCurDir = fc.getCurrentDirectory();
			}
		});
		btnStegFile.setBounds(685, 78, 89, 23);
		frame.getContentPane().add(btnStegFile);
		
		scrollMsg = new JScrollPane();
		scrollMsg.setBounds(297, 52, 477, 91);
		frame.getContentPane().add(scrollMsg);
		
		taMsg = new JTextArea();
		taMsg.setFont(new Font("Arial", Font.PLAIN, 14));
		scrollMsg.setViewportView(taMsg);
		taMsg.setWrapStyleWord(true);
		taMsg.setLineWrap(true);
		
		lblMessage = new JLabel("Message:");
		lblMessage.setBounds(224, 56, 63, 14);
		frame.getContentPane().add(lblMessage);
		
		lblSelectOne = new JLabel("Select One:");
		lblSelectOne.setBounds(10, 135, 82, 14);
		frame.getContentPane().add(lblSelectOne);
		
		rdbtnlsb2 = new JRadioButton("2-LSB (Recommended)");
		rdbtnlsb2.setToolTipText(lsb2TT);
		rdbtnlsb2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRadioClick(3);
			}
		});
		rdbtnlsb2.setSelected(true);
		rdbtnlsb2.setBounds(10, 152, 158, 23);
		frame.getContentPane().add(rdbtnlsb2);
		
		rdbtnlsb1 = new JRadioButton("1-LSB");
		rdbtnlsb1.setToolTipText(lsb1TT);
		rdbtnlsb1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleRadioClick(4);
			}
		});
		rdbtnlsb1.setBounds(10, 177, 133, 23);
		frame.getContentPane().add(rdbtnlsb1);
		
		JLabel lblLogs = new JLabel("Logs:");
		lblLogs.setBounds(10, 207, 46, 23);
		frame.getContentPane().add(lblLogs);
		
		JScrollPane scrollLogs = new JScrollPane();
		scrollLogs.setBounds(10, 228, 764, 202);
		frame.getContentPane().add(scrollLogs);
		
		taLogs = new JTextArea();
		taLogs.setLineWrap(true);
		taLogs.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollLogs.setViewportView(taLogs);
		taLogs.setWrapStyleWord(true);
		
		JLabel lblAbout = new JLabel("About");
		lblAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(null,"iSteg v-"+VCODE+"\nby Ibrahim Rafi\nVisit: github.com/rafiibrahim8/iSteg to learn more.");
			}
		});
		lblAbout.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblAbout.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAbout.setForeground(Color.BLUE);
		lblAbout.setBounds(728, 5, 46, 14);
		frame.getContentPane().add(lblAbout);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(488, 152, 187, 25);
		passwordField.setToolTipText(passwordTT);
		frame.getContentPane().add(passwordField);
		defaultEchoChar = passwordField.getEchoChar();
		
		showFileUI();
	}

	protected void hideMag() {
		mkSteg.write(new File(tfTopImage.getText()).toPath(),taMsg.getText(),rdbtnlsb2.isSelected()?2:1,passwordField.getPassword());
		makeStegImage();
	}
	
	protected void hideFile() {
		mkSteg.write(new File(tfTopImage.getText()).toPath(), new File(tfBottomFile.getText()).toPath(),rdbtnlsb2.isSelected()?2:1,passwordField.getPassword());
		makeStegImage();
	}
	
	protected void reveal() {
		mkSteg.read(new File(tfStegFile.getText()).toPath(), passwordField.getPassword());
		showErrorLog(mkSteg.getError());
		if(mkSteg.getError()==StegDym.SUCCESS || mkSteg.getError()==StegDym.SUCCESS_NOPASS) {
			if(mkSteg.getResultType() == StegDym.FILE) {
				readFileSteg();
			}
			
			else if(mkSteg.getResultType() == StegDym.MESSAGE) {
				readMsgSteg();
			}
			else
				log("An unexpected error occurred.");
		}
	}



	private void readMsgSteg() {
		log("Here is the message:\n"+mkSteg.getMessageResult());
	}

	private void readFileSteg() {
		JFileChooser fc = new JFileChooser();
		fc.setSelectedFile(new File(mkSteg.getFileName()));
		String path=null;
		do {
			fc.setCurrentDirectory(fcCurDir);
			fcSaveDialogReturn = fc.showSaveDialog(null);
			if(fcSaveDialogReturn != JFileChooser.APPROVE_OPTION)
				break;
			path = fc.getSelectedFile().getAbsolutePath().toString();
		} while(confirmReplace(path));
		if(fcSaveDialogReturn==JFileChooser.APPROVE_OPTION) {
			try {
				FileOutputStream fos = new FileOutputStream(new File(path));
				fos.write(mkSteg.getFileResult());
				fos.close();
			} catch (Exception e) {
				log("Error in saving file.");
			}
			log("File saved successfully.");
		}
		else if(fcSaveDialogReturn == JFileChooser.CANCEL_OPTION)
			log("Saving oparation cancelled by user.");
		else
			log("Unable to save file.");
		fcCurDir = fc.getCurrentDirectory();
	}

	private void makeStegImage() {
		int error = mkSteg.getError();
		showErrorLog(error);
		if(error==StegDym.SUCCESS || error==StegDym.SUCCESS_NOPASS) {
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FilterTheFiles("PNG File",new String[] {".png"}));
			String path=null;
			do {
				fc.setCurrentDirectory(fcCurDir);
				fcSaveDialogReturn = fc.showSaveDialog(null);
				if(fcSaveDialogReturn != JFileChooser.APPROVE_OPTION)
					break;
				path = fc.getSelectedFile().getAbsolutePath().toString();
				if(!path.endsWith(".png"))
					path+=".png";
				
			} while(confirmReplace(path));
			if(fcSaveDialogReturn==JFileChooser.APPROVE_OPTION) {
				try {
					FileOutputStream fos = new FileOutputStream(new File(path));
					ImageIO.write(mkSteg.getStegImg(),"png", fos);
					fos.close();
				} catch (Exception e) {
					log("Error in saving file.");
				}
				log("Steganographic image saved successfully.");
			}
			else if(fcSaveDialogReturn == JFileChooser.CANCEL_OPTION)
				log("Saving oparation cancelled by user.");
			else
				log("Unable to save file.");
			fcCurDir = fc.getCurrentDirectory();
		}
	}

	private boolean confirmReplace(String path) {
		File file = new File(path);
		if(file.exists()) {
			int response = JOptionPane.showConfirmDialog(null,"Do you want to replace "+file.getName()+"?","Confirm Action",JOptionPane.YES_NO_CANCEL_OPTION);
			if(response == 2)
				fcSaveDialogReturn = JFileChooser.CANCEL_OPTION;
			else if(response == 1)
				return true;
		}
		return false;
	}

	private void showErrorLog(int error) {
		switch(error) {
		case StegDym.ERR_BITCOUNT:
			log("ERROR-101. Contact developer.");
			break;
		case StegDym.ERR_CIPHERFAILED:
			log("Cipherfailed error. Please try again.");
			break;
		case StegDym.ERR_FILEREAD:
			log("Unable to read one or more file(s).");
			break;
		case StegDym.ERR_FILEWRITE:
			log("Very unexpected error. Contact developer.");
			break;
		case StegDym.ERR_LOWIMGSIZE:
			taLogs.append(">> The image: \""+tfTopImage.getText()+"\" is too small to hold all of your data. ");
			if(rdbtnlsb1.isSelected())
				taLogs.append("Try using LSB-2 instead. Or a higher resolution image.\n");
			else
				taLogs.append("Try using a higher resolution image.\n");
			break;
		case StegDym.ERR_NOSTEG:
			log("The image: \""+tfStegFile.getText()+"\" does not contain any steganographic data.");
			break;
		case StegDym.ERR_NOTANIMAGE:
			if(rdbtnRevealFilemesssage.isSelected())
				log("The file: \""+tfStegFile.getText()+"\" does not contain proper image data.");
			else
				log("The file: \""+tfTopImage.getText()+"\" does not contain proper image data.");
			break;
		case StegDym.ERR_PASSREQ:
			log("The image contains encrypted steganographic data. Password required.");
			break;
		case StegDym.ERR_WRONGPWD:
			log("The password is incorrect. Try again.");
			break;
		case StegDym.SUCCESS:
			if(mkSteg.getResultType() == StegDym.MESSAGE)
				log("Oparation completed successfully.");
			else
				log("Oparation completed successfully. Save your file now.");
			break;
		case StegDym.SUCCESS_NOPASS:
			if(mkSteg.getResultType() == StegDym.MESSAGE)
				log("Oparation Completed successfully. The steganographic data was not encrypted with passsword.");
			else
				log("Oparation Completed successfully. The steganographic data was not encrypted with passsword. Save your file now.");
			break;
		default:
			log("ERROR-102. Contact developer.");
			break;
		}
	}

	protected void log(String string) {
		taLogs.append(">> "+string+"\n");
	}

	protected void handleRadioClick(int i) {
		switch(i) {
		case 0:
			rdbtnHideAFile.setSelected(true);
			rdbtnHideAMessage.setSelected(false);
			rdbtnRevealFilemesssage.setSelected(false);
			showFileUI();
			break;
		case 1:
			rdbtnHideAFile.setSelected(false);
			rdbtnHideAMessage.setSelected(true);
			rdbtnRevealFilemesssage.setSelected(false);
			ShowMsgUI();
			break;
		case 2:
			rdbtnHideAFile.setSelected(false);
			rdbtnHideAMessage.setSelected(false);
			rdbtnRevealFilemesssage.setSelected(true);
			showRevealUI();
			break;
		case 3:
			rdbtnlsb2.setSelected(true);
			rdbtnlsb1.setSelected(false);
			break;
		case 4:
			rdbtnlsb2.setSelected(false);
			rdbtnlsb1.setSelected(true);
		}
		
	}

	private void showRevealUI() {
		setTopImageVisible(false);
		setMsgVisible(false);
		setBottomFileVisible(false);
		setStegFileVisible(true);
		setLSBRDVisible(false);
	}

	private void ShowMsgUI() {
		setTopImageVisible(true);
		setBottomFileVisible(false);
		setMsgVisible(true);
		setStegFileVisible(false);
		setLSBRDVisible(true);
	}

	private void showFileUI() {
		setTopImageVisible(true);
		setBottomFileVisible(true);
		setStegFileVisible(false);
		setMsgVisible(false);
		setLSBRDVisible(true);
		
	}
	
	private void setLSBRDVisible(boolean value) {
		rdbtnlsb2.setVisible(value);
		rdbtnlsb1.setVisible(value);
		lblSelectOne.setVisible(value);
	}

	private void setTopImageVisible(boolean value) {
		lblTopImage.setVisible(value);
		btnTopImage.setVisible(value);
		tfTopImage.setVisible(value);
	}
	private void setStegFileVisible(boolean value) {
		tfStegFile.setVisible(value);
		lblStegFile.setVisible(value);
		btnStegFile.setVisible(value);
	}
	private void setMsgVisible(boolean value) {
		taMsg.setVisible(value);
		lblMessage.setVisible(value);
		scrollMsg.setVisible(value);
	}
	private void setBottomFileVisible(boolean value) {
		lblBottomFile.setVisible(value);
		btnBottomFile.setVisible(value);
		tfBottomFile.setVisible(value);
	}
}
