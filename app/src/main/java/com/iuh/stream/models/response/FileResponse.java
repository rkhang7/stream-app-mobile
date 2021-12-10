package com.iuh.stream.models.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private String id;
    private int length;
    private int chunkSize;
    private Date uploadDate;
    private String filename;
    private String contentType;
}
