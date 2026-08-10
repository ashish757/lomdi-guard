package com.lomdi.dsih.domain.usecase

import com.lomdi.dsih.data.model.SmsAnalysisResult

/**
 * Hybrid Rule-Based + Edge AI SMS analysis engine.
 */
object SmsAnalyzer {

    /**
     * Entry point for SMS analysis following the Decision Tree flow.
     */
    fun analyze(sender: String, body: String): SmsAnalysisResult {
        var requiresEdgeAi = false
        var senderRisk: String
        var linkRisk: String
        var contentRisk: String
        var score = 0

        // Node 1: Institutional Verification
        val isInstitutional = sender.length < 10 && sender.contains("-")
        if (isInstitutional) {
            senderRisk = "Sender matches institutional format ($sender). Applying banking template rules."
            score += 5 
        } else {
            senderRisk = "Sender '$sender' is an unverified standard mobile number, not a registered bank entity."
            score += 25
        }

        // Node 2: Categorized Link Analysis
        val links = SmsLinkExtractor.extractLinks(body)
        if (links.isNotEmpty()) {
            requiresEdgeAi = true
            val classifications = links.map { SmsLinkExtractor.classifyLink(it) }
            val highestRiskLink = classifications.maxByOrNull { it.riskScore }
            
            linkRisk = highestRiskLink?.description ?: "Message contains suspicious payloads."
            score += (highestRiskLink?.riskScore ?: 40)
        } else {
            linkRisk = "No suspicious links or URLs detected in the message body."
        }

        // Node 3: Intent & Keyword Heuristics
        val urgencyKeywords = listOf("otp", "urgent", "debited", "click", "blocked", "kyc", "won", "reward")
        val foundKeywords = urgencyKeywords.filter { body.lowercase().contains(it) }
        
        if (foundKeywords.isNotEmpty()) {
            requiresEdgeAi = true
            val keywordsStr = foundKeywords.joinToString(", ")
            contentRisk = "High-urgency keywords detected: $keywordsStr."
            score += 30
        } else {
            contentRisk = "No immediate urgency or transactional trigger keywords found via standard rules."
        }

        // Final Edge AI Pass (Mock)
        if (requiresEdgeAi || score > 40) {
            val aiRisk = runEdgeAiNLP(body)
            score += aiRisk
            val aiAssessment = if (aiRisk > 10) "High Risk Intent" else "Low Risk Intent"
            contentRisk += " | Edge AI NLP: $aiAssessment."
        }

        return SmsAnalysisResult(
            totalRiskScore = score.coerceAtMost(100),
            senderRisk = senderRisk,
            linkRisk = linkRisk,
            contentRisk = contentRisk,
            requiresEdgeAi = requiresEdgeAi
        )
    }

    private fun runEdgeAiNLP(body: String): Int {
        return if (body.length > 100 && body.contains("http")) 20 else 5
    }
}
