import React, { useState, useEffect } from 'react';
import { 
  Target, 
  TrendingUp, 
  Clock, 
  Award, 
  AlertTriangle,
  CheckCircle,
  BookOpen,
  Zap,
  Info,
  Play,
  Pause
} from 'lucide-react';
import api from '../services/api';

interface StrategyStatus {
  strategy_active: boolean;
  focus_areas: {
    [key: string]: {
      priority: number;
      progress: number;
    };
  };
  estimated_success_rate: number;
  recommended_study_hours: number;
  completed_study_hours: number;
}

interface ExamPhase {
  number: number;
  name: string;
  minutes: number;
  tasks: string[];
  targetPoints: number;
}

const StrategyPanel: React.FC = () => {
  const [isActive, setIsActive] = useState(false);
  const [status, setStatus] = useState<StrategyStatus | null>(null);
  const [examStrategy, setExamStrategy] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [showInfo, setShowInfo] = useState(false);

  useEffect(() => {
    fetchStatus();
  }, []);

  const fetchStatus = async () => {
    try {
      const response = await api.get('/strategy/status');
      setStatus(response.data);
      setIsActive(response.data.strategy_active);
    } catch (error) {
      console.error('Error fetching strategy status:', error);
    }
  };

  const fetchExamStrategy = async () => {
    try {
      const response = await api.get('/strategy/exam-time-strategy');
      setExamStrategy(response.data);
    } catch (error) {
      console.error('Error fetching exam strategy:', error);
    }
  };

  const toggleStrategy = async () => {
    setLoading(true);
    try {
      if (isActive) {
        await api.post('/strategy/deactivate');
        setIsActive(false);
      } else {
        await api.post('/strategy/activate');
        setIsActive(true);
        fetchExamStrategy();
      }
      fetchStatus();
    } catch (error) {
      console.error('Error toggling strategy:', error);
    } finally {
      setLoading(false);
    }
  };

  const getPriorityColor = (priority: number) => {
    switch(priority) {
      case 1: return 'text-red-600 bg-red-100';
      case 2: return 'text-yellow-600 bg-yellow-100';
      case 3: return 'text-green-600 bg-green-100';
      default: return 'text-gray-600 bg-gray-100';
    }
  };

  const getPriorityLabel = (priority: number) => {
    switch(priority) {
      case 1: return 'KRITISCH';
      case 2: return 'WICHTIG';
      case 3: return 'PUFFER';
      default: return 'OPTIONAL';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <Target className="w-8 h-8 text-indigo-600" />
          <div>
            <h2 className="text-2xl font-bold text-gray-900">
              60%-Erfolgsstrategie
            </h2>
            <p className="text-sm text-gray-600">
              Post-RAID Prüfungsstrategie für sicheres Bestehen
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-3">
          <button
            onClick={() => setShowInfo(!showInfo)}
            className="p-2 text-gray-600 hover:text-indigo-600 transition-colors"
          >
            <Info className="w-6 h-6" />
          </button>
          
          <button
            onClick={toggleStrategy}
            disabled={loading}
            className={`
              px-6 py-3 rounded-lg font-semibold transition-all
              flex items-center gap-2
              ${isActive 
                ? 'bg-red-500 hover:bg-red-600 text-white' 
                : 'bg-indigo-600 hover:bg-indigo-700 text-white'}
              ${loading ? 'opacity-50 cursor-not-allowed' : ''}
            `}
          >
            {isActive ? (
              <>
                <Pause className="w-5 h-5" />
                Strategie deaktivieren
              </>
            ) : (
              <>
                <Play className="w-5 h-5" />
                Strategie aktivieren
              </>
            )}
          </button>
        </div>
      </div>

      {/* Info Box */}
      {showInfo && (
        <div className="mb-6 p-4 bg-blue-50 border-2 border-blue-200 rounded-lg">
          <h3 className="font-semibold text-blue-900 mb-2">Was ist neu?</h3>
          <ul className="space-y-1 text-sm text-blue-800">
            <li>❌ RAID-Systeme sind NICHT mehr prüfungsrelevant</li>
            <li>✅ IPv6 und PLY-Dateiformate sind NEU</li>
            <li>✅ Fokus auf Netzplantechnik (25 Punkte)</li>
            <li>✅ PowerShell-Debugging (8 Punkte)</li>
            <li>✅ OSI-Modell praktisch anwenden</li>
          </ul>
        </div>
      )}

      {/* Status Overview */}
      {status && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="p-4 bg-gradient-to-br from-green-50 to-green-100 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-green-600 font-medium">Erfolgschance</p>
                <p className="text-2xl font-bold text-green-900">
                  {status.estimated_success_rate}%
                </p>
              </div>
              <TrendingUp className="w-8 h-8 text-green-500" />
            </div>
          </div>

          <div className="p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-blue-600 font-medium">Lernfortschritt</p>
                <p className="text-2xl font-bold text-blue-900">
                  {status.completed_study_hours}h / {status.recommended_study_hours}h
                </p>
              </div>
              <Clock className="w-8 h-8 text-blue-500" />
            </div>
          </div>

          <div className="p-4 bg-gradient-to-br from-purple-50 to-purple-100 rounded-lg">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-purple-600 font-medium">Zielpunkte</p>
                <p className="text-2xl font-bold text-purple-900">
                  60-65 Punkte
                </p>
              </div>
              <Award className="w-8 h-8 text-purple-500" />
            </div>
          </div>
        </div>
      )}

      {/* Focus Areas */}
      {isActive && status && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <BookOpen className="w-5 h-5" />
            Fokus-Bereiche nach Priorität
          </h3>
          
          <div className="space-y-3">
            {Object.entries(status.focus_areas)
              .sort(([,a], [,b]) => a.priority - b.priority)
              .map(([category, data]) => (
                <div key={category} className="border rounded-lg p-3">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-3">
                      <span className={`px-2 py-1 rounded text-xs font-semibold ${getPriorityColor(data.priority)}`}>
                        {getPriorityLabel(data.priority)}
                      </span>
                      <span className="font-medium text-gray-900">{category}</span>
                    </div>
                    <span className="text-sm text-gray-600">{data.progress}%</span>
                  </div>
                  
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div 
                      className={`h-2 rounded-full transition-all ${
                        data.progress >= 60 ? 'bg-green-500' : 
                        data.progress >= 40 ? 'bg-yellow-500' : 'bg-red-500'
                      }`}
                      style={{ width: `${data.progress}%` }}
                    />
                  </div>
                </div>
              ))}
          </div>
        </div>
      )}

      {/* Exam Time Strategy */}
      {isActive && examStrategy && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-3 flex items-center gap-2">
            <Clock className="w-5 h-5" />
            Zeitmanagement-Strategie (90 Minuten)
          </h3>
          
          <div className="space-y-2">
            {examStrategy.phases.map((phase: ExamPhase) => (
              <div key={phase.number} className="flex items-center gap-4 p-3 bg-gray-50 rounded-lg">
                <div className="flex-shrink-0 w-12 h-12 bg-indigo-600 text-white rounded-full flex items-center justify-center font-bold">
                  {phase.number}
                </div>
                <div className="flex-grow">
                  <div className="font-medium text-gray-900">
                    {phase.name} ({phase.minutes} Min)
                  </div>
                  <div className="text-sm text-gray-600">
                    {phase.tasks.join(', ')}
                  </div>
                </div>
                <div className="flex-shrink-0 text-right">
                  <div className="font-semibold text-indigo-600">
                    +{phase.targetPoints} Punkte
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Quick Actions */}
      {isActive && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button
            onClick={() => window.location.href = '/learning?mode=strategy'}
            className="p-4 border-2 border-indigo-600 text-indigo-600 rounded-lg hover:bg-indigo-50 transition-colors flex items-center justify-center gap-2"
          >
            <Target className="w-5 h-5" />
            <span className="font-semibold">Strategiebasiertes Lernen starten</span>
          </button>
          
          <button
            onClick={() => window.location.href = '/learning?mode=quickwin'}
            className="p-4 border-2 border-green-600 text-green-600 rounded-lg hover:bg-green-50 transition-colors flex items-center justify-center gap-2"
          >
            <Zap className="w-5 h-5" />
            <span className="font-semibold">Quick-Wins (30 Punkte)</span>
          </button>
        </div>
      )}

      {/* Success Alert */}
      {isActive && (
        <div className="mt-6 p-4 bg-green-50 border-l-4 border-green-500 rounded">
          <div className="flex items-start gap-3">
            <CheckCircle className="w-5 h-5 text-green-600 mt-0.5" />
            <div>
              <p className="font-semibold text-green-900">
                Strategie aktiv!
              </p>
              <p className="text-sm text-green-700 mt-1">
                Ihre Fragen werden jetzt nach der 60%-Erfolgsstrategie priorisiert. 
                Fokus liegt auf Netzplantechnik, PowerShell, IPv6 und OSI-Modell.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StrategyPanel;