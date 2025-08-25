import { NavLink } from 'react-router-dom';
import { Home, BookOpen, FileQuestion, Trophy, Settings, X, Shield } from 'lucide-react';
import { useAuthStore } from '../../stores/authStore';

interface SidebarProps {
  mobile?: boolean;
  onClose?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobile, onClose }) => {
  const { user } = useAuthStore();
  const isAdmin = user?.roles.includes('ADMIN');

  const navItems = [
    { to: '/dashboard', icon: Home, label: 'Dashboard' },
    { to: '/learning', icon: BookOpen, label: 'Lernen' },
    { to: '/exam', icon: FileQuestion, label: 'Prüfung' },
    { to: '/progress', icon: Trophy, label: 'Fortschritt' },
    { to: '/profile', icon: Settings, label: 'Profil' },
  ];

  if (isAdmin) {
    navItems.push({ to: '/admin', icon: Shield, label: 'Admin' });
  }

  return (
    <div className="flex flex-col h-full bg-white border-r border-gray-200">
      {mobile && (
        <div className="flex items-center justify-between p-4 border-b border-gray-200">
          <span className="text-lg font-semibold">Navigation</span>
          <button
            onClick={onClose}
            className="p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>
      )}
      
      <nav className="flex-1 px-4 py-6 space-y-1">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center space-x-3 px-3 py-2 rounded-lg transition-colors ${
                isActive
                  ? 'bg-primary-50 text-primary-600'
                  : 'text-gray-700 hover:bg-gray-100'
              }`
            }
            onClick={mobile ? onClose : undefined}
          >
            <item.icon className="w-5 h-5" />
            <span className="font-medium">{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  );
};