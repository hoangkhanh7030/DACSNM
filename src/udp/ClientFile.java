package udp;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import common.FileInfo;

public class ClientFile extends Frame {

	private final Font textFont = new Font("Tahoma", Font.PLAIN, 20);
	private final Font textHightlightFont = new Font("Tahoma", Font.BOLD, 20);
	private final Font textItalic = new Font("Tahoma", Font.ITALIC, 20);

	private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	private DatagramSocket clientSocket;
	private static int serverPort = 0;
	
	private static String serverHost = "0";
	public static ClientFile clientTransfer;
	public static String strFileName = "", strFilePath = "";
	InetAddress inetAddress;
	DatagramPacket sendPacket;

	public static void main(String[] args) throws IOException {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Nhap dia chi cua may server de ket noi: ");
		System.out.flush();
		serverHost = stdin.readLine();
		System.out.print("Nhap dia chi cong de ket noi voi may server: ");
		System.out.flush();
		serverPort = Integer.parseInt(stdin.readLine());
		clientTransfer = new ClientFile();

	}

	public ClientFile() {
		ClientFileView();
		initEvent();
	}

	public Label lblSelectFile;
	public Label lblTitle;
	public Label lblStudentName, lblTeacherName;
	public Label lblStudentClass;
	public TextField tfFile, tfFile2;
	public Button btnBrowse;
	public Button btnSend;
	public Button btnReset;

	private void ClientFileView() {
		setTitle("Client");
		setSize(700, 500);

		setLayout(null);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		lblTitle = new Label("Chương trình truyền file từ máy Client");
		lblTitle.setFont(textFont);
		lblTitle.setForeground(Color.red);
		add(lblTitle);
		lblTitle.setBounds(150, 30, 450, 50);
		lblSelectFile = new Label("Đường dẫn file cần truyền :");
		add(lblSelectFile);
		lblSelectFile.setBounds(80, 100, 200, 20);
		lblStudentName = new Label("Sinh Viên Thực Hiện: Hoàng Văn Khánh");
		lblStudentName.setFont(textItalic);
		lblStudentName.setBounds(80, 300, 400, 20);
		add(lblStudentName);

		lblStudentClass = new Label("Lớp : 16T2");
		lblStudentClass.setFont(textItalic);
		lblStudentClass.setBounds(80, 320, 200, 20);
		add(lblStudentClass);
		lblTeacherName = new Label("Giáo Viên Hướng Dẫn:Nguyễn Văn Nguyên");
		lblTeacherName.setFont(textItalic);
		lblTeacherName.setBounds(80, 340, 440, 20);
		add(lblTeacherName);

		tfFile = new TextField("");
		add(tfFile);
		tfFile.setBounds(80, 134, 300, 40);
//		tfFile2 = new TextField("");
//		add(tfFile2);
//		tfFile2.setBounds(100, 150, 200, 20);
		btnBrowse = new Button("Chọn File");
		btnBrowse.setFont(textFont);
		btnBrowse.addActionListener(new btnBrow());
		btnBrowse.setFocusable(false);
		btnBrowse.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnBrowse.setBounds(400, 133, 120, 40);
		add(btnBrowse);

		btnSend = new Button("Gửi");
		btnSend.addActionListener(new btnBrow());
		btnSend.setFocusable(false);
		btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnSend.setBounds(80, 200, 100, 30);
		add(btnSend);

		btnReset = new Button("Xóa");
		btnReset.addActionListener(new btnBrow());
		btnReset.setFocusable(false);
		btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnReset.setBounds(181, 200, 100, 30);
		add(btnReset);

		show();
		try {
			clientSocket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initEvent() {
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btnBrowse.setFont(textHightlightFont);
				btnBrowse.setForeground(Color.red);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnBrowse.setFont(textFont);
				btnBrowse.setForeground(Color.black);
			}
		});
	}

	public static String showDialog() {
		FileDialog fd = new FileDialog(new Frame(), "Select File...", FileDialog.LOAD);
		fd.show();
		return fd.getDirectory() + fd.getFile();
	}

	public void waitMillisecond(long millisecond) {
		try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class btnBrow implements ActionListener {

		public void actionPerformed(ActionEvent ae) {
			byte[] arrByteOfSentFile = null;
			if (ae.getSource() == btnBrowse) {
				strFilePath = showDialog();
				tfFile.setText(strFilePath);
				int intIndex = strFilePath.lastIndexOf("\\");
				strFileName = strFilePath.substring(intIndex + 1);

			}
			if (ae.getSource() == btnSend) {
				try {
					String destinationDir = "D:\\abc\\";
					File fileSend = new File(strFilePath);

					InputStream inputStream = new FileInputStream(fileSend);
					BufferedInputStream bis = new BufferedInputStream(inputStream);
					inetAddress = InetAddress.getByName(serverHost);
					byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];

					// get file size
					long fileLength = fileSend.length();
					int piecesOfFile = (int) (fileLength / PIECES_OF_FILE_SIZE);
					int lastByteLength = (int) (fileLength % PIECES_OF_FILE_SIZE);

					// check last bytes of file
					if (lastByteLength > 0) {
						piecesOfFile++;
					}

					// split file into pieces and assign to fileBytess
					byte[][] fileBytess = new byte[piecesOfFile][PIECES_OF_FILE_SIZE];
					int count = 0;
					while (bis.read(bytePart, 0, PIECES_OF_FILE_SIZE) > 0) {
						fileBytess[count++] = bytePart;
						bytePart = new byte[PIECES_OF_FILE_SIZE];
					}

					// read file info
					FileInfo fileInfo = new FileInfo();
					fileInfo.setFilename(fileSend.getName());
					fileInfo.setFileSize(fileSend.length());
					fileInfo.setPiecesOfFile(piecesOfFile);
					fileInfo.setLastByteLength(lastByteLength);
					fileInfo.setDestinationDirectory(destinationDir);

					// send file info
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(fileInfo);
					sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, inetAddress,
							serverPort);
					clientSocket.send(sendPacket);

					// send pieces of file
					for (int i = 0; i < (count - 1); i++) {
						sendPacket = new DatagramPacket(fileBytess[i], PIECES_OF_FILE_SIZE, inetAddress, serverPort);
						clientSocket.send(sendPacket);
						waitMillisecond(40);
					}
					// send last bytes of file
					sendPacket = new DatagramPacket(fileBytess[count - 1], PIECES_OF_FILE_SIZE, inetAddress,
							serverPort);
					clientSocket.send(sendPacket);
					waitMillisecond(40);

					JOptionPane.showMessageDialog(null, "Ban da gui  file toi Server", "Xac nhan",
							JOptionPane.INFORMATION_MESSAGE);
					bis.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			if (ae.getSource() == btnReset) {
				tfFile.setText("");
			}

		}

	}
}
