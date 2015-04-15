import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;


public class MediaConnect {

	public static void main(String[] args) {

		try {
			System.out.println("Client Socket starts.");
			final Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress("127.0.0.1", 1236);
			//socket.setReuseAddress(true);
			socket.connect(addr, 5000);
			
			Thread t = new Thread() {
				@Override
				public void run() {
					System.out.println("Client Socket is Connected!");
					
					try {
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						DataInputStream dis = new DataInputStream(socket.getInputStream());
						
						System.out.println("Client Running...");

						Scanner scanner = new Scanner(System.in);
						try {
							for (int j=0; j<2; j++) {
								for (int i=0; i<10; i++) {
									
									System.out.print("State : ");
									int state = scanner.nextInt();
									System.out.print("ID : ");
									int id = scanner.nextInt();
									
									System.out.println();
									
									dos.writeInt(state);
									dos.writeInt(id);
									//dos.writeInt(j*100+i);
									//dos.writeInt(j*100+i*10);
									dos.flush();
									
									//int read = dis.read(bytes);
									//System.out.println("Read! : " + read);
									//System.out.println(bytes);
								}
								Thread.sleep(3000);
							}
						} catch (Exception e) { ; }
						
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.out.println("Client Thread is dead.");
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			
			t.start();
			t.join();
			
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Client Socket is Finished!");
	}

}
