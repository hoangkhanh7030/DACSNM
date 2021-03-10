package udp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import common.FileInfo;



public class ServerFile extends Frame {
	private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
    private DatagramSocket serverSocket;
    private static int port = 0;
	public static ServerFile serverTransfer; 
	public static String strFileName = "", strFilePath = "";
	public static String fileName = "", fileSize= "",piecesOfFile = "",LastBytesLength =" ";
	
	
	public static void main(String[] args) throws IOException {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Chon port de ket noi: ");
		System.out.flush();
		port = Integer.parseInt(stdin.readLine());
		serverTransfer = new ServerFile();
		
	}
	public Label lblSelectFile;
	public Label lblTitle;
	public Label lblStudentName;
	public Label lblStudentClass;
	public TextField tfFile , tfFile2;
	public Button btnBrowse;
	public Button btnSend;
	public Button btnReset;
	public ServerFile() {
//		setTitle("Chuong trinh truyen File phia Server");
//		setSize(700, 500);
//		setLayout(null);
//		addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//		lblTitle = new Label("Chuong trinh truyen File may Client ");
//		add(lblTitle);
//		lblTitle.setBounds(80, 30, 450, 50);
//		lblSelectFile = new Label("Duong dan file can truyen:");
//		add(lblSelectFile);
//		lblSelectFile.setBounds(80, 100, 200, 20);
//		lblStudentName = new Label("Sinh vien thuc hien:Hoàng Văn Khánh");
//		add(lblStudentName);
//		lblStudentName.setBounds(150, 300, 250, 20);
//		lblStudentClass = new Label("Lop : 16T2");
//		add(lblStudentClass);
//		lblStudentClass.setBounds(150, 320, 100, 20);
//		tfFile = new TextField("");
//		add(tfFile);
//		tfFile.setBounds(80, 134, 200, 20);
//		tfFile2 = new TextField("");
//		add(tfFile2);
//		tfFile2.setBounds(100, 150, 200, 20);
//		btnBrowse = new Button("Chon File");
//		btnBrowse.addActionListener(new btnBrow());
//		add(btnBrowse);
//		btnBrowse.setBounds(300, 133, 70, 20);
//		btnSend = new Button("Gui");
//		//btnSend.addActionListener(this);
//		add(btnSend);
//		btnSend.setBounds(140, 200, 50, 20);
//		btnReset = new Button("Xoa");
//		btnReset.addActionListener(new btnBrow());
//		add(btnReset);
//		btnReset.setBounds(210, 200, 50, 20);
//		show();
		try {
			byte[] receiveData = new byte[PIECES_OF_FILE_SIZE];
			
	        DatagramPacket receivePacket;
			serverSocket = new DatagramSocket(port);
			System.out.println("Server is opened on port " + port);
			
			while(true) {
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            serverSocket.receive(receivePacket);
	            
	            InetAddress inetAddress = receivePacket.getAddress();
	            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            FileInfo fileInfo = (FileInfo) ois.readObject();
	            
	            System.out.println("Receiving file...");
	            System.out.println(receivePacket);
	            File fileReceive = new File(fileInfo.getDestinationDirectory() 
	                    + fileInfo.getFilename());
	            BufferedOutputStream bos = new BufferedOutputStream(
	                    new FileOutputStream(fileReceive));
	            // write pieces of file
	            for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
	                receivePacket = new DatagramPacket(receiveData, receiveData.length, 
	                        inetAddress, port);
	                serverSocket.receive(receivePacket);
	                bos.write(receiveData, 0, PIECES_OF_FILE_SIZE);
	            }
	            // write last bytes of file
	            receivePacket = new DatagramPacket(receiveData, receiveData.length, 
	                    inetAddress, port);
	            serverSocket.receive(receivePacket);
	            bos.write(receiveData, 0, fileInfo.getLastByteLength());
	            bos.flush();
	            System.out.println("Done!");

	           // bos.close();
				
			}
			
			 
			
			
		} catch (Exception e) {
			System.out.println(e);
			 
		}
		
	}
	public static String showDialog() {
		FileDialog fd = new FileDialog(new Frame(), "Select File...", FileDialog.LOAD);
		fd.show();
		return fd.getDirectory() + fd.getFile();
	}
	
	private class btnBrow implements ActionListener{
		public void  actionPerformed(ActionEvent ae) {
			byte[] arrByteOfSentFile = null;
			if (ae.getSource() == btnBrowse) {
				strFilePath = showDialog();
				tfFile.setText(strFilePath);
				int intIndex = strFilePath.lastIndexOf("\\");
				strFileName = strFilePath.substring(intIndex + 1);
			
			
			}
			if(ae.getSource() == btnSend) {
				
			}
			if(ae.getSource() == btnReset) {
				tfFile.setText("");
			}
		
		
	}
	
	}
	
	
}
