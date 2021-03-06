package univ.lecture.riotapi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j;
import univ.lecture.riotapi.model.CalcApp;
import univ.lecture.riotapi.model.Result;


@RestController
@RequestMapping("/api/v1")
@Log4j
public class CalcController {
    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/calc", method = RequestMethod.POST)
    public Result Calculator(@RequestBody String token) throws IOException {
        token = URLDecoder.decode(token,"UTF-8");
    	final String endpoint = "http://52.79.162.52:8080/api/v1/answer";
        String[] s = token.split(" ");
    	CalcApp cal = new CalcApp();
        double rst = cal.calc(s);
        System.out.println(token);
        String response = restTemplate.postForObject(endpoint, rst, String.class);
        Result result = new Result(6, System.currentTimeMillis(), rst);
        // 값을 계산한 시간을 넣어줌.

		URL url = new URL(endpoint);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		conn.setRequestMethod("POST");


        JSONObject data = new JSONObject();
        data.put("teamId", result.getTeamId());
        data.put("now", result.getNow());
        data.put("result", result.getResult());

        OutputStreamWriter osw = new OutputStreamWriter(
		conn.getOutputStream());

		try {
			osw.write(data.toString());
			osw.flush();


			BufferedReader br = null;

			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			String line = null;

			while ((line = br.readLine()) != null) {
				System.out.println(line);

			}

			osw.close();
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        return result;
    }
}
