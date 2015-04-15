import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MediaServer {

	public static void main(String[] args) {
		try {
			System.out.println("Server Socket starts.");
			final ServerSocket server = new ServerSocket(1237);
			
			for(int i=0; i<3; i++) { 
				final Socket socket = server.accept();
				
				Thread t = new Thread() {
					@Override
					public void run() {
	
						try {
							System.out.println("Server Socket is Connected!");
							
							DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
							DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
							//BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());

							System.out.println("Server Running...");
							while(true) {
								try {
//									byte[] bytes = new byte[4];
//									int read = bis.read(bytes);
//									if (read == -1)
//										break;
//									System.out.println("Read : " + ByteBuffer.wrap(bytes).getInt());
									
									System.out.println("Read : " + dis.readInt());
									System.out.println("Read2 : " + dis.readInt());
									
								} catch (EOFException e) {
									break;
									
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Server Socket is Finished!");
					}
				};
				
				t.start();
			}
			//t.join();
			
		} catch (Exception e) {
			
		}
		System.out.println("Server Socket is Finished!");
	}

}
