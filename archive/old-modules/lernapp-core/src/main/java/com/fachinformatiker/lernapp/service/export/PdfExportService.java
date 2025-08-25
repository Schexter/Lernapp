package com.fachinformatiker.lernapp.service.export;

import com.fachinformatiker.lernapp.service.progress.ProgressTrackingService;
import com.fachinformatiker.lernapp.service.progress.ProgressTrackingService.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

/**
 * Service for exporting progress reports as PDF
 * Note: This is a simplified version that generates HTML for PDF conversion
 * In production, you would use a library like iText or Apache PDFBox
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PdfExportService {
    
    private final ProgressTrackingService progressTrackingService;
    
    /**
     * Generate a PDF progress report for a user
     */
    public byte[] generateProgressReport(Long userId) {
        log.info("Generating PDF progress report for user {}", userId);
        
        try {
            // Get user statistics
            DetailedStatistics stats = progressTrackingService.getDetailedStatistics(userId);
            LearningStreak streak = progressTrackingService.getStreak(userId);
            LearningPace pace = progressTrackingService.analyzeLearningPace(userId);
            
            // Generate HTML content
            String htmlContent = generateHtmlReport(stats, streak, pace);
            
            // Convert HTML to PDF (simplified - returns HTML as bytes)
            // In production, use a proper HTML to PDF converter
            return htmlContent.getBytes();
            
        } catch (Exception e) {
            log.error("Error generating PDF report for user {}", userId, e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    /**
     * Generate HTML report content
     */
    private String generateHtmlReport(DetailedStatistics stats, LearningStreak streak, LearningPace pace) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html lang='de'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Lernfortschritt Bericht</title>");
        html.append("<style>");
        html.append(getCssStyles());
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>📚 Fachinformatiker Lernapp</h1>");
        html.append("<h2>Persönlicher Fortschrittsbericht</h2>");
        html.append("<p class='date'>Erstellt am: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("</p>");
        html.append("</div>");
        
        // Summary Section
        html.append("<div class='section'>");
        html.append("<h3>Zusammenfassung</h3>");
        html.append("<div class='stats-grid'>");
        
        html.append("<div class='stat-box'>");
        html.append("<div class='stat-value'>").append(stats.getTotalQuestionsAttempted()).append("</div>");
        html.append("<div class='stat-label'>Fragen beantwortet</div>");
        html.append("</div>");
        
        html.append("<div class='stat-box'>");
        html.append("<div class='stat-value'>").append(Math.round(stats.getOverallSuccessRate() * 100)).append("%</div>");
        html.append("<div class='stat-label'>Erfolgsquote</div>");
        html.append("</div>");
        
        html.append("<div class='stat-box'>");
        html.append("<div class='stat-value'>").append(stats.getMasteredQuestions()).append("</div>");
        html.append("<div class='stat-label'>Gemeisterte Fragen</div>");
        html.append("</div>");
        
        html.append("<div class='stat-box'>");
        html.append("<div class='stat-value'>").append(stats.getTotalStudyTimeMinutes()).append(" min</div>");
        html.append("<div class='stat-label'>Gesamte Lernzeit</div>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</div>");
        
        // Learning Streak Section
        html.append("<div class='section'>");
        html.append("<h3>Lernstreak</h3>");
        html.append("<p>Aktueller Streak: <strong>").append(streak.getCurrentStreak()).append(" Tage</strong></p>");
        html.append("<p>Längster Streak: <strong>").append(streak.getLongestStreak()).append(" Tage</strong></p>");
        html.append("<p>Insgesamt aktive Tage: <strong>").append(streak.getTotalActiveDays()).append("</strong></p>");
        html.append("</div>");
        
        // Learning Pace Section
        html.append("<div class='section'>");
        html.append("<h3>Lerntempo</h3>");
        html.append("<p>Kategorie: <strong>").append(pace.getPaceCategory()).append("</strong></p>");
        html.append("<p>Durchschnitt pro Tag: <strong>").append(Math.round(pace.getAverageQuestionsPerDay())).append(" Fragen</strong></p>");
        html.append("<p>Aktive Tage (letzte 30): <strong>").append(pace.getActiveDaysLast30()).append("</strong></p>");
        html.append("<p>Empfehlung: ").append(pace.getRecommendation()).append("</p>");
        html.append("</div>");
        
        // Weak Areas Section
        if (stats.getWeakAreas() != null && !stats.getWeakAreas().isEmpty()) {
            html.append("<div class='section'>");
            html.append("<h3>Verbesserungsbereiche</h3>");
            html.append("<ul>");
            for (String area : stats.getWeakAreas()) {
                html.append("<li>").append(area).append("</li>");
            }
            html.append("</ul>");
            html.append("</div>");
        }
        
        // Mastery Distribution
        if (stats.getMasteryDistribution() != null && !stats.getMasteryDistribution().isEmpty()) {
            html.append("<div class='section'>");
            html.append("<h3>Verteilung nach Meisterschaftsgrad</h3>");
            html.append("<table>");
            html.append("<tr><th>Box</th><th>Anzahl Fragen</th></tr>");
            for (Map.Entry<String, Long> entry : stats.getMasteryDistribution().entrySet()) {
                html.append("<tr>");
                html.append("<td>Box ").append(entry.getKey()).append("</td>");
                html.append("<td>").append(entry.getValue()).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
            html.append("</div>");
        }
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Dieser Bericht wurde automatisch generiert von der Fachinformatiker Lernapp</p>");
        html.append("</div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Get CSS styles for the PDF report
     */
    private String getCssStyles() {
        return """
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 20px;
                color: #333;
            }
            .header {
                text-align: center;
                border-bottom: 2px solid #667eea;
                padding-bottom: 20px;
                margin-bottom: 30px;
            }
            h1 {
                color: #667eea;
                margin: 0;
            }
            h2 {
                color: #4a5568;
                margin: 10px 0;
            }
            h3 {
                color: #667eea;
                border-bottom: 1px solid #e2e8f0;
                padding-bottom: 10px;
            }
            .date {
                color: #718096;
                font-size: 14px;
            }
            .section {
                margin-bottom: 30px;
                page-break-inside: avoid;
            }
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
                margin: 20px 0;
            }
            .stat-box {
                background: #f7fafc;
                padding: 15px;
                border-radius: 8px;
                text-align: center;
            }
            .stat-value {
                font-size: 24px;
                font-weight: bold;
                color: #667eea;
            }
            .stat-label {
                font-size: 12px;
                color: #718096;
                margin-top: 5px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 15px 0;
            }
            th, td {
                padding: 10px;
                text-align: left;
                border-bottom: 1px solid #e2e8f0;
            }
            th {
                background: #f7fafc;
                font-weight: bold;
            }
            ul {
                margin: 15px 0;
                padding-left: 25px;
            }
            li {
                margin: 8px 0;
            }
            .footer {
                margin-top: 50px;
                padding-top: 20px;
                border-top: 1px solid #e2e8f0;
                text-align: center;
                font-size: 12px;
                color: #718096;
            }
            @media print {
                body {
                    margin: 0;
                    padding: 15px;
                }
                .section {
                    page-break-inside: avoid;
                }
            }
        """;
    }
    
    /**
     * Generate a certificate of completion
     */
    public byte[] generateCertificate(Long userId, String courseName) {
        log.info("Generating certificate for user {} - course: {}", userId, courseName);
        
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Zertifikat</title>");
        html.append("<style>");
        html.append(getCertificateStyles());
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        html.append("<div class='certificate'>");
        html.append("<div class='border'>");
        html.append("<div class='content'>");
        
        html.append("<h1>ZERTIFIKAT</h1>");
        html.append("<p class='subtitle'>Dies bestätigt, dass</p>");
        html.append("<h2 class='name'>Max Mustermann</h2>");
        html.append("<p class='achievement'>erfolgreich den Kurs</p>");
        html.append("<h3 class='course'>").append(courseName).append("</h3>");
        html.append("<p class='achievement'>abgeschlossen hat</p>");
        
        html.append("<div class='date'>").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy"))).append("</div>");
        
        html.append("<div class='signatures'>");
        html.append("<div class='signature'>");
        html.append("<div class='line'></div>");
        html.append("<p>Kursleiter</p>");
        html.append("</div>");
        html.append("<div class='signature'>");
        html.append("<div class='line'></div>");
        html.append("<p>Fachinformatiker Lernapp</p>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        html.append("</body>");
        html.append("</html>");
        
        return html.toString().getBytes();
    }
    
    /**
     * Get CSS styles for certificates
     */
    private String getCertificateStyles() {
        return """
            body {
                margin: 0;
                padding: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                background: #f3f4f6;
            }
            .certificate {
                width: 800px;
                height: 600px;
                background: white;
                box-shadow: 0 0 30px rgba(0, 0, 0, 0.1);
            }
            .border {
                margin: 20px;
                height: calc(100% - 40px);
                border: 3px solid #667eea;
                position: relative;
            }
            .content {
                padding: 60px;
                text-align: center;
                height: calc(100% - 120px);
                display: flex;
                flex-direction: column;
                justify-content: center;
            }
            h1 {
                font-size: 48px;
                color: #667eea;
                margin: 0 0 20px 0;
                font-weight: bold;
                letter-spacing: 3px;
            }
            .subtitle {
                font-size: 18px;
                color: #4a5568;
                margin: 20px 0 10px 0;
            }
            .name {
                font-size: 36px;
                color: #2d3748;
                margin: 10px 0;
                font-weight: bold;
            }
            .achievement {
                font-size: 16px;
                color: #4a5568;
                margin: 15px 0;
            }
            .course {
                font-size: 28px;
                color: #667eea;
                margin: 10px 0;
            }
            .date {
                margin: 30px 0;
                font-size: 16px;
                color: #718096;
            }
            .signatures {
                display: flex;
                justify-content: space-around;
                margin-top: 50px;
            }
            .signature {
                text-align: center;
            }
            .line {
                width: 200px;
                height: 1px;
                background: #2d3748;
                margin: 0 auto 10px;
            }
            .signature p {
                font-size: 14px;
                color: #4a5568;
                margin: 0;
            }
            @media print {
                body {
                    background: white;
                }
                .certificate {
                    box-shadow: none;
                }
            }
        """;
    }
}