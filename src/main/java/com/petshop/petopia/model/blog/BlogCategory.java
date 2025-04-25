package com.petshop.petopia.model.blog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "blog_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BlogCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bigCategoryID;

    private String name;
    private String description;

    @OneToMany(mappedBy = "blogCategory")
    private List<BlogPost> blogPosts;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}