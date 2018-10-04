package io.vilya.backup.itellyou;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @author vilya
 */
public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        DataSource dataSource = new ApacheDataSource();
        DataRepository dataRepository = new JsonDataRepository(Paths.get("msdn-itellyou-cn-backup.json"));

        List<CategoryDTO> categoryDTOS = listCategory(dataSource);
        CategoryDTO currentCategory = null;
        ProductDTO currentProduct = null;
        ProductLanguageDTO currentlanguage = null;
        List<Product> products = new ArrayList<>();
        for (CategoryDTO categoryDTO : categoryDTOS) {
            currentCategory = categoryDTO;
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
            List<ProductDTO> productDTOS = listProduct(dataSource, currentCategory.getId());
            for (ProductDTO productDTO : productDTOS) {
                currentProduct = productDTO;
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                List<ProductLanguageDTO> productLanguageDTOS = listProductLanguage(dataSource, currentProduct.getId());
                for (ProductLanguageDTO productLanguageDTO : productLanguageDTOS) {
                    currentlanguage = productLanguageDTO;
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                    List<FileDTO> fileDTOS = listFile(dataSource, currentProduct.getId(), currentlanguage.getId());
                    for (FileDTO fileDTO : fileDTOS) {
                        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
                        FileDetailDTO fileDetailDTO = getDetail(dataSource, fileDTO.getId());
                        Product product = new Product();
                        product.setCategoryId(currentCategory.getId());
                        product.setProduct(currentProduct.getName());
                        product.setProductId(currentProduct.getId());
                        product.setLanguage(currentlanguage.getLang());
                        product.setLanguagId(currentlanguage.getId());
                        product.setId(fileDTO.getId());
                        product.setName(fileDetailDTO.getFileName());
                        product.setSize(fileDetailDTO.getSize());
                        product.setSha1(fileDetailDTO.getSha1());
                        product.setPostData(fileDetailDTO.getPostDateString());
                        product.setUrl(fileDetailDTO.getDownload());
                        products.add(product);
                    }
                }
            }
        }
        dataRepository.insert(products);
    }

    private static List<CategoryDTO> listCategory(DataSource dataSource) throws IOException {
        return dataSource.getRootNodes()
                .stream()
                .map(t -> new CategoryDTO(t))
                .collect(Collectors.toList());
    }

    private static List<ProductDTO> listProduct(DataSource dataSource, String categoryId) throws IOException, InterruptedException {
        JSONArray data = dataSource.index(categoryId);
        return data.toJavaList(JSONObject.class)
                .stream()
                .map(t -> new ProductDTO(t.getString("id"), t.getString("name")))
                .collect(Collectors.toList());
    }

    private static List<ProductLanguageDTO> listProductLanguage(DataSource dataSource, String productId) throws IOException, InterruptedException {
        JSONObject data = dataSource.getLang(productId);
        JSONArray list = data.getJSONArray("result");
        return list.toJavaList(JSONObject.class)
                .stream()
                .map(t -> new ProductLanguageDTO(t.getString("id"), t.getString("lang")))
                .collect(Collectors.toList());
    }

    private static List<FileDTO> listFile(DataSource dataSource, String productId, String langId) throws IOException, InterruptedException {
        JSONObject data = dataSource.getList(productId, langId);
        JSONArray list = data.getJSONArray("result");
        return list.toJavaList(JSONObject.class)
                .stream()
                .map(t -> {
                    FileDTO fileDTO = new FileDTO();
                    fileDTO.setId(t.getString("id"));
                    fileDTO.setName(t.getString("name"));
                    fileDTO.setPost(t.getString("post"));
                    fileDTO.setUrl(t.getString("url"));
                    return fileDTO;
                })
                .collect(Collectors.toList());
    }

    private static FileDetailDTO getDetail(DataSource dataSource, String fileId) throws IOException, InterruptedException {
        JSONObject data = dataSource.getProduct(fileId);
        JSONObject result = data.getJSONObject("result");
        FileDetailDTO fileDetailDTO = new FileDetailDTO();
        fileDetailDTO.setDownload(result.getString("DownLoad"));
        fileDetailDTO.setFileName(result.getString("FileName"));
        fileDetailDTO.setPostDateString(result.getString("PostDateString"));
        fileDetailDTO.setSha1(result.getString("SHA1"));
        fileDetailDTO.setSize(result.getString("size"));
        return fileDetailDTO;
    }

}
