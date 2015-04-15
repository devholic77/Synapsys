import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


public class ControlConnect {

	public static void main(String[] args) {

		try {
			System.out.println("Client Socket starts.");
			final Socket socket = new Socket();
			SocketAddress addr = new InetSocketAddress("127.0.0.1", 1235);
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
						for(int i=0; i<10; i++) {
							try {
								String str = "01:01:1234:4321:0:\n";
								dos.write(str.getBytes());
								dos.flush();
								
								System.out.println(str);
	
								Thread.sleep(1000);
							
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
			System.out.println("Client Socket is Finished!");
			
		} catch (Exception e) {
			
		}
	}

}
