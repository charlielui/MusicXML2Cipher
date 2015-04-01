package musicxmlconverter;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class MainWindow
{

	private JFrame frmMusicxmlToNumber;
	private JTextField tfFileURL;
	private JSpinner spinner;
	final JFileChooser fc = new JFileChooser();

	int octaveChange = 0;
	boolean disableSlur = true;
	// String fileName;
	// String pdfFile;

	xmlcipher converter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainWindow window = new MainWindow();
					window.frmMusicxmlToNumber.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		frmMusicxmlToNumber = new JFrame();
		frmMusicxmlToNumber.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 15));
		frmMusicxmlToNumber.setTitle("MusicXML to Number");
		frmMusicxmlToNumber.setBounds(100, 100, 500, 350);
		frmMusicxmlToNumber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMusicxmlToNumber.getContentPane().setLayout(null);

		JLabel lblMusicxmlToNumber = new JLabel("MusicXML to Number Notation Converter");
		lblMusicxmlToNumber.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblMusicxmlToNumber.setHorizontalAlignment(SwingConstants.CENTER);
		lblMusicxmlToNumber.setBounds(31, 24, 381, 14);
		frmMusicxmlToNumber.getContentPane().add(lblMusicxmlToNumber);

		JLabel lblByCharlieLui = new JLabel("by Charlie Lui");
		lblByCharlieLui.setHorizontalAlignment(SwingConstants.CENTER);
		lblByCharlieLui.setBounds(155, 49, 132, 14);
		frmMusicxmlToNumber.getContentPane().add(lblByCharlieLui);

		JLabel lblxmlFile = new JLabel("MusicXML File (.xml)");
		lblxmlFile.setBounds(31, 74, 132, 14);
		frmMusicxmlToNumber.getContentPane().add(lblxmlFile);

		tfFileURL = new JTextField();
		tfFileURL.setFont(new Font("Tahoma", Font.PLAIN, 15));
		tfFileURL.setBounds(31, 105, 426, 29);
		frmMusicxmlToNumber.getContentPane().add(tfFileURL);
		tfFileURL.setColumns(10);

		JButton btnOpen = new JButton("Open...");
		btnOpen.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();

					tfFileURL.setText(file.getAbsolutePath());

				}
			}
		});
		btnOpen.setBounds(31, 149, 116, 42);
		frmMusicxmlToNumber.getContentPane().add(btnOpen);

		JButton btnConvert = new JButton("CONVERT");
		btnConvert.setFont(new Font("Tahoma", Font.PLAIN, 19));
		btnConvert.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String inputFileURL = tfFileURL.getText();
					File inputFile = new File(inputFileURL);
					String fileName = inputFile.getName();
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));
					String filePath = inputFileURL;
					filePath = filePath.substring(0, inputFileURL.lastIndexOf(File.separator));
					File outputFile = new File(filePath + "\\" + fileName + ".pdf");
					Files.deleteIfExists(outputFile.toPath());
					if (outputFile.exists() && !outputFile.isDirectory())
					{
						//			Files.delete(outputFile.toPath());
						throw new IOException("Output File " + outputFile.getAbsolutePath() + " Already Exists");
					}
					octaveChange = (Integer)spinner.getValue();
					converter = new xmlcipher(octaveChange, disableSlur);
					converter.convertDoc(inputFileURL, outputFile.getAbsolutePath());
				} catch (Exception exception)
				{
					// JOptionPane.showMessageDialog(null,
					// exception.getCause());
					JOptionPane.showMessageDialog(null, exception.getMessage());
					System.out.println("OMGOMGOMGOMG");
				}
			}
		});

		JCheckBox chckbxSlur = new JCheckBox("Slur");
		chckbxSlur.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxSlur.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				disableSlur = ((JCheckBox) arg0.getSource()).isSelected();
			}
		});

		JLabel lblOptions = new JLabel("Options");
		lblOptions.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblOptions.setBounds(310, 165, 92, 14);
		frmMusicxmlToNumber.getContentPane().add(lblOptions);
		chckbxSlur.setBounds(327, 177, 105, 44);
		frmMusicxmlToNumber.getContentPane().add(chckbxSlur);

		spinner = new JSpinner();
		spinner.setFont(new Font("Tahoma", Font.PLAIN, 15));
		spinner.setBounds(296, 223, 46, 29);
		frmMusicxmlToNumber.getContentPane().add(spinner);

		JLabel lblOctaveChange = new JLabel("Octave Change");
		lblOctaveChange.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOctaveChange.setBounds(352, 226, 130, 26);
		frmMusicxmlToNumber.getContentPane().add(lblOctaveChange);
		btnConvert.setBounds(30, 237, 208, 53);
		frmMusicxmlToNumber.getContentPane().add(btnConvert);
	}
}
