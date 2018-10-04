package io.vilya.backup.itellyou;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
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
public class ApacheDataSource implements DataSource {

    private static final CloseableHttpClient httpClient;

    static {
        httpClient = HttpClients.createDefault();
    }

    public List<String> getRootNodes() throws IOException {
        Document doc = Jsoup.connect(ROOT_URL).get();
        Elements roots = doc.select("#accordion>div>.panel-collapse");
        var prefixLength = "collapse_".length();
        return roots.stream().map(t -> t.id().substring(prefixLength)).collect(Collectors.toList());
    }

    public JSONArray index(String rootId) throws IOException, InterruptedException {
        HttpPost httpPost = new HttpPost(URI.create(ROOT_URL + "/Category/Index"));
        httpPost.setEntity(new StringEntity("id=" + rootId));
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Referer", "http://msdn.itellyou.cn/");
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return JSON.parseObject(response.getEntity().getContent(), JSONArray.class);
        }
    }

    public JSONObject getLang(String id) throws IOException, InterruptedException {
        HttpPost httpPost = new HttpPost(URI.create(ROOT_URL + "/Category/GetLang"));
        httpPost.setEntity(new StringEntity("id=" + id));
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Referer", "http://msdn.itellyou.cn/");
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return JSON.parseObject(response.getEntity().getContent(), JSONObject.class);
        }
    }

    public JSONObject getList(String id, String lang) throws IOException, InterruptedException {
        String requestBody = String.format("id=%s&lang=%s&filter=true", id, lang);
        HttpPost httpPost = new HttpPost(URI.create(ROOT_URL + "/Category/GetList"));
        httpPost.setEntity(new StringEntity(requestBody));
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Referer", "http://msdn.itellyou.cn/");
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return JSON.parseObject(response.getEntity().getContent(), JSONObject.class);
        }
    }

    public JSONObject getProduct(String id) throws IOException, InterruptedException {
        HttpPost httpPost = new HttpPost(URI.create(ROOT_URL + "/Category/GetProduct"));
        httpPost.setEntity(new StringEntity("id=" + id));
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Referer", "http://msdn.itellyou.cn/");
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            return JSON.parseObject(response.getEntity().getContent(), JSONObject.class);
        }
    }

}
