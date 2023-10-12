package com.thinkpalm.ChatApplication.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "message_history")
public class MessageHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "message_id")
    private MessageModel message;

    @Column
    private String edited_content;

    @ManyToOne
    @JoinColumn(name = "edited_by")
    private UserModel user;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp edited_at;

}
