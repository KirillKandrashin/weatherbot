import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JSONsParser {
    private static String API_STARTING = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private static String API_ENDING = "&units=metric&APPID=13d0573fb1af0c6fe7da1e7c8d07479c";
    private static DateTimeFormatter JSONDATEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter MYDATEFORMAT = DateTimeFormatter.ofPattern("MMM-dd HH:mm", Locale.US);

    public String FormingAns(String city, String type) {
        String result;
        try {
            String JSONdata = GettingJSONdata(city);
            List<String> listofdata = JSONdataToList(JSONdata, type);
            result = String.format("%s:%s%s", city, System.lineSeparator(), FindingFullInfo(listofdata));
        } catch (IllegalArgumentException e) {
            return String.format("Can't find \"%s\" city. Try another one ", city);
        } catch (Exception e) {
            return "Bot tired with his hard work";
        }
        return result;
    }

    private static String GettingJSONdata(String city) throws Exception {
        String urlst = API_STARTING + city + API_ENDING;
        URL urlOb = new URL(urlst);

        HttpURLConnection connection = (HttpURLConnection) urlOb.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();  //если 200 - все ок
        if (responseCode == 404) {
            throw new IllegalArgumentException();
        }

        BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder result = new StringBuilder();
        while ((inputLine = stream.readLine()) != null) {
            result.append(inputLine);
        }
        stream.close();

        return result.toString();
    }


    private static List<String> JSONdataToList(String data, String type) throws Exception {
        List<String> fulldatalist = new ArrayList<>();
        JsonNode rawdata = new ObjectMapper().readTree(data).get("list");

        String ftimess;
        String stimess;
        int time = LocalTime.now().getHour();
        String times;
        if (type.equals("now")) {
            if (time < 10) {
                times = "0" + time + ":" + "00";
                int ftimes = time + 1;
                int stimes = time - 1;
                ftimess = "0" + ftimes + ":" + "00";
                stimess = "0" + stimes + ":" + "00";
            }else{
                times = time + ":" + "00";
                int ftimes = time + 1;
                int stimes = time - 1;
                ftimess = "0" + ftimes + ":" + "00";
                stimess = "0" + stimes + ":" + "00";
            }
            for (JsonNode one_object : rawdata) {
                String forecastTime = one_object.get("dt_txt").toString();
                if (forecastTime.contains(times) || forecastTime.contains(ftimess) || forecastTime.contains(stimess)) {
                    fulldatalist.add(one_object.toString());
                }
            }
        } else if (type.equals("today")){
            for (JsonNode one_object : rawdata) {
                fulldatalist.add(one_object.toString());
            }

        }
        return fulldatalist;
    }

    private static String FindingFullInfo(List<String> weatherList) throws Exception {
        StringBuilder sb = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();

        for (String line : weatherList) {
            String date;
            JsonNode mainNode;
            JsonNode weatherNode;
            try {
                mainNode = mapper.readTree(line).get("main");
                weatherNode = mapper.readTree(line).get("weather");
                for (JsonNode one_Node : weatherNode) {
                    date = mapper.readTree(line).get("dt_txt").toString();
                    sb.append(formatForecastData(date, one_Node.get("description").toString(), mainNode.get("temp").asDouble()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String formatForecastData(String date, String description, double temperature) {
        LocalDateTime forecastDateTime = LocalDateTime.parse(date.replaceAll("\"", ""), JSONDATEFORMAT);
        String formattedDateTime = forecastDateTime.format(MYDATEFORMAT);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + Math.round(temperature) + "°C";
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature)) + "°C";
        }

        String formattedDescription = description.replaceAll("\"", "");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd", Locale.US);
        String strdateFormat = dateFormat.format(new Date());
        if (formattedDateTime.contains(strdateFormat)) {
            return String.format("%s   %s %s %s", formattedDateTime, formattedTemperature, formattedDescription, System.lineSeparator());
        }
        return "";
    }
}
