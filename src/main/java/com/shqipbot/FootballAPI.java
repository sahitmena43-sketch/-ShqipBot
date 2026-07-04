package com.shqipbot;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import java.io.IOException;

public class FootballAPI {
    
    private static final String BASE_URL = "https://v3.football.api-sports.io";
    private final String apiKey;
    private final OkHttpClient client;
    
    public FootballAPI(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }
    
    /**
     * Kërko një ndeshje sipas emrave të skuadrave dhe datës
     * @param team1 Emri i skuadrës 1
     * @param team2 Emri i skuadrës 2
     * @param date Data në format YYYY-MM-DD
     * @return Rezultati i ndeshjes ose null nëse nuk gjendet
     */
    public MatchResult getMatchResult(String team1, String team2, String date) {
        try {
            // Kërko ID-të e skuadrave
            int team1Id = getTeamId(team1);
            int team2Id = getTeamId(team2);
            
            if (team1Id == 0 || team2Id == 0) {
                return null;
            }
            
            // Kërko ndeshjen
            String url = BASE_URL + "/fixtures?date=" + date + 
                        "&team=" + team1Id + "&status=FT";
            
            Request request = new Request.Builder()
                .url(url)
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", "v3.football.api-sports.io")
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return null;
                }
                
                String jsonResponse = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                JsonArray fixtures = jsonObject.getAsJsonArray("response");
                
                if (fixtures == null || fixtures.isEmpty()) {
                    return null;
                }
                
                // Gjej ndeshjen ku luajnë të dyja skuadrat
                for (int i = 0; i < fixtures.size(); i++) {
                    JsonObject fixture = fixtures.get(i).getAsJsonObject();
                    JsonObject teams = fixture.getAsJsonObject("teams");
                    JsonObject homeTeam = teams.getAsJsonObject("home");
                    JsonObject awayTeam = teams.getAsJsonObject("away");
                    
                    int homeId = homeTeam.get("id").getAsInt();
                    int awayId = awayTeam.get("id").getAsInt();
                    
                    if ((homeId == team1Id && awayId == team2Id) ||
                        (homeId == team2Id && awayId == team1Id)) {
                        
                        JsonObject goals = fixture.getAsJsonObject("goals");
                        int homeGoals = goals.get("home").getAsInt();
                        int awayGoals = goals.get("away").getAsInt();
                        String status = fixture.getAsJsonObject("fixture")
                            .getAsJsonObject("status")
                            .get("short").getAsString();
                        
                        MatchResult result = new MatchResult();
                        result.team1Name = homeTeam.get("name").getAsString();
                        result.team2Name = awayTeam.get("name").getAsString();
                        result.team1Goals = homeGoals;
                        result.team2Goals = awayGoals;
                        result.status = status; // "FT" = Finished
                        
                        if (homeGoals > awayGoals) {
                            result.winner = homeTeam.get("name").getAsString();
                            result.winnerType = 1;
                        } else if (awayGoals > homeGoals) {
                            result.winner = awayTeam.get("name").getAsString();
                            result.winnerType = 2;
                        } else {
                            result.winner = "Draw";
                            result.winnerType = 3;
                        }
                        
                        return result;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Gabim në API: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Kërko ID-në e një skuadre sipas emrit
     */
    private int getTeamId(String teamName) throws IOException {
        String url = BASE_URL + "/teams?name=" + teamName.replace(" ", "%20");
        
        Request request = new Request.Builder()
            .url(url)
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "v3.football.api-sports.io")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return 0;
            }
            
            String jsonResponse = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray teams = jsonObject.getAsJsonArray("response");
            
            if (teams == null || teams.isEmpty()) {
                return 0;
            }
            
            return teams.get(0).getAsJsonObject().get("team").getAsJsonObject()
                .get("id").getAsInt();
        }
    }
    
    /**
     * Klasa për rezultatin e ndeshjes
     */
    public static class MatchResult {
        public String team1Name;
        public String team2Name;
        public int team1Goals;
        public int team2Goals;
        public String status;
        public String winner;
        public int winnerType; // 1 = Skuadra 1 fiton, 2 = Skuadra 2 fiton, 3 = Barazim
    }
}