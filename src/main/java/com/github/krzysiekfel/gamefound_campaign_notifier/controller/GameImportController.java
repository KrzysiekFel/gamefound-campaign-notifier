package com.github.krzysiekfel.gamefound_campaign_notifier.controller;

import com.github.krzysiekfel.gamefound_campaign_notifier.service.GameCsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// TODO: zabezpieczyć ten endpoint
@RestController
@RequestMapping("/admin/games")
public class GameImportController {

    private final GameCsvImportService gameCsvImportService;

    public GameImportController(GameCsvImportService gameCsvImportService) {
        this.gameCsvImportService = gameCsvImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<GameCsvImportService.GameImportSummary> importGames(
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(gameCsvImportService.importFromCsv(file.getInputStream()));
    }
}
