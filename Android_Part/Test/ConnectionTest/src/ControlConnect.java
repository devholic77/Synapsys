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
					int count = 100;
					int togle = 0; 
					try {
						DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
						//DataInputStream dis = new DataInputStream(socket.getInputStream());
						Thread.sleep(5000);
						System.out.println("Client Running...");
						for(int i=0; i<1000; i++) {
							try {
								
								//count += 20;
								if(togle == 0)
								{
									String str = "01:01:350:300:0:\n";
									dos.write(str.getBytes());
									dos.flush();									
									System.out.println(str);
									togle = 1;
								
								}else if(togle == 1)
								{
									String str = "01:04:350:300:0:\n";
									dos.write(str.getBytes());
									dos.flush();									
									System.out.println(str);
									togle = 2;								
								}else
								{
									String str = "01:00:350:300:0:\n";
									dos.write(str.getBytes());
									dos.flush();									
									System.out.println(str);
									togle = 0;		
								
								}
	
								Thread.sleep(10);
							
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
