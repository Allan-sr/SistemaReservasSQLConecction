package una.sistemareservas.negocio;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class AIExtractorService {

    private static final String GEMINI_MODEL = "gemini-3.6-flash";    private static final String API_KEY = obtenerApiKey();
    private final Gson gson = new Gson();

    private static String obtenerApiKey() {
        // 1. Buscar en variables de entorno
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key;
        }

        // 2. Buscar en el archivo config.properties local
        try {
            File file = new File("config.properties");
            if (file.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                    return props.getProperty("GEMINI_API_KEY");
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    public ReservaExtraidaDTO extraerDatosReserva(String frase, List<String> categoriasDisponibles) throws Exception {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new IllegalStateException("La clave GEMINI_API_KEY no está configurada en config.properties ni en variables de entorno.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=" + API_KEY;
        String promptSystem = buildSystemPrompt(categoriasDisponibles);

        JsonObject requestJson = new JsonObject();

        // Instrucciones del sistema
        JsonObject systemInstruction = new JsonObject();
        JsonArray systemParts = new JsonArray();
        JsonObject systemText = new JsonObject();
        systemText.addProperty("text", promptSystem);
        systemParts.add(systemText);
        systemInstruction.add("parts", systemParts);
        requestJson.add("systemInstruction", systemInstruction);

        // Contenido del mensaje del usuario
        JsonArray contents = new JsonArray();
        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        JsonArray userParts = new JsonArray();
        JsonObject userText = new JsonObject();
        userText.addProperty("text", frase);
        userParts.add(userText);
        userContent.add("parts", userParts);
        contents.add(userContent);
        requestJson.add("contents", contents);

        // Configuración para forzar respuesta en formato JSON
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("responseMimeType", "application/json");
        requestJson.add("generationConfig", generationConfig);

        // Envío de la solicitud HTTP
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en la API de Gemini (" + response.statusCode() + "): " + response.body());
        }

        // Extracción de la respuesta
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");

        if (candidates == null || candidates.size() == 0) {
            throw new RuntimeException("Gemini no devolvió una respuesta válida.");
        }

        String jsonContent = candidates.get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts").get(0).getAsJsonObject()
                .get("text").getAsString();

        return gson.fromJson(jsonContent, ReservaExtraidaDTO.class);
    }

    private String buildSystemPrompt(List<String> categoriasDisponibles) {
        String hoy = LocalDate.now().toString();
        return "Eres un asistente de inteligencia artificial para un sistema de reservas de recursos.\n"
                + "Fecha de referencia de hoy: " + hoy + "\n"
                + "Categorías válidas registradas en el sistema: " + gson.toJson(categoriasDisponibles) + "\n\n"
                + "Tu tarea es analizar la petición en lenguaje natural del usuario y extraer los datos en un objeto JSON estricto con la siguiente estructura:\n"
                + "{\n"
                + "  \"actividad\": \"descripción breve de la actividad\",\n"
                + "  \"fecha\": \"YYYY-MM-DD\",\n"
                + "  \"horaInicio\": \"HH:mm\",\n"
                + "  \"horaFin\": \"HH:mm\",\n"
                + "  \"categorias\": [\"categoria1\", \"categoria2\"]\n"
                + "}\n"
                + "REGLAS STRICTAS:\n"
                + "1. Mapea únicamente las categorías solicitadas a las descripciones exactas presentes en la lista de categorías válidas proporcionada.\n"
                + "2. Responde ÚNICAMENTE con el objeto JSON sin texto introductorio ni caracteres de marcado como ```json.";
    }
}