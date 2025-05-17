package project;

import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;

class msgth extends Thread {
	private final String token = "ajk123#%k2!lsd!234!%^^f17!@#sdfs!@$3$*s1s56!@#";
	static ArrayList<Socket> users = new ArrayList<Socket>();
	static Hashtable<String, Socket> usrNick = new Hashtable<String, Socket>();
	public String myName;
	public static final Map<String, PrintWriter> clientWriters = new HashMap<>();
	private List<String> friend = new ArrayList<String>();
	private PrintWriter pwriter;
	private String idfilePath = "C:\\javaproject\\USERS";
	String fdPath;
	File idFolder;
	Socket socket;
	OutputStream output = null;
	InputStream input = null;
	BufferedReader reader = null;

	public msgth(Socket socket) {
		this.socket = socket;
		users.add(socket);
		try {
			output = socket.getOutputStream();
			input = socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
		} catch (Exception e) {
			System.out.println("쓰레드 생성 에러");
		}
	}

	private void send(String msg) throws Exception {
		output.write(msg.getBytes());
		output.flush();
	}

	public void run() {
		try {
			String msg;
			pwriter = new PrintWriter(socket.getOutputStream(), true);
			if (!token.equals(msg = reader.readLine())) {
				System.out.println("접근 권한 없음");
				socket.close();
				return;
			} else if (!Server.serverVersion.equals(msg = reader.readLine())) {
				send("업데이트 필요! 버전: " + Server.serverVersion);
				System.out.println("업데이트가 필요한 버전을 사용하고 있습니다");
				socket.close();
				return;
			}
			send("버전: " + Server.serverVersion);
			firstAccess();
			clientWriters.put(myName, pwriter);
			while (true) {
				if ((msg = reader.readLine()) != null) {
					if (msg.startsWith("/msg")) {
						sendPrivateMessage(msg);
					} else if (msg.equals("/exit")) {
						System.out.println(socket.getLocalAddress() + ":" + socket.getLocalPort() + " 연결해제");
						clientWriters.remove(myName);
						usrNick.remove(myName);
						return;
					} else if (msg.startsWith("/fl")) {
						String fl = "";
						for (String data : friend)
							fl = fl + " " + data;
						send("/#/fl" + fl);
					} else if (msg.startsWith("/af")) {
						String[] parts = msg.split(" ");
						if (parts.length >= 2) {
							try {
								String targetNickname = parts[1];
								File targetFolder = new File(idfilePath + targetNickname);
								if (targetFolder.exists() || friend.contains(targetNickname)) {
									if (!friend.contains(targetNickname)) {
										File fl = new File(idfilePath + myName + "\\Friend.list");
										BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fl, true));
										if (fl.isFile() && fl.canWrite()) {
											bufwriter.write(targetNickname + "\n");
											bufwriter.close();
										}
										friend.add(targetNickname);
										send("/#/info " + targetNickname + "을 친구로 추가하였습니다.");
									} else
										send("/#/error " + targetNickname + "가 이미 추가 되어있습니다.");
								} else
									send("/#/error " + targetNickname + "을 찾을 수 없습니다.");
							} catch (Exception e) {
								System.out.println("파일 불러오기 오류 명령어:af");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(socket.getLocalAddress() + ":" + socket.getLocalPort() + " 연결해제");
			clientWriters.remove(myName);
			usrNick.remove(myName);
		}
	}

	private void sendPrivateMessage(String message) {
		String[] parts = message.split(" ");
		if (parts.length >= 3) {
			String targetNickname = parts[1];
			String privateMessage = message.substring(message.indexOf(targetNickname) + targetNickname.length() + 1);
			PrintWriter targetWriter = clientWriters.get(targetNickname);
			if (targetWriter != null && friend.contains(targetNickname)) {
				targetWriter.println("/#/recvmsg " + myName + " " + privateMessage);
			} else {
				try {
					send("/#/error 친구 '" + targetNickname + "'이 접속해있지 않습니다.");
				} catch (Exception e) {
					System.out.println("메세지 전달 오류");
				}
			}
		} else {
			try {
				send("잘못된 명령어입니다.");
			} catch (Exception e) {
				System.out.println("메세지 전달 오류");
			}
		}
	}

	public String encrypt(String text) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes());
		} catch (NoSuchElementException e) {
			System.out.println("패스워드 암호화 실패");
		} finally {
			return bytesToHex(md.digest());
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for (byte b : bytes)
			builder.append(String.format("%02x", b));
		return builder.toString();
	}

	private void firstAccess() {
		String msg;
		int loginAccess = -1;
		do {
			try {
				msg = reader.readLine();
				if (msg.startsWith(":c:login")) {
					try {
						String id = reader.readLine();
						String pwd = encrypt(reader.readLine());
						fdPath = idfilePath + id;
						idFolder = new File(fdPath);
						String line = "";
						if (idFolder.exists()) {
							BufferedReader pwdfile = new BufferedReader(
									new FileReader(idfilePath + id + "\\pwd.passKey"));
							String str = pwdfile.readLine();
							pwdfile.close();

							if (str.equals(pwd)) {
								myName = id;
								send(myName);
								loginAccess = 1;
								BufferedReader pdfile = new BufferedReader(
										new FileReader(idfilePath + id + "\\Friend.list"));
								while ((line = pdfile.readLine()) != null)
									friend.add(line);
							} else {
								send("-1");
							}

						} else {
							send("-1");
						}
					} catch (NoSuchElementException e) {
						System.out.println("로그인 에러");
					}

				} else if (msg.startsWith(":c:sign_up")) {
					try {
						String id = reader.readLine();
						String idpath = idfilePath + id;
						idFolder = new File(idpath);
						if (!idFolder.exists()) {
							try {
								idFolder.mkdir();
								send("/#/info");
								try {
									String pwd = encrypt(reader.readLine());
									File pwdfolder = new File(idpath + "\\pwd.passKey");
									File friendlist = new File(idpath + "\\Friend.list");
									friendlist.createNewFile();
									pwdfolder.createNewFile();
									FileWriter fw = new FileWriter(pwdfolder);
									BufferedWriter bw = new BufferedWriter(fw);
									bw.write(pwd);
									bw.close();
									myName = id;
									send(myName);
									loginAccess = 1;
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								System.out.println("회원가입 아이디 생성 실패");
							}
						} else
							send("/#/error");
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

		} while (loginAccess == -1);
		usrNick.put(myName, socket);
	}

}

public class Server {
	static String serverVersion = "v0.0.1b";

	public static void main(String[] args) {
		int socket = 80;

		try {
			ServerSocket ss = new ServerSocket(socket);
			System.out.println("서버열림 " + serverVersion);
			while (true) {
				Socket user = ss.accept();
				System.out.println("클라이언트 입장 " + user.getLocalAddress() + " : " + user.getLocalPort());
				msgth th = new msgth(user);
				th.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}