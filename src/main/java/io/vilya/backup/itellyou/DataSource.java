package io.vilya.backup.itellyou;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author vilya
 */
public interface DataSource {

    String ROOT_URL = "http://msdn.itellyou.cn";

    List<String> getRootNodes() throws IOException;

    JSONArray index(String rootId) throws IOException, InterruptedException;

    JSONObject getLang(String id) throws IOException, InterruptedException;

    JSONObject getList(String id, String lang) throws IOException, InterruptedException;

    JSONObject getProduct(String id) throws IOException, InterruptedException;

}
