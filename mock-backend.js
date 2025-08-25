const express = require('express');
const cors = require('cors');

const app = express();
const PORT = 8081;

// CORS für alle Origins erlauben
app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: '*'
}));

app.use(express.json());

// Health Check
app.get('/api/health', (req, res) => {
  console.log('🎯 Health check requested');
  res.json({
    status: 'OK',
    message: 'Node.js Mock Backend is running!',
    timestamp: Date.now()
  });
});

// Mock Categories
app.get('/api/learning/categories', (req, res) => {
  console.log('🎯 Getting categories');
  const categories = [
    'Netzwerktechnik',
    'Programmierung',
    'Datenbanken',
    'Betriebssysteme',
    'Datenschutz'
  ];
  res.json(categories);
});

// Mock Question
app.get('/api/learning/next-question', (req, res) => {
  console.log('🎯 Getting next question');
  const question = {
    id: 1,
    questionText: 'Was ist TCP/IP?',
    options: [
      'Ein Übertragungsprotokoll',
      'Ein Betriebssystem',
      'Eine Programmiersprache',
      'Eine Datenbank'
    ],
    category: 'Netzwerktechnik',
    difficulty: 1
  };
  res.json(question);
});

// Mock Answer
app.post('/api/learning/answer', (req, res) => {
  console.log('🎯 Submitting answer:', req.body);
  const { answer } = req.body;
  const isCorrect = answer === 'Ein Übertragungsprotokoll';
  
  res.json({
    correct: isCorrect,
    correctAnswer: 'Ein Übertragungsprotokoll',
    explanation: 'TCP/IP ist ein Übertragungsprotokoll für Netzwerkkommunikation.',
    pointsEarned: isCorrect ? 10 : 0
  });
});

app.listen(PORT, () => {
  console.log(`🚀 Mock Backend running on http://localhost:${PORT}`);
  console.log('✅ Ready to serve Frontend requests!');
});
