package io.vilya.backup.itellyou;

import lombok.Data;

/**
 * @author vilya
 */
@Data
public class FileDetailDTO {

    private String download;

    private String fileName;

    private String postDateString;

    private String sha1;

    private String size;

}
