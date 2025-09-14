package com.backend.AI.controller;

import lombok.Data;

@Data
public class DocumentRequest {
    private String title;
    private String content;
    private String documentType;
}