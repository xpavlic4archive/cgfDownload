import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StarterFast {
	public static void main(String[] args) throws Throwable {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 2000000; i++) {
			pool.execute(new MyRunnable(i));

		}
	}

	static class  MyRunnable implements Runnable {

		private int number;

		public MyRunnable(int i) {
			number = i;
		}

		@Override
		public void run() {
			try {
				URL oracle = new URL(
						"http://www.cgf.cz/CheckHcp.aspx?MemberNumber="
								+ Starter.normalize(number));
				URLConnection yc = oracle.openConnection();
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(
						yc.getInputStream()));
				int n = number% 1000;
				if (!new File("out/"+n).isDirectory()) {
					new File("out/"+n).mkdir();
				}
				FileWriter fw = new FileWriter(new File("out/"+ n+"/"+ number));
				BufferedWriter bw = new BufferedWriter(fw);
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					bw.write(inputLine);
				}
				bw.flush();
				bw.close();
				in.close();
				if (number% 100 ==0) {
					System.out.println("" + number); 
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
