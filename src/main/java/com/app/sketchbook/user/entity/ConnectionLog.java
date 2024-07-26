package com.app.sketchbook.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Setter
@Getter
@Entity
public class ConnectionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private SketchUser user;

    @Column(columnDefinition = "NUMBER(1)")
    private boolean success;
    private Date connectedTime;
    private String ip;
    private String region;
    private String browser;
}
