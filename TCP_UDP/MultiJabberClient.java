import java.net.*;
import java.io.*;

public class MultiJabberClient {
	static class JabberClientThread extends Thread {
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private int id = count++;

		public JabberClientThread(InetAddress addr) {
			System.out.println("Making client " + id);
			count++;
			try {
				socket = new Socket(addr, MultiJabberServer.PORT);
			} catch (IOException e) {
				// If the creation of the socket fails,
				// nothing needs to be cleaned up.
			}
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				// Enable auto-flush:
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				start();
			} catch (IOException e) {
				// The socket should be closed on any
				// failures other than the socket
				// constructor:
				try {
					socket.close();
				} catch (IOException e2) {
				}
			}
			// Otherwise the socket will be closed by
			// the run() method of the thread.
		}

		public void run() {
			try {
				for (int i = 0; i < 25; i++) {
					out.println("Client " + id + ": " + i);
					String str = in.readLine();
					System.out.println(str);
				}
				out.println("END");
			} catch (IOException e) {
			} finally {
				// Always close it:
				try {
					socket.close();
				} catch (IOException e) {
				}
				count--; // Ending this thread
			}
		}
	}
	
	static final int MAX_THREADS = 5;
	public static int count = 0;

	public static void main(String[] args) throws IOException, InterruptedException {
		InetAddress addr = InetAddress.getByName(null);
		while (true) {
			System.out.println(count);
			if (count < MAX_THREADS)
				new JabberClientThread(addr);
			else{
				System.exit(0);
			}
			Thread.currentThread().sleep(100);
		}
	}
}