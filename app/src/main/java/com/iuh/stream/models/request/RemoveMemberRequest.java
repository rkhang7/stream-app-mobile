package com.iuh.stream.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveMemberRequest {
    private String id;
    private String memberId;
}
