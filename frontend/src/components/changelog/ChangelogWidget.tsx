import React from 'react';
import { Sparkles, CheckCircle, Bug, Wrench, Info, Calendar } from 'lucide-react';

interface ChangelogEntry {
  version: string;
  date: string;
  type: 'feature' | 'bugfix' | 'improvement' | 'info';
  title: string;
  description: string;
  highlights?: string[];
}

const changelog: ChangelogEntry[] = [
  {
    version: "1.2",
    date: "2025-01-21",
    type: "feature",
    title: "Prüfungsmodus Toggle",
    description: "Der Prüfungsmodus kann jetzt ein- und ausgeschaltet werden",
    highlights: [
      "Button umbenannt zu 'Nur Prüfungsorientiert lernen'",
      "Toggle zwischen prüfungsorientierten und allen Fragen",
      "Dynamische UI-Anpassung je nach Modus"
    ]
  },
  {
    version: "1.1",
    date: "2025-01-21",
    type: "bugfix",
    title: "Kleine Bugs zerdrückt",
    description: "Verschiedene Fehlerbehebungen und Verbesserungen",
    highlights: [
      "Login/Registrierung mit detaillierten Fehlermeldungen",
      "Kategorie-Filter funktioniert jetzt korrekt",
      "Mock-Daten entfernt - nur echte Daten"
    ]
  },
  {
    version: "1.0",
    date: "2025-01-20",
    type: "feature",
    title: "App Launch",
    description: "Erste Version der Lernapp mit allen Grundfunktionen",
    highlights: [
      "525+ Prüfungsfragen importiert",
      "Lern- und Prüfungsmodus",
      "Fortschrittsverfolgung",
      "Dashboard mit Statistiken"
    ]
  }
];

interface ChangelogWidgetProps {
  compact?: boolean;
  maxEntries?: number;
}

export const ChangelogWidget: React.FC<ChangelogWidgetProps> = ({ 
  compact = false, 
  maxEntries = 3 
}) => {
  const getIcon = (type: ChangelogEntry['type']) => {
    switch(type) {
      case 'feature':
        return <Sparkles className="w-5 h-5 text-green-600" />;
      case 'bugfix':
        return <Bug className="w-5 h-5 text-red-600" />;
      case 'improvement':
        return <Wrench className="w-5 h-5 text-blue-600" />;
      case 'info':
        return <Info className="w-5 h-5 text-gray-600" />;
    }
  };

  const getTypeLabel = (type: ChangelogEntry['type']) => {
    switch(type) {
      case 'feature':
        return 'Neu';
      case 'bugfix':
        return 'Behoben';
      case 'improvement':
        return 'Verbessert';
      case 'info':
        return 'Info';
    }
  };

  const getTypeColor = (type: ChangelogEntry['type']) => {
    switch(type) {
      case 'feature':
        return 'bg-green-100 text-green-800 border-green-200';
      case 'bugfix':
        return 'bg-red-100 text-red-800 border-red-200';
      case 'improvement':
        return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'info':
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const entriesToShow = changelog.slice(0, maxEntries);

  if (compact) {
    return (
      <div className="bg-gradient-to-br from-indigo-50 to-purple-50 rounded-lg p-4 border border-indigo-200">
        <div className="flex items-center gap-2 mb-3">
          <Sparkles className="w-5 h-5 text-indigo-600" />
          <h3 className="text-lg font-semibold text-gray-900">Was ist neu?</h3>
        </div>
        <div className="space-y-2">
          {entriesToShow.map((entry, index) => (
            <div key={index} className="flex items-start gap-2">
              {getIcon(entry.type)}
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-1">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${getTypeColor(entry.type)}`}>
                    v{entry.version}
                  </span>
                  <span className="text-xs text-gray-500">{entry.date}</span>
                </div>
                <p className="text-sm font-medium text-gray-900">{entry.title}</p>
                <p className="text-xs text-gray-600 mt-0.5">{entry.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-lg p-6">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-lg">
            <Sparkles className="w-6 h-6 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Was ist neu?</h2>
            <p className="text-sm text-gray-600">Die neuesten Updates und Verbesserungen</p>
          </div>
        </div>
      </div>

      <div className="space-y-4">
        {entriesToShow.map((entry, index) => (
          <div key={index} className="border-l-4 border-indigo-500 pl-4 py-2">
            <div className="flex items-start justify-between mb-2">
              <div className="flex items-center gap-3">
                {getIcon(entry.type)}
                <div>
                  <div className="flex items-center gap-2">
                    <h3 className="text-lg font-semibold text-gray-900">{entry.title}</h3>
                    <span className={`text-xs px-2 py-1 rounded-full font-medium border ${getTypeColor(entry.type)}`}>
                      {getTypeLabel(entry.type)}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-sm font-medium text-indigo-600">Version {entry.version}</span>
                    <span className="text-gray-400">•</span>
                    <div className="flex items-center gap-1 text-sm text-gray-500">
                      <Calendar className="w-3 h-3" />
                      {entry.date}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <p className="text-gray-700 mb-2">{entry.description}</p>
            
            {entry.highlights && entry.highlights.length > 0 && (
              <ul className="space-y-1">
                {entry.highlights.map((highlight, hIndex) => (
                  <li key={hIndex} className="flex items-start gap-2 text-sm text-gray-600">
                    <CheckCircle className="w-4 h-4 text-green-500 flex-shrink-0 mt-0.5" />
                    <span>{highlight}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>
        ))}
      </div>

      <div className="mt-6 pt-4 border-t border-gray-200">
        <p className="text-sm text-gray-500 text-center">
          Wir arbeiten ständig daran, die App zu verbessern. Bleiben Sie dran für weitere Updates!
        </p>
      </div>
    </div>
  );
};

export default ChangelogWidget;