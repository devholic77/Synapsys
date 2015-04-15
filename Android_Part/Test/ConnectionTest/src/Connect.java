import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class Connect {

	public static void main(String[] args) {
		
		try {
			System.out.println("Client Socket starts.");
			final Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress("127.0.0.1", 1234);
			//socket.setReuseAddress(true);
			socket.connect(addr, 5000);
			
			Thread t = new Thread() {
				@Override
				public void run() {
					System.out.println("Client Socket is Connected!");
					
					try {
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						//DataInputStream dis = new DataInputStream(socket.getInputStream());

						System.out.println("Client Running...");
						for(int i=0; i<100; i++) {
							try {
								File file;
								if (i%3 == 0)
									file = new File("/home/yeonho/Desktop/bin3.jpg");
								else if (i%3 == 1)
									file =	new File("/home/yeonho/Desktop/bin2.jpg");
								else
									file =	new File("/home/yeonho/Desktop/bin.jpg");
								
								dos.writeInt((int) file.length());
								dos.flush();
								
								FileInputStream fis = new FileInputStream(file);
								byte[] bytes = new byte[(int) file.length()];
								fis.read(bytes);
								//dos.write("1:2:3:4:5:\n".getBytes());
								dos.write(bytes);
								dos.flush();
								
								//dis.read(bytes);
								//System.out.println(new String(bytes));
	
								Thread.sleep(3000);
							
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.println("Client Thread is dead.");
				}
				
			};
			
			t.start();
			t.join();
			
			socket.close();
			
		} catch (Exception e) {
			
		}
		System.out.println("Client Socket is Finished!");
	}

}
