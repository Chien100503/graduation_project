package com.petshop.petopia.dto.response.user;

import com.petshop.petopia.component.Global;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Integer id;
    private String name;
    private Global.ItemType itemType;
    private String imageUrl;
}
