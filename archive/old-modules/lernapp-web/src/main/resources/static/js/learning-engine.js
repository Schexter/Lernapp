// Learning Engine JavaScript für Frontend
// Erstellt von Hans Hahn - Alle Rechte vorbehalten

class LearningEngine {
    constructor() {
        this.sessionId = null;
        this.currentQuestion = null;
        this.questions = [];
        this.score = 0;
        this.answered = 0;
    }

    async startSession(config = {}) {
        try {
            const response = await fetch('/api/learning-engine/session/start', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    category: config.category || 'all',
                    difficulty: config.difficulty || 'mixed',
                    count: config.count || 10
                })
            });

            if (!response.ok) {
                console.warn('Session start failed, using demo mode');
                return this.startDemoSession(config);
            }

            const data = await response.json();
            this.sessionId = data.sessionId;
            this.questions = data.questions || [];
            return data;
        } catch (error) {
            console.error('Error starting session:', error);
            return this.startDemoSession(config);
        }
    }

    startDemoSession(config) {
        // Demo-Session ohne Backend
        this.sessionId = 'demo-' + Date.now();
        this.questions = this.generateDemoQuestions(config.count || 10);
        return {
            sessionId: this.sessionId,
            status: 'demo',
            questions: this.questions
        };
    }

    generateDemoQuestions(count) {
        const questions = [];
        const templates = [
            {
                text: "Was ist der Unterschied zwischen TCP und UDP?",
                options: [
                    "TCP ist verbindungsorientiert, UDP verbindungslos",
                    "UDP ist verbindungsorientiert, TCP verbindungslos",
                    "Beide sind verbindungsorientiert",
                    "Beide sind verbindungslos"
                ],
                correct: 0
            },
            {
                text: "Welche OSI-Schicht ist für Routing zuständig?",
                options: [
                    "Schicht 2 (Data Link)",
                    "Schicht 3 (Network)",
                    "Schicht 4 (Transport)",
                    "Schicht 7 (Application)"
                ],
                correct: 1
            },
            {
                text: "Was bedeutet RAID 5?",
                options: [
                    "Spiegelung ohne Parität",
                    "Striping ohne Redundanz",
                    "Striping mit verteilter Parität",
                    "Doppelte Spiegelung"
                ],
                correct: 2
            }
        ];

        for (let i = 0; i < count; i++) {
            const template = templates[i % templates.length];
            questions.push({
                id: i + 1,
                ...template
            });
        }

        return questions;
    }

    async submitAnswer(questionId, answerIndex) {
        try {
            const response = await fetch('/api/learning-engine/answer', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    sessionId: this.sessionId,
                    questionId: questionId,
                    answer: answerIndex
                })
            });

            if (!response.ok) {
                return this.processDemoAnswer(questionId, answerIndex);
            }

            return await response.json();
        } catch (error) {
            return this.processDemoAnswer(questionId, answerIndex);
        }
    }

    processDemoAnswer(questionId, answerIndex) {
        const question = this.questions.find(q => q.id === questionId);
        const correct = question && question.correct === answerIndex;
        
        if (correct) this.score++;
        this.answered++;

        return {
            correct: correct,
            explanation: correct ? "Richtig! Gut gemacht!" : "Leider falsch. Die richtige Antwort wäre Option " + (question.correct + 1),
            score: this.score,
            progress: Math.round((this.answered / this.questions.length) * 100)
        };
    }

    getNextQuestion() {
        const unanswered = this.questions.find((q, index) => index >= this.answered);
        return unanswered || null;
    }

    getProgress() {
        return {
            total: this.questions.length,
            answered: this.answered,
            correct: this.score,
            percentage: Math.round((this.answered / this.questions.length) * 100)
        };
    }
}

// Auto-initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    window.learningEngine = new LearningEngine();
    
    // Start button handler
    const startButton = document.querySelector('.session-start-btn, button[onclick*="session"]');
    if (startButton) {
        startButton.addEventListener('click', async function() {
            const categorySelect = document.querySelector('select');
            const category = categorySelect ? categorySelect.value : 'all';
            
            const session = await window.learningEngine.startSession({
                category: category,
                count: 10
            });
            
            console.log('Session started:', session);
            
            // Show first question
            const firstQuestion = window.learningEngine.getNextQuestion();
            if (firstQuestion) {
                displayQuestion(firstQuestion);
            }
        });
    }
});

function displayQuestion(question) {
    // Finde Container für Frage
    const container = document.querySelector('.question-container, .modal-body, main');
    if (!container) return;

    const html = `
        <div class="question-card">
            <h3>Frage ${question.id}</h3>
            <p>${question.text}</p>
            <div class="options">
                ${question.options.map((opt, i) => `
                    <button class="option-btn" onclick="selectAnswer(${question.id}, ${i})">
                        ${String.fromCharCode(65 + i)}) ${opt}
                    </button>
                `).join('')}
            </div>
        </div>
    `;
    
    container.innerHTML = html;
}

async function selectAnswer(questionId, answerIndex) {
    const result = await window.learningEngine.submitAnswer(questionId, answerIndex);
    
    // Zeige Feedback
    alert(result.explanation);
    
    // Nächste Frage
    const nextQuestion = window.learningEngine.getNextQuestion();
    if (nextQuestion) {
        displayQuestion(nextQuestion);
    } else {
        // Quiz beendet
        const progress = window.learningEngine.getProgress();
        alert(`Quiz beendet! Du hast ${progress.correct} von ${progress.total} Fragen richtig beantwortet!`);
    }
}