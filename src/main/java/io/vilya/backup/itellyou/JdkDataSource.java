package io.vilya.backup.itellyou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vilya
 */
@Slf4j
public class JdkDataSource implements DataSource {

    private static final HttpClient httpClient;

    static {
        httpClient = HttpClient.newHttpClient();
    }

    @Override
    public List<String> getRootNodes() throws IOException {
        Document doc = Jsoup.connect(ROOT_URL).get();
        Elements roots = doc.select("#accordion>div>.panel-collapse");
        var prefixLength = "collapse_".length();
        return roots.stream().map(t -> t.id().substring(prefixLength)).collect(Collectors.toList());
    }

    @Override
    public JSONArray index(String rootId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/Category/Index"))
                .POST(HttpRequest.BodyPublishers.ofString("id=" + rootId))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://msdn.itellyou.cn/")
                .build();
        HttpResponse<String> response = httpClient.send(request, t -> HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8));
        return JSON.parseArray(response.body());
    }

    @Override
    public JSONObject getLang(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(ROOT_URL + "/Category/GetLang"))
                .POST(HttpRequest.BodyPublishers.ofString("id=" + id))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://msdn.itellyou.cn/")
                .build();
        HttpResponse<String> response = httpClient.send(request, t -> HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8));
        return JSON.parseObject(response.body());
    }

    @Override
    public JSONObject getList(String id, String lang) throws IOException, InterruptedException {
        String requestBody = String.format("id=%s&lang=%s&filter=true", id, lang);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(ROOT_URL + "/Category/GetList"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://msdn.itellyou.cn/")
                .build();
        HttpResponse<String> response = httpClient.send(request, t -> HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8));
        return JSON.parseObject(response.body());
    }

    @Override
    public JSONObject getProduct(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(ROOT_URL + "/Category/GetProduct"))
                .POST(HttpRequest.BodyPublishers.ofString("id=" + id))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Referer", "http://msdn.itellyou.cn/")
                .build();
        HttpResponse<String> response = httpClient.send(request, t -> HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8));
        return JSON.parseObject(response.body());
    }

}
