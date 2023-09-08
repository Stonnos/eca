package eca.client.dto;

import lombok.Data;

/**
 * Upload instances response dto.
 *
 * @author Roman Batygin
 */
@Data
public class UploadInstancesResponseDto {


    /**
     * Instances uuid
     */
    private String uuid;

    /**
     * Instances file MD5 hash sum
     */
    private String md5Hash;

    /**
     * Expire at date
     */
    private String expireAt;
}
