package NetEvent;

import Cos.CosHttpClient;
import Cos.FileOP;
import com.qcloud.cos.request.GetFileLocalRequest;
import com.qcloud.cos.request.StatFileRequest;

import java.util.ArrayList;

/**
 * Created by xy16 on 17-2-12.
 */
public class Test {
	public static void main(String[] args) {
		Client client = new Client();
		Thread netThread = new Thread(client);
		netThread.start();
		synchronized (client) {
			try {
				client.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			client.launchRequest("Test", "123456");
			client.enterQuestion("1");
			client.sendContent("hello", new ArrayList<>(), "1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
