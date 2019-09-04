package my.org;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSONReader {

	private static String getJSONFromURL(String url) throws IOException {
		InputStream istream = new URL(url).openStream();
		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(istream, Charset.forName("UTF-8")));
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = reader.read()) != -1) {
				sb.append((char) cp);
			}
			return sb.toString();
		} finally {
			istream.close();
		}
	}

	public static void main(String agrs[]) throws IOException, ScriptException {
		String jsonStr = getJSONFromURL(
				"https://samples.openweathermap.org/data/2.5/box/city?bbox=12,32,15,37,10&appid=b6907d289e10d714a6e88b30761fae22");

		// parsing the JSON using JAVA javascript engine
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine eg = sem.getEngineByName("javascript");
		String script = "Java.asJSONCompatible(" + jsonStr + ")";

		Object result = eg.eval(script);
		Map jsonArray = (Map) result;
		List<Map<String, String>> listofCity = (List<Map<String, String>>) jsonArray.get("list");
		FileOutputStream  logOutStream = new FileOutputStream("c://temp//cityLog.txt");		
		for (Map<String, String> cityMap : listofCity) {
			Map<String, String> tempCityMap = filterCity(cityMap);
			if (null != tempCityMap && null != tempCityMap.get("name"))
			{
				System.out.println(tempCityMap.get("name"));
				logOutStream.write(tempCityMap.get("name").getBytes());
				logOutStream.write(System.lineSeparator().getBytes());
			}
		}
		logOutStream.close();
	}

	private static Map<String, String> filterCity(Map<String, String> mapOfCity) {
		return mapOfCity.entrySet().stream()
				.filter(city -> "name".equals(city.getKey()) && city.getValue().startsWith("T"))
				.collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));
	}

}
