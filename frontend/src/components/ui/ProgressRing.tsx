interface ProgressRingProps {
  progress: number; // 0-100
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

export const ProgressRing: React.FC<ProgressRingProps> = ({ 
  progress, 
  size = 'md', 
  className = '' 
}) => {
  const sizeClasses = {
    sm: 'w-16 h-16',
    md: 'w-24 h-24', 
    lg: 'w-32 h-32'
  };
  
  const radius = size === 'sm' ? 28 : size === 'lg' ? 58 : 44;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (progress / 100) * circumference;
  
  return (
    <div className={`relative ${sizeClasses[size]} ${className}`}>
      <svg 
        className="w-full h-full transform -rotate-90" 
        viewBox="0 0 120 120"
      >
        {/* Background circle */}
        <circle
          cx="60"
          cy="60" 
          r={radius}
          fill="none"
          stroke="#E5E7EB"
          strokeWidth="8"
        />
        {/* Progress circle */}
        <circle
          cx="60"
          cy="60"
          r={radius}
          fill="none"
          stroke="#2F6FED"
          strokeWidth="8"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={strokeDashoffset}
          className="transition-all duration-300 ease-in-out"
        />
      </svg>
      
      {/* Progress text */}
      <div className="absolute inset-0 flex items-center justify-center">
        <span className={`font-bold text-gray-900 ${
          size === 'sm' ? 'text-sm' : size === 'lg' ? 'text-2xl' : 'text-lg'
        }`}>
          {progress}%
        </span>
      </div>
    </div>
  );
};