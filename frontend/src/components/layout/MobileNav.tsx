import { NavLink } from 'react-router-dom';
import { Home, BookOpen, FileQuestion, Trophy } from 'lucide-react';

export const MobileNav = () => {
  const navItems = [
    { to: '/dashboard', icon: Home, label: 'Home' },
    { to: '/learning', icon: BookOpen, label: 'Lernen' },
    { to: '/exam', icon: FileQuestion, label: 'Prüfung' },
    { to: '/progress', icon: Trophy, label: 'Fortschritt' },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200">
      <nav className="flex justify-around py-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex flex-col items-center space-y-1 px-3 py-2 ${
                isActive ? 'text-primary-600' : 'text-gray-600'
              }`
            }
          >
            <item.icon className="w-6 h-6" />
            <span className="text-xs">{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  );
};