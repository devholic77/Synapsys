import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;


public class DisplayServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		byte[] bytes = "Hello World".getBytes();
		
		ByteBuffer buffer = ByteBuffer.allocate(100).put(bytes);
		System.out.println(buffer.toString());
		System.out.println(buffer.position() + "/" + buffer.arrayOffset() + "/" + buffer.capacity());
		
		try {
			System.out.println("Server Socket starts.");
			final ServerSocket server = new ServerSocket(1230);
			
			for(int i=0; i<3; i++) {
				final Socket socket = server.accept();
				
				Thread t = new Thread() {
					@Override
					public void run() {
	
						try {
							System.out.println("Server Socket is Connected!");
							
							DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
							//DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
							BufferedInputStream dis = new BufferedInputStream(socket.getInputStream());
							
							File file = new File("/home/yeonho/Desktop/test.jpg");
							file.createNewFile();
							
							while(true) {
								System.out.println("Server Running...");
								byte[] bytes = new byte[102400];
								//System.out.println("Before : " + new String(bytes, "UTF-8"));
								
								System.out.println("Available1 : " + dis.available());
								int read = dis.read(bytes);
								
								System.out.println("Read : " + read + " / " + (bytes[0]==(byte)0xFF) + (bytes[1]==(byte)0xD8));
								System.out.println("Read2 : " + (bytes[read-2] == (byte)0xFF ) + " / " + (bytes[read-1] == (byte)0xD9));
								
								FileOutputStream fos = new FileOutputStream(file);
								fos.write(bytes, 0, read);
								
								//System.out.println("After : " + new String(bytes, "UTF-8"));
								
								//dos.write("Hello".getBytes());
								//dos.flush();
								
								//Thread.sleep(1000);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
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
