package old;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xy16 on 16-12-31.
 * 主类
 */
public class Server {
	//server settings
	static final int port = 6666;
	static final String hostName = "localHost";
	static final String ip = "127.0.0.1";
	static final int maxThread = 1000;
	//public static Map<String, ArrayList<Socket>> question_socket_list;
	//public static Map<String, Socket> user_socket_list;

	public static void main(String[] args) throws IOException {
		//server init
		ServerSocket serverSocket = new ServerSocket(6666);
		System.out.println("Started: "+serverSocket);

		//question_socket_list = new HashMap<>();
		//user_socket_list = new HashMap<>();

		//创建线程池
		ExecutorService pool = Executors.newFixedThreadPool(maxThread);

		try {
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("Accept: " + socket);
				//新建线程
				pool.submit(new ServerThread(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			serverSocket.close();
		}

	}
}
