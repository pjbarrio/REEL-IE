import java.util.ArrayList;
import java.util.Map;

import com.davidsoergel.conja.Function;
import com.davidsoergel.conja.Parallel;


public class TestParalelization {

	public static void main(String[] args) {

		ArrayList<String> strings = new ArrayList<String>();
		strings.add("Goncalo");
		strings.add("Pablo");


		Map<String, Integer> results = Parallel.map(strings, new Function<String, Integer>(){
			public Integer apply(String obj) {
				Integer result = obj.length();
				return result;
			}
		});
		
		System.out.println(results);
	}

}
