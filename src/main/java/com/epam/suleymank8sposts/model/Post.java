package com.epam.suleymank8sposts.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column
    private long authorId;

    @Column
    private String text;

    @Column
    private Date postedAt;
}
