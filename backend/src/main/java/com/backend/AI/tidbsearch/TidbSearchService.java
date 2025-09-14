package com.backend.AI.tidbsearch;


import org.springframework.stereotype.Service;

@Service
public class TidbSearchService {

    /**
     * Prototype function: in future this will query TiDB.
     * For now it just returns a static response.
     */
    public String searchRequiredData(String sessionId, String prompt) {
        // later: run SQL queries or B+ tree lookups here
        return "TiDB successfully responded";
    }
}

